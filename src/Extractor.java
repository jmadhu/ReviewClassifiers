import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Extractor {
    public static final int MAX_WORD_LENGTH = 20;
    public static final String REVIEW_BEGIN_TOKEN = "review/text:";
    public static final String REVIEW_END_TOKEN = "product/productId:";
    public static final String SCORE_TOKEN = "review/score:";
    public ArrayList<Example> examples;
    
    public Extractor() {
        examples = new ArrayList<Example>();
    }
    
    /**
     * extract vocabulary and examples from file at filepath
     * 
     * @param filepath
     */
    public HashMap<String, Integer> extractVocabulary(String filepath) {
        HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
        boolean buildingExample = false;
        Example example = new Example();
        try {
        	Scanner scanner = new Scanner(new File(filepath));
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (buildingExample && word.equals(REVIEW_END_TOKEN)) {
                    buildingExample = false;
                    //examples.add(example);
                    //example = new Example();
                } else {
                    if (!buildingExample) {
//                        if (word.equals(SCORE_TOKEN)) {
//                            double score = Double.parseDouble(scanner.next());
//                            example.isPositive = score >= 2.5;
                        if (word.equals(REVIEW_BEGIN_TOKEN)) {
                            buildingExample = true;
                        }
                    } else {
                        if (word.length() <= MAX_WORD_LENGTH) {
//                            Integer exampleCount = example.wordCounts.get(word);
//                            example.wordCounts.put(word, exampleCount == null ? 1 : exampleCount + 1);
                        	String[] words = processWord(word);
                        	for(String w: words){
                            	Integer vocabCount = vocabulary.get(w);
                            	if(w.equals(""))
                            		continue;
                            	System.out.println(w);
                            	vocabulary.put(w, vocabCount == null? 1 : vocabCount + 1);
                            }
                        }
                        // append if EOF is reached
//                        if (!scanner.hasNext()) {
//                            examples.add(example);
//                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return vocabulary;
    }
    private String[] processWord(String word){
    	return word.replaceAll("(?!\')\\p{Punct}", " ").toLowerCase().split("\\s+");
    }

    
    public static void main(String[] args){
        Extractor extractor = new Extractor();
        HashMap<String, Integer> vocabulary = extractor.extractVocabulary("foods_fake.txt");
        System.out.println("done");
    }
}
