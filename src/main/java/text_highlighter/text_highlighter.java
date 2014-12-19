package text_highlighter;

import java.io.*;

public class text_highlighter {

    private static float minDistance = 0.7f;
    private static String places, matches;

    public static void main(final String[] args) {
        try {
            places = readFile("./data/sample_places.xml");
            matches = readFile("./data/sample_all_titles.xml");
        }
        catch (IOException e) {
            System.out.println("readFile() failed.\n");
            e.printStackTrace();
        }

        // split into words
        String[] words = places.split("\\s+");
        String[] titles = matches.split("\\s+");

        // delete title tags
        for (int h=0;h<titles.length;h++) {
            titles[h] = titles[h].replace("<title>","");
            titles[h] = titles[h].replace("</title>","");
            //System.out.println(titles[h]);
        }

        // iterate through words and highlight every title
        for (int i=0; i<words.length; i++) {
            for (int j=0; j<titles.length; j++) {
                if (words[i].contains(titles[j])) {
                    // highlight only exact matches for now
                    int start = words[i].indexOf(titles[j]);
                    words[i] = words[i].substring(0, start) + "<HIGHLIGHT>" + words[i].substring(start, start + titles[j].length()) + "</HIGHLIGHT>" + words[i].substring(start + titles[j].length(), words[i].length());
                    System.out.println(words[i]);
                }
            }
        }

        // rebuild places
        String finalResult = "";
        for (int k = 0; k<words.length;k++) {
            finalResult += words[k];
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
