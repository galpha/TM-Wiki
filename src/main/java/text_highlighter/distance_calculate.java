package text_highlighter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class distance_calculate {

    public static void main(final String[] args) {
        String places = null, matches = null;
        try {
            places = readFile("./data/all_places.xml");
            matches = readFile("./data/all_title.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // split into words
        // \\s+ -> matches sequence of one or more whitespace characters
        String[] words = places.split("\\s+");
        String[] titles = matches.split("\\s+");

        // delete title tags
        for (int h=0; h<titles.length; h++) {
            titles[h] = titles[h].replace("<title>","");
            titles[h] = titles[h].replace("</title>","");
            System.out.println(titles[h]);
        }

        AbstractStringMetric metric = new Levenshtein();
        final float minDistance = 0.7F;

        // iterate through words and highlight every title
        for (int i=0; i<words.length; i++) {
            for (int j=0; j<titles.length; j++) {
                float result = metric.getSimilarity(words[i], titles[j]); // Wert zwischen 0 und 1.0
                if (result >= minDistance){
                    System.out.println(words[i] + " <==> " +titles[j] + " | Wert: " + result);
                    words[i] = "<HIGHLIGHT>" + words[i] + "</HIGHLIGHT>";
                    break; // wurde schon markiert, weiter mit dem naechsten Wort

                }
            }
        }

        // rebuild places
        String finalResult = "";
        for (int k = 0; k<words.length;k++) {
            finalResult += words[k] + " ";
        }

        writeFile(finalResult);

    }

    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }

    private static void writeFile(String s) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter( new FileWriter( "./data/sample_places_highlighted.xml"));
            writer.write(s);
        }
        catch ( IOException e) {
            System.out.println("writeFile() failed.\n");
            e.printStackTrace();
        }
        finally {
            try {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e) {
                System.out.println("writeFile() closing failed.\n");
                e.printStackTrace();
            }
        }
    }
}
