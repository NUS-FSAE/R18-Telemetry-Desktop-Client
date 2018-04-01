package sample;
import java.io.*;
import java.net.*;
import java.util.TimerTask;

public class udpListener extends TimerTask {
    private static final int PORT = 8018;
    private static final int PACKET_SIZE = 10;
    private static boolean buffer1_received = false;
    private static boolean buffer2_received = false;
    private static boolean buffer3_received = false;
    private static boolean buffer4_received = false;
    private static boolean buffer5_received = false;
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
                                buffer1_received = true;
                                break;
                            case 0x41:
                                System.arraycopy(packet.getData(),0,buffer2,0,PACKET_SIZE);
                                buffer2_received = true;
                                break;
                            case 0x42:
                                System.arraycopy(packet.getData(),0,buffer3,0,PACKET_SIZE);
                                buffer3_received = true;
                                break;
                            case 0x43:
                                System.arraycopy(packet.getData(),0,buffer4,0,PACKET_SIZE);
                                buffer4_received = true;
                                break;
                            case 0x44:
                                System.arraycopy(packet.getData(),0,buffer5,0,PACKET_SIZE);
                                buffer5_received = true;
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

    public boolean buffer1_isReceived() { return buffer1_received; }
    public boolean buffer2_isReceived() { return buffer2_received; }
    public boolean buffer3_isReceived() { return buffer3_received; }
    public boolean buffer4_isReceived() { return buffer4_received; }
    public boolean buffer5_isReceived() { return buffer5_received; }

    public void setBuffer1_read() { buffer1_received = false; }
    public void setBuffer2_read() { buffer2_received = false; }
    public void setBuffer3_read() { buffer3_received = false; }
    public void setBuffer4_read() { buffer4_received = false; }
    public void setBuffer5_read() { buffer5_received = false; }


}
