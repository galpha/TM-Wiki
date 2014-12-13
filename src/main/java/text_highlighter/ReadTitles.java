package text_highlighter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Liefert alle Titel als Array zurueck
 * @author Yves
 *
 */
public class ReadTitles {
	
	/**
	 * Zum Testen der Funktion readTitles.
	 * Liest alle Titel ein und gibt sie auf der Konsole aus.
	 * @param args
	 */
	public static void main(String[] args){			
		String[] titles = null;		
		titles = readTitles("./data/all_title.xml");		
		for(String title : titles){
			System.out.println(title);			
		}
	}
	
	/**
	 * Liest xml mit allen Titeln ein.
	 * Entfernt dabei die Tags.
	 * @param file Pfad zur XML
	 * @return String array mit einer Zeile aus der XML pro Eintrag
	 */
	public static String[] readTitles(String file){
		ArrayList<String> titles = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader( new FileReader (file));
			String line;
			while( ( line = reader.readLine() ) != null ) {
				if (line.equals("<all_title>") || line.equals("</all_title>"))
					continue;
				// entfernt title-Tags und entfernt alle Leerzeichen am Anfang und am Ende
				line = line.replace("<title>", "").replace("</title>", "").trim();
				/*
				 * Hier wird die ganze Zeile gespeichert, weil z.B. New York nicht
				 * in zwei Eintraege getrennt werden sollte,
				 * da sonst ueberall New markiert werden wuerde.
				 */
				titles.add(line);
				//System.out.println(line);				
			}						
			reader.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}			
		return titles.toArray(new String[titles.size()]);
	}
}
