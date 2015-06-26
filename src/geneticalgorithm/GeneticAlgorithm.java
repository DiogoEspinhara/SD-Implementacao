package geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import neuralnetwork.NeuralNetwork;

public class GeneticAlgorithm {

	//Atributos de objeto.
	private int sizeChromosome, sizePopulation, quantityMaximumGenerations, quantityMaximumEpochs;
	private double rateCrossover, rateMutation, acceptsError, learningRate;
	private int[] layersHiddenAndOutput;
	private HashMap<String, double[][]> listOfInputs;
	private double[][] outputs;
	private String[] firstPopulation;
	private NeuralNetwork neuralNetworkActive;
	
	public GeneticAlgorithm(int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome, int quantityMaximumGenerations, double rateCrossover, double rateMutation
			, double acceptsError, double learningRate, int quantityMaximumEpochs) {
		this(null, null, layersHiddenAndOutput, sizePopulation, sizeChromosome, quantityMaximumGenerations, rateCrossover, rateMutation, acceptsError, learningRate, quantityMaximumEpochs);
	}
	
	public GeneticAlgorithm(int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome, int quantityMaximumGenerations, double rateCrossover, double rateMutation
			, double acceptsError, double learningRate, int quantityMaximumEpochs, String[] firstPopulation) {
		this(null, null, layersHiddenAndOutput, sizePopulation, sizeChromosome, quantityMaximumGenerations, rateCrossover, rateMutation, acceptsError, learningRate, quantityMaximumEpochs, firstPopulation);
	}	
	
	public GeneticAlgorithm(HashMap<String, double[][]> listOfInputs, double[][] outputs, int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome
			, int quantityMaximumGenerations, double rateCrossover
			, double rateMutation, double acceptsError
			, double learningRate, int quantityMaximumEpochs) {
		
		this(listOfInputs, outputs, layersHiddenAndOutput, sizePopulation, sizeChromosome, quantityMaximumGenerations, rateCrossover, rateMutation, acceptsError, learningRate, quantityMaximumEpochs, null);
	}
	
