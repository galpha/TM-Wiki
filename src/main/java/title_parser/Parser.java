package title_parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;


public class Parser {

    private static final String BASE_URL = "http://de.wikipedia.org/w/index.php?title=%s&printable=yes";

    private String path;

    public Parser(String path) {
        this.path = path;
    }

    public String getPlaces() throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            try {
                Document document = Jsoup.connect(String.format(BASE_URL, URLEncoder.encode(line, "UTF-8"))).get();
                Elements div = document.getElementsByTag("div");
                System.out.println("Wikipedia:     " + line);


                for (int i = 0; i < div.size(); i++) {
                    if (div.get(i).hasAttr("id")) {
                        String id = div.get(i).attr("id");

                        if (id.contains("normdaten")) {
                            if (div.get(i).text().contains("Geografikum")) {
                                stringBuilder.append(line);
                                stringBuilder.append(System.lineSeparator());

                                System.out.println(line);
                            }
                        }
                    }

                }

            } catch (IOException e) {
                System.out.println("Connection failed e.g. (405 , 404...)");
                //e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}
