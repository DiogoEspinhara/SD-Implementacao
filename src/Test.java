import geneticalgorithm.GeneticAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.encog.Encog;

import neuralnetwork.NeuralNetwork;

public class Test {  
  
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int length = 100;
		double[][] INPUT0001 = new double[length][1];
		double[][] INPUT0010 = new double[length][1];
		double[][] INPUT0011 = new double[length][2];
		double[][] INPUT0100 = new double[length][1];
		double[][] INPUT0101 = new double[length][2];
		double[][] INPUT0110 = new double[length][2];
		double[][] INPUT0111 = new double[length][3];
		double[][] INPUT1000 = new double[length][1];
		double[][] INPUT1001 = new double[length][2];
		double[][] INPUT1010 = new double[length][2];
		double[][] INPUT1011 = new double[length][3];
		double[][] INPUT1100 = new double[length][2];
		double[][] INPUT1101 = new double[length][3];
		double[][] INPUT1110 = new double[length][3];
		double[][] INPUT1111 = new double[length][4];
		double[][] OUTPUT = new double[length][1]; 
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\Diogo\\Desktop\\baseSD3.csv"))));
	    	
			String line;
			int i = 0;
			while ((line = bufferedReader.readLine()) != null){
				line = line.replace("\r", "").replace("\n", "");
				
				if (! line.equals("")){
					String[] vector = line.split(",");
					double[] vectorF = new double[]{Double.parseDouble(vector[0])
												  , Double.parseDouble(vector[1])
												  , Double.parseDouble(vector[2])
												  , Double.parseDouble(vector[3])
												  , Double.parseDouble(vector[4])};

				    INPUT0001[i] = new double[]{vectorF[3]};
					INPUT0010[i] = new double[]{vectorF[2]};
					INPUT0011[i] = new double[]{vectorF[2], vectorF[3]};
					INPUT0100[i] = new double[]{vectorF[1]};
					INPUT0101[i] = new double[]{vectorF[1], vectorF[3]};
					INPUT0110[i] = new double[]{vectorF[1], vectorF[2]};
					INPUT0111[i] = new double[]{vectorF[1], vectorF[2], vectorF[3]};
					INPUT1000[i] = new double[]{vectorF[0]};
					INPUT1001[i] = new double[]{vectorF[0],vectorF[3]};
					INPUT1010[i] = new double[]{vectorF[0],vectorF[2]};
					INPUT1011[i] = new double[]{vectorF[0],vectorF[2], vectorF[3]};
					INPUT1100[i] = new double[]{vectorF[0],vectorF[1]};
					INPUT1101[i] = new double[]{vectorF[0],vectorF[1], vectorF[3]};
					INPUT1110[i] = new double[]{vectorF[0],vectorF[1], vectorF[2]};
					INPUT1111[i] = new double[]{vectorF[0],vectorF[1], vectorF[2], vectorF[3]};
					
					OUTPUT[i] = new double[]{vectorF[4]};

					i++;
				}
			}
			
			bufferedReader.close();
			
			//Instancia um objeto HashMap para armazenar as entradas possíveis do sistema.
			HashMap<String, double[][]> listOfInputs = new HashMap<>(15);
			listOfInputs.put("0001", INPUT0001);
			listOfInputs.put("0010", INPUT0010);
			listOfInputs.put("0011", INPUT0011);
			listOfInputs.put("0100", INPUT0100);
			listOfInputs.put("0101", INPUT0101);
			listOfInputs.put("0110", INPUT0110);
			listOfInputs.put("0111", INPUT0111);			
			listOfInputs.put("1000", INPUT1000);
			listOfInputs.put("1001", INPUT1001);
			listOfInputs.put("1010", INPUT1010);
			listOfInputs.put("1011", INPUT1011);
			listOfInputs.put("1100", INPUT1100);
			listOfInputs.put("1101", INPUT1101);
			listOfInputs.put("1110", INPUT1110);
			listOfInputs.put("1111", INPUT1111);
			
			for (i = 0; i < 100; i++){
			
		    	//Instancia um objeto GeneticalAlgorithm para os testes.
				//GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(listOfInputs, OUTPUT, new int[]{10, 1}, 4, 4, 1000, 0.5, 0.05, 0.00001, 0.6, 50000, new String[]{"1111","1100", "0101", "0001"});
				
				//Inicializa o algoritmo genético.
				//geneticAlgorithm.start();
				
				NeuralNetwork network = new NeuralNetwork(new int[]{4, 10, 1}, "1111");
				network.train(INPUT1111, OUTPUT, 0.1, 10e-5, 50000);
				
				System.out.println(i+" / "+network.getInputFeatures()+" / "+network.getError());	
				
				NeuralNetwork network2 = new NeuralNetwork(new int[]{3, 10, 1}, "1011");
				network2.train(INPUT1011, OUTPUT, 0.1, 10e-5, 50000);
				
				System.out.println(i+" / "+network2.getInputFeatures()+" / "+network2.getError());
			}
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
	}

}
