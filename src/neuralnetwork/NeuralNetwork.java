package neuralnetwork;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class NeuralNetwork implements Comparable<NeuralNetwork>{
		
	//Atributos de objeto.
	private BasicNetwork basicNetwork;
	private int[] layers;
	private String inputFeatures;
	private double error;
	
	public NeuralNetwork(int[] layers, String inputFeatures) {
		super();
		
		//Inicializa os atributos.
		this.layers = layers;
		this.inputFeatures = inputFeatures;
		
		//Executa o m�todo que constroi a rede neural.
		build();
	}
	
	public void setLayers(int[] layers){
		this.layers = layers;
	}
	
	public String getInputFeatures() {
		return inputFeatures;
	}

	public void setInputFeatures(String inputFeatures) {
		this.inputFeatures = inputFeatures;
	}
	
	public double getError(){
		return this.error;
	}
	
	public void build(){
		
		//Instancia um novo objeto BasicNetwork.
		basicNetwork = new BasicNetwork();
		
		//Percorre o vetor de camadas e adiciona na rede neural. 
		for (int i = 0; i < layers.length; i++){
			
			//Verifica se a camada corrente � a de entrada ou a de saida.
			if (i == 0){
				//Adiciona a camada de entrada.
				basicNetwork.addLayer(new BasicLayer(null, true, layers[i]));
			}
			//Verifica se � a �ltima camada.
			else if (i == layers.length-1){
				//Adiciona a camada de sa�da.
				basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, layers[i]));
			}
			else{
				//Adiciona na rede a camada intermedi�ria(oculta) corrente.
				basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, layers[i]));
			}		
		}
		
		//Finaliza a estrutura da rede neural.
		basicNetwork.getStructure().finalizeStructure();
		
		//Reinicia a rede neural.
		basicNetwork.reset();		
	}
	
	public void train(double[][] inputs, double[][] outputs, double rate, double acceptsError, int quantityMaximumEpochs){
		
		//Instancia um objeto MLDataSet para conter a base de treinamento.
		//MLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);
		NeuralDataSet trainingSet = new BasicNeuralDataSet(inputs, outputs);
		
		//Instancia um objeto ResilientPropagation
		final Train train = new ResilientPropagation(basicNetwork, trainingSet);
				
		//Enquanto n�o atingir a taxa de erro desejada, e a n�o ultrapassar a quantidade m�xima de �pocas. 
		do {
			
			//Executa o BackPropagation.
			train.iteration();			
			
			//System.out.println(inputFeatures+"# Iteration #" + backPropagation.getIteration() + " Error:" + backPropagation.getError());			
		} while ((train.getError() > acceptsError) && (train.getIteration() < quantityMaximumEpochs));
		
		//Executa o m�todo que finaliza o treinamento da rede neural.
		train.finishTraining();
		
		//Atualiza o valor do atributo rateError com o valor do erro do objeto BackPropagation atual.
		this.error = Math.abs(train.getError());
	}
	
	public double[] classify(double[] input){
		
		//Vari�vel local para referenciar o vetor de sa�da da rede neural.
		double[] output = new double[layers[layers.length-1]];
		
		//Executa a computa��o da rede neural.
		basicNetwork.compute(input, output);
		
		//Retorna a vari�vel de refer�ncia do vetor de sa�da da rede neural.
		return output;		
	}

	@Override
	public int compareTo(NeuralNetwork o) {
		
		if (this.error < o.getError()){
			return -1;
		}
		else if (this.error == o.getError()){
			return 0;
		}
		else{
			return 1;
		}
	}
	
}
