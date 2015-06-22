/* 
* Encog(tm) Examples v3.1 - Java Version 
* http://www.heatonresearch.com/encog/ 
* http://code.google.com/p/encog-java/ 
  
* Copyright 2008-2012 Heaton Research, Inc. 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*     http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
*    
* For more information on Heaton Research copyrights, licenses  
* and trademarks visit: 
* http://www.heatonresearch.com/copyright 
*/  

import neuralnetwork.NeuralNetwork;

import org.encog.Encog;  
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;  
import org.encog.ml.data.MLDataPair;  
import org.encog.ml.data.MLDataSet;  
import org.encog.ml.data.basic.BasicMLDataSet;  
import org.encog.neural.networks.BasicNetwork;  
import org.encog.neural.networks.layers.BasicLayer;  
import org.encog.neural.networks.training.propagation.back.Backpropagation;
  
/** 
* XOR: This example is essentially the "Hello World" of neural network 
* programming.  This example shows how to construct an Encog neural 
* network to predict the output from the XOR operator.  This example 
* uses backpropagation to train the neural network. 
*  
* This example attempts to use a minimum of Encog features to create and 
* train the neural network.  This allows you to see exactly what is going 
* on.  For a more advanced example, that uses Encog factories, refer to 
* the XORFactory example. 
*  
*/  
public class XORHelloWorld {  
  
    /** 
     * The input necessary for XOR. 
     */  
	public static double XOR_INPUT1[][] = { { 0.0 }, { 1.0 },  
        { 0.0, 1.0 }, { 1.0, 1.0 } };  
	
	public static double XOR_INPUT2[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },  
            { 0.0, 1.0 }, { 1.0, 1.0 } };  
  
    public static double XOR_INPUT3[][] = { { 0.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 },  
        { 0.0, 1.0, 0.0 }, { 1.0, 1.0, 0.0 } };  

    /** 
     * The ideal data necessary for XOR. 
     */  
    public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };  
      
    /** 
     * The main method. 
     * @param args No arguments are used. 
     */  
    public static void main(final String args[]) {  
          
        /*// create a neural network, without using a factory  
        BasicNetwork network = new BasicNetwork();  
        network.addLayer(new BasicLayer(null,true,2));  
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,5));  
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,5));  
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));  
        network.getStructure().finalizeStructure();  
        network.reset();  
        
        // create training data  
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);  
          
        // train the neural network  
        final Backpropagation train = new Backpropagation(network, trainingSet);  
        
        do {  
            train.iteration();  
            System.out.println("Epoch #" + train.getIteration() + " Error:" + train.getError());
        } while(train.getError() > 10e-8);  
  
        double[] out = new double[1];
        network.compute(new double[]{1.0, 1.0}, out);
        System.out.println("Saída: "+Math.round(out[0]));
        
        // test the neural network  
        System.out.println("Neural Network Results:");  
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());  
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)  
                    + ", actual=" + Math.round(output.getData(0)) + ",ideal=" + pair.getIdeal().getData(0));  
        }
          
        Encog.getInstance().shutdown();*/
    	
    	for (int i = 0; i < 100; i++){
	    	NeuralNetwork neuralNetwork = new NeuralNetwork(new int[]{2, 10, 1}, "110");    	
	    	
	    	neuralNetwork.train(XOR_INPUT2, XOR_IDEAL, 0.003, 0.0001, 1000000);
	    	System.out.println(i+" = "+neuralNetwork.getError());
	    	//System.out.println(neuralNetwork.classify(new double[]{0.0, 0.0})[0]);
	    	//System.out.println(neuralNetwork.classify(new double[]{1.0, 0.0})[0]);
	    	//System.out.println(neuralNetwork.classify(new double[]{0.0, 1.0})[0]);
	    	//System.out.println(neuralNetwork.classify(new double[]{1.0, 1.0})[0]);
    	}
    }  
}  