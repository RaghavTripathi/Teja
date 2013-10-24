package edu.ncsu.ip.teja.reciever.server;

public class P2mpServer {
 
    int portNumber;
    String fileName;
    float lossProbability;
    
      
    
    public P2mpServer(int portNumber, String fileName, float lossProbability) {
        super();
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.lossProbability = lossProbability;
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
    public float getLossProbability() {
        return lossProbability;
    }
    public void setLossProbability(float lossProbability) {
        this.lossProbability = lossProbability;
    }
    
    public void init() {
        
        
    }
    
    
}
