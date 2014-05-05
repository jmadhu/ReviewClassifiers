//Submitted by Madhuvanthi Jayakumar and Christian Eubank (jmadhu/cge)

import java.io.FileNotFoundException;
import java.io.IOException;

public class NeuralNet implements Classifier{
  private double[][] inputToHidden; //weights from input to hidden layers
  private double[] hiddenToOutput;  //weights from hidden layer to output
  private int hiddenU;
  
  public NeuralNet(DataSet d){
    //design: 1 hidden layer, 10 units in hidden layer, alpha = 0.1, 100 epochs
    this(d, 10, 0.1, 100);
  }
  
  public NeuralNet(DataSet d, int hiddenUnits, 
                                     double learningRate, int epoch){
    //three layers:
    //layerZ is dot product of previous layer and weights and layer = g(dotProd)
    double[] input = new double[d.numAttrs];
    double[] hiddenZ = new double[hiddenUnits]; //size: set by parameter
    double[] hidden = new double[hiddenUnits];
    double outputZ = 0; //only one output unit: binary classification
    double output = 0; 
    
    //weights between layers:
    inputToHidden = new double[hiddenUnits][d.numAttrs]; 
    hiddenToOutput = new double[hiddenUnits];
    hiddenU = hiddenUnits;
    
    //deltas for back propogation:
    double outputDelta = 0;
    double[] hiddenDelta = new double[hiddenUnits];
    double[] inputDelta = new double[d.numAttrs];
    int realOutput;
    
    for(int i = 0; i < hiddenUnits; i++) {
      for(int j = 0; j < d.numAttrs; j++) {             
        inputToHidden[i][j] = Math.random() - 0.5;
      }
    }
    for(int i = 0; i < hiddenUnits; i++){ 
      hiddenToOutput[i] = Math.random() - 0.5;
    }
    
    for(int e = 0; e < epoch; e++) {
      for(int t = 0; t < d.numTrainExs; t++) {
        //resetting for each example:
        for(int i = 0; i < hiddenUnits; i++) {
          hiddenZ[i]=0;
        }
        outputZ=0;
        
        //set input layer
        for(int i = 0; i < d.numAttrs; i++) {
          if (d.trainEx[t][i] > 0)
            input[i] = 1;
          else
            input[i] = -1;
        }
        
        //FORWARD PROPOGATION:
        //hidden layer:
        for(int i = 0; i < hiddenUnits; i++) {
          //calculate dot product of in values and weights
          //note: index of the form inputToHidden[hiddenIndex][inputIndex]
          for(int j = 0; j < d.numAttrs; j++) {
            hiddenZ[i] += input[j] * inputToHidden[i][j];
          }
          hidden[i] = g(hiddenZ[i]);
        }
        
        //output layer:
        for(int i = 0; i < hiddenUnits; i++) {
          //dot product of hidden layer and hidden weights
          outputZ += hidden[i] * hiddenToOutput[i];
        }
        output = g(outputZ);
        
        //delta at output layer:
        outputDelta = gprime(outputZ) * (d.trainLabel[t] - output);
        
        //BACKWARD PROPOGATION:
        //delta at hidden layer:
        for(int i = 0; i < hiddenUnits; i++) {
          hiddenDelta[i] = gprime(hiddenZ[i]) * 
            (hiddenToOutput[i] * outputDelta); 
          //update weight from hidden layer to output layer
          hiddenToOutput[i] += learningRate * hidden[i] * outputDelta;
        }
        
        //delta at input layer:
        for(int i = 0; i < d.numAttrs; i++) {
          for(int j = 0; j < hiddenUnits; j++) {
            inputToHidden[j][i] += learningRate * input[i] * hiddenDelta[j];
          }
        }
      }
    }
  }
  
  //sigmoid activation function
  private double g(double x) {
    return ( 1 / (1 + Math.exp(-x)) );
  }
  
  //derivative of sigmoid
  private double gprime(double x) {
    double gFunc = g(x);
    return gFunc * (1- gFunc);
  }
  
  public int predict(int[] ex) {
    double[] hiddenLayerZ = new double[hiddenU];
    double[] hiddenLayer = new double[hiddenU];
    double outputLayerZ = 0;
    double outputLayer = 0;
    int result;
    
    for(int i = 0; i < hiddenU; i++) {
      for(int j = 0; j < ex.length; j++) {
        int attribute = (ex[j] > 0) ? 1 : -1;
        hiddenLayerZ[i] += attribute * inputToHidden[i][j];
      }
      hiddenLayer[i] = g(hiddenLayerZ[i]);
    }
    
    for(int i = 0; i < hiddenU; i++) {
      outputLayerZ += hiddenLayer[i] * hiddenToOutput[i];
    }
    
    outputLayer = g(outputLayerZ);
    return (outputLayer < 0.5) ? 0 : 1;
  }
  
  public String algorithmDescription() {
    return "Mutlilayer feed-forward neural network over binary attributes "
      + "with 1 hidden layer of " + hiddenU + " nodes";
  }
  
  public String author() { 
    return "jmadhu/kevinlee";
  }
  
  //tests the algorithm by reading in filestem
  public static void main(String argv[])
    throws FileNotFoundException, IOException {
    
//    if (argv.length < 1) {
//      System.err.println("argument: filestem");
//      return;
//    }
    String filestem = "c";
    DataSet d = new DataSet(filestem, 200);
    //default values
    int hu = 10;
    double lr = 0.1;
    int e = 100;
    if(argv.length>1){
    	hu = Integer.parseInt(argv[1]);
    	lr = new Double(argv[2]);
    	e = Integer.parseInt(argv[3]);
    }
    Classifier c = new NeuralNet(d, hu, lr, e);
    d.printTestPredictions(c, filestem);
  }
}
