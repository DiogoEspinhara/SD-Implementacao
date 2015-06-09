package geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import neuralnetwork.NeuralNetwork;

public class GeneticAlgorithm {

	//Atributos de objeto.
	private int sizeChromosome, sizePopulation, quantityMaximumGenerations;
	private double rateCrossover, rateMutation, acceptsRateAccuracy;
	private int[] layersHiddenAndOutput;
	private HashMap<String, double[][]> listOfInputs;
	private double[][] outputs;
	
	public GeneticAlgorithm(HashMap<String, double[][]> listOfInputs, double[][] outputs, int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome
			, int quantityMaximumGenerations, double rateCrossover
			, double rateMutation, double acceptsRateAccuracy) {
		//Inicializa os atributos.
		this.listOfInputs = listOfInputs;
		this.outputs = outputs;
		this.layersHiddenAndOutput = layersHiddenAndOutput;
		this.sizePopulation = sizePopulation;
		this.sizeChromosome = sizeChromosome;
		this.quantityMaximumGenerations = quantityMaximumGenerations;
		this.rateCrossover = rateCrossover;
		this.rateMutation = rateMutation;
		this.acceptsRateAccuracy = acceptsRateAccuracy;
	}
		
	public NeuralNetwork start(double rate, int quantityMaximumEpochs){
		
		//Instancia um vetor para armazenar a população(cromossomos).
		String[] population = generatePopulation();
		
		//Instancia um objeto ArrayList para armazenar os objetos NeuralNetwork de cada hipótese(cromossomo).
		ArrayList<NeuralNetwork> listOfNeuralNetworks = new ArrayList<NeuralNetwork>(); 
		
		//Variável local para armazenar a probabilidade acumulada entre os objetos NeuralNetwork gerados.
		double acumulateRate = 0;	
		
		//Fica em loop enquanto não ultrapassar a quantidade de gerações, e não atingir a taxa de erro aceita. 
		for (int g = 1; g <= quantityMaximumGenerations; g++){
			
			//Percorre as hipóteses(cromossomos) da geração g.
			for (String chromosome : population){
			
				//Instancia um novo vetor de inteiros para determinar as quantidades de unidades em cada camada da rede neural referente ao cromossomo corrente.
				int[] layers = new int[layersHiddenAndOutput.length + 1];
				layers[0] = chromosome.replace("0", "").length();
				System.arraycopy(layersHiddenAndOutput, 0, layers, 1, layersHiddenAndOutput.length);			
				
				//Instancia um objeto NeuralNetwork para a população.
				NeuralNetwork neuralNetworkChromosome = new NeuralNetwork(layers, chromosome);
				
				//Executa o treinamento da rede neural referente ao cromossomo corrente.
				neuralNetworkChromosome.train(listOfInputs.get(chromosome), outputs, rate, acceptsRateAccuracy, quantityMaximumEpochs);
				
				//Verifica se a taxa de erro é menor do que a taxa de erro aceitável.
				if (neuralNetworkChromosome.getRateAccuracy() >= acceptsRateAccuracy){
					//Retorna a referência do objeto NeuralNetwork corrente.
					return neuralNetworkChromosome;
				}
				else{
					//Adiciona na lista a variável de referência do objeto NeuralNetwork referente ao cromossomo corrente.
					listOfNeuralNetworks.add(neuralNetworkChromosome);
					
					//Incrementa o valor da variável local acumulateRate para o sorteio.
					acumulateRate += neuralNetworkChromosome.getRateAccuracy();
				}					
			}			
			
			//Ordena a lista de objetos NeuralNetwork gerados pelos cromossomos(hipóteses).
			Collections.sort(listOfNeuralNetworks);
			
			//Gera a nova população através do sorteio entre as hipóteses, sendo aquelas com maior aptidão tendo maior probabilidade no sorteio.
			String[] newPopulation = rafflePopulation(listOfNeuralNetworks, acumulateRate);
			
			//Executa o método que realiza o crossover entre as hipóteses sorteadas.
			generateCrossover(newPopulation);
			
			//Atualiza o valor da variável population com a referência do vetor com a nova população.
			population = newPopulation;
		}
		
		//Retorna a variável de referência do objeto NeuralNetwork com a melhor taxa de acerto(menor erro).
		return listOfNeuralNetworks.get(0);
	}
	
