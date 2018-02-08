package sample;
import java.io.*;
import java.net.*;

public class udpListener implements Runnable{
    private static final int PORT = 8018;
    private static final int PACKET_SIZE = 8;
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
                            case (byte)0x640:
                                buffer1 = packet.getData();
                                break;
                            case (byte)0x641:
                                buffer2 = packet.getData();
                                break;
                            case (byte)0x642:
                                buffer3 = packet.getData();
                                break;
                            case (byte)0x643:
                                buffer4 = packet.getData();
                                break;
                            case (byte)0x644:
                                buffer5 = packet.getData();
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
