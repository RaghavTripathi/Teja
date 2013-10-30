package edu.ncsu.ip.teja.reciever.main;



import edu.ncsu.ip.teja.reciever.server.P2mpServer;
import edu.ncsu.ip.teja.reciever.utils.Utils;

public class Initialize {


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String rex = "[0-9]+";
        
        System.out.println("The IP address is :"+ Utils.getLocalIpAddress());
        
        if (args.length != 3){
            System.out.println("Usage : java -jar reciever.jav port# <output_file> <p>[0-1]");
            System.exit(0);
        }
        
        double prob = Double.parseDouble(args[2]);
        if (!args[0].matches(rex) || prob <0 || prob >1 ){
            System.out.println("Usage : java -jar reciever.jav port# <output_file> <p>[0-1]");
            System.exit(0);
        }
        
        
        P2mpServer p2mpser = new P2mpServer(Integer.parseInt(args[0]), args[1], prob);
        p2mpser.init();
    }

}