	private void generateCrossover(String[] newPopulation) {
		//Gera um número randômico entre 1 e sizeChromosome - 1.
		//Necessário para identificar a posição do crossover.
		int positionCrossover = 1 + Math.round((float) (Math.random() * sizeChromosome - 2));
		
		//Percorre a nova população e realiza o crossover com valor de probabilidade do atributo rateCrossover.
		for (int i = 0; i < newPopulation.length/2; i+=2){
			
			//Gera um número randômico e verifica se é menor do que o valor do atributo rateCrossover.
			if (Math.random() < rateCrossover){
				
				//Realiza o crossover entre as hipóteses i e i+1 da nova população.
				String firstChild = newPopulation[i].substring(0, positionCrossover)+newPopulation[i+1].substring(positionCrossover, sizeChromosome - positionCrossover);
				String secondChild = newPopulation[i+1].substring(0, positionCrossover)+newPopulation[i].substring(positionCrossover, sizeChromosome - positionCrossover);
				
				//Adiciona os filhos gerados nas posições dos pais, executando o processo de mutação em cada filho..
				newPopulation[i] = generateMutation(firstChild);
				newPopulation[i+1] = generateMutation(secondChild);					
			}				
		}
	}
	
	private String generateMutation(String chromosome){
		
		//Variável local para receber a String binária da hipótese com a mutação.
		String chromosomeMutant = "";
		
		//Percorre todos o cromossomo e verifica se realiza uma mutação com probabilidade igual a rateMutation.
		for (int i = 0; i < chromosome.length(); i++){
			
			//Gera um número randômico entre 0 e 1. Verifica se o número é menor do que rateMutation.
			if (Math.random() < rateMutation){
				
				//Realiza a mutação no indice i do cromossomo passado como argumento.
				chromosomeMutant = chromosomeMutant.concat(chromosome.charAt(i) == '0' ? "1" : "0");
			}
			else{
				//Concatena na string de saída o valor no indice i do cromossomo.
				chromosomeMutant = chromosomeMutant.concat(chromosome.substring(i, i+1));
			}			
		}
		
		//Retorna a variável de referência da string construída através do processo de mutação acima.
		return chromosomeMutant;
	}

	private String[] rafflePopulation(ArrayList<NeuralNetwork> listOfNeuralNetworks, double acumulateRate) {
		//Instancia um novo vetor para armazenar a nova população.
		String[] newPopulation = new String[sizePopulation];
		
		//Realiza os sizePopulation sorteios.
		for (int i = 0; i < newPopulation.length; i++){
			
			//Gera um número aleatório entre 0 e acumulateRate.
			double number = Math.random() * acumulateRate;
			
			//Percorre os objetos NeuralNetwork da lista até que o número sorteado seja menor do que a soma acumuada das taxas.
			double acumulate = 0;
			
			for (NeuralNetwork neuralNetwork : listOfNeuralNetworks){
				
				//Soma a taxa de acerto do objeto NeuralNetwork ao valor acumulado.
				acumulate += neuralNetwork.getRateAccuracy();
				
				//Verifica se a taxa de acerto do objeto NueralNetwork corrente somado com a acumuada é maior do que o número sorteado.
				if (number <= acumulate){
					newPopulation[i] = neuralNetwork.getInputFeatures();
					
					//Para o loop interno.
					break;
				}					
			}
		}
		
		//Retorna a variável de referência do array contendo a nova população.
		return newPopulation;
	}

	private String[] generatePopulation(){
		
		//Instancia um vetor para armazenar a população(cromossomos).
		String[] population = new String[sizePopulation];
		
		//Gera a primeira população aleatoriamente.
		for (int i = 0; i < sizePopulation; i++){
			
			//Gera o cromossomo(hipótese) i.
			char[] chromosome = new char[sizeChromosome]; 
			Arrays.fill(chromosome, '0');
			
			//Gera um número aleatório que determina a quantidade máxima de 1s que podem ser atribuídos ao cromossomo corrente i.
			int quantity = Math.round((float) Math.random() * (sizeChromosome-1));
			for (int j = 0; j < quantity; j++){
				//Atribui 1 a posição aleatória j do cromossomo atual.
				chromosome[(int) (Math.random()*(sizeChromosome-1))] = 1;
			}
			
			//Atribui a string binária gerada pelo cromossomo
			population[i] = String.valueOf(chromosome);			
		}
		
		//Retorna a variável de referência do vetor population gerado acima.
		return population;
	}
}
