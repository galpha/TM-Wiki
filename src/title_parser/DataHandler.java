import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by me on 01.12.14.
 */
public class DataHandler
{
	public String openFile(String filePath)
	{
		String text = null;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try
		{
			fileReader = new FileReader(filePath);
			bufferedReader = new BufferedReader(fileReader);

			StringBuilder stringBuilder = new StringBuilder();
			String line;

			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line);
				stringBuilder.append(System.lineSeparator());
			}

			text = stringBuilder.toString();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (fileReader != null)
			{
				try
				{
					fileReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return text;
	}

	public boolean writeFile(String data, String filePath)
	{
		boolean successful = false;

		try
		{
			Files.write(Paths.get(filePath), data.getBytes());

			successful = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return successful;
	}
}
