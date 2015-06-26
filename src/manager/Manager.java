package manager;

import java.io.IOException;
import java.util.HashMap;

import database.DataBaseHelper;
import geneticalgorithm.GeneticAlgorithm;

public class Manager {
	
	//Atributos de objeto.
	private GeneticAlgorithm geneticAlgorithm;
	private int secondsForDecisions, quantityOfVMs, secondsForUpdateData;
	private Thread threadPrediction, threadUpdateData;
	private HashMap<String, double[][]> listOfInputs;
	
	public Manager(int quantityOfVMs, int secondsForDecisions, int secondsForUpdateData){
		
		//Inicializa os valores dos atributos de objeto.
		this.secondsForDecisions = secondsForDecisions;
		this.secondsForUpdateData = secondsForUpdateData;
		
		//Instancia um objeto GeneticAlgorithm para o módulo preditor.
		geneticAlgorithm = new GeneticAlgorithm(new int[]{10, 1}, 4, 4, 100, 0.3, 0.05, 10e-5, 0.1, 50000);
		
		//Executa o método que atualiza a arquitetura do sistema.
		updateArchitecture(quantityOfVMs);
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
					Thread.sleep(secondsForDecisions * 1000);

					//Enquanto não existir uma rede neural válida, espera.
					while(geneticAlgorithm.isNeuralNetworkActive() == false){
						Thread.sleep(500);
					}					
						
					//Pega o vetor atual de entrada para a classificação.
					double[][] vectorInputs = listOfInputs.get(geneticAlgorithm.getNeuralNetworkActive().getInputFeatures());
					int i = (int) (Math.random() * vectorInputs.length - 1);
					double[] input = vectorInputs[i];
					
					//Executa a classificacao dos valores atuais.
					int resultPrediction =  geneticAlgorithm.getPrediction(input);
					
					System.out.println(i+": Alterando a arquitetura de "+quantityOfVMs+" para: "+resultPrediction);
										
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
					//Pega a base de dados atualizada.
					listOfInputs = DataBaseHelper.getDataBase();
					
					//Atualiza a lista de entradas do objeto GenericAlgorithm com os novos dados.
					geneticAlgorithm.updateDataAndStart(listOfInputs, listOfInputs.get("output"));
					
					System.out.println("Atualização - Melhores características: "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Erro: "+geneticAlgorithm.getNeuralNetworkActive().getError());
					
					//Aguarda por um determinado período de tempo.
					Thread.sleep(secondsForUpdateData * 1000);
					
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
