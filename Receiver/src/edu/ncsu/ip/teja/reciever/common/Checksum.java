package edu.ncsu.ip.teja.reciever.common;


public class Checksum {

    public static String create(byte[] data) {
        
        int length = data.length;        
        long FF00 = 0xff00;
        long FF = 0xff;
        long sum = 0, tmp;
        int i = 0;
        
        while (length > 1) {
            tmp = (((data[i] << 8) & FF00) | ((data[i + 1]) & FF));
            sum += tmp;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
            i += 2;
            length -= 2;
        }

        if (length > 0) {
            sum += (data[i] << 8 & 0xFF00);
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }

        long checksum = ~sum;
        checksum = checksum & 0xFFFF;
        return Long.toBinaryString(checksum);
    }
}
