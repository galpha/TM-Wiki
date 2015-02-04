//package text_highlighter;

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
            //xmlTitles = readFile("./data/test_roh.txt");
            xmlTitles = readFile("./data/trainingsdaten_roh.txt");
        }
        catch (IOException e) {
            System.out.println("readFile() failed.\n");
            e.printStackTrace();
        }

        // seperate into lines
        String[] titles = xmlTitles.split("\\n");
        String result = "";
        System.out.println("split successful.\n");

        // delete title tags and skip first and last line
        for (int h = 0; h < titles.length; h++) {
            if ( titles[h].length() > 2 && !titles[h].substring(0,1).equals("\t") ) {
                if ( !titles[h].contains("|") && !titles[h].contains("=") && !titles[h].contains("“") && !titles[h].contains("_")) {
                    result += titles[h] + "\n";
                }
                else {
                    if (titles[h].contains("LOCATION")) {
                        titles[h] = titles[h].replace("“", "");
                        titles[h] = titles[h].replace("|", "");
                        titles[h] = titles[h].replace("=", "");
                        titles[h] = titles[h].replace("»", "");
                        result += titles[h] + "\n";
                    }
                }
            }

        }

        System.out.println("iteration successful.\n");

        //writeFile(result, "./data/test_bereinigt.txt");
        writeFile(result, "./data/trainingsdaten_bereinigt.txt");
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
