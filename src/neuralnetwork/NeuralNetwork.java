package neuralnetwork;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

public class NeuralNetwork implements Comparable<NeuralNetwork>{
		
	//Atributos de objeto.
	private BasicNetwork basicNetwork;
	private int[] layers;
	private String inputFeatures;
	private double rateAccuracy;
	
	public NeuralNetwork(int[] layers, String inputFeatures) {
		super();
		
		//Inicializa os atributos.
		this.layers = layers;
		this.inputFeatures = inputFeatures;
		
		//Executa o método que constroi a rede neural.
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
	
	public double getRateAccuracy(){
		return this.rateAccuracy;
	}
	
	public void build(){
		
		//Instancia um novo objeto BasicNetwork.
		basicNetwork = new BasicNetwork();
		
		//Percorre o vetor de camadas e adiciona na rede neural. 
		for (int i = 0; i < layers.length; i++){
			
			//Verifica se a camada corrente é a de entrada.
			if (i == 0){
				//Adiciona a camada de entrada a rede neural.
				basicNetwork.addLayer(new BasicLayer(null, false, layers[i]));
			}
			//Verifica se a camada corrente é a de saída.
			else if (i == layers.length-1){
				//Adiciona a camada de saída a rede neural.
				basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, layers[i]));
			}
			else{
				//Adiciona na rede a camada intermediária(oculta) corrente.
				basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, layers[i]));
			}		
		}
		
		//Finaliza a estrutura da rede neural.
		basicNetwork.getStructure().finalizeStructure();
		
		//Reinicia a rede neural.
		basicNetwork.reset();
	}
	
	public void train(double[][] inputs, double[][] outputs, double rate, double acceptsRateAccuracy, int quantityMaximumEpochs){
		
		//Instancia um objeto MLDataSet para conter a base de treinamento.
		MLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);
		
		//Instancia um objeto ResilientPropagation
		final Backpropagation backPropagation = new Backpropagation(basicNetwork, trainingSet);
		
		//Atualiza a taxa de aprendizado.
		backPropagation.setLearningRate(rate);
		
		//Enquanto não atingir a taxa de erro desejada, e a não ultrapassar a quantidade máxima de épocas. 
		do {
			
			//Executa o BackPropagation.
			backPropagation.iteration();
			
		} while (((1.0 - backPropagation.getError()) < acceptsRateAccuracy) && (backPropagation.getIteration() < quantityMaximumEpochs));
		
		//Atualiza o valor do atributo rateError com o valor do erro do objeto BackPropagation atual.
		this.rateAccuracy = 1 - backPropagation.getError();
	}
	
	public double[] classify(double[] input){
		
		//Variável local para referenciar o vetor de saída da rede neural.
		double[] output = new double[layers[layers.length-1]];
		
		//Executa a computação da rede neural.
		basicNetwork.compute(input, output);
		
		//Retorna a variável de referência do vetor de saída da rede neural.
		return output;		
	}

	@Override
	public int compareTo(NeuralNetwork o) {
		
		if (this.rateAccuracy > o.getRateAccuracy()){
			return -1;
		}
		else if (this.rateAccuracy == o.getRateAccuracy()){
			return 0;
		}
		else{
			return 1;
		}
	}
	
}
