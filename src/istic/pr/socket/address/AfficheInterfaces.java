package istic.pr.socket.address;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class AfficheInterfaces {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		 Enumeration<NetworkInterface> netinterfs = NetworkInterface.getNetworkInterfaces();
		 while(netinterfs.hasMoreElements()) {
			 NetworkInterface netinterf = netinterfs.nextElement();
			 System.out.println(netinterf.getName() + ": " + netinterf.getDisplayName());
			 
			 Enumeration<InetAddress> inetaddrs = netinterf.getInetAddresses();
			 while (inetaddrs.hasMoreElements()) {
		            InetAddress ip = inetaddrs.nextElement();
		            System.out.println("->" + ip.toString());
		        }
		 }
	            
	
		}
		catch (Exception e) {
			 e.printStackTrace();
		}
	}

}