	public GeneticAlgorithm(HashMap<String, double[][]> listOfInputs, double[][] outputs, int[] layersHiddenAndOutput, int sizePopulation, int sizeChromosome
			, int quantityMaximumGenerations, double rateCrossover
			, double rateMutation, double acceptsError
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
		this.acceptsError = acceptsError;
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
		
	/**
	 * Método utilizado para executar o algoritmo genético e identificar qual o melhor objeto NeuralNetwork.
	 */
	public void start(){		
		//Variável de referência para referenciar o objeto NeuralNetwork com melhor o resultado.
		NeuralNetwork bestNeuralNetwork = null;
		
		//Instancia um objeto HashMap para armazenar resultados de redes neurais. 
		//Isso evitará redundâncias de processamento entre arquiteturas identicas.
		HashMap<String, NeuralNetwork> tableOfNeuralNetworksPrevious = new HashMap<String, NeuralNetwork>();
		
		//Variável local para referenciar o vetor de String que contém a população corrente(atual).
		//Inicializa o seu valor com o valor(endereço) do atributo firstPopulation.
		String[] population = firstPopulation;
		
		//Instancia um objeto ArrayList para armazenar os objetos NeuralNetwork de cada hipótese(cromossomo).
		ArrayList<NeuralNetwork> listOfNeuralNetworks = new ArrayList<NeuralNetwork>(); 
		
		//Fica em loop enquanto não ultrapassar a quantidade de gerações, e não atingir a taxa de erro aceita. 
		LoopGeneration:
		for (int g = 1; g <= quantityMaximumGenerations; g++){
			
			//System.out.println("Geração "+g);
			
			//Percorre as hipóteses(cromossomos) da geração g.
			for (String chromosome : population){
				
				//Variável local para referenciar o objeto NeuralNetwork corrente.
				NeuralNetwork neuralNetworkChromosome = null;
				
				//Verifica se a hipótese atual não foi ainda processada anteriormente.
				if (! tableOfNeuralNetworksPrevious.containsKey(chromosome)){
					
					//Instancia um novo vetor de inteiros para determinar as quantidades de unidades em cada camada da rede neural referente ao cromossomo corrente.
					int[] layers = new int[layersHiddenAndOutput.length + 1];
					layers[0] = chromosome.replace("0", "").length();
					System.arraycopy(layersHiddenAndOutput, 0, layers, 1, layersHiddenAndOutput.length);			
					
					//Instancia um objeto NeuralNetwork para a população.
					neuralNetworkChromosome = new NeuralNetwork(layers, chromosome);
					
					//Executa o treinamento da rede neural referente ao cromossomo corrente.
					neuralNetworkChromosome.train(listOfInputs.get(chromosome), outputs, learningRate, acceptsError, quantityMaximumEpochs);
					
					//Adiciona na tabela Hash a variável de referência do objeto NeuralNetwork instanciado acima para o cromossomo corrente.
					tableOfNeuralNetworksPrevious.put(chromosome, neuralNetworkChromosome);
				}
				else{
					//Recupera a variável de referência do objeto NeuralNetwork gerado anteriormente para o cromossomo atual.
					neuralNetworkChromosome = tableOfNeuralNetworksPrevious.get(chromosome);
				}
				
				//System.out.println(chromosome+" = "+neuralNetworkChromosome.getError());
				
				//Verifica se a taxa de erro é menor do que a taxa de erro aceitável.
				if (neuralNetworkChromosome.getError() <= acceptsError){
					//Atualiza a variável local bestNeuralNetwork.
					bestNeuralNetwork = neuralNetworkChromosome;
					
					//Para a execução do loop de gerações.
					break LoopGeneration;
				}
				else{					
					//Adiciona na lista a variável de referência do objeto NeuralNetwork referente ao cromossomo corrente.
					listOfNeuralNetworks.add(neuralNetworkChromosome);
					
					//Verifica se o objeto NeuralNetwork corrente obteve o resultado superior ao melhor resultado encontrado anteriormente.
					if ((bestNeuralNetwork == null) || (neuralNetworkChromosome.getError() < bestNeuralNetwork.getError())){
						
						//Atualiza o valor da variável de referência bestNeuralNetwork com a referência do objeto NeuralNetwork corrente.
						bestNeuralNetwork = neuralNetworkChromosome;						
					}
				}
			}			
			
			//Gera a nova população através do sorteio entre as hipóteses, sendo aquelas com maior aptidão tendo maior probabilidade no sorteio.
			String[] newPopulation = rafflePopulation(listOfNeuralNetworks);
			
			//Executa o método que realiza o crossover entre as hipóteses sorteadas.
			generateCrossover(newPopulation);
			
			//Atualiza o valor da variável population com a referência do vetor com a nova população.
			population = newPopulation;
			
			//Verifica se a geração corrente(atual) não é a última.
			if (g != quantityMaximumGenerations-1){				
				//Limpa a lista de objetos NeuralNetwork da geração corrente.
				listOfNeuralNetworks.clear();
			}			
		}
		
		//Atualiza o valor do atributo neuralNetworkActive com a variável de referência do objeto NeuralNetwork com o melhor resultado.
		this.neuralNetworkActive = bestNeuralNetwork;		
	}
	
	/**
	 * Método usado para realizar o cruzamento entre as melhores hipóteses(cromossomos) passados como argumento.
	 * @param newPopulation
	 */
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
				
				//Verifica se o primeiro filho é inválido. Ou seja, igual a 0.
				if (Integer.parseInt(firstChild) == 0){					
					//Com uma taxa de 50 % de chance, escolhe um dos pais para tomar o lugar do primeiro filho.
					firstChild = Math.random() < 0.5 ? newPopulation[index] : newPopulation[index + 1];
				}
				
				//Verifica se o segundo filho é inválido. Ou seja, igual a 0.
				if (Integer.parseInt(secondChild) == 0){					
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
	
	/**
	 * Método utilizado para realizar mutações em um determinado cromossomo(hipótese).
	 * @param chromosome
	 * @return
	 */
	private String generateMutation(String chromosome){
		
		//Variável local para receber a String binária da hipótese com a mutação.
		String chromosomeMutant = "";
		
		//Enquanto não produzir um cromossomo válido, continua gerando mutações.
		while ((chromosomeMutant.equals("")) || (Integer.parseInt(chromosomeMutant) == 0)){
			
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

	/**
	 * Método para executar o sorteio entre as hipóteses relacionadas a cada objeto NeuralNetwork. 
	 * O objeto NeuralNetwork com menor erro tem maior probabilidade de ser sorteado.
	 * @param listOfNeuralNetworks
	 * @return
	 */
	private String[] rafflePopulation(ArrayList<NeuralNetwork> listOfNeuralNetworks) {
		//Instancia um novo vetor para armazenar a nova população.
		String[] newPopulation = new String[sizePopulation];
		
		//Realiza os sizePopulation sorteios.
		for (int i = 0; i < newPopulation.length; i++){
			
			//Instancia um objeto ArrayList de NeuralNetwork para armazenar variáveis de referências de 4 sorteios.
			ArrayList<NeuralNetwork> listForRaffle = new ArrayList<NeuralNetwork>(4);
			
			//Escolhe aleatoriamente variáveis de referência da lista de NeuralNetworks passada como argumento.
			for (int j = 0; j < newPopulation.length; j++){
				listForRaffle.add(listOfNeuralNetworks.get((int) (Math.random() * listOfNeuralNetworks.size())));
			}
			
			//Ordena a lista dos sorteados.
			Collections.sort(listForRaffle);
			
			//Pega a variável do objeto NeuralNetwork da lista do sorteio que contém o menor erro.
			newPopulation[i] = listForRaffle.get(0).getInputFeatures();
		}
		
		//Retorna a variável de referência do array contendo a nova população.
		return newPopulation;
	}

	/**
	 * Método para gerar um array de Strings binárias contendo uma população de hipóteses(cromossomos).
	 * @return
	 */
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
	
	/**
	 * Método utilizado para atualizar os dados e iniciar o algoritmo genético novamente para selecionar uma rede neural com melhor acerto.
	 * @param listOfInputs
	 * @param outputs
	 */
	public void updateDataAndStart(HashMap<String, double[][]> listOfInputs, double[][] outputs){
		
		//Atualiza o valor do atributo listOfInputs com a variável de referência passada como argumento.
		this.listOfInputs = listOfInputs;
		
		//Atualiza o valor do atributo outputs.
		this.outputs = outputs;
		
		//Inicia a execução do algoritmo genético.
		start();		
	}
	
	/**
	 * Método para identificar se já existe uma rede neural ativa.
	 * @return
	 */
	public boolean isNeuralNetworkActive(){
		return (neuralNetworkActive != null);
	}
	
	/**
	 * Método para retornar a variável de referência do objeto NeuralNetwork ativo(atual).
	 * @return
	 */
	public NeuralNetwork getNeuralNetworkActive(){
		return neuralNetworkActive;
	}
	
	/**
	 * Método para executar uma classificação da rede neural e predizer a quantidade estimada de VMs.
	 * @param inputs
	 * @return
	 */
	public int getPrediction(double[] input){
		
		//Pega a variável de referência do objeto NeuralNetwork corrente(atual). 
		//Evita que ocorra erros quando a rede neural atual for substituída.
		NeuralNetwork network = neuralNetworkActive;
		
		//Executa a classificação da rede neural e retorna a saída da rede neural.
		return (int) Math.round(network.classify(input)[0] * 100);
	}
}
