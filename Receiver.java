import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Receiver {
	int portNumber = 3000;
	static int sequenceNumber = 0;

	public void performReceiving() throws Exception {

		// Creating a Datagram Socket for communication
		DatagramSocket socket = new DatagramSocket(portNumber);

		int packetCounter = 1;
		
		//Flags for corruption instance 
		boolean firstOccurrenceOf3 = true;
		int counterFirstOccurrenceOf3 = 0;
		
		//Flags for receiver delay instance (timeout) 
		boolean firstOccurrenceOf7 = true;
		int counterFirstOccurrenceOf7 = 0;
		
		//Set functionality to ensure no duplicate packets are sending from receiver 
		List<String> packetArray = new ArrayList<String>();
		
		while (packetCounter <= 10) {

			// To receive a packet from the sender and put the contents of the message into a buffer
			byte[] buffer = new byte[1024];
			DatagramPacket receivingPacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(receivingPacket);
			InetAddress IPAddress = receivingPacket.getAddress();
			int port = receivingPacket.getPort();

			// Received Message & entering contents into array delimited upon spaces 
			String receivedMessage = new String(receivingPacket.getData(), 0, receivingPacket.getLength());
			String[] splitArray = receivedMessage.split("\\s+");
			
			//Creating/Simulating a corruption for every packetID that is a multiple of 3 
			if( Integer.parseInt(splitArray[3]) % 3 == 0 && firstOccurrenceOf3 == true) {
				
				//Manually changing the CheckSum value of Receiver end in order to ensure 
				//corruption occurs 
				splitArray[0] = splitArray[0] + " corrupt";
				
				//Condition to check if CheckSum's match, otherwise corruption error occurs 
				if( Integer.parseInt(splitArray[1]) != generateCheckSum(splitArray[0]) ) {
					String temporary = splitArray[0];
					String[] temporaryArray = temporary.split(" ");
					System.out.println(temporaryArray[0] + " received incorrectly");
					Packet packet = new Packet("INCORRECT", generateCheckSum("INCORRECT"), Integer.parseInt(splitArray[3]) );
					String serverResponseMessage = packet.creatingPacket();
					buffer = serverResponseMessage.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, port);
					socket.send(sendPacket);
					firstOccurrenceOf3 = false;
					counterFirstOccurrenceOf3++;
				}
			}
			
			//Creating/Simulating an instance of timeout or response delay to sender 
			if( Integer.parseInt(splitArray[3]) % 7 == 0 && firstOccurrenceOf7 == true ) {
				Thread.sleep(10000);
				firstOccurrenceOf7 = false;
				counterFirstOccurrenceOf7++;
			}

			if( Integer.parseInt(splitArray[1]) == generateCheckSum(splitArray[0]) )  {
				
				//Resetting flags
				if( counterFirstOccurrenceOf3 == 1 ) {
					firstOccurrenceOf3 = true;
					counterFirstOccurrenceOf3 = 0;
				}
				
				//Resetting flags
				if( counterFirstOccurrenceOf7 == 1 ) {
					firstOccurrenceOf7 = true;
					counterFirstOccurrenceOf7 = 0;
				}
				
				//Building response
				if( !packetArray.contains(splitArray[0]) ) {
					System.out.println(splitArray[0] + " received correctly");
					Packet packet = new Packet(splitArray[0], sequenceNumber, packetCounter );
					String serverResponseMessage = packet.creatingPacket();
					buffer = serverResponseMessage.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, port);
					socket.send(sendPacket);
					packetArray.add(splitArray[0]);
					System.out.println("send ACK for " + splitArray[0]);
					System.out.println("\n");
					getSequenceNum();
					packetCounter++; 
				}

			}
			else {
				continue;
			}
		}

		socket.close();
	}
	
    // Alternates the 0 and 1
    public static void getSequenceNum() {
    	if(sequenceNumber == 0) 
    		sequenceNumber = 1;
		else 
			sequenceNumber = 0;
    }
    
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
		Receiver receiver = new Receiver();
		receiver.performReceiving();
	}
}