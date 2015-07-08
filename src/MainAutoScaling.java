import manager.Manager;


public class MainAutoScaling {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		//Instancia um objeto Manager e começa a executar o gerenciador.
		Manager manager = new Manager(10, 0.1, 5);
		manager.start();
	}

}
