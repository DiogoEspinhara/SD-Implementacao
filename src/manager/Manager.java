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
		
		//Instancia um objeto GeneticAlgorithm para o módulo preditor.
		geneticAlgorithm = new GeneticAlgorithm(new int[]{10, 1}, 4, 4, 100, 0.9, 0.05, 10e-5, 0.05, 5000);
		
		//Instancia um objeto NeuralNetwork com todas as entradas para os testes.
		defaultNeuralNetwork = new NeuralNetwork(new int[]{4, 10, 1}, "1111");
		
		//Executa o método que atualiza a arquitetura do sistema.
		updateArchitecture(quantityOfVMs);
		
		//Carrega os dados para o teste de validação.
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
	 * Método para iniciar a execução do gerenciador.
	 */
	public void start(){
		
		//Verifica se ainda não foi instanciado um objeto Thread para as atualizações de dados do módulo preditor.
		if (threadUpdateData == null){
			//Instancia um objeto Thread para executar as atualizações de dados do módulo preditor.
			threadUpdateData = new Thread(new RunnableUpdateData());
			threadUpdateData.start();
		}
		
		//Verifica se ainda não foi instanciado um objeto Thread para a tomada de decisão.
		if (threadPrediction == null){
			//Instancia um objeto Thread para executar as ações de decisão.
			threadPrediction = new Thread(new RunnablePrediction());	
			threadPrediction.start();
		}
	}
	
	/*
	 * Método para atualizar a arquitetura das máquinas virtuais de acordo com uma nova quantidade de máquinas virtuais.
	 */
	public void updateArchitecture(int newQuantityOfVMs){
		
		//Atualiza o valor do atributo quantityOfVMs.
		this.quantityOfVMs = newQuantityOfVMs;
	}
	
	//Classe interna para executar as tomadas de decisão.
	private class RunnablePrediction implements Runnable {
		@Override
		public void run() {			
			
			//Loop infinito que ficará executando a predição.
			while (true){
				try {
					//Dorme por um intervalo de tempo, evita que fique executando decisões toda hora.
					Thread.sleep((int) Math.round(secondsForDecisions * 1000));

					//Enquanto não existir uma rede neural válida, espera.
					while((geneticAlgorithm.isNeuralNetworkActive() == false) || (isUpdateData)){
						Thread.sleep(500);
					} 					
						
					//Pega o vetor atual de entrada para a classificação.
					double[][] vectorInputs = listOfInputsTest.get(geneticAlgorithm.getNeuralNetworkActive().getInputFeatures());
					int i = (int) (Math.random() * vectorInputs.length - 1);
					double[] input = vectorInputs[i];
					
					//Executa a classificacao para os dados corrente.
					int resultPrediction = (int) Math.round(geneticAlgorithm.getPrediction(input) * 100);
					int resultPredictionDefault = (int) Math.round(defaultNeuralNetwork.classify(listOfInputsTest.get(defaultNeuralNetwork.getInputFeatures())[i])[0] * 100);
					
					//Pega a saída real para a entrada i.
					int target = (int) Math.round(listOfInputsTest.get("output")[i][0] * 100); 
					
					//Calcula os erros para as saídas da rede neural ativa pelo algoritmo genético e para a rede neural com todas as entradas.
					errorGenericAlgoritm += Math.abs(resultPrediction - target);
					errorDefault += Math.abs(resultPredictionDefault - target);
					
					System.out.println(Arrays.toString(input));
					System.out.println("Algoritmo Genético "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Saída: "+resultPrediction+" Target: "+target+" Diferença: "+(target - resultPrediction)+" Somatório dos Erros: "+errorGenericAlgoritm);
					System.out.println("Rede Neural Padrão "+defaultNeuralNetwork.getInputFeatures()+" Saída: "+resultPredictionDefault+" Target: "+target+" Diferença: "+(target - resultPredictionDefault)+" Somatório dos Erros: "+errorDefault);
					System.out.println();	
					
					//Executa o método que toma uma ação com base na quantidade de VMs estimada.
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
	
	//Classe interna para executar as atualizações de dados para o módulo preditor.
	private class RunnableUpdateData implements Runnable{

		@Override
		public void run() {			
			//Loop infinito para executar atualizações de dados do módulo preditor.
			while(true){
				try {
					isUpdateData = true;
					
					//Pega a base de dados atualizada.
					listOfInputs = DataBaseHelper.getDataBase("C:\\Users\\Diogo\\Desktop\\SD\\baseSD4.csv");
					
					//Realiza o treinamento padrão com a mesma base de dados.
					defaultNeuralNetwork.train(listOfInputs.get(defaultNeuralNetwork.getInputFeatures()), listOfInputs.get("output"), 0.1, 10e-5, 50000);
					System.out.println("Atualização - Rede Neural Padrão:"+defaultNeuralNetwork.getError());
					
					//Atualiza a lista de entradas do objeto GenericAlgorithm com os novos dados.
					geneticAlgorithm.updateDataAndStart(listOfInputs, listOfInputs.get("output"));					
					System.out.println("Atualização - Melhores características: "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Erro: "+geneticAlgorithm.getNeuralNetworkActive().getError());
					
					System.out.println("");
					
					isUpdateData = false;
					
					//Aguarda por um determinado período de tempo.
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
