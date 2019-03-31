package istic.pr.socket.tcp.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class ClientTCP {

	public static void main(String[] args) {
		int port = 9999;
		String ip = "";
		String name;
		Charset charset;
		
		try {
			name = args[0];
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Error name is not in parameters\nName set as: Default_name");
			name = "Default_name";
		}
		
		try {
			charset = Charset.forName(args[1]);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Error charset not defined in parameters\nCharset set as: UTF-8");
		} catch(UnsupportedCharsetException e) {
			System.out.println("Unsupported charset\nCharset set as: UTF-8");
		} finally {
			charset = Charset.forName("UTF-8");
		}
		
		try {
			while (!checkip(ip)) {
				System.out.println("IP du serveur ?");
				ip = lireMessageAuClavier();
				// verif format
			}
			
			// Creer une socket client
			Socket socket = new Socket(InetAddress.getByName(ip), port);
			
			try {
				// Creer reader et writer associes
				BufferedReader in = creerReader(charset, socket);
				PrintWriter out = creerPrinter(charset, socket);
				String msg = "";
				envoyerNom(out, name);
				 
				// Tant que le mot "fin" n'est pas lu sur le clavier,
				// Lire un message au clavier
				// Envoyer le message au serveur
				// Recevoir et afficher la reponse du serveur
				while(!msg.equalsIgnoreCase("fin")) {
					msg = lireMessageAuClavier();
					envoyerMessage(out, msg);
					System.out.println(recevoirMessage(in));
				}
				end(socket, in, out);
			} finally {
				socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoie le nom du client
	 * 
	 * @param printer
	 * @param nom
	 * @throws IOException
	 */
	public static void envoyerNom(PrintWriter printer, String nom) throws IOException {
		//envoi "NAME:nom" au serveur
		envoyerMessage(printer, "NAME:"+nom);
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
		// Cree un BufferedReader associe a la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream(), cs));
	}

	public static PrintWriter creerPrinter(Charset cs, Socket socketVersUnClient) throws IOException {
		// Cree un PrintWriter associe a la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream(), cs));
	}

	public static String recevoirMessage(BufferedReader reader) throws IOException {
		// Identique serveur
		return reader.readLine();
	}

	public static void envoyerMessage(PrintWriter printer, String message) throws IOException {
		// Envoyer le message vers le client
		printer.println(message);
		printer.flush();
	}

	public static void end(Socket socket, BufferedReader in, PrintWriter out) throws IOException {
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		if (socket != null)
			socket.close();
	}
}