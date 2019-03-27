package istic.pr.socket.tcp.echo;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class ServeurTCP {

    public static void main(String[] args) {

        //Attente des connexions sur le port 9999
    	int portEcoute = 9999;
    	try {
	    	ServerSocket socketServeur = new ServerSocket(portEcoute);
	    	
	    	while (true) {
	    		Socket socketVersUnClient = socketServeur.accept();
	    		traiterSocketCliente(socketVersUnClient);
	    		
	    	}


        //Dans une boucle, pour chaque socket clientes, appeler traiterSocketCliente
    	
    	} catch (IOException e) { //Traitement des exceptions
    		e.printStackTrace();
    	}
    	
    }

    public static void traiterSocketCliente(Socket socketVersUnClient) {
        //Créer printer et reader
    	creerPrinter(socketVersUnClient);
    	creerReader(socketVersUnClient);
    	

        //Tant qu’il y’a un message à lire via recevoirMessage

        //Envoyer message au client via envoyerMessage

        //Si plus de ligne à lire fermer socket cliente
    }

    public static BufferedReader creerReader(Socket socketVersUnClient)
    throws IOException {
        //créé un BufferedReader associé à la Socket
    }

    public static PrintWriter creerPrinter(Socket socketVersUnClient) throws
    IOException {
        //créé un PrintWriter associé à la Socket
    }

    public static String recevoirMessage(BufferedReader reader) throws
    IOException {
        //Récupérer une ligne
        //Retourner la ligne lue ou null si aucune ligne à lire.
    }

    public static void envoyerMessage(PrintWriter printer, String message)
    throws IOException {
        //Envoyer le message vers le client
    }

}