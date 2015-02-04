//package createTraining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schreibt die Trainings.tsv datei für den NER
 */
public class Main {


  public static BufferedReader in;
  public static Map<String, String> words;
  private static Pattern space = Pattern.compile(" ");
  private static Pattern dot = Pattern.compile("\\.");
  private static Pattern highlight = Pattern.compile("<HIGHLIGHT>(.*?)</HIGHLIGHT>");
  private static StringBuilder strB;
  private static String output="";
  private static boolean dotFound = false;


  public static void main(String[] args) throws IOException {
    strB = new StringBuilder();

    BufferedWriter writer =
      new BufferedWriter(new FileWriter("./out"));

    in = new BufferedReader(new FileReader("./all_sentences.data"));
    String line;
      while ((line = in.readLine()) != null) {
          words = new HashMap<>();
          prozessSentece(line);
        writer.write(output);
        output = "";
      }
    writer.close();

//
//
//
//    getMarkedWords(words); //schreibt Wort LOCATION
//    getOWords(words); // schreibt Wort O

  }

  private static void prozessSentece(String line){
    findHighlight(line);
    findOthers(line);
    checkDotFoundAndWrite();
  }

  private static void findHighlight(String line){
    Matcher matcher = highlight.matcher(line);
    while (matcher.find()) {
      String place = matcher.group(1).replaceAll(
        "[\\Q][{}(,:|=)\"�';<>%\\E]", "");
      if(!place.contains(" ")){
        if(place.contains("HIGHLIGHT")){
          place = place.replace("HIGHLIGHT", "");
        }
        if(place.contains(".")){
          dotFound = true;
          place = place.replace(".", "");
        }else{
          dotFound =false;
        }
       words.put(place, "LOCATION");
      }
    }
  }

  private static void findOthers(String line){
    String[] isolatedWords = space.split(line);
    for (int i = 0; i < isolatedWords.length; i++) {
      if(!isolatedWords[i].contains("<Highlight>")){
        String word = isolatedWords[i].replaceAll(
          "[\t\\Q][{}(,:|=)\"�';<>%\\E]", "");
        if(!words.containsKey(word)){
          if(word.length()>=1) {
            if (!word.contains(" ")) {
              if (!word.contains("HIGHLIGHT")) {
                if(word.contains(".")){
                  dotFound=true;
                  word = word.replace(".", "");
                }else{
                  dotFound=false;

                }
                words.put(word, "O");
              }
            }
          }
        }
      }
    }
  }

  private static void checkDotFoundAndWrite(){
    for (Map.Entry<String, String> entry : words.entrySet()) {
      output += (String.format("%s\t%s\n", entry.getKey(), entry.getValue()));
    }
    if(dotFound){
      output += "\n";
      dotFound = false;
    }
  }

//  private static Map<String, String> getMarkedWords(Map<String, String> words) throws
//    IOException {
//
//    String zeile;
//    while ((zeile = in.readLine()) != null) {
//      Matcher matcher = highlight.matcher(zeile);
//      while (matcher.find()) {
//        String place = matcher.group(1).replaceAll(
//          "[\\Q][{}(.,:|=)\"�';" + "<>%\\E]", "");
//        if(!place.contains(" ")){
//          if(place.contains("HIGHLIGHT")){
//            place = place.replace("HIGHLIGHT", "");
//          }
//          words.put(place, "LOCATION");
//        }
//      }
//    }
//    System.out.println("Location geschrieben!");
//    return words;
//  }
//
//  private static Map<String, String> getOWords(Map<String, String> words) throws
//    IOException {
//
//    String zeile = null;
//    in = new BufferedReader(new FileReader("/home/galpha/Studium/textmining/all_sentences"));
//    while ((zeile = in.readLine()) != null) {
//      String[] isolatedWords = space.split(zeile);
//      for (int i = 0; i < isolatedWords.length - 1; i++) {
//        if(!isolatedWords[i].contains("<Highlight>")){
//          String word = isolatedWords[i].replaceAll(
//            "[\t\\Q][{}(.,:|=)\"�';" + "<>%\\E]", "");
//          if(!words.containsKey(word)){
//            if(word.length()>=1) {
//              if (!word.contains(" ")) {
//                if (!word.contains("HIGHLIGHT")) {
//                  words.put(word, "O");
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//
//    System.out.println("O-Words geschrieben!");
//    return words;
//  }


  private static void writeFile(String output) throws IOException {

//
//
//        writer.write(output);
//
//      writer.close();

  }
}
