package istic.pr.socket.tcp.chat;

import java.io.BufferedReader;

public class RecevoirMessages implements Runnable {

	BufferedReader in;
	public RecevoirMessages(BufferedReader in) {
		this.in = in;
	}
	public void run() {
		try{ClientTCP.recevoirTout(in);

	}catch(Exception e) {
		System.out.println("Thread bug");
		e.printStackTrace();
	}
	}
}
