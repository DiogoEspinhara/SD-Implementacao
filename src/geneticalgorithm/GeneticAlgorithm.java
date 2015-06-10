package geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import neuralnetwork.NeuralNetwork;

public class GeneticAlgorithm {

	//Atributos de objeto.
	private int sizeChromosome, sizePopulation, quantityMaximumGenerations, quantityMaximumEpochs;
	private double rateCrossover, rateMutation, acceptsRateAccuracy, learningRate;
	private int[] layersHiddenAndOutput;
	private HashMap<String, double[][]> listOfInputs;
	private double[][] outputs;
	private String[] firstPopulation;
	
	public GeneticAlgorithm(HashMap<String, double[][]> listOfInputs, double[][] outputs, int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome
			, int quantityMaximumGenerations, double rateCrossover
			, double rateMutation, double acceptsRateAccuracy
			, double learningRate, int quantityMaximumEpochs) {
		
		this(listOfInputs, outputs, layersHiddenAndOutput, sizePopulation, sizeChromosome, quantityMaximumGenerations, rateCrossover, rateMutation, acceptsRateAccuracy, learningRate, quantityMaximumEpochs, null);
	}
	
	public GeneticAlgorithm(HashMap<String, double[][]> listOfInputs, double[][] outputs, int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome
			, int quantityMaximumGenerations, double rateCrossover
			, double rateMutation, double acceptsRateAccuracy
			, double learningRate, int quantityMaximumEpochs
			, String[] firstPopulation) {
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
		this.learningRate = learningRate;
		this.quantityMaximumEpochs = quantityMaximumEpochs;
		
		//Verifica se foi passado um argumento válido para o parâmetro firstPopulation.
		if (firstPopulation != null){
			//Atualiza o valor do atributo firstPopulation com o valor do parâmetro firstPopulation.
			this.firstPopulation = firstPopulation;
		}
		else{
			//Executa o método que gera uma população aleatória. Atualiza o valor do atributo firstPopulation com a variável de referência retornada pelo método.
			this.firstPopulation = generatePopulation();
		}
	}
		
	public NeuralNetwork start(){
		
		//Variável local para referenciar o vetor de String que contém a população corrente(atual).
		//Inicializa o seu valor com o valor(endereço) do atributo firstPopulation.
		String[] population = firstPopulation;
		
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
				
				try{
				//Executa o treinamento da rede neural referente ao cromossomo corrente.
				neuralNetworkChromosome.train(listOfInputs.get(chromosome), outputs, learningRate, acceptsRateAccuracy, quantityMaximumEpochs);
				}catch(Exception e){
					e.printStackTrace();
				}
				
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
			
			//Verifica se a geração corrente(atual) é a última.
			if (g == quantityMaximumGenerations-1){
				
				//Retorna a variável de referência do objeto NeuralNetwork no topo da lista.
				return listOfNeuralNetworks.get(0);
			}
			else{
				//Limpa a lista de objetos NeuralNetwork da geração corrente.
				listOfNeuralNetworks.clear();
				
				//Limpa(zera) o valor da variável acumulateRate.
				acumulateRate = 0;
			}
		}
		
		//Retorna nulo caso a execução do método chegue a este comando.
		return null;
	}
	
	private void generateCrossover(String[] newPopulation) {
		//Gera um número randômico entre 1 e sizeChromosome - 1.
		//Necessário para identificar a posição do crossover.
		int positionCrossover = 1 + Math.round((float) (Math.random() * (sizeChromosome - 3)));
		
		//Percorre a nova população e realiza o crossover com valor de probabilidade do atributo rateCrossover.
		for (int index = 0; index < newPopulation.length/2; index += 2){
			
			//Gera um número randômico e verifica se é menor do que o valor do atributo rateCrossover.
			if (Math.random() < rateCrossover){
				try{
				//Realiza o crossover entre as hipóteses i e i+1 da nova população.
				String firstChild = newPopulation[index].substring(0, positionCrossover)+newPopulation[index+1].substring(positionCrossover, sizeChromosome);
				String secondChild = newPopulation[index+1].substring(0, positionCrossover)+newPopulation[index].substring(positionCrossover, sizeChromosome);
				
				//Verifica se o primeiro filho é inválido. Ou seja, igual a 000.
				if (firstChild.equals("000")){					
					//Com uma taxa de 50 % de chance, escolhe um dos pais para tomar o lugar do primeiro filho.
					firstChild = Math.random() < 0.5 ? newPopulation[index] : newPopulation[index + 1];
				}
				
				//Verifica se o segundo filho é inválido. Ou seja, igual a 000.
				if (secondChild.equals("000")){					
					//Com uma taxa de 50 % de chance, escolhe um dos pais para tomar o lugar do segundo filho.
					secondChild = Math.random() < 0.5 ? newPopulation[index] : newPopulation[index + 1];
				}
				
				//Adiciona os filhos gerados nas posições dos pais, executando o processo de mutação em cada filho..
				newPopulation[index] = generateMutation(firstChild);
				newPopulation[index+1] = generateMutation(secondChild);
				
				}
				catch(Exception e){
					e.printStackTrace();
				}				
			}				
		}
	}
	
	private String generateMutation(String chromosome){
		
		//Variável local para receber a String binária da hipótese com a mutação.
		String chromosomeMutant = "";
		
		//Enquanto não produzir um cromossomo válido, continua gerando mutações.
		while ((chromosomeMutant.equals("")) || (chromosomeMutant.equals("000"))){
			
			//Limpa o valor da variável local chromossomeMutant para gerar o novo cromossomo através de mutações.
			chromosomeMutant = "";
			
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
			
			loopInternal:
			for (NeuralNetwork neuralNetwork : listOfNeuralNetworks){
				
				//Soma a taxa de acerto do objeto NeuralNetwork ao valor acumulado.
				acumulate += neuralNetwork.getRateAccuracy();
				
				//Verifica se a taxa de acerto do objeto NueralNetwork corrente somado com a acumuada é maior do que o número sorteado.
				if (number <= acumulate){
					newPopulation[i] = neuralNetwork.getInputFeatures();
					
					//Para o loop interno.
					break loopInternal;
				}					
			}
		}
		
		//Retorna a variável de referência do array contendo a nova população.
		return newPopulation;
	}

	private String[] generatePopulation(){
		
		//Instancia um vetor para armazenar a população(cromossomos).
		String[] population = new String[sizePopulation];
		
		//Gera uma população aleatoriamente.
		for (int index = 0; index < sizePopulation; index++){
			
			//Gera o cromossomo(hipótese) i.
			char[] chromosome = new char[sizeChromosome]; 
			Arrays.fill(chromosome, '0');
			
			//Gera um número aleatório que determina a quantidade máxima de 1s que podem ser atribuídos ao cromossomo corrente i.
			int quantity = 1 + Math.round((float) Math.random() * (sizeChromosome-1));
			for (int j = 0; j < quantity; j++){
				//Atribui 1 a posição aleatória j do cromossomo atual.
				chromosome[(int) (Math.random()*(sizeChromosome-1))] = '1';
			}
			
			//Atribui a string binária gerada pelo cromossomo
			population[index] = String.valueOf(chromosome);		
		}
		
		//Retorna a variável de referência do vetor population gerado acima.
		return population;
	}
}
