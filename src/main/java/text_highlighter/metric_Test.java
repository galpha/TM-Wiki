package text_highlighter;

import uk.ac.shef.wit.simmetrics.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

public class metric_Test {

    public static void main(final String[] args) {

        //Levenshtein(), CosineSimilarity(), EuclideanDistance() oder MongeElkan()
        AbstractStringMetric metric = new MongeElkan();
        float result = metric.getSimilarity("Dresden", "Treten");
        System.out.println("Test: " + result);
    }

}
