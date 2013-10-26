package edu.ncsu.ip.teja.sender.client;

import java.net.InetAddress;

public final class Receiver {

    private final InetAddress receiverAddr;
    private final int receiverPort;
    private final int timeoutInMSec;
    
    public Receiver(InetAddress receiverAddr, int receiverPort, int timeout) {
        super();
        this.receiverAddr = receiverAddr;
        this.receiverPort = receiverPort;
        this.timeoutInMSec = timeout;
    }

    public InetAddress getReceiverAddr() {
        return receiverAddr;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public int getTimeoutInMSec() {
        return timeoutInMSec;
    }
}
