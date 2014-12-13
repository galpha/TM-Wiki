package text_highlighter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * Markiert im Text alle Vorkommen der Titles und deren Abweichungen. 
 * @author Yves
 *
 */
public class ReadPlacesCompareAndWrite {
	
	/**
	 * Metric um Distanc zu berechnen 
	 */
	private static AbstractStringMetric metric = new Levenshtein();
	
	/**
	 * Zum Testen der Funktion readPlacesCompareAndWrite.
	 * Markiert im Text alle Vorkommen der Titles und deren Abweichungen.
	 * @param args
	 */
	public static void main(String[] args){
		String[] titles = {"Leipzig", "Dresden", "Halle"};
		String outputFile = "./data/sample_places_highlighted.xml";
		readPlacesCompareAndWrite("./data/all_places.xml", titles, outputFile);
	}
	
	/**
	 * Liest Zeile fuer Zeile den Text ein und vergleicht diese dann mit 
	 * den Titeln und markiert gegebenfalls alle Ortschaften.
	 * Schreibt das Erbnis wieder in eine XML.
	 * @param file XML-Datei mit allen Texten.
	 * @param titles String-Array mit allen Titeln.
	 * @param outputFile XML-Datei, in die geschrieben werden soll.
	 */
	public static void readPlacesCompareAndWrite(String file, String[] titles, String outputFile){         
		try {
			BufferedReader reader = new BufferedReader( new FileReader (file)); 
			BufferedWriter writer = new BufferedWriter( new FileWriter( outputFile));
	        String line = null;
	        
	        /*
	         * Liest Zeile fuer Zeile, damit der Speicher nicht ueberlauuft
	         * TODO evtl. noch eine andere Moeglichkeit?
	         */
	        while( ( line = reader.readLine() ) != null ) {
	        	String[] orgWords = line.split("\\s+"); // Originale unveraendert, zum Speichern
	        	String[] words = orgWords.clone();  // Punktuation wird entfernt usw.
	        	for(int i=0; i<words.length; i++){
	        		words[i] = words[i].replaceAll("[\\Q][{},.;!?<>%\\E]", ""); // welche Symbole noch?
	        		//System.out.println(words[i]);
	        	}        	        	        	
	        	
	        	// Hier wird jedes Wort mit den Titeln verglichen.
	        	compareWords(orgWords, words, titles);
	        	
	        	// Schreibe bearbeitete Zeile schon mal in output
	        	StringBuilder stringBuilder = new StringBuilder();
	        	for(String word : orgWords){
	        		stringBuilder.append( word + " " );
	        		/*
	        		 *  TODO Was ist mit der original Struktur,
	        		 *  also mit Umbruechen usw.?
	        		 */
	        	}	     
	        	//System.out.println(stringBuilder.toString());
	        	writer.write(stringBuilder.toString());
	            
	        }
	        reader.close();
	        writer.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} 
	}
		
	/**
	 * Vergleicht Woerter aus dem Korpus mit den Titeln auf Aehnlichkeit.
	 * Ist die Distance klein genug, wird das Originalwort markiert.
	 * @param orgWords Originalwort, wie es im Text vorkommt.
	 * @param words Wort aus dem Text, aber ohne Satzzeichen.
	 * @param titles Alle Titel, mit denen verglichen wird.
	 */
	private static void compareWords(String[] orgWords, String[] words, String[] titles){
		for(int w=0; w<words.length; w++){
			for(int t=0; t<titles.length; t++){
				float distance = metric.getSimilarity(words[w], titles[t]);
				if (distance > 0.81){
					orgWords[w] = "<HIGHLIGHT>" + orgWords[w] + "</HIGHLIGHT>";
					// mache mit naechstem Wort weiter, denn dieses wurde schon 
					// markiert und muss nicht mehr mit den anderen Titeln verglichen werden:					
					break; 					
				}
				// TODO Welche distance ist geeignet?
				// TODO Wenn Titel aus mehreren Woertern besteht, dann vergleiche auch mit mehreren!
				// TODO verschiedene Metricen kombinieren, um Ergebnis zu verbessern?
				// TODO Was ist mit Zeilenumbruechen? ... New \n York ...
				
			}
		}
	}

}
