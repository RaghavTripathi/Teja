package edu.ncsu.ip.teja.sender.main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import edu.ncsu.ip.teja.sender.client.P2MPClient;
import edu.ncsu.ip.teja.sender.client.Receiver;

public class Initialize {
    
    public static void main(String[] args) {
        
        int numberOfReceivers = args.length - 4;            //server port number, filename, MSS, timeout
        if (numberOfReceivers <= 0) {
            System.out.println("Please pass atleast one receiver address as command line argument");
            System.out.println("Usage: p2mpclient server-1 server-2 server-3 server-port# file-name MSS timeoutInMilliSeconds");
            System.exit(1);
        }
        
        String receiverAddrs[] = new String[numberOfReceivers];   
        int i = 0;
        
        System.out.println("Number of receivers: " + numberOfReceivers);
        for (i = 0; i < numberOfReceivers; i++) {
            receiverAddrs[i] = args[i];
        }
        
        int receiverPort = Integer.parseInt(args[i++]);
        String filename = args[i++];
        int mss = Integer.parseInt(args[i++]);
        int timeout = Integer.parseInt(args[i++]);
        
        List<Receiver> receiverList = generateReceiverList(receiverAddrs, receiverPort, timeout);
        
        P2MPClient client = new P2MPClient(receiverList, filename, mss);
        client.init();
        System.out.println("p2mpclient finished. Exiting!");
        System.exit(1);
    }
    
    private static List<Receiver> generateReceiverList(String[] receiverAddrs, int receiverPort, int timeout) {
        
        List<Receiver> receiverList = new LinkedList<Receiver>();
        
        for(String receiverAddr : receiverAddrs) {
            Receiver receiver = null;
            try {
                receiver = new Receiver(InetAddress.getByName(receiverAddr),
                                                    receiverPort,
                                                    timeout);
            } catch (UnknownHostException e) {
                System.out.println("Receiver host " + receiverAddr + " not found");
                continue;
            }    
            receiverList.add(receiver);
        }
        return receiverList;
    }
}
