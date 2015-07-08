package manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import neuralnetwork.NeuralNetwork;
import database.DataBaseHelper;
import geneticalgorithm.GeneticAlgorithm;

public class Manager {
	
	//Atributos de objeto.
	private GeneticAlgorithm geneticAlgorithm;
	private NeuralNetwork defaultNeuralNetwork;
	private int quantityOfVMs, errorGenericAlgoritm;
	private double secondsForDecisions, secondsForUpdateData;
	private Thread threadPrediction, threadUpdateData;
	private HashMap<String, double[][]> listOfInputs;
	
	//Atributos de testes.
	private int errorDefault = 0;
	private boolean isUpdateData = false;
	private HashMap<String, double[][]> listOfInputsTest;
	
	public Manager(int quantityOfVMs, double secondsForDecisions, double secondsForUpdateData){
		
		//Inicializa os valores dos atributos de objeto.
		this.secondsForDecisions = secondsForDecisions;
		this.secondsForUpdateData = secondsForUpdateData;
		
		//Instancia um objeto GeneticAlgorithm para o m�dulo preditor.
		geneticAlgorithm = new GeneticAlgorithm(new int[]{10, 1}, 4, 4, 100, 0.9, 0.05, 10e-5, 0.05, 5000);
		
		//Instancia um objeto NeuralNetwork com todas as entradas para os testes.
		defaultNeuralNetwork = new NeuralNetwork(new int[]{4, 10, 1}, "1111");
		
		//Executa o m�todo que atualiza a arquitetura do sistema.
		updateArchitecture(quantityOfVMs);
		
		//Carrega os dados para o teste de valida��o.
		try {
			listOfInputsTest = DataBaseHelper.getDataBase("C:\\Users\\Diogo\\Desktop\\SD\\baseSD5.csv");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * M�todo para iniciar a execu��o do gerenciador.
	 */
	public void start(){
		
		//Verifica se ainda n�o foi instanciado um objeto Thread para as atualiza��es de dados do m�dulo preditor.
		if (threadUpdateData == null){
			//Instancia um objeto Thread para executar as atualiza��es de dados do m�dulo preditor.
			threadUpdateData = new Thread(new RunnableUpdateData());
			threadUpdateData.start();
		}
		
		//Verifica se ainda n�o foi instanciado um objeto Thread para a tomada de decis�o.
		if (threadPrediction == null){
			//Instancia um objeto Thread para executar as a��es de decis�o.
			threadPrediction = new Thread(new RunnablePrediction());	
			threadPrediction.start();
		}
	}
	
	/*
	 * M�todo para atualizar a arquitetura das m�quinas virtuais de acordo com uma nova quantidade de m�quinas virtuais.
	 */
	public void updateArchitecture(int newQuantityOfVMs){
		
		//Atualiza o valor do atributo quantityOfVMs.
		this.quantityOfVMs = newQuantityOfVMs;
	}
	
	//Classe interna para executar as tomadas de decis�o.
	private class RunnablePrediction implements Runnable {
		@Override
		public void run() {			
			
			//Loop infinito que ficar� executando a predi��o.
			while (true){
				try {
					//Dorme por um intervalo de tempo, evita que fique executando decis�es toda hora.
					Thread.sleep((int) Math.round(secondsForDecisions * 1000));

					//Enquanto n�o existir uma rede neural v�lida, espera.
					while((geneticAlgorithm.isNeuralNetworkActive() == false) || (isUpdateData)){
						Thread.sleep(500);
					} 					
						
					//Pega o vetor atual de entrada para a classifica��o.
					double[][] vectorInputs = listOfInputsTest.get(geneticAlgorithm.getNeuralNetworkActive().getInputFeatures());
					int i = (int) (Math.random() * vectorInputs.length - 1);
					double[] input = vectorInputs[i];
					
					//Executa a classificacao para os dados corrente.
					int resultPrediction = (int) Math.round(geneticAlgorithm.getPrediction(input) * 100);
					int resultPredictionDefault = (int) Math.round(defaultNeuralNetwork.classify(listOfInputsTest.get(defaultNeuralNetwork.getInputFeatures())[i])[0] * 100);
					
					//Pega a sa�da real para a entrada i.
					int target = (int) Math.round(listOfInputsTest.get("output")[i][0] * 100); 
					
					//Calcula os erros para as sa�das da rede neural ativa pelo algoritmo gen�tico e para a rede neural com todas as entradas.
					errorGenericAlgoritm += Math.abs(resultPrediction - target);
					errorDefault += Math.abs(resultPredictionDefault - target);
					
					System.out.println(Arrays.toString(input));
					System.out.println("Algoritmo Gen�tico "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Sa�da: "+resultPrediction+" Target: "+target+" Diferen�a: "+(target - resultPrediction)+" Somat�rio dos Erros: "+errorGenericAlgoritm);
					System.out.println("Rede Neural Padr�o "+defaultNeuralNetwork.getInputFeatures()+" Sa�da: "+resultPredictionDefault+" Target: "+target+" Diferen�a: "+(target - resultPredictionDefault)+" Somat�rio dos Erros: "+errorDefault);
					System.out.println();	
					
					//Executa o m�todo que toma uma a��o com base na quantidade de VMs estimada.
					updateArchitecture(resultPrediction);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(Exception e){
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
		}
	};
	
	//Classe interna para executar as atualiza��es de dados para o m�dulo preditor.
	private class RunnableUpdateData implements Runnable{

		@Override
		public void run() {			
			//Loop infinito para executar atualiza��es de dados do m�dulo preditor.
			while(true){
				try {
					isUpdateData = true;
					
					//Pega a base de dados atualizada.
					listOfInputs = DataBaseHelper.getDataBase("C:\\Users\\Diogo\\Desktop\\SD\\baseSD4.csv");
					
					//Realiza o treinamento padr�o com a mesma base de dados.
					defaultNeuralNetwork.train(listOfInputs.get(defaultNeuralNetwork.getInputFeatures()), listOfInputs.get("output"), 0.1, 10e-5, 50000);
					System.out.println("Atualiza��o - Rede Neural Padr�o:"+defaultNeuralNetwork.getError());
					
					//Atualiza a lista de entradas do objeto GenericAlgorithm com os novos dados.
					geneticAlgorithm.updateDataAndStart(listOfInputs, listOfInputs.get("output"));					
					System.out.println("Atualiza��o - Melhores caracter�sticas: "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Erro: "+geneticAlgorithm.getNeuralNetworkActive().getError());
					
					System.out.println("");
					
					isUpdateData = false;
					
					//Aguarda por um determinado per�odo de tempo.
					Thread.sleep((int) Math.round(secondsForUpdateData * 1000));
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		
	}
	
}
