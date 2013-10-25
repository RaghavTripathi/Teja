package edu.ncsu.ip.teja.reciever.main;



import edu.ncsu.ip.teja.reciever.server.P2mpServer;
import edu.ncsu.ip.teja.reciever.utils.Utils;

public class Initialize {


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("The IP address is :"+ Utils.getLocalIpAddress());
        
        P2mpServer p2mpser = new P2mpServer(Integer.parseInt(args[0]), args[1], Double.parseDouble(args[2]));
        p2mpser.init();
    }

}
