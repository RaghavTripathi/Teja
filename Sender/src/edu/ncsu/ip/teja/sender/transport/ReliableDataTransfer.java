package edu.ncsu.ip.teja.sender.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    private final boolean isEOF;
    
    public ReliableDataTransfer(Receiver receiver, byte[] data, int sequenceNumber, boolean isEOF) {
        super();
        this.receiver = receiver;
        this.data = data;
        this.sequenceNumber = sequenceNumber;
        this.isEOF = isEOF;
    }
    
    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(receiver.getTimeoutInMSec());
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating client socket", e);
            return;
        }
        
        DatagramPacket dataPacket = createDatagramPacket();
        if (dataPacket == null) {
            System.out.println("Unable to create datagram packet. Returning ...");
            return;
        }
        
        byte[] ackBuffer = new byte[11256];
        boolean ackReceived = false;
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
        
        while (!ackReceived) {
            // System.out.println(new Date() + " : Sending data with sequence number: " + sequenceNumber);
            try {
                socket.send(dataPacket);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception while sending data packet", e);
                return;
            }

            try {
                socket.receive(ackPacket);
            } catch (SocketTimeoutException e) {
                //System.out.println(new Date() + " : Timed out after waiting for " + receiver.getTimeoutInMSec() + " milliseconds. Retrying");
                continue;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception while recieving ack packet", e);
                return;
            }
            
            ackReceived = isCorrectACKReceived(ackPacket);
        }
        socket.close();
    }
    
    private DatagramPacket createDatagramPacket() {
        // TODO: Create checksum
        String checkSum = Checksum.create(data);
        Header header = new Header(getSequenceNumber(), checkSum, type.DATA, isEOF);
        Datagram datagram = new Datagram(header, data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        DatagramPacket packet = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(datagram);
            oos.flush();
            byte[] buf = baos.toByteArray();
            packet = new DatagramPacket(buf, buf.length, receiver.getReceiverAddr(), receiver.getReceiverPort());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating ObjectOutputStream", e);
            return null;
        } finally { 
            try {
                oos.close();
                baos.close();
            } catch (Exception e) {
                System.out.println("Exception in finally block: " + e.getMessage());
            }       
        }
        return packet;
    }

    private boolean isCorrectACKReceived(DatagramPacket ackPacket) {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(ackPacket.getData());
        ObjectInputStream ois = null;
        boolean ackReceived = false;
        try {
            ois = new ObjectInputStream(bais);
            Datagram ack = (Datagram) ois.readObject();
            if (ack.getHeader().getDatagramType() == type.ACK 
                    && ack.getHeader().getSequenceNumber() == getSequenceNumber()) {
                ackReceived = true;
            } else {
                System.out.println("Correct ACK not received!");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating ObjectInputStream", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Exception while creating ack object", e);
        } finally { 
            try {
                ois.close();
                bais.close();
            } catch (Exception e) {
                System.out.println("Exception in finally block: " + e.getMessage());
            }       
        }
        return ackReceived;
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
