//...

package istic.pr.socket.tcp.echo;

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
		System.out.println(" IP du serveur ?");
		String ip = lireMessageAuClavier();
		// verif format TODO

		int port;// pour si on fait le mode "en une saisie"
		System.out.println("Port ? ");
		// verif format. possibilitÈ de mettre 0.0.0.0:0 pour l'adresse complete, TODO
		Scanner sc = new Scanner(System.in);
		port = sc.nextInt();

		Socket socket = new Socket(InetAddress.getByName(ip), port);
		// crÈer reader et writer associÈs
		sc.close();
		BufferedReader in = creerReader(socket);
		PrintWriter out = creerPrinter(socket);

		// Tant que le mot ´finª níest pas lu sur le clavier,
		// Lire un message au clavier
		// envoyer le message au serveur
		// recevoir et afficher la rÈponse du serveur

		String msg = "";
		while (!msg.equalsIgnoreCase("fin")) {
			msg = lireMessageAuClavier();
			envoyerMessage(out, msg);
			System.out.println(recevoirMessage(in));
		}
		// envoyer le message au serveur

		// recevoir et afficher la rÈponse du serveur
	}

	public static String lireMessageAuClavier() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("> ");
		String str = sc.nextLine();
		sc.close();
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
}