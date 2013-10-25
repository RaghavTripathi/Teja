package edu.ncsu.ip.teja.sender.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ncsu.ip.teja.dao.Datagram;
import edu.ncsu.ip.teja.dao.Header;
import edu.ncsu.ip.teja.dao.Header.type;
import edu.ncsu.ip.teja.sender.client.Receiver;
import edu.ncsu.ip.teja.sender.common.Checksum;

public class ReliableDataTransfer implements Runnable {

    private static Logger LOGGER = Logger.getLogger(ReliableDataTransfer.class.getSimpleName());
    
    private final Receiver receiver;
    private final byte[] data;
    private final int sequenceNumber;
    
    public ReliableDataTransfer(Receiver receiver, byte[] data, int sequenceNumber) {
        super();
        this.receiver = receiver;
        this.data = data;
        this.sequenceNumber = sequenceNumber;
    }
    
    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating client socket", e);
            return;
        }
        
        // Create header, datagram objects
        String checkSum = Checksum.create(data);
        Header header = new Header(getSequenceNumber(), checkSum, type.DATA);
        Datagram datagram = new Datagram(header, data);

        // Create byte[] for datagram object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(datagram);
            oos.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating ObjectOutputStream", e);
            return;
        }

        byte[] buf = baos.toByteArray();
        byte[] bufferLength = BigInteger.valueOf(buf.length).toByteArray();
        DatagramPacket bufferLengthPacket = new DatagramPacket(bufferLength, 
                                                                4, 
                                                                receiver.getReceiverAddr(), 
                                                                receiver.getReceiverPort());
        
        try {
            socket.send(bufferLengthPacket);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while sending bufferLengthPacket", e);
            return;
        }
        
        socket.close();
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public byte[] getData() {
        return data;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
