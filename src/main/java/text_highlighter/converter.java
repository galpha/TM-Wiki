//package text_highlighter;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class converter {

    private static String xmlTitles;

    public static void main(final String[] args) {
        try {
            //xmlTitles = readFile("./data/test_roh.txt");
            xmlTitles = readFile("./data/final_trainingsdaten.txt");
        }
        catch (IOException e) {
            System.out.println("readFile() failed.\n");
            e.printStackTrace();
        }

        String result = xmlTitles.replace("/O ", "\tO\n");
        result = result.replace("/LOCATION ", "\tLOCATION\n");

        writeFile(result, "./data/final_trainingsdaten.tsv");
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
