import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class filter {

    private static String xmlTitles;

    public static void main(final String[] args) {
        try {
            xmlTitles = readFile("./data/all_title.xml");
        }
        catch (IOException e) {
            System.out.println("readFile() failed.\n");
            e.printStackTrace();
        }

        // seperate into lines
        String[] titles = xmlTitles.split("\\n");

        // titles1 - only one-word-titles; titles2 - the rest
        String result1 = "";
        String result2 = "";

        // delete title tags and skip first and last line
        for (int h = 1; h < (titles.length-1); h++) {
            titles[h] = titles[h].replace("\t<title>","");
            titles[h] = titles[h].replace("</title>","");

            //if (titles[h].contains(" ") || titles[h].contains("(") || titles[h].contains(")") || titles[h].contains("-") || titles[h].contains(".")) {
            //if (titles[h].contains("(") || titles[h].contains(")") || titles[h].contains("[") || titles[h].contains("]")) {
            //if (titles[h].contains("-")) {
            if (titles[h].contains(".")) {
                result2 += titles[h] + "\n";
            }
            //else {
            //    result1 += titles[h] + "\n";
            //}
        }

        // save lists
        //writeFile(result1, "./data/one_word_titles.xml");
        //writeFile(result2, "./data/multi_word_titles.xml");
        //writeFile(result2, "./data/braces.xml");
        //writeFile(result2, "./data/dashes.xml");
        writeFile(result2, "./data/dots.xml");
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

    private static void writeFile(String s, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter( new FileWriter(path));
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
