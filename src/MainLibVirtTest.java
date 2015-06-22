
import org.libvirt.*;

public class MainLibVirtTest {
	
	public static void main(String[] args) {
        
		try{
			//Conecta com o QEMU.
            Connect conn = new Connect("qemu:///system", true);
            
            //Percorre a lista de IDs dos dom�nios ativos.
            for (int idDomain : conn.listDomains()){
            	
            	//Pega a vari�vel de refer�ncia do dom�nio com ID igual ao valor de idDomain corrente.
                Domain testDomain = conn.domainLookupByID(idDomain);
                
                //Exibe algumas informa��es.
                System.out.println("Domain:" + testDomain.getName() + " id " +
                                   testDomain.getID() + " running " +
                                   testDomain.getOSType());            
            }
		} catch (LibvirtException e) {
            System.out.println("exception caught:"+e);
            System.out.println(e.getError());
        }        
    }
}
