package edu.ncsu.ip.teja.dao;

import java.io.Serializable;

public class Header implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int sequenceNumber;
    private final String checksum;
    private final type datagramType;    
    public static enum type {DATA, ACK};
    
    public Header(int sequenceNumber, String checksum, type datagramType) {
        super();
        this.sequenceNumber = sequenceNumber;
        this.checksum = checksum;
        this.datagramType = datagramType;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getChecksum() {
        return checksum;
    }

    public type getDatagramType() {
        return datagramType;
    } 
}
