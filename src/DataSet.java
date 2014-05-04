import java.util.*;
import java.io.*;

/**
 * This class represents a dataset, including names for the classes,
 * all attributes and their values.  The class also includes a
 * constructor that can read the dataset from data files, as well as a
 * method for printing the predictions of a classifier on each of the
 * test examples in the format required for submission.
 **/
public class DataSet {

    
    public final int TRAIN=0;
    public final int TEST=1;
    
    /** number of training examples **/
    public int numTrainExs;

    /** an array of training examples, each of which is itself an
     * array of integer values so that <tt>trainEx[i][a]</tt> is the
     * value of attribute <tt>a</tt> on example <tt>i</tt> **/
    public int trainEx[][];

    /** an array of labels for the training examples **/
    public int trainLabel[];
    
    /** number of attributes **/
    public int numAttrs;

    /** names of the two classes **/
    public String className[] = null;
    
    /** number of test examples **/
    public int numTestExs;
    
    /** an array of test examples, each one an array of integer values **/
    public int testEx[][];

    public static void main(String args[]){
    	try {
			DataSet ds = new DataSet("a", 600);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /** This constructor constructs an empty dataset with no training
     * examples, no test examples, no attributes, and two classes with
     * default names.
     */
    public DataSet(){
    	numTestExs=0;
    }

    /** This constructor reads in data from the files
     * <tt>filestem.names</tt>, <tt>filestem.train</tt> and
     * <tt>filestem.test</tt>, and then sets up all of the public
     * fields.  See assignment instructions for information on the
     * required format of these files. 
     **/
    public DataSet(String filestem, int numAttrs)
	throws FileNotFoundException, IOException {
    	ArrayList<int[]> attr_list = new ArrayList<int[]>();
        ArrayList<Integer> lab_list = new ArrayList<Integer>();
        this.numAttrs = numAttrs;	
		String[] words = null;
		String line = "";
		// read data files
		int attStart=0;
		for(int traintest=0; traintest<2; traintest++){
			if(traintest==TRAIN){
				try{
					open_file(filestem + ".train");
				}
				catch(Exception e){
					continue;
				}
				attStart=1;	//label
			}
			if(traintest==TEST){
				try{
					open_file(filestem + ".test");
				}
				catch(Exception e){
					continue;
				}
				attStart=0;	//no label
			}
		    while((line = read_line()) != null) {
				line = line.trim( );
				if (line.equals(""))
				    continue;
		
				words = line.split("\\s+");
				if(traintest==0){
				    if (words[0].equals("+1")) {
				    	lab_list.add(new Integer(1));
				    } else {
				    	lab_list.add(new Integer(-1));
				    } 
				}
			    
			    int[] attributes = new int[numAttrs];
			    
				String[] att = new String[2];
				int[] attr = new int[2];
				for (int i = attStart; i < words.length; i++) {
					att = words[i].split(":");
					attr[0]= Integer.parseInt(att[0]);
					attr[1]= Integer.parseInt(att[1]);
					attributes[attr[0]] = attr[1]; 
	
				}
				attr_list.add(attributes);
		    }	//end of while
	    
	    if(traintest==TRAIN){
		    numTrainExs = attr_list.size();
		    trainEx = new int[0][];
		    trainEx=(int[][]) attr_list.toArray(trainEx);
		    trainLabel = new int[numTrainExs];
		    for (int i = 0; i < numTrainExs; i++) {
		    	trainLabel[i] = (lab_list.get(i));
			}
		    for(int i=0; i<numTrainExs; i++){
		    	System.out.println("label: " + trainLabel[i]);
		    	for(int j=0; j<numAttrs; j++){
		    		System.out.print(trainEx[i][j] + " ");
		    	}
		    	System.out.println();
		    }
	    }
	    else{
		    numTestExs = attr_list.size();
		    testEx = new int[0][];
		    testEx=(int[][]) attr_list.toArray(trainEx);
		    for(int i=0; i<numTrainExs; i++){
		    	for(int j=0; j<numAttrs; j++){
		    		System.out.print(trainEx[i][j] + " ");
		    	}
		    	System.out.println();
		    }
	    }
	}	
	in.close();
	in = null;
	filename = null;
    }


    /** This method prints out the predictions of classifier
     * <tt>c</tt> on each of the test examples in the format required
     * for submission.  The result is sent to the given
     * <tt>PrintStream</tt>.
     **/
    public void printTestPredictions(Classifier c,
				     PrintStream out) {
	out.println(c.author());
	out.println(".");
	out.println(c.algorithmDescription());
	out.println(".");
	for(int i = 0; i < numTestExs; i++) {
	    out.println(className[c.predict(testEx[i])]);
	}
    }

    /** This method prints out the predictions of classifier
     * <tt>c</tt> on each of the test examples in the format required
     * for submission.  The result is printed to the file
     * <tt>filestem.testout</tt>.
     **/
    public void printTestPredictions(Classifier c,
				     String filestem)
    throws FileNotFoundException {
	PrintStream out;

	try {
	    out = new PrintStream(new BufferedOutputStream(new
		FileOutputStream(filestem + ".testout")));
	} catch (FileNotFoundException e) {
	    System.err.println("Cannot open file " + filestem + ".testout");
	    throw e;
	}
	printTestPredictions(c, out);

	out.close();
    }


    /*********************** private ********************************/

    private String filename;
    private int line_count;
    private BufferedReader in;

    private void open_file(String filename) throws FileNotFoundException {
	BufferedReader in;
	
	this.filename = filename;
	this.line_count = 0;

	try {
	    in = new BufferedReader(new FileReader(filename));
	} catch (FileNotFoundException e) {
	    System.err.print("File "+filename+" not found.\n");
	    throw e;
	}
	this.in = in;
    }

    private String read_line() throws IOException {
	String line;

	line_count++;

	try {
	    line = in.readLine();
	}
	catch (IOException e) {
	    System.err.println("Error reading line "+line_count+" in file "+filename);
	    throw e;
	}
	return line;
    }


}    
