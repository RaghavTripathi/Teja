package edu.ncsu.ip.teja.sender.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ncsu.ip.teja.sender.transport.ReliableDataTransfer;

public class P2MPClient {
    
    private static Logger LOGGER = Logger.getLogger(P2MPClient.class.getSimpleName());
    
    private final List<Receiver> receiverList;
    private final String filename;
    private final int mss;
    
    public P2MPClient(List<Receiver> receiverList, String filename, int mss) {
        super();
        this.receiverList = receiverList;
        this.filename = filename;
        this.mss = mss;
    }
    
    public void init() {
        byte[] fileContent = readBytesFromFile();
        int filePointer = 0;
        int sequenceNumber = 0;
        
        while (filePointer <= fileContent.length) {
            int end;
            List<Thread> rdtThreadList = new LinkedList<Thread>();
            if (filePointer + getMss() > fileContent.length) {
                end = fileContent.length;
            } else {
                end = filePointer + getMss();
            }
            
            byte[] data = Arrays.copyOfRange(fileContent, filePointer, end);
            filePointer = filePointer + getMss();
            
            for(Receiver receiver : receiverList) {
                ReliableDataTransfer rdtThread = new ReliableDataTransfer(receiver, data, sequenceNumber++);
                rdtThreadList.add(new Thread(rdtThread));
            }   
            
            for (Thread rdtThread : rdtThreadList) {
                rdtThread.start();
            }
            
            for (Thread rdtThread : rdtThreadList) {
                try {
                    rdtThread.join();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Interrupted while waiting for RDT thread to finish", e);
                }
            }
        }
    }
    
    private byte[] readBytesFromFile() {
        RandomAccessFile f = null;
        byte[] b = null;
        try {
            f = new RandomAccessFile(getFilename(), "r");
            b = new byte[(int)f.length()];
            f.read(b);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException while reading file", e);
        } finally {
            try {
                f.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IOException closing file", e);
            }
        }
        
        return b;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public String getFilename() {
        return filename;
    }

    public int getMss() {
        return mss;
    }
}
