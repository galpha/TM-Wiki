package article_parser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Schreibt alle Sätze welche <Highlight></Highlight> tags enthalten raus.
 */
public class Main {
  public static void main(String[] args) {
    try {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();

      FileReader reader = new FileReader
        ("/home/galpha/Studium/textmining/result3.xml");
      InputSource inputSource = new InputSource(reader);

      xmlReader.setContentHandler(new ArticleContentHandler());

      xmlReader.parse(inputSource);


    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
  }
}