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
		
		//Instancia um objeto GeneticAlgorithm para o m�dulo preditor.
		geneticAlgorithm = new GeneticAlgorithm(new int[]{10, 1}, 4, 4, 100, 0.3, 0.05, 10e-5, 0.1, 50000);
		
		//Executa o m�todo que atualiza a arquitetura do sistema.
		updateArchitecture(quantityOfVMs);
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
					Thread.sleep(secondsForDecisions * 1000);

					//Enquanto n�o existir uma rede neural v�lida, espera.
					while(geneticAlgorithm.isNeuralNetworkActive() == false){
						Thread.sleep(500);
					}					
						
					//Pega o vetor atual de entrada para a classifica��o.
					double[][] vectorInputs = listOfInputs.get(geneticAlgorithm.getNeuralNetworkActive().getInputFeatures());
					int i = (int) (Math.random() * vectorInputs.length - 1);
					double[] input = vectorInputs[i];
					
					//Executa a classificacao dos valores atuais.
					int resultPrediction =  geneticAlgorithm.getPrediction(input);
					
					System.out.println(i+": Alterando a arquitetura de "+quantityOfVMs+" para: "+resultPrediction);
										
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
					//Pega a base de dados atualizada.
					listOfInputs = DataBaseHelper.getDataBase();
					
					//Atualiza a lista de entradas do objeto GenericAlgorithm com os novos dados.
					geneticAlgorithm.updateDataAndStart(listOfInputs, listOfInputs.get("output"));
					
					System.out.println("Atualiza��o - Melhores caracter�sticas: "+geneticAlgorithm.getNeuralNetworkActive().getInputFeatures()+" Erro: "+geneticAlgorithm.getNeuralNetworkActive().getError());
					
					//Aguarda por um determinado per�odo de tempo.
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
