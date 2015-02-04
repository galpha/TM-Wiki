package article_parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;


public class ArticleContentHandler implements ContentHandler {

  private Article article;

  private BufferedWriter fileWriter;

  private StringBuilder articleText;

  private boolean isArticleStarted;

  private int articleCount = 0;
  private int highlightCount = 0;
  private int markedSentenceCount = 0;


  public void characters(char[] ch, int start, int length) throws SAXException {

    if (isArticleStarted) {
      articleText.append(new String(ch, start, length));
    }
  }

  public void startElement(String uri, String localName, String qName,
    Attributes atts) throws SAXException {
    isArticleStarted = false;


    if (localName.equals("article")) {
      articleText = new StringBuilder();
      articleCount++;
      isArticleStarted = true;
      article = new Article();
    }

    if (localName.equals("HIGHLIGHT")) {
      isArticleStarted = true;
      highlightCount++;
      articleText.append("<HIGHLIGHT>");
    }

  }


  public void endElement(String uri, String localName, String qName) throws
    SAXException {

    if (localName.equals("HIGHLIGHT")) {
      isArticleStarted = true;
      articleText.append("</HIGHLIGHT>");
    }

    if (localName.equals("article")) {
      isArticleStarted = false;
      String articleString = articleText.toString();
      article.setText(articleString);
      try {
        printSentences();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }


  }

  private void printSentences() throws IOException {
    String articleString = article.getText();
    String[] sentences = articleString.split(Pattern.quote("."));
    for (int i = 0; i < sentences.length - 1; i++) {
      if (sentences[i].contains("HIGHLIGHT")) {
        fileWriter.write(String.format("\t%s%s\n", sentences[i], ". "));
        markedSentenceCount++;
      }
    }
  }


  public void endDocument() throws SAXException {

    System.out.println("Article: " + articleCount);
    System.out.println("Highlights: " + highlightCount);
    System.out.println("Sentences: " + markedSentenceCount);


    try {
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void endPrefixMapping(String prefix) throws SAXException {
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws
    SAXException {
  }

  public void processingInstruction(String target, String data) throws
    SAXException {
  }

  public void setDocumentLocator(Locator locator) {
  }

  public void skippedEntity(String name) throws SAXException {
  }

  public void startDocument() throws SAXException {

    try {
      fileWriter = new BufferedWriter(
        new FileWriter("/home/galpha/Studium/textmining/all_sentences"));
    } catch (IOException e) {
    }
  }

  public void startPrefixMapping(String prefix, String uri) throws
    SAXException {
  }

}