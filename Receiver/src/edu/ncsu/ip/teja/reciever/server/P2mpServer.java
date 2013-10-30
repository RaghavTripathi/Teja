package edu.ncsu.ip.teja.reciever.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;

import edu.ncsu.ip.teja.dao.Datagram;
import edu.ncsu.ip.teja.dao.Header;
import edu.ncsu.ip.teja.dao.Header.type;
import edu.ncsu.ip.teja.reciever.common.Checksum;

public class P2mpServer {
 
    int portNumber;
    String fileName;
    double lossProbability;
    protected DatagramSocket socket = null;
    int lastSequenceNumber;
      
    
    public P2mpServer(int portNumber, String fileName, double lossProbability) {
        super();
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.lossProbability = lossProbability;
        lastSequenceNumber = -1;
    }
    public int getPortNumber() {
        return portNumber;
    }
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public double getLossProbability() {
        return lossProbability;
    }
    public void setLossProbability(float lossProbability) {
        this.lossProbability = lossProbability;
    }
    
    public int getLastSequenceNumber() {
        return lastSequenceNumber;
    }
    public void setLastSequenceNumber(int lastSequenceNumber) {
        this.lastSequenceNumber = lastSequenceNumber;
    }
    public void init() {
        try {
            
            boolean check = true;
            FileOutputStream fos = new FileOutputStream(new File(getFileName()));
            
            while (check){
                   
                socket = new DatagramSocket(getPortNumber());
                byte[] buf = new byte[11256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                // convert this byte stream to object thing
                ByteArrayInputStream baos = new ByteArrayInputStream(buf);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Datagram datagram = (Datagram) oos.readObject();
                int presentSequenceNumber = (datagram.getHeader()).getSequenceNumber();
                String checksum =  (datagram.getHeader()).getChecksum();
                
                if(checkPacketDrop(getLossProbability())){
                
                   System.out.println("Packet drop ,"+presentSequenceNumber);
        
                } else if (checksum.equalsIgnoreCase(Checksum.create(datagram.getData()))){
                                  
                    sendAck(socket,presentSequenceNumber, packet.getAddress(), packet.getPort());
                    
                } else  if (checkValidSequenceNumber(presentSequenceNumber)){
                    // WRITE TO A FILE
                    writeByteArrayToFile(fos, datagram.getData());

                    // SEND AN ACK BACK
                    sendAck(socket,presentSequenceNumber, packet.getAddress(), packet.getPort());
                    if (datagram.getHeader().isEOF()){
                            check = false;
                    }
                    
                } else {
                   
                    sendAck(socket,presentSequenceNumber, packet.getAddress(), packet.getPort());
                }
                socket.close(); 
            }  
                
            fos.close();
        } catch (SocketException e) {
              e.printStackTrace();
        } catch (IOException e) {
              e.printStackTrace();
        } catch (ClassNotFoundException e) {
              e.printStackTrace();
        }
        
    }
  
    public void sendAck(DatagramSocket socket,int seqNumber, InetAddress IPAddress, int port) {
      
        try {
            Header hd = new Header(seqNumber, "1010101010101010", type.ACK,false);
            Datagram dg = new Datagram(hd, null);
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            ObjectOutputStream objos = new ObjectOutputStream(ba);
            objos.writeObject(dg);
            objos.flush();
            byte[] buffer= ba.toByteArray();
            int bufferLen = buffer.length;;
            DatagramPacket packetSend = new DatagramPacket(buffer, bufferLen,IPAddress, port);
            socket.send(packetSend);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    public boolean checkPacketDrop(double value){
        //Random generator = new Random();
        //double num = generator.nextDouble();
        double num = Math.random();
        while( num == 0 ) {
            num = Math.random();
        }
        
        if (num <= value)
            return true;
        else
            return false;
    }
    
    public void writeByteArrayToFile( FileOutputStream fos, byte[] bos){
        
        try {
            fos.write(bos);
            fos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
    }
       
        
    }
    
    public boolean checkValidSequenceNumber(int presentSequenceNumber) {
        int previousSequenceNumber = getLastSequenceNumber();
       
        if (previousSequenceNumber == -1 || (previousSequenceNumber != presentSequenceNumber)) {
                setLastSequenceNumber(presentSequenceNumber);
                return true;
        } else{
                return false;
        }
    }
    
    
}
