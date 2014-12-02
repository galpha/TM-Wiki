/**
 * Created by me on 01.12.14.
 */
public class Main
{
	public static void main(String[] args)
	{
		DataHandler dataHandler = new DataHandler();
		Parser parser = new Parser(dataHandler.openFile("data/start_data2.txt"));
		dataHandler.writeFile(parser.getPlaces(), "data/final_data2.txt");
	}
}
