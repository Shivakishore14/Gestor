    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestor.server;

/**
 *
 * @author root
 */
public class GestorServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	// TODO code application logic here
	System.out.println(util.fun());
	System.out.println(util.newClient( "127.1.1.1", "testSystem2" ));
    }
    
}
