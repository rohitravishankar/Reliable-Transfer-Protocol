import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Sender {
	static String hostName = "127.0.0.1";
	static int sequenceNumber = 0;

	public void performSending() throws Exception {

		// Creating a Datagram Socket for communication
		DatagramSocket client = new DatagramSocket();
		InetAddress inetAddress = InetAddress.getByName(hostName);

		int packetCounter = 1; 

		while (packetCounter <= 10){

			// Sending information to the receiver
			// Creating a new packet
			Packet packet = new Packet("packet" + packetCounter, sequenceNumber, packetCounter);
			String content = packet.creatingPacket();

			//Send the packet
			DatagramPacket sendPacket = new DatagramPacket(content.getBytes(), content.getBytes().length, inetAddress, 3000);		
			client.send(sendPacket);
			System.out.println("send packet" + packetCounter);
			System.out.println("\n"); 
			
			client.setSoTimeout(10000);

			//Receiving data from Receiver  
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				client.receive(receivePacket);
				String receivedMessage = new String(receiveData);
				String splitArray[] = receivedMessage.split(" ");
				if(splitArray[0].equals("INCORRECT")) {
					continue;
				}
				if( Integer.parseInt(splitArray[1]) == generateCheckSum(splitArray[0]) ){
					System.out.println( "received ACK for " + splitArray[0] );
					getSequenceNum();
					packetCounter ++; 
				}
				else {
					continue;
				}
			} catch (SocketTimeoutException e) {
				System.out.println("timeout has been detected");
				continue;
			}
		}

		client.close();
	}

	// Alternates the 0 and 1 Sequence Numbers 
	public static void getSequenceNum() {
		if(sequenceNumber == 0) 
			sequenceNumber = 1;
		else 
			sequenceNumber = 0;
	}
	
	//Generating CheckSum Values for given packet
	public int generateCheckSum(String data) {

		int value = 0 ; 
		int charVal; 

		for (int i=0; i<data.length(); i++) {
			charVal = ((int)data.charAt(i)); 
			value += charVal; 
		}

		return value; 
	}

	public static void main(String[] args) throws Exception {
		Sender sender = new Sender();
		sender.performSending();
	}
}
