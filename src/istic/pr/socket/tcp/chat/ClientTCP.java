package istic.pr.socket.tcp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientTCP {

	public static void main(String[] args) {
		int port = 9999;
		String ip = "";
		String outMsg = "$fin";
		try {
			while (!checkip(ip)) {
				System.out.println(" IP du serveur ?");
				ip = lireMessageAuClavier();
				// verif format
			}

			// creer une socket client
			Socket socket = new Socket(InetAddress.getByName(ip), port);

			try {

				// Attention tu programmes du code de la fonction envoyerNom dans le main

				String nom = "";
				Charset charset = Charset.forName("UTF-8");
				if (args.length > 0)
					nom += args[0];
				if (args.length > 1) {
					try {
						charset = Charset.forName(args[1]);
					} catch (Exception e) {
						charset = Charset.forName("UTF-8");// default
					}
				}

				// creer reader et writer associes
				BufferedReader in = creerReader(charset, socket);
				PrintWriter out = creerPrinter(charset, socket);
				String msg = "";
				envoyerNom(out, nom); // Ici on doit utiliser la fc envoyerNom

				// Tant que le mot "fin" n'est pas lu sur le clavier,
				// Lire un message au clavier
				// envoyer le message au serveur
				// recevoir et afficher la reponse du serveur

				Executor service = Executors.newFixedThreadPool(1);
				while (!msg.equalsIgnoreCase(outMsg)) {
					service.execute(new RecevoirMessages(in));
					msg = lireMessageAuClavier();

					envoyerMessage(out, msg);
				}
				end(socket, in, out);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Erreur, aucun nom en argument lors de l'appel du main");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * envoie le nom du client
	 * 
	 * @param printer
	 * @param nom
	 * @throws IOException
	 */
	public static void envoyerNom(PrintWriter printer, String nom) throws IOException {
		// envoi "NAME:nom" au serveur
		envoyerMessage(printer, "NAME:" + nom);
	}

	/**
	 * Verifie le format de l'ip
	 * 
	 * @param ip string de l'ip en ipv4 ex:"127.0.0.1"
	 * @return l'ip est valide
	 */
	private static boolean checkip(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}
			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}
			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static String lireMessageAuClavier() throws IOException {
		return new BufferedReader(new InputStreamReader(System.in)).readLine();
	}

	public static BufferedReader creerReader(Charset cs, Socket socketVersUnClient) throws IOException {
		// cree un BufferedReader associe a la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream(), cs));
	}

	public static PrintWriter creerPrinter(Charset cs, Socket socketVersUnClient) throws IOException {
		// cree un PrintWriter associe a la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream(), cs));
	}

	public static String recevoirMessage(BufferedReader reader) throws IOException {
		// identique serveur
		return reader.readLine();
	}

	public static void envoyerMessage(PrintWriter printer, String message) throws IOException {
		// Envoyer le message vers le client
		printer.println(message);
		printer.flush();
	}

	public static void end(Socket socket, BufferedReader in, PrintWriter out) {
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

	public static synchronized void recevoirTout(BufferedReader in) {
		String msg = "";
		try {
			while ((msg = recevoirMessage(in)) != null) {
				System.out.println(msg);
			}
		} catch (IOException e) {
			System.exit(0);// TODO Dégeulasse? je compte le corriger. !au cazou
		}
	}
}