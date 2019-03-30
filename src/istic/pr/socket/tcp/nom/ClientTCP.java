package istic.pr.socket.tcp.nom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientTCP {

	public static void main(String[] args) {
		int port = 9999;
		String ip = "";
		try {
			while (!checkip(ip)) {
				System.out.println(" IP du serveur ?");
				ip = lireMessageAuClavier();
				// verif format
			}
		
			// creer une socket client
			Socket socket = new Socket(InetAddress.getByName(ip), port);
			
			try {
				// creer reader et writer associes
				BufferedReader in = creerReader(socket);
				PrintWriter out = creerPrinter(socket);
				String msg = "";
				
				envoyerNom(out,args[0]);
	
				// Tant que le mot "fin" n'est pas lu sur le clavier,
				// Lire un message au clavier
				// envoyer le message au serveur
				// recevoir et afficher la reponse du serveur
				while (!msg.equalsIgnoreCase("fin")) {
					msg = lireMessageAuClavier();
					envoyerMessage(out, msg);
					System.out.println(recevoirMessage(in));
				}
				end(socket, in, out);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("Erreur, aucun nom en argument lors de l'appel du main");
				e.printStackTrace();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			finally {
				socket.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * envoie le nom du client
	 * @param printer
	 * @param nom
	 * @throws IOException
	 */
	public static void envoyerNom(PrintWriter printer, String nom) throws IOException {
		envoyerMessage(printer, "NAME:"+nom);
	    //envoi "NAME: nom" au serveur
	}
	
	/**
	 * V�rifie le format de l'ip
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
		return new BufferedReader(new InputStreamReader(System.in)).readLine();
	}

	public static BufferedReader creerReader(Socket socketVersUnClient) throws IOException {
		// cree un BufferedReader associé à la Socket
		return new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
	}

	public static PrintWriter creerPrinter(Socket socketVersUnClient) throws IOException {
		// cree un PrintWriter associé à la Socket
		return new PrintWriter(new OutputStreamWriter(socketVersUnClient.getOutputStream()));
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