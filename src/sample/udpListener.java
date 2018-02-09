package sample;
import java.io.*;
import java.net.*;

public class udpListener implements Runnable{
    public static Boolean received = false;
    private static final int PORT = 8018;
    private static final int PACKET_SIZE = 9;
    private byte[] data = new byte[PACKET_SIZE];
    private byte[] buffer1 = new byte[PACKET_SIZE];
    private byte[] buffer2 = new byte[PACKET_SIZE];
    private byte[] buffer3 = new byte[PACKET_SIZE];
    private byte[] buffer4 = new byte[PACKET_SIZE];
    private byte[] buffer5 = new byte[PACKET_SIZE];
    public static Object lock = new Object();

    @Override
    public void run() {
        while (true) {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(PORT);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    synchronized (lock) {
                        switch (packet.getData()[0]) {
                            case 0x40:
                                System.arraycopy(packet.getData(),0,buffer1,0,PACKET_SIZE);
                                break;
                            case 0x41:
                                System.arraycopy(packet.getData(),0,buffer2,0,PACKET_SIZE);
                                break;
                            case 0x42:
                                System.arraycopy(packet.getData(),0,buffer3,0,PACKET_SIZE);
                                break;
                            case 0x43:
                                System.arraycopy(packet.getData(),0,buffer4,0,PACKET_SIZE);
                                break;
                            case 0x44:
                                System.arraycopy(packet.getData(),0,buffer5,0,PACKET_SIZE);
                                break;
                            default:
                                break;
                        }
                    }
                }

            } catch (Exception e) {
                if (e.equals(new SocketTimeoutException())) {
                    socket.close();
                }
            }
        }
    }

    public byte[] getBuffer1() {
        return buffer1;
    }
    public byte[] getBuffer2() {
        return buffer2;
    }
    public byte[] getBuffer3() {
        return buffer3;
    }
    public byte[] getBuffer4() {
        return buffer4;
    }
    public byte[] getBuffer5() {
        return buffer5;
    }


}
