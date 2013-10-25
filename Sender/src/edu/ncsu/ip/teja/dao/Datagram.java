package edu.ncsu.ip.teja.dao;

import java.io.Serializable;

public class Datagram implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Header header;
    private final byte[] data;
    
    public Datagram(Header header, byte[] data) {
        super();
        this.header = header;
        this.data = data;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getData() {
        return data;
    }
}
