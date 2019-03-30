package istic.pr.socket.tcp.nom;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class ServeurTCP {

    public static void main(String[] args) {

        //Attente des connexions sur le port 9999
    	int portEcoute = 9999;
    	try {
    		ServerSocket socketServeur = new ServerSocket(portEcoute);
			try {
		    	System.out.println("Server open with ip: " + socketServeur.getInetAddress() + " port: " + socketServeur.getLocalPort());
		    	
		    	//Dans une boucle, pour chaque socket clientes, appeler traiterSocketCliente
		    	while (true) {
		    		Socket socketVersUnClient = socketServeur.accept();
		    		traiterSocketCliente(socketVersUnClient);
		    	}
		    }
			catch(IOException e) {
				e.printStackTrace();
			}
			finally {
	    		socketServeur.close();
	    	}
    	}
		catch(IOException e) {
			e.printStackTrace();
		}
    }
    
    public static String avoirNom(BufferedReader reader) throws IOException {
        //retourne le nom du client (en utilisant split de la classe String par exemple)
    	String[] parts = reader.readLine().split(":");
    	if(parts.length == 2) {
    		if(parts[0].equals("NAME")) {
        		return parts[1];
        	}
        	else {
        		return null;
        	}
    	}
    	else {
    		return null;
    	}
    }
    
    public static void traiterSocketCliente(Socket socketVersUnClient) throws IOException {
        
    	//Cree printer et reader
    	PrintWriter printer = creerPrinter(socketVersUnClient);
    	BufferedReader reader = creerReader(socketVersUnClient);
	    
    	try {
	    	String msg;
	    	String name = avoirNom(reader);
	    	if(name == null) {
	    		envoyerMessage(printer, "Erreur envoi du nom invalide");
	    	}
	    	else {
		        //Tant qu’il y’a un message à lire via recevoirMessage
		    	while((msg = recevoirMessage(reader)) != null) {
		    		System.out.println("Msg received: " + msg);
		    		//Envoyer message au client via envoyerMessage
		    		envoyerMessage(printer, name + ">" + msg);
		    	}
	    	}
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    	finally {
    		socketVersUnClient.close();
    		printer.close();
    		reader.close();
    	}
        //Si plus de ligne a lire fermer socket cliente
    	socketVersUnClient.close();
    	printer.close();
		reader.close();
    }

    public static BufferedReader creerReader(Socket socketVersUnClient)
    throws IOException {
        // cree un BufferedReader associe à la Socket
    	return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
    }

    public static PrintWriter creerPrinter(Socket socketVersUnClient) throws
    IOException {
        // cree un PrintWriter associe a la Socket
    	return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream()));
    }

    public static String recevoirMessage(BufferedReader reader) throws
    IOException {
        //Recuperer une ligne
    	return reader.readLine();
        //Retourner la ligne lue ou null si aucune ligne à lire.
    }

    public static void envoyerMessage(PrintWriter printer, String message)
    throws IOException {
    	printer.println(message);
    	printer.flush();
        //Envoyer le message vers le client
    }

}