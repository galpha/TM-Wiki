import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by me on 01.12.14.
 */
public class Parser
{
	private static final String BASE_URL = "http://de.wikipedia.org/w/index.php?title=%s&printable=yes";
	//private static final String BASE_URL = "http://de.wikipedia.org/wiki/%s";

	private String data;

	public Parser(String data)
	{
		this.data = data;
	}

	public String getPlaces()
	{
		String[] lines = data.split(System.lineSeparator());
		StringBuilder stringBuilder = new StringBuilder();

		for (String line : lines)
		{
			try
			{
				Document document = Jsoup.connect(String.format(BASE_URL, URLEncoder.encode(line, "UTF-8"))).get();
				Elements div = document.getElementsByTag("div");

				for (int i = 0; i < div.size(); i++)
				{
					if (div.get(i).hasAttr("id"))
					{
						String id = div.get(i).attr("id");

						if (id.contains("normdaten"))
						{
							if (div.get(i).text().contains("Geografikum"))
							{
								stringBuilder.append(line);
								stringBuilder.append(System.lineSeparator());

								System.out.println(line);
							}
						}
					}

				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return stringBuilder.toString();
	}
}
