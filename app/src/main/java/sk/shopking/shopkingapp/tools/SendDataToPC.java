package sk.shopking.shopkingapp.tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendDataToPC {
    private DatagramSocket socket;
    private InetAddress address;

    public SendDataToPC(String addressString) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(addressString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SendDataToPC(InetAddress address) {
        try {
            socket = new DatagramSocket();
            this.address = address;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendData(String msg) {
        DatagramPacket packet = null;
        try {
            byte[] buf = msg.getBytes();
            packet = new DatagramPacket(buf, buf.length, address, 4446);
            socket.send(packet);
            //packet = new DatagramPacket(buf, buf.length);
            //socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String received = new String(packet.getData(), 0, packet.getLength());
        //return received;
    }

    public void close() {
        socket.close();
    }
}
