//...

package istic.pr.socket.tcp.nom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.*;

public class ClientTCP {

	public static void main(String[] args) throws IOException, IOException {
		// crÈer une socket client

		String ip = "";
		while (!checkip(ip)) {
			System.out.println(" IP du serveur ?");
			ip = lireMessageAuClavier();
			// verif format TODO
		}
		int port = 9999;// pour si on fait le mode "en une saisie"

		/*
		 * System.out.println("Port ? "); // verif format. possibilitÈ de mettre
		 * 0.0.0.0:0 pour l'adresse complete, TODO Scanner sc = new Scanner(System.in);
		 * port = sc.nextInt();
		 */
		Socket socket = new Socket(InetAddress.getByName(ip), port);
		// crÈer reader et writer associÈs
		// sc.close();
		BufferedReader in = creerReader(socket);
		PrintWriter out = creerPrinter(socket);

		// Tant que le mot ´finª níest pas lu sur le clavier,
		// Lire un message au clavier
		// envoyer le message au serveur
		// recevoir et afficher la rÈponse du serveur

		String msg = "";
		String nom = args[0];
		envoyerMessage(out,nom);
		while (!msg.equalsIgnoreCase("fin")) {
			System.out.println(nom +"> ");
			msg = lireMessageAuClavier();
			envoyerMessage(out, msg);
			System.out.println(recevoirMessage(in));
		}
		// envoyer le message au serveur

		// recevoir et afficher la rÈponse du serveur
	
	end(socket, in, out);
	}

	/**
	 * envoie le nom du client
	 * @param printer
	 * @param nom
	 * @throws IOException
	 */
	public static void envoyerNom(PrintWriter printer, String nom) throws
	IOException {		
	envoyerMessage(printer, nom);
	    //envoi ´ NAME: nom ª au serveur
	}
/**
 * VÈrifie le format de l'ip
 * @param ip string de l'ip en ipv4 ex:"127.0.0.1"
 * @return l'ip est valide
 */
	private static boolean checkip(String ip) {
		 try {
		        if ( ip == null || ip.isEmpty() ) {
		            return false;
		        }
		        String[] parts = ip.split( "\\." );
		        if ( parts.length != 4 ) {
		            return false;
		        }
		        for ( String s : parts ) {
		            int i = Integer.parseInt( s );
		            if ( (i < 0) || (i > 255) ) {
		                return false;
		            }
		        }
		        if ( ip.endsWith(".") ) {
		            return false;
		        }
		        return true;
		    } catch (NumberFormatException nfe) {
		        return false;
		    }
		 
	}

	public static String lireMessageAuClavier() throws IOException {
		Scanner sc = new Scanner(System.in);
		String str = sc.nextLine();
		// sc.close();
		return str;
	}

	public static BufferedReader creerReader(Socket socketVersUnClient) throws IOException {
		// cr√©√© un BufferedReader associ√© √† la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
	}

	public static PrintWriter creerPrinter(Socket socketVersUnClient) throws IOException {
		// cr√©√© un PrintWriter associ√© √† la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream()));
	}

	public static String recevoirMessage(BufferedReader reader) throws IOException {
		return reader.readLine();
		// identique serveur
	}

	public static void envoyerMessage(PrintWriter printer, String message) throws IOException {
		printer.println(message);
		printer.flush();
		// Envoyer le message vers le client
	}
	public static void end (Socket socket, BufferedReader in, PrintWriter out) {
		
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}