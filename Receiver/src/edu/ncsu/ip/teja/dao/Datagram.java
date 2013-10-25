package edu.ncsu.ip.teja.dao;

public class Datagram {
    
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
