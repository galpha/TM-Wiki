package title_parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PageContentHandler implements ContentHandler {

    private String currentValue;
    private Page page;
    private StringBuilder buf;

    private BufferedWriter fileWriter;



    public void characters(char[] ch, int start, int length)
            throws SAXException {



        for (int i = start; i < start + length; i++) {
            buf.append(ch[i]);
        }

        currentValue = buf.toString();
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        buf = new StringBuilder();
        if (localName.equals("page")) {
            page = new Page();


        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        boolean redirect = false;

        String XML_OPEN_PLACE="<place>";
        String XML_CLOSE_PLACE="</place>";
        String XML_TITLE_OPEN="<title>";
        String XML_TITLE_CLOSE="</title>";
        String XML_ARTICLE_OPEN="<article>";
        String XML_ARTICLE_CLOSE="</article>";

        if (localName.equals("title")) {
            page.setTitle(currentValue);
//            System.out.println(currentValue);
        }

        if (localName.equals("text")) {
            page.setText(currentValue);
//            System.out.println(currentValue);
        }

        if (localName.equals("redirect title")){
            redirect = true;
        }


        if (localName.equals("page")) {
            if (!redirect) {
                if (page.getText().contains("TYP=g")) {
                    try{
                        fileWriter.write(String.format("\t%s", XML_OPEN_PLACE));
                        fileWriter.newLine();
                        fileWriter.write(String.format("\t%s%s%s", XML_TITLE_OPEN, page.getTitle(), XML_TITLE_CLOSE));
                        fileWriter.newLine();
                        fileWriter.write(String.format("\t\t%s", XML_ARTICLE_OPEN));
                        fileWriter.newLine();
                        fileWriter.write(String.format("\t\t%s", page.getText()));
                        fileWriter.newLine();
                        fileWriter.write(String.format("\t\t%s", XML_ARTICLE_CLOSE));
                        fileWriter.newLine();
                        fileWriter.write(String.format("\t%s", XML_CLOSE_PLACE));
                        fileWriter.newLine();
                    }catch (IOException e){

                    }

                }
            }
        }
    }


    public void endDocument() throws SAXException {

        String XML_CLOSE="</wiki>";

        try {
            fileWriter.write(String.format("%s",XML_CLOSE));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
        String XML_OPEN="<wiki>";

        try {
            fileWriter = new BufferedWriter(new FileWriter("all_places.xml"));
            fileWriter.write(String.format("%s", XML_OPEN));
            fileWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

}