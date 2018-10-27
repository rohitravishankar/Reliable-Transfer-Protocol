public class Packet {

	String data;
	int sequenceNumber;
	int chksum; 
	int packetID; 

	Packet(String d, int sequenceNumber, int packetID) {
		this.data = d; 
		this.sequenceNumber = sequenceNumber;
		this.packetID = packetID; 
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

	public String creatingPacket() {
		String generatedPacket = this.data + " " + generateCheckSum(this.data)+" "+ this.sequenceNumber + " " + this.packetID;
		return generatedPacket; 
	}


}
