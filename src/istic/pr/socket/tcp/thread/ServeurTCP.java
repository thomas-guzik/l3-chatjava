package istic.pr.socket.tcp.thread;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServeurTCP {

	public static void main(String[] args) {
		// Attente des connexions sur le port 9999
		int portEcoute = 9999;
		Charset cs;
		try {
			cs = Charset.forName(args[0]);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Error charset not defined in parameters\nCharset set as: UTF-8");
			cs = Charset.forName("UTF-8");
		}
		
		int nbThreads;
		try { 
			nbThreads = Integer.parseInt(args[1]);
			if(nbThreads < 1 || nbThreads > 64) {
				throw new Exception();
			}
		} catch(Exception e) { // NumberFormatException, ArrayIndexOutOfBoundsException, Exception
			System.out.println("Invalid pool of threads, set the number as 5");
			nbThreads = 5;
		}

		try {
			ServerSocket socketServeur = new ServerSocket(portEcoute);
			try {
				System.out.println("Server open with ip: " + socketServeur.getInetAddress() + " port: "
						+ socketServeur.getLocalPort());
				// Dans une boucle, pour chaque socket clientes, appeler traiterSocketCliente
				/*while (true) {
					Socket socketVersUnClient = socketServeur.accept();
					traiterSocketCliente(socketVersUnClient, cs);
				}*/
				
				Executor service = Executors.newFixedThreadPool(nbThreads);
				while (true) {
					Socket socketVersUnClient = socketServeur.accept();
					service.execute(new TraiteUnClient(socketVersUnClient, cs));
				}
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

		try {
			String msg;
			String name = avoirNom(reader);
			if (name == null) {
				envoyerMessage(printer, "Erreur envoi du nom invalide");
			} else {
				System.out.println("New client: "+name);
				// Tant qu’il y’a un message à lire via recevoirMessage
				while ((msg = recevoirMessage(reader)) != null) {
					System.out.println("from: "+name+" > Msg received: " + msg);
					// Envoyer message au client via envoyerMessage
					envoyerMessage(printer, name + "> " + msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socketVersUnClient.close();
			printer.close();
			reader.close();
		}
		// Si plus de ligne a lire fermer socket cliente
		System.out.println("Client closed");
		socketVersUnClient.close();
		printer.close();
		reader.close();
	}
	
	public static String avoirNom(BufferedReader reader) throws IOException {
		// Retourne le nom du client (en utilisant split de la classe String par exemple)
		try {
			String[] parts = reader.readLine().split(":");
			if(parts.length == 2) {
				if(parts[0].equals("NAME")) {
					return parts[1];
				}
				else {
					System.out.println("Error cmd NAME not found");
					return null;
				}
			}
			else {
				System.out.println("Error not cmd found");
				return null;
			}
		} catch(NullPointerException e) {
			return null;
		}
	}

	public static BufferedReader creerReader(Charset charset, Socket socketVersUnClient) throws IOException {
		// Cree un BufferedReader associe a la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream(), charset));
	}

	public static PrintWriter creerPrinter(Charset charset, Socket socketVersUnClient) throws IOException {
		// Cree un PrintWriter associe a la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream(), charset));
	}
	
	public static String recevoirMessage(BufferedReader reader) throws IOException {
		// Recupérer une ligne
		return reader.readLine();
		// Retourner la ligne lue ou null si aucune ligne a lire.
	}

	public static void envoyerMessage(PrintWriter printer, String message) throws IOException {
		printer.println(message);
		printer.flush();
		// Envoyer le message vers le client
	}
}