package edu.ncsu.ip.teja.sender.client;

import java.net.InetAddress;

public final class Receiver {

    private final InetAddress receiverAddr;
    private final int receiverPort;
    private final int timeoutInSec;
    private boolean ackReceived;
    
    public Receiver(InetAddress receiverAddr, int receiverPort, int timeout,
            boolean ackReceived) {
        super();
        this.receiverAddr = receiverAddr;
        this.receiverPort = receiverPort;
        this.timeoutInSec = timeout;
        this.ackReceived = ackReceived;
    }

    public boolean isAckReceived() {
        return ackReceived;
    }

    public void setAckReceived(boolean ackReceived) {
        this.ackReceived = ackReceived;
    }

    public InetAddress getReceiverAddr() {
        return receiverAddr;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public int getTimeoutInSec() {
        return timeoutInSec;
    }
}
