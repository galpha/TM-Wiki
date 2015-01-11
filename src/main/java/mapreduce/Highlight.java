package mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.mortbay.log.Log;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by me on 09.01.15.
 */
public class Highlight
{
	/**
	 * Mapper bekommt XML Abschnitte mit <place>...</place> als Input und erzeugt Schlüssel/Werte Paare: Title/getaggter Text
	 */
	public static class PlaceMapper extends Mapper<LongWritable, Text, Text, Text>
	{
		private List<String> titles; // Liste für die Titel

		@Override
		protected void setup(Context context) throws IOException, InterruptedException
		{
			Path path = new Path("file:///home/me/Downloads/MapReduceTest/Highlight/all_title.xml"); // Pfad zu all_titles.xml, falls auf HDFS, dann hdfs://
			FileSystem fileSystem = FileSystem.get(new Configuration());
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));

			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null)
			{
				sb.append(line).append("\n");
			}

			reader.close();

			String titlesXML = sb.toString();

			Document document = Jsoup.parse(titlesXML, "UTF-8", Parser.xmlParser());
			Elements elements = document.select("title");

			titles = new ArrayList<String>();

			for (int i = 0; i < elements.size(); i++)
			{
				titles.add(elements.get(i).text());
			}
		}

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			String xml = value.toString();

			Document document = Jsoup.parse(xml, "UTF-8", Parser.xmlParser());
			Elements elements = document.select("place");

			String propertyName = elements.select("title").text();
			String propertyValue = elements.select("article").text();

			if (propertyName != null && propertyValue != null && propertyName.length() > 0 && propertyValue.length() > 0)
			{
				context.write(new Text(propertyName.trim()), new Text(tagPlaces(propertyValue.trim())));
			}
		}

		private String tagPlaces(String text)
		{
			String[] orgWords = text.split("\\s+");
			String[] words = orgWords.clone();

			for (int i = 0; i < orgWords.length; i++)
			{
				int c = words[i].indexOf("/");

				if (c > 0)
				{
					words[i] = words[i].replace(words[i].substring(c), "");
				}

				words[i] = words[i].replaceAll("[\\Q][{}()\"�',.;!?<>%\\E]", "");
			}

			compareWords(orgWords, words);

			StringBuilder sb = new StringBuilder();

			for (String word : orgWords)
			{
				sb.append(word + " ");
			}

			return sb.toString();
		}

		private void compareWords(String[] orgWords, String[] words)
		{
			AbstractStringMetric metric = new Levenshtein();

			for (int w = 0; w < words.length; w++)
			{
				for (int t = 0; t < titles.size(); t++)
				{
					int count = titles.get(t).length() - titles.get(t).replace(" ", "").length();

					int countReal = 0;
					String wordToCompare = words[w];

					if (count > 0)
					{
						for (int co = 1; co + w < words.length && co <= count; co++)
						{
							wordToCompare += " " + words[w + co];
							countReal++;
						}
					}

					float distance = metric.getSimilarity(wordToCompare, titles.get(t));
					if (distance > 0.8333333)
					{
						if (count == 0)
						{
							orgWords[w] = orgWords[w].replace(words[w], "<HIGHLIGHT>" + words[w] + "</HIGHLIGHT>");
						}
						else
						{
							orgWords[w] = orgWords[w].replace(words[w], "<HIGHLIGHT>" + words[w]);
							orgWords[w + countReal] = orgWords[w + countReal].replace(words[w + countReal], words[w + countReal] + "</HIGHLIGHT>");
						}

						w += countReal;

						break;
					}
				}
			}
		}
	}

	/**
	 * Reducer baut eigentlich nur die XML-Datei zusammen
	 */
	public static class PlaceReducer extends Reducer<Text, Text, Text, Text>
	{
		private Text outputKey = new Text();

		@Override
		protected void setup(Context context) throws IOException, InterruptedException
		{
			context.write(new Text("<wiki>"), new Text(""));
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException
		{
			context.write(new Text("</wiki>"), new Text(""));
		}

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{

			for (Text value : values)
			{
				if (value.toString() != null && value.toString().trim().length() > 0)
				{
					String test = constructXML(key, value);
					outputKey.set(test);
					context.write(outputKey, new Text(""));
				}
			}

		}

		public static String constructXML(Text key, Text value)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("\t<title>");
			sb.append(key.toString());
			sb.append("</title>\n\t<article>\n\t\t");
			sb.append(value.toString());
			sb.append("\n\t</aricle>");

			return sb.toString();
		}
	}

	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		conf.set("xmlinput.start", "<place>");
		conf.set("xmlinput.end", "</place>");
		Job job = Job.getInstance(conf, "highlight");
		job.setInputFormatClass(XmlInputFormat.class);
		job.setJarByClass(Highlight.class);
		job.setMapperClass(PlaceMapper.class);
		//job.setCombinerClass(PlaceReducer.class);
		job.setReducerClass(PlaceReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
