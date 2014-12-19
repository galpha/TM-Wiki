package text_highlighter;

public class distance_calculate {

    public static void main(final String[] args) {
        System.out.println("START - readTitles");

        String[] titles = ReadTitles.readTitles("./data/all_title.xml");
        String places = "./data/all_places.xml";
        String outputFile = "./data/places_highlighted.xml";

        System.out.println("START - ReadPlacesCompareAndWrite");

        ReadPlacesCompareAndWrite.readPlacesCompareAndWrite(places, titles, outputFile);

        System.out.println("Finish");
    }
}
