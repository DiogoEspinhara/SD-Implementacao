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
		
		//Verifica se foi passado um argumento v�lido para o par�metro firstPopulation.
		if (firstPopulation != null){
			//Atualiza o valor do atributo firstPopulation com o valor do par�metro firstPopulation.
			this.firstPopulation = firstPopulation;
		}
		else{
			//Executa o m�todo que gera uma popula��o aleat�ria. Atualiza o valor do atributo firstPopulation com a vari�vel de refer�ncia retornada pelo m�todo.
			this.firstPopulation = generatePopulation();
		}
	}
		
	public NeuralNetwork start(){
		
		//Vari�vel local para referenciar o vetor de String que cont�m a popula��o corrente(atual).
		//Inicializa o seu valor com o valor(endere�o) do atributo firstPopulation.
		String[] population = firstPopulation;
		
		//Instancia um objeto ArrayList para armazenar os objetos NeuralNetwork de cada hip�tese(cromossomo).
		ArrayList<NeuralNetwork> listOfNeuralNetworks = new ArrayList<NeuralNetwork>(); 
		
		//Vari�vel local para armazenar a probabilidade acumulada entre os objetos NeuralNetwork gerados.
		double acumulateRate = 0;	
		
		//Fica em loop enquanto n�o ultrapassar a quantidade de gera��es, e n�o atingir a taxa de erro aceita. 
		for (int g = 1; g <= quantityMaximumGenerations; g++){
			
			//Percorre as hip�teses(cromossomos) da gera��o g.
			for (String chromosome : population){
				
				//Instancia um novo vetor de inteiros para determinar as quantidades de unidades em cada camada da rede neural referente ao cromossomo corrente.
				int[] layers = new int[layersHiddenAndOutput.length + 1];
				layers[0] = chromosome.replace("0", "").length();
				System.arraycopy(layersHiddenAndOutput, 0, layers, 1, layersHiddenAndOutput.length);			
				
				//Instancia um objeto NeuralNetwork para a popula��o.
				NeuralNetwork neuralNetworkChromosome = new NeuralNetwork(layers, chromosome);
				
				try{
				//Executa o treinamento da rede neural referente ao cromossomo corrente.
				neuralNetworkChromosome.train(listOfInputs.get(chromosome), outputs, learningRate, acceptsRateAccuracy, quantityMaximumEpochs);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				//Verifica se a taxa de erro � menor do que a taxa de erro aceit�vel.
				if (neuralNetworkChromosome.getRateAccuracy() >= acceptsRateAccuracy){
					//Retorna a refer�ncia do objeto NeuralNetwork corrente.
					return neuralNetworkChromosome;
				}
				else{
					//Adiciona na lista a vari�vel de refer�ncia do objeto NeuralNetwork referente ao cromossomo corrente.
					listOfNeuralNetworks.add(neuralNetworkChromosome);
					
					//Incrementa o valor da vari�vel local acumulateRate para o sorteio.
					acumulateRate += neuralNetworkChromosome.getRateAccuracy();
				}
			}			
			
			//Ordena a lista de objetos NeuralNetwork gerados pelos cromossomos(hip�teses).
			Collections.sort(listOfNeuralNetworks);
			
			//Gera a nova popula��o atrav�s do sorteio entre as hip�teses, sendo aquelas com maior aptid�o tendo maior probabilidade no sorteio.
			String[] newPopulation = rafflePopulation(listOfNeuralNetworks, acumulateRate);
			
			//Executa o m�todo que realiza o crossover entre as hip�teses sorteadas.
			generateCrossover(newPopulation);
			
			//Atualiza o valor da vari�vel population com a refer�ncia do vetor com a nova popula��o.
			population = newPopulation;
			
			//Verifica se a gera��o corrente(atual) � a �ltima.
			if (g == quantityMaximumGenerations-1){
				
				//Retorna a vari�vel de refer�ncia do objeto NeuralNetwork no topo da lista.
				return listOfNeuralNetworks.get(0);
			}
			else{
				//Limpa a lista de objetos NeuralNetwork da gera��o corrente.
				listOfNeuralNetworks.clear();
				
				//Limpa(zera) o valor da vari�vel acumulateRate.
				acumulateRate = 0;
			}
		}
		
		//Retorna nulo caso a execu��o do m�todo chegue a este comando.
		return null;
	}
	
	private void generateCrossover(String[] newPopulation) {
		//Gera um n�mero rand�mico entre 1 e sizeChromosome - 1.
		//Necess�rio para identificar a posi��o do crossover.
		int positionCrossover = 1 + Math.round((float) (Math.random() * (sizeChromosome - 3)));
		
		//Percorre a nova popula��o e realiza o crossover com valor de probabilidade do atributo rateCrossover.
		for (int index = 0; index < newPopulation.length/2; index += 2){
			
			//Gera um n�mero rand�mico e verifica se � menor do que o valor do atributo rateCrossover.
			if (Math.random() < rateCrossover){
				try{
				//Realiza o crossover entre as hip�teses i e i+1 da nova popula��o.
				String firstChild = newPopulation[index].substring(0, positionCrossover)+newPopulation[index+1].substring(positionCrossover, sizeChromosome);
				String secondChild = newPopulation[index+1].substring(0, positionCrossover)+newPopulation[index].substring(positionCrossover, sizeChromosome);
				
				//Verifica se o primeiro filho � inv�lido. Ou seja, igual a 000.
				if (firstChild.equals("000")){					
					//Com uma taxa de 50 % de chance, escolhe um dos pais para tomar o lugar do primeiro filho.
					firstChild = Math.random() < 0.5 ? newPopulation[index] : newPopulation[index + 1];
				}
				
				//Verifica se o segundo filho � inv�lido. Ou seja, igual a 000.
				if (secondChild.equals("000")){					
					//Com uma taxa de 50 % de chance, escolhe um dos pais para tomar o lugar do segundo filho.
					secondChild = Math.random() < 0.5 ? newPopulation[index] : newPopulation[index + 1];
				}
				
				//Adiciona os filhos gerados nas posi��es dos pais, executando o processo de muta��o em cada filho..
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
		
		//Vari�vel local para receber a String bin�ria da hip�tese com a muta��o.
		String chromosomeMutant = "";
		
		//Enquanto n�o produzir um cromossomo v�lido, continua gerando muta��es.
		while ((chromosomeMutant.equals("")) || (chromosomeMutant.equals("000"))){
			
			//Limpa o valor da vari�vel local chromossomeMutant para gerar o novo cromossomo atrav�s de muta��es.
			chromosomeMutant = "";
			
			//Percorre todos o cromossomo e verifica se realiza uma muta��o com probabilidade igual a rateMutation.
			for (int i = 0; i < chromosome.length(); i++){
				
				//Gera um n�mero rand�mico entre 0 e 1. Verifica se o n�mero � menor do que rateMutation.
				if (Math.random() < rateMutation){
					
					//Realiza a muta��o no indice i do cromossomo passado como argumento.
					chromosomeMutant = chromosomeMutant.concat(chromosome.charAt(i) == '0' ? "1" : "0");
				}
				else{
					//Concatena na string de sa�da o valor no indice i do cromossomo.
					chromosomeMutant = chromosomeMutant.concat(chromosome.substring(i, i+1));
				}			
			}
		}
			
		//Retorna a vari�vel de refer�ncia da string constru�da atrav�s do processo de muta��o acima.
		return chromosomeMutant;
	}

	private String[] rafflePopulation(ArrayList<NeuralNetwork> listOfNeuralNetworks, double acumulateRate) {
		//Instancia um novo vetor para armazenar a nova popula��o.
		String[] newPopulation = new String[sizePopulation];
		
		//Realiza os sizePopulation sorteios.
		for (int i = 0; i < newPopulation.length; i++){
			
			//Gera um n�mero aleat�rio entre 0 e acumulateRate.
			double number = Math.random() * acumulateRate;
			
			//Percorre os objetos NeuralNetwork da lista at� que o n�mero sorteado seja menor do que a soma acumuada das taxas.
			double acumulate = 0;
			
			loopInternal:
			for (NeuralNetwork neuralNetwork : listOfNeuralNetworks){
				
				//Soma a taxa de acerto do objeto NeuralNetwork ao valor acumulado.
				acumulate += neuralNetwork.getRateAccuracy();
				
				//Verifica se a taxa de acerto do objeto NueralNetwork corrente somado com a acumuada � maior do que o n�mero sorteado.
				if (number <= acumulate){
					newPopulation[i] = neuralNetwork.getInputFeatures();
					
					//Para o loop interno.
					break loopInternal;
				}					
			}
		}
		
		//Retorna a vari�vel de refer�ncia do array contendo a nova popula��o.
		return newPopulation;
	}

	private String[] generatePopulation(){
		
		//Instancia um vetor para armazenar a popula��o(cromossomos).
		String[] population = new String[sizePopulation];
		
		//Gera uma popula��o aleatoriamente.
		for (int index = 0; index < sizePopulation; index++){
			
			//Gera o cromossomo(hip�tese) i.
			char[] chromosome = new char[sizeChromosome]; 
			Arrays.fill(chromosome, '0');
			
			//Gera um n�mero aleat�rio que determina a quantidade m�xima de 1s que podem ser atribu�dos ao cromossomo corrente i.
			int quantity = 1 + Math.round((float) Math.random() * (sizeChromosome-1));
			for (int j = 0; j < quantity; j++){
				//Atribui 1 a posi��o aleat�ria j do cromossomo atual.
				chromosome[(int) (Math.random()*(sizeChromosome-1))] = '1';
			}
			
			//Atribui a string bin�ria gerada pelo cromossomo
			population[index] = String.valueOf(chromosome);		
		}
		
		//Retorna a vari�vel de refer�ncia do vetor population gerado acima.
		return population;
	}
}
