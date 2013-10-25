package edu.ncsu.ip.teja.sender.main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ncsu.ip.teja.sender.client.P2MPClient;
import edu.ncsu.ip.teja.sender.client.Receiver;

public class Initialize {

    private static Logger LOGGER = Logger.getLogger(Initialize.class.getSimpleName());
    
    public static void main(String[] args) {
        
        int numberOfReceivers = args.length - 3;            //server port number, filename, MSS
        if (numberOfReceivers <= 0) {
            System.out.println("Please pass atleast one receiver address as command line argument");
            System.out.println("Exiting!");
            System.exit(1);
        }
        
        String receiverAddrs[] = new String[numberOfReceivers];   
        int i = 0;
        
        LOGGER.info("Number of receivers: " + numberOfReceivers);
        for (i = 0; i < numberOfReceivers; i++) {
            receiverAddrs[i] = args[i];
        }
        
        int receiverPort = Integer.parseInt(args[i++]);
        String filename = args[i++];
        int mss = Integer.parseInt(args[i++]);
        
        List<Receiver> receiverList = generateReceiverList(receiverAddrs, receiverPort);
        
        P2MPClient client = new P2MPClient(receiverList, filename, mss);
        client.init();
        
        System.out.println("P2MPClient finished. Exiting!");
        System.exit(1);
    }
    
    private static List<Receiver> generateReceiverList(String[] receiverAddrs, int receiverPort) {
        int timeoutInSeconds = generateTimeoutFromRTT(receiverAddrs, receiverPort);
        List<Receiver> receiverList = new LinkedList<Receiver>();
        
        for(String receiverAddr : receiverAddrs) {
            Receiver receiver = null;
            try {
                receiver = new Receiver(InetAddress.getByName(receiverAddr),
                                                    receiverPort,
                                                    timeoutInSeconds,
                                                    true);
            } catch (UnknownHostException e) {
                LOGGER.log(Level.SEVERE, "Receiver host not found", e);
            }
            
            receiverList.add(receiver);
        }
        return receiverList;
    }
    
    /*
     * TODO: Generate timeout by sending packets to receiver and then calculate as 3*RTT
     */
    private static int generateTimeoutFromRTT(String[] receivers, int receiverPort) {
        return 5;
    }
}
