import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Extractor {
    public static final int MAX_WORD_LENGTH = 20;
    public static final String REVIEW_BEGIN_TOKEN = "review/text:";
    public static final String REVIEW_END_TOKEN = "product/productId:";
    public static final String SCORE_TOKEN = "review/score:";
    public ArrayList<Example> examples;
    public static int wordCount;
    public static int uniqueCount;
    public static HashMap<String, Integer> vocabulary;
    
    public Extractor() {
        examples = new ArrayList<Example>();
        vocabulary = new HashMap<String, Integer>();
        wordCount=0;
        uniqueCount=0;
    }
    
    public static int getUniqueWordCount(){
    	uniqueCount = vocabulary.size();
    	return uniqueCount;
    }

    public static int getTotalWordCount(){
    	for(String s: vocabulary.keySet()){
    		wordCount += vocabulary.get(s);
    	}
    	return wordCount;
    }
    /**
     * extract vocabulary and examples from file at filepath
     * 
     * @param filepath
     */
    public HashMap<String, Integer> extractVocabulary(String filepath) {
        
        boolean buildingExample = false;
        Example example = new Example();
        StopWordsSet stopwords = new StopWordsSet();
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
                            	if(w.equals("") || stopwords.isStopWord(w))
                            		continue;
                            	
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
    
    private static void freqwords(HashMap<String, Integer> vocab, int threshold){
    	Set<String> keys = vocab.keySet();
    	Set<String> toremove = new HashSet<String>();
    	for(String s: keys){
    		if(vocab.get(s)<threshold)
    			toremove.add(s);
    	}
    	for(String s: toremove){
    		vocab.remove(s);
    	}    	
    }
    
    private static void topwords(HashMap<String, Integer> vocab, int top){
        ValueComparator valcomp =  new ValueComparator(vocab);
        TreeMap<String, Integer> tm = new TreeMap<String,Integer>(valcomp);
        tm.putAll(vocab);
        
    	Set<String> keys = tm.keySet();
    	
    	int count=0;
    	for(String s: keys){
    		count++;
    		if(count<=top)
    			continue;
    		vocab.remove(s);
    	}
    	
    }
    
    
    private String[] processWord(String word){
    	return word.replaceAll("(?!\')\\p{Punct}", " ").toLowerCase().split("\\s+");
    }

    
    public static void main(String[] args){
        Extractor extractor = new Extractor();
        HashMap<String, Integer> vocabulary = extractor.extractVocabulary("foods_fake.txt");
        System.out.println("Total Word Count: " + Extractor.getTotalWordCount());
        System.out.println("Unique Word Count: " + Extractor.getUniqueWordCount());
//      Total Word Count: 5874
//      Unique Word Count: 1434
        // keep only the words that occur at least x times.
        // Extractor.freqwords(vocabulary, 5);
        // keep only the top x words.
        Extractor.topwords(vocabulary, 5);
        System.out.println(vocabulary.size());
        System.out.println("done");
    }
    
}
class ValueComparator implements Comparator<String> {

    HashMap<String, Integer> base;
    public ValueComparator(HashMap<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) < base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}
