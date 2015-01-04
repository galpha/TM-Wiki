package text_highlighter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Liefert alle Titel als Array zurueck
 * @author Yves, Stefan
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
		titles = readTitles("./data/multi_word_titles.xml");
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
			//BufferedReader reader = new BufferedReader( new FileReader (file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")); // UTF-8 ist wichtig!
			String line, clearLine;

			// stop-Wort Liste: immer führendes Leerzeichen mit angeben um Formatierung zu bewahren
			// TODO: Liste erweitern
			String[] stopWordList = {" an", " am", " ob", " der", " und", " im", " bei", " vor", " dem"};

			while( ( line = reader.readLine() ) != null ) {
				if (line.equals("<all_title>") || line.equals("</all_title>"))
					continue;
				// entfernt title-Tags und entfernt alle Leerzeichen am Anfang und am Ende
				line = line.replace("<title>", "").replace("</title>", "").trim();
				clearLine = line;

				// sucht nach stop-Wort und löscht es in Kopie
				for (String stopWord : stopWordList) {
					clearLine = clearLine.replace(stopWord, "");
				}

				// füge originale Zeile und eventuell bereinigte Kopie hinzu
				titles.add(line);
				if (!clearLine.equals(line)) {
					titles.add(clearLine);
				}
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
