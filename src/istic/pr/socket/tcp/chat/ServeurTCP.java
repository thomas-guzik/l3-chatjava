package istic.pr.socket.tcp.chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServeurTCP {

	private static List<PrintWriter> printerSocketActives = new ArrayList<PrintWriter>();
	private static String outMsg = "$fin";

	public static void main(String[] args) {
		// Attente des connexions sur le port 9999
		int portEcoute = 9999;
		int nbThreads = 5; // default threadpool
		Charset cs = Charset.forName("UTF-8"); // mis en global.

		if (args.length > 0) {
			try {
				cs = Charset.forName(args[0]);
			} catch (Exception e) {
				cs = Charset.forName("UTF-8");
			}
			try {
				nbThreads = Integer.parseInt(args[1]);
				if (nbThreads < 1 || nbThreads > 64) {
					nbThreads = 5;
					System.out.println("invalid pool of threads: " + nbThreads);
				}
			} catch (Exception e) {
				System.out.println("invalid pool of threads2" + nbThreads);
				nbThreads = 5;

			}
		}

		try {
			ServerSocket socketServeur = new ServerSocket(portEcoute);

			try {
				System.out.println("Server open with ip: " + socketServeur.getInetAddress() + " port: "
						+ socketServeur.getLocalPort());
				// Dans une boucle, pour chaque socket clientes, appeler traiterSocketCliente
				/*
				 * while (true) { Socket socketVersUnClient = socketServeur.accept();
				 * traiterSocketCliente(socketVersUnClient, cs); }
				 */

				Executor service = Executors.newFixedThreadPool(nbThreads);
				while (true) {
					Socket socketVersUnClient = socketServeur.accept();
					service.execute(new TraiteUnClient(socketVersUnClient, cs));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socketServeur.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected static void traiterSocketCliente(Socket socketVersUnClient, Charset cs) throws IOException {
		// Cree printer et reader
		PrintWriter printer = creerPrinter(cs, socketVersUnClient);
		BufferedReader reader = creerReader(cs, socketVersUnClient);
		String name = "???";
		try {
			String msg;
			name = avoirNom(reader);
			if (name == null) {
				envoyerMessage(printer, "Erreur envoi du nom invalide");
			} else {
				System.out.println("New client: " + name);
				// Tant qu’il y’a un message à lire via recevoirMessage
				ajouterPrinterSocketActives(printer);
				envoyerATouteLesSocketsActive("Bienvenue � " + name);
				while ((msg = recevoirMessage(reader)) != null && (!msg.equalsIgnoreCase(outMsg))) {
					System.out.println("from: " + name + " > Msg received: " + msg);
					if (!msg.equalsIgnoreCase(outMsg))
						envoyerATouteLesSocketsActive(name + "> " + msg);

				}
			}
		} catch (IOException e) {
			System.out.println("Client closed by mistake");
			e.printStackTrace();
		} finally {
			socketVersUnClient.close();
			System.out.println("Client closed");
			enleverPrinterSocketActives(printer);
			envoyerATouteLesSocketsActive("--->" + name + " Left");
			printer.close();
			reader.close();
		}
		// Si plus de ligne a lire fermer socket cliente
		socketVersUnClient.close();
		printer.close();
		reader.close();
	}

	public static String avoirNom(BufferedReader reader) throws IOException {
		// retourne le nom du client (en utilisant split de la classe String par
		// exemple)
		String[] parts = reader.readLine().split(":");
		if (parts.length == 2) {
			if (parts[0].equals("NAME")) {
				return parts[1];
			} else {
				System.out.println("Errparts" + parts[1]);
				return null;
			}
		} else {
			System.out.println("errSplit" + parts[0]);
			return null;
		}
	}

	public static BufferedReader creerReader(Charset charset, Socket socketVersUnClient) throws IOException {
		// créé un BufferedReader associé à la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream(), charset));
	}

	public static PrintWriter creerPrinter(Charset charset, Socket socketVersUnClient) throws IOException {
		// créé un PrintWriter associé à la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream(), charset));
	}

	public static String recevoirMessage(BufferedReader reader) throws IOException {
		// Récupérer une ligne
		return reader.readLine();
		// Retourner la ligne lue ou null si aucune ligne à lire.
	}

	public static void envoyerMessage(PrintWriter printer, String message) throws IOException {
		printer.println(message);
		printer.flush();
		// Envoyer le message vers le client
	}

	public static synchronized void ajouterPrinterSocketActives(PrintWriter printer) {
		// ajouter le printer � la liste
		printerSocketActives.add(printer);
	}

	public static synchronized void enleverPrinterSocketActives(PrintWriter printer) {
		// enlever le printer � la liste
		for (int i = 0; i < printerSocketActives.size(); i++) {
			if (printerSocketActives.get(i) == printer)
				printerSocketActives.remove(i);
		}
	}

	public static synchronized void envoyerATouteLesSocketsActive(String msg) throws IOException {
		// envoie le message � toutes les sockets actives
//		System.out.println(msg);System.out.println(printerSocketActives.size());
		for (int i = 0; i < printerSocketActives.size(); i++) {

			envoyerMessage(printerSocketActives.get(i), msg);
		}
	}
}