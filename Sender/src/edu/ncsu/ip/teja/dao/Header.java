package edu.ncsu.ip.teja.dao;

import java.io.Serializable;

public class Header implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int sequenceNumber;
    private final String checksum;
    private final type datagramType;
    private final boolean isEOF;
    public static enum type {DATA, ACK};
    
    public Header(int sequenceNumber, String checksum, type datagramType, boolean isEOF) {
        super();
        this.sequenceNumber = sequenceNumber;
        this.checksum = checksum;
        this.datagramType = datagramType;
        this.isEOF = isEOF;
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

    public boolean isEOF() {
        return isEOF;
    }
}
