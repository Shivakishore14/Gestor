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
public class GestorInit {
    public static void main(String args[]){
	new ServerInitiator(9008).start();
    }
}
