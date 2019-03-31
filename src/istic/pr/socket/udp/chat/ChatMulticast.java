package istic.pr.socket.udp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ChatMulticast {
	private static boolean recvOn;
	
	public static void main(String[] args) {
		ReceiveRunnable recvRun = new ReceiveRunnable();
		Thread t1 = new Thread(recvRun);
		t1.start();
		try {
			MulticastSocket s = new MulticastSocket(9999);
			InetAddress group = InetAddress.getByName("225.0.4.7");
			
			System.out.println("Name ?");
			String name = lireMessageAuClavier();
			try {
				String msg = lireMessageAuClavier();
				while(!msg.equals("fin")) {
					envoyerMessage(s, name + ">" + msg);
					msg = lireMessageAuClavier();
				}
				
				System.out.println("Closing... time needed: 1s");
				recvRun.quit();
				t1.join();
				envoyerMessage(s, name + " quit the chat");
				s.close();
			}
			finally {
				recvRun.quit();
				s.close();
			}
		} catch (UnknownHostException e) {
			// InetAddress
			e.printStackTrace();
		} catch (IOException e) {
			// MulticastSocket
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("FIN");
		System.out.println("Statut du thread " + t1.getName() + " = " +t1.getState());
		
	}
	
	public static void envoyerMessage(MulticastSocket s, String message) throws IOException {
		s.send(new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("225.0.4.7"), 9999));
	}
	
	public static String recevoirMessage(MulticastSocket s) throws IOException {
		// bloque tant qu’un message n’est pas reçu
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		s.setSoTimeout(1000);
		while(recvOn && packet.getAddress() == null ) {
			try {
				s.receive(packet);
			}
			catch(SocketTimeoutException e) {}
		}
		return new String(packet.getData());
	}
	
	public static String lireMessageAuClavier() throws IOException, SocketException {
		return new BufferedReader(new InputStreamReader(System.in)).readLine();
	}
	
	private static class ReceiveRunnable implements Runnable {
		private MulticastSocket recvSocket = null;
		private InetAddress group;
		
		public void run() {
			String recvMsg;
			recvOn = true;
			try {
				recvSocket = new MulticastSocket(9999);
				group = InetAddress.getByName("225.0.4.7");
				try {
					recvSocket.joinGroup(group);
					while(recvOn) {
						recvMsg = recevoirMessage(recvSocket);
						System.out.println(recvMsg);
					}
				}
				finally {
					quit();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void quit() {
			if(recvSocket != null) {
				recvOn = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				recvSocket.close();
			}
		}
	}
}
