package istic.pr.socket.tcp.echo;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class ServeurTCP {

    public static void main(String[] args) throws IOException {

        //Attente des connexions sur le port 9999
    	int portEcoute = 9999;
    	ServerSocket socketServeur = new ServerSocket(portEcoute);
    	
    	//Dans une boucle, pour chaque socket clientes, appeler traiterSocketCliente
    	while (true) {
    		Socket socketVersUnClient = socketServeur.accept();
    		traiterSocketCliente(socketVersUnClient);
    	}
    }

    public static void traiterSocketCliente(Socket socketVersUnClient) throws IOException {
        //Créer printer et reader
    	PrintWriter printer = creerPrinter(socketVersUnClient);
    	BufferedReader reader = creerReader(socketVersUnClient);
    	
    	String msg;
        //Tant qu’il y’a un message à lire via recevoirMessage
    	while((msg = recevoirMessage(socketVersUnClient)) != null) {
    		//Envoyer message au client via envoyerMessage
    		envoyerMessage(printer, msg);
    	}

        //Si plus de ligne à lire fermer socket cliente
    }

    public static BufferedReader creerReader(Socket socketVersUnClient)
    throws IOException {
        //créé un BufferedReader associé à la Socket
    	return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
    }

    public static PrintWriter creerPrinter(Socket socketVersUnClient) throws
    IOException {
        //créé un PrintWriter associé à la Socket
    	return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream()));
    }

    public static String recevoirMessage(Socket socketVersUnClient) throws
    IOException {
        //Récupérer une ligne
    	return socketVersUnClient.readLine();
        //Retourner la ligne lue ou null si aucune ligne à lire.
    }

    public static void envoyerMessage(PrintWriter printer, String message)
    throws IOException {
    	printer.println(message);
    	printer.flush();
        //Envoyer le message vers le client
    }

}