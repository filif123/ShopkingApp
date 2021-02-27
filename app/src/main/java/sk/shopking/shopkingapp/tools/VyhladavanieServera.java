package sk.shopking.shopkingapp.tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class VyhladavanieServera {

    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    public VyhladavanieServera() throws Exception {
        this.address = InetAddress.getByName("255.255.255.255");
    }

    public String discoverServer(String msg) throws IOException {
        initializeSocketForBroadcasting();
        copyMessageOnBuffer(msg);

        for (InetAddress inetAddress : listAllBroadcastAddresses()) {
            broadcastPacket(inetAddress);
        }
        // When we want to broadcast not just to local network, call listAllBroadcastAddresses() and execute broadcastPacket for each value.
        //broadcastPacket(address);

        return receivePacket();
    }

    public List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();

            for (InterfaceAddress address : interfaceAddresses) {
                if (address.getBroadcast() != null){
                    broadcastList.add(address.getBroadcast());
                }
            }

        }
        return broadcastList;
    }

    private void initializeSocketForBroadcasting() throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
    }

    private void copyMessageOnBuffer(String msg) {
        buf = msg.getBytes();
    }

    private void broadcastPacket(InetAddress address) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
    }

    private String receivePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(2000);
        try{
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            return address.getHostAddress();
        }catch (SocketTimeoutException s){
            return null;
        }
    }

    public void close() {
        socket.close();
    }
}
