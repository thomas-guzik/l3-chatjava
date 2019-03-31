package istic.pr.socket.tcp.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTCP {

	public static void main(String[] args) {
		int port = 9999;
		String ip = "";
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
				BufferedReader in = creerReader(socket);
				PrintWriter out = creerPrinter(socket);
				String msg = "";
				
				// Tant que le mot "fin" n'est pas lu sur le clavier,
				// Lire un message au clavier
				// Envoyer le message au serveur
				// Recevoir et afficher la reponse du serveur
				while (!msg.equalsIgnoreCase("fin")) {
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
 * Verifie le format de l'ip
 * @param ip string de l'ip en ipv4 ex:"127.0.0.1"
 * @return l'ip est valide
 */
	private static boolean checkip(String ip) {
	try {
		if(ip == null || ip.isEmpty()) {
			return false;
		}
		String[] parts = ip.split( "\\." );
		if( parts.length != 4 ) {
			return false;
		}
		for( String s : parts ) {
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
		return new BufferedReader(new InputStreamReader(System.in)).readLine();
	}

	public static BufferedReader creerReader(Socket socketVersUnClient) throws IOException {
		// Cree un BufferedReader associe a la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
	}

	public static PrintWriter creerPrinter(Socket socketVersUnClient) throws IOException {
		// Cree un PrintWriter associe a la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream()));
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
	
	public static void end (Socket socket, BufferedReader in, PrintWriter out) throws IOException{
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		if (socket != null)
			socket.close();
	}
}