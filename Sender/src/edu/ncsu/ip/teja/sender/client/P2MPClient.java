package edu.ncsu.ip.teja.sender.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.ncsu.ip.teja.sender.transport.ReliableDataTransfer;

public class P2MPClient {
    
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
        
        if (fileContent == null) {
            return;
        }
        
        int filePointer = 0;
        int sequenceNumber = 0;
        boolean isEOF = false;
        
        while (filePointer <= fileContent.length) {
            
            int end;
            if (filePointer + getMss() > fileContent.length) {
                end = fileContent.length;
                isEOF = true;
            } else {
                end = filePointer + getMss();
                isEOF = false;
            }
            byte[] data = Arrays.copyOfRange(fileContent, filePointer, end);
            filePointer = filePointer + getMss();
            
            List<Thread> rdtThreadList = new LinkedList<Thread>();
            for(Receiver receiver : receiverList) {
                ReliableDataTransfer rdtThread = new ReliableDataTransfer(receiver, data, sequenceNumber, isEOF);
                rdtThreadList.add(new Thread(rdtThread));
            }
            
            for (Thread rdtThread : rdtThreadList) {
                rdtThread.start();
            }
            
            for (Thread rdtThread : rdtThreadList) {
                try {
                    rdtThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Current thread interrupted while waiting for RDT thread to finish: " + e.getMessage());
                }
            }
            
            sequenceNumber = sequenceNumber^1;
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
            System.out.println("File not found" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException while reading file" + e.getMessage());
        } finally {
            try {
                f.close();
            } catch (IOException e) {
                System.out.println("IOException closing file: " + e.getMessage());
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
