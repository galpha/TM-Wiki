package text_highlighter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
		String[] titles = {"Leipzig", "Dresden", "Halle", "Sachsen", "Deutschland", 
				"Sachsen-Anhalt", "Thüringen", "Mitteleuropa", "Frankfurt am Main", "New York"};		
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
			//BufferedReader reader = new BufferedReader( new FileReader (file));
			// Angabe von UTF-8 ist zwingend, da z.B. Umlaute Fehler bringen
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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
	        		//words[i] = words[i].replaceAll("[\\Q][{},.;!?<>%\\E]", ""); // welche Symbole noch?
	        		int c = words[i].indexOf("/"); 
	        		// TODO nur das Wort davor, wie sieht es mit danach aus? evtl. den Nachteil als Word wieder einuegen?
	        		if (c>0){
	        			// Rest loeschen
	        			words[i] = words[i].replace(words[i].substring(c), "");
	        		}
	        		words[i] = words[i].replaceAll("[\\Q][{}()\"“',.;!?<>%\\E]", ""); // welche Symbole noch?
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
				// besteht der Titel aus mehreren Woertern, dann pruefe auch mehrere
				int count = titles[t].length() - titles[t].replace(" ", "").length(); // Anzahl Leerzeichen
				int countReal = 0; // wegen Zeilenende, siehe int co in for-Schleife
				String wordToCompare = words[w];
				if (count > 0){
					for(int co = 1; co+w < words.length && co <= count; co++ ){
						wordToCompare += " " + words[w+co];
						countReal++; 
					}
					//System.out.println(wordToCompare);
				}					
				
				float distance = metric.getSimilarity(wordToCompare, titles[t]);				
				if (distance > 0.8333333){ // 0.8333333 verwirft nur sehr wenige richtige, aber erheblich viele Falsche					
					if (count == 0){
						// nur ein Wort, dann markiere dieses, aber keine Sonderzeichen usw.
						orgWords[w] = orgWords[w].replace(words[w], "<HIGHLIGHT>" + words[w] + "</HIGHLIGHT>");
					}
					else{
						// mehrere Woerter, setze Tag vor das erste und nach dem Letzten
						orgWords[w] = orgWords[w].replace(words[w], "<HIGHLIGHT>" + words[w]);
						orgWords[w+countReal] = orgWords[w+countReal].replace(words[w+countReal], words[w+countReal] + "</HIGHLIGHT>");
					}
					// wenn mehrere Woerter, dann folgende Woerter ueberspringen
					w += countReal;					
					// mache mit naechstem Wort weiter, denn dieses wurde schon 
					// markiert und muss nicht mehr mit den anderen Titeln verglichen werden:					
					break; 					
				}
				// TODO Welche distance ist geeignet?				
				// TODO verschiedene Metricen kombinieren, um Ergebnis zu verbessern?
				// TODO hier vllt. auch Stoppwoerter entfernen?
				
			}
		}
	}

}
