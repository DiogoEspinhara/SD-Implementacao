import geneticalgorithm.GeneticAlgorithm;

import java.util.HashMap;

import neuralnetwork.NeuralNetwork;


public class CopyOfTest {  
  
    public static double XOR_INPUT001[][] = { { 0.0 }, { 0.0 },  
        { 0.0 }, { 0.0 }, {1.0} };  
  
    public static double XOR_INPUT010[][] = { { 0.0 }, { 0.0 },  
        { 1.0 }, { 1.0 }, {1.0} };      
  
    public static double XOR_INPUT011[][] = { { 0.0, 0.0 }, { 0.0, 0.0 },  
        { 1.0, 0.0 }, { 1.0, 0.0 }, {1.0, 1.0} };  
	
	public static double XOR_INPUT100[][] = { { 0.0 }, { 1.0 },  
        { 0.0}, { 1.0}, {1.0} };    
  
    public static double XOR_INPUT101[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },  
        { 0.0, 0.0 }, { 1.0, 0.0 }, {1.0, 1.0} };  
	
	public static double XOR_INPUT110[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },  
            { 0.0, 1.0 }, { 1.0, 1.0 }, {1.0, 1.0} };  
  
    public static double XOR_INPUT111[][] = { { 0.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 },  
        { 0.0, 1.0, 0.0 }, { 1.0, 1.0, 0.0 }, {1.0, 1.0, 1.0} };  

    
    public static double XOR_OUTPUT[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 }, {1.0} };  

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	
    	//Instancia um objeto HashMap para armazenar as entradas possíveis do sistema.
		HashMap<String, double[][]> listOfInputs = new HashMap<>(3);
		listOfInputs.put("001", XOR_INPUT001);
		listOfInputs.put("010", XOR_INPUT010);
		listOfInputs.put("011", XOR_INPUT011);
		listOfInputs.put("100", XOR_INPUT100);
		listOfInputs.put("101", XOR_INPUT101);
		listOfInputs.put("110", XOR_INPUT110);
		listOfInputs.put("111", XOR_INPUT111);
		
		for (int i = 0; i < 1000; i++){
		
	    	//Instancia um objeto GeneticalAlgorithm para os testes.
			GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(listOfInputs, XOR_OUTPUT, new int[]{5, 1}, 2, 3, 1000, 0.9, 0.05, 0.000001, 0.1, 50000, new String[]{"110", "111"});
			
			//Inicializa o algoritmo genético e pega o objeto NeuralNetwork resultante.
			geneticAlgorithm.start();
			
			System.out.println(geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" / "+geneticAlgorithm.getNeuralNetworkActive().getError());
			
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
