package istic.pr.socket.tcp.chat;

import java.net.Socket;
import java.nio.charset.Charset;

public class TraiteUnClient implements Runnable {
	Socket socket;
	Charset cs;
	public TraiteUnClient(Socket socketVersUnClient, Charset cs) {
		this.socket=socketVersUnClient;
		this.cs=cs;
	}

	public void run() {
		try{
			ServeurTCP.traiterSocketCliente(socket, cs);
		}catch(Exception e) {
			System.out.println("Thread: run bug");
			e.printStackTrace();
		}
	}
}
