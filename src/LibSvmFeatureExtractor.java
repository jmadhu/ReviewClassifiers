import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;


public class LibSvmFeatureExtractor {
    private static final double TRAIN_TEST_RATIO = 0.8;
    private static final String TRAIN_FILE = "a.train";
    private static final String TEST_FILE = "a.test";

    public static void extract(String filepath, Set<String> vocabulary, int numExamples) {
        boolean buildingExample = false;
        Example example = new Example();
        try{
            new File(TRAIN_FILE).delete();
            new File(TEST_FILE).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        int reviewNum = 1;
        try {
        	Scanner scanner = new Scanner(new File(filepath));
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (buildingExample && word.equals(Extractor.REVIEW_END_TOKEN)) {
                    buildingExample = false;
                    output(example, vocabulary);
                    example = new Example();
                    if (numExamples != -1 && reviewNum > numExamples) {
                        break;
                    }
                    reviewNum++;
                } else {
                    if (!buildingExample) {
                        if (word.equals(Extractor.SCORE_TOKEN)) {
                            double score = Double.parseDouble(scanner.next());
                            example.isPositive = score >= 2.5;
                        } else if (word.equals(Extractor.REVIEW_BEGIN_TOKEN)) {
                            buildingExample = true;
                        }
                    } else {
                        if (word.length() <= Extractor.MAX_WORD_LENGTH) {
                        	String[] words = Extractor.processWord(word);
                        	for(String w: words){
                            	if(!vocabulary.contains(w))
                            		continue;
                                Integer exampleCount = example.wordCounts.get(word);
                                example.wordCounts.put(word, exampleCount == null ? 1 : exampleCount + 1);
                                // append if EOF is reached
                                if (!scanner.hasNext()) {
                                    // add to file
                                    output(example, vocabulary);
                                }
                            }
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static void output(Example example, Set<String> vocabulary) {
        StringBuilder builder = new StringBuilder();
        builder.append(example.isPositive ? "+1" : "-1");
        int featureNum = 1;
        for (String feature: vocabulary) {
            if (example.wordCounts.containsKey(feature)) {
                builder.append(" " + featureNum + ":" + example.wordCounts.get(feature));
            }
            featureNum++;
        }
        // ignore examples without features
        if (builder.length() == 2) {
            return;
        }
        builder.append("\n");  
        try {
            String content = builder.toString();
            File file;
            if (Math.random() >= TRAIN_TEST_RATIO) {
                file = new File(TEST_FILE);
            } else {
                file = new File(TRAIN_FILE);
            }
    
            if (!file.exists()) {
                file.createNewFile();
            }
 
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
