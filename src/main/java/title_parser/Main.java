package title_parser;

import java.io.IOException;

/**
 * Created by me on 01.12.14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        DataHandler dataHandler = new DataHandler();
        Parser parser = new Parser("data/all_title");
        dataHandler.writeFile(parser.getPlaces(), "data/all_places.txt");
    }
}
