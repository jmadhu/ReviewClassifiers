import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;


public class LibSvmFeatureExtractor {
    private static double TRAIN_TEST_RATIO = 0.5;

    public static void extract(String filepath, Set<String> vocabulary) {
        boolean buildingExample = false;
        Example example = new Example();
        try {
        	Scanner scanner = new Scanner(new File(filepath));
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (buildingExample && word.equals(Extractor.REVIEW_END_TOKEN)) {
                    buildingExample = false;
                    output(example, vocabulary);
                    example = new Example();
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
        builder.append("\n");  
        try {
            String content = builder.toString();
            File file;
            if (Math.random() >= TRAIN_TEST_RATIO) {
                file = new File("a.train");
            } else {
                file = new File("a.test");
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
