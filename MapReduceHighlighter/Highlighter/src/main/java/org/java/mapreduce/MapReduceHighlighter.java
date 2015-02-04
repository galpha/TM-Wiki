package org.java.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Highlighting auf MapReduce
 */
public class MapReduceHighlighter {

  /**
   * MapperClass
   */
  public static class PlaceMapper extends
    Mapper<LongWritable, Text, Text, Text> {
    private List<String> titles; // Liste für die Titel
    private List<String> stopWordList;
    private Map<Character, Integer> startIndex;
    private static final Pattern space = Pattern.compile(" ");

    /**
     * Init Method zum bereitstellen von Lists und Maps und einlesen der
     * all_title.xml welche alle Wikipedia Überschriften beinhaltet welche
     * vom typ geographikum sind (typ=g)
     * @param context zu bearbeitender context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context) throws IOException,
      InterruptedException {
      stopWordList = new ArrayList<>();
      prepareStopWordList();
      startIndex = new HashMap<>();
      Path path = new Path("/user/hduser/input/all_title.xml"); // auf hdfs
      FileSystem fileSystem = FileSystem.get(new Configuration());
      BufferedReader reader =
        new BufferedReader(new InputStreamReader(fileSystem.open(path)));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      reader.close();
      String titlesXML = sb.toString();
      Document document = Jsoup.parse(titlesXML, "UTF-8", Parser.xmlParser());
      Elements elements = document.select("title");
      titles = new ArrayList<>();
      for (int i = 0; i < elements.size(); i++) {
        titles.add(elements.get(i).text());
      }
      Collections.sort(titles);
    }

    /**
     * MapJob liest wikipedia artikel ein. Text der artikel wird an tagplaces
     * übergeben
     * @param key key
     * @param value value
     * @param context context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws
      IOException, InterruptedException {
      String xml = value.toString();
      Document document = Jsoup.parse(xml, "UTF-8", Parser.xmlParser());
      Elements elements = document.select("place");
      String propertyName = elements.select("title").text();
      String propertyValue = elements.select("article").text();
      if (propertyName != null && propertyValue != null &&
        propertyName.length() > 0 && propertyValue.length() > 0) {
        context.write(new Text(propertyName.trim()),
          new Text(tagPlaces(propertyValue.trim())));
      }
    }


    /**
     * Bekommt Wikipedia-Artikel text und splitet jedes wort
     * @param text artikel text
     * @return text with highlights
     */
    private String tagPlaces(String text) {
      String[] orgWords = space.split(text);
      for (int w = 0; w < orgWords.length; w++) {

        int b = orgWords[w].indexOf("/");
        if (b > 0) {
          orgWords[w] = orgWords[w].replace(orgWords[w].substring(b), "");
        }
        orgWords[w] = orgWords[w].replaceAll("[\\Q][{}()\"�';<>%\\E]", "");
      }

      compareWords(orgWords);
      StringBuilder sb = new StringBuilder();
      for (String word : orgWords) {
        sb.append(word + " ");
      }
      return sb.toString();
    }

    /**
     * Stellt Stop-Wort Liste bereit
     */
    private void prepareStopWordList() {
      String[] stopWords =
        {"Der", "Das", "Die", "Ein", "Aus", "Für", "So", "Und", "Ist", "Es",
         "Zu", "In", "Sein", "Von", "Mit", "An", "Auf", "Sie", "Er", "Auch",
         "Als", "Bei"};
      stopWordList = Arrays.asList(stopWords);
    }

    /**
     * Vergleicht ob wort in der Stoppwortliste enthalten (dient nur der
     * übersicht)
     * @param word zu testendes wort
     * @return true or false
     */
    private boolean compareStopWord(String word) {
      return stopWordList.contains(word);
    }

    /**
     * Methode highlightet wörter im text
     * @param orgWords Original text als array
     * @param spaceCount wie viele leerzeichen enthielt das wort aus den
     *                   alltitles
     * @param w stellt im orgwords array
     * @param word word welches gehighlightet werden soll
     */
    private void highlightWord(String[] orgWords, int spaceCount, int w,
      String word) {
      word = word.replaceAll("[\\Q][{}()\"�'.,!?;<>%\\E]", "");
      if (spaceCount == 0) {
        orgWords[w] =
          orgWords[w].replace(word, "<HIGHLIGHT>" + word + "</HIGHLIGHT>");
      } else {
        orgWords[w] =
          orgWords[w].replace(orgWords[w], "<HIGHLIGHT>" + orgWords[w]);
        orgWords[w + spaceCount] = orgWords[w + spaceCount]
          .replace(orgWords[w + spaceCount],
            orgWords[w + spaceCount] + "<HIGHLIGHT>");
      }
    }

    /**
     * Methode zum testen der grundbedingungen 1. wort nicht leerer String 2.
     * ist wort großgeschrieben (nur dann zum vergleich relevant) 3. ist es
     * nicht in der stoppwortliste
     * @param word wort zum testen
     * @return true or false
     */
    private boolean checkWord(String word) {
      return !word.isEmpty() && Character.isUpperCase(word.charAt(0)) &&
        !compareStopWord(word);
    }

    /**
     * Methode welche den Start index des jehweiligen buchstaben aus der map
     * startindex zurückgibt z.B ist unser Wort Frankfurt soll der vergleich
     * mit allen Ortsnamen auch erst bei F anfangen.
     * @param firstCharakter buchstabe welcher gesucht werden soll
     * @return int startindex
     */
    private int getStartIndex(char firstCharakter) {
      int start;
      if (startIndex.containsKey(firstCharakter)) {
        start = startIndex.get(firstCharakter);
      } else {
        start = 0;
      }
      return start;
    }

    /**
     * Wenn ein Buchstabe noch nicht in der map startIndex enthalten ist wird
     * er mit seinem index hinzugefügt
     * @param firstCharakter buchstabe
     * @param t dessen startindex
     */
    private void setStartIndex(char firstCharakter, int t) {
      if (!startIndex.containsKey(firstCharakter)) {
        startIndex.put(firstCharakter, t);
      }
    }

    /**
     * Methode die zählt wie viele leerzeichen ein wort der Ortsliste besitzt
     * z.b. Frankfurt am Main besitzt 3 leerzeichen also werden aus dem text
     * das wort und dessen zwei nachfolger benutzt für den vergleich mit
     * Frankfurt am Main
     * @param title ort aus der ortliste
     * @return anzahl der leerzeichen des ortes
     */
    private int countSpacesInTitle(String title) {
      int spaceCount = 0;
      for (int i = 0; i < title.length(); i++) {
        char c = title.charAt(i);
        if (c == ' ') {
          spaceCount++;
        }
      }
      return spaceCount;
    }

    /**
     * Testen der grundbedingung des OrgWords arrays
     * @param orgWords original wörter des artikels als array
     * @param w stelle im orgwords array
     * @param spaceCount anzahl der gezählten leerzeichen des titels
     * @return true wenn nachfolge wörter existieren, false wenn nicht
     */
    private boolean checkOrgWordCapacity(String[] orgWords, int w,
      int spaceCount) {
      return w + spaceCount <= orgWords.length - 1;
    }

    /**
     * Methode welche das das wort und dessen nachfolger zusammen baut falls
     * im titel leerzeichen vorkommen
     * @param orgWords original wörter des artikels
     * @param word zu vergleichendes wort
     * @param w index in OrgWords
     * @param spaceCount anzahl der gezählten leerzeichen des titels
     * @return zusammen gebautes wort zum vergleichen
     */
    private String buildWordToCompare(String[] orgWords, String word, int w,
      int spaceCount) {
      for (int j = 1; j <= spaceCount; j++) {
        String nextWord = orgWords[w + j];
        word = word + space + nextWord;
      }
      return word;
    }


    /**
     * Compare Methode: Festlegen der levenstein grenze auf 0,83333333. Für
     * jedes Wort des Original Textes wird geprüft das es nicht leer ist
     * Großgeschrieben ist oder zu den stoppwörtern gehört. Es wird getestet
     * ob der erste buchstabe des wortes bereits bekannt ist (wenn ja wird
     * start index gesetzt wenn nicht bleibt start index 0). Nun wird ab dem
     * start index die liste aller bekannten Ortschaften durchgegangen. Wenn
     * ein neuer anfangsbuchstabe gefunden wird dieser und dessen index
     * eingetragen. Für jeden ort wird die anzahl dessen leerzeichen gezählt
     * und nach dem der zu vergleichende string aus dem text zusammen gebaut.
     * Dann wird die leevenstein distanz berechnet und falls diese höher als
     * die festgesetzte grenze ausfällt wird das wort markiert, falls nicht
     * wird mit dem nächsten wort fortgefahren. Falls der ort keine
     * leerzeichen beinhaltet wird gleich die levenstein distanz berechnet
     * und evtl. markiert oder nicht.
     * @param orgWords
     */
    private void compareWords(String[] orgWords) {
      AbstractStringMetric metric = new Levenshtein();
      double border = 0.83333333;
      for (int w = 0; w < orgWords.length; w++) {
        String word = orgWords[w];
        if (checkWord(word)) {
          int start = getStartIndex(word.charAt(0));
          for (int t = start; t < titles.size(); t++) {
            String title = titles.get(t);
            setStartIndex(title.charAt(0), t);
            int spaceCount = countSpacesInTitle(title);
            if (spaceCount != 0) {
              if (checkOrgWordCapacity(orgWords, w, spaceCount)) {
                String compareWord =
                  buildWordToCompare(orgWords, word, w, spaceCount);
                double distance = metric.getSimilarity(compareWord, title);
                if (Double.compare(distance, border) >= 0) {
                  highlightWord(orgWords, spaceCount, w, word);
                  w += spaceCount;
                  break;
                } else {
                  word = orgWords[w];
                }
              }
            } else {
              double distance = metric.getSimilarity(word, titles.get(t));
              if (Double.compare(distance, border) > 0) {
                highlightWord(orgWords, 0, w, word);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Reducer Class welche eine .xml mit folgendem format ausgibt
   * Um die berechnung überhaupt in machbarer zeit auszuführen haben wir den
   * text korpus verkleinert und haben uns nur auf wikipedia artikel zu
   * ortschaften konzentriert.
   * <wiki>
   *   <place>
   *     <title>
   *       überschrift
   *       <article>
   *         text
   *       </article>
   *     </title>
   *   </place>
   * </wiki>
   */
  public static class PlaceReducer extends Reducer<Text, Text, Text, Text> {
    private Text outputKey = new Text();

    @Override
    protected void setup(Context context) throws IOException,
      InterruptedException {
      context.write(new Text("<wiki>"), new Text(""));
    }

    @Override
    protected void cleanup(Context context) throws IOException,
      InterruptedException {
      context.write(new Text("</wiki>"), new Text(""));
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values,
      Context context) throws IOException, InterruptedException {
      for (Text value : values) {
        if (value.toString() != null && value.toString().trim().length() > 0) {
          outputKey.set(constructXML(key, value));
          context.write(outputKey, new Text(""));
        }
      }
    }

    public static String constructXML(Text key, Text value) {
      StringBuilder sb = new StringBuilder();
      sb.append("<place>");
      sb.append("\n\t<title>");
      sb.append(key.toString());
      sb.append("</title>\n\t<article>\n\t\t");
      sb.append(value.toString());
      sb.append("\n\t</article>");
      sb.append("\n</place>");
      return sb.toString();
    }
  }

  /**
   * Steuer Methode
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    conf.set("xmlinput.start", "<place>");
    conf.set("xmlinput.end", "</place>");
    Job job = Job.getInstance(conf, "highlight");
    job.setInputFormatClass(XmlInputFormat.class);
    job.setJarByClass(MapReduceHighlighter.class);
    job.setMapperClass(PlaceMapper.class);
    job.setReducerClass(PlaceReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat
      .addInputPath(job, new Path("/user/hduser/input/all_places.xml"));
    FileOutputFormat.setOutputPath(job, new Path("/user/hduser/output/mr/"));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

