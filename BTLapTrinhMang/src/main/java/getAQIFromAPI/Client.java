package getAQIFromAPI;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static int destPort = 12399;
    public static String hostname = "localhost";
    public static void main(String[] args) {
        DatagramSocket socket;
        DatagramPacket dpsend, dpreceive;
        InetAddress add; Scanner stdIn;
        System.out.println("Don't input 'BYE' unless to quit !");
        try {
            add = InetAddress.getByName(hostname);	//UnknownHostException
            socket = new DatagramSocket();			//SocketException
            stdIn = new Scanner(System.in);
            while(true) {
                System.out.print("Client input: ");
                String tmp = stdIn.nextLine();
                byte[] data = tmp.getBytes();
                dpsend = new DatagramPacket(data, data.length, add, destPort);
                System.out.println("Client sent '" + tmp + "' to server from port " + socket.getLocalPort());
                socket.send(dpsend);
                if(tmp.equals("bye")) {
                    System.out.println("Client socket closed");
                    stdIn.close();
                    socket.close();
                    break;
                }
                // Get response from server
                dpreceive = new DatagramPacket(new byte[512], 512);
                socket.receive(dpreceive);
                tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
                System.out.println(tmp);
            }
        } catch (IOException e) { System.err.println(e);}
    }
}