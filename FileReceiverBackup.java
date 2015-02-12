// Author: Chai Ming Xuan

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.net.DatagramSocket;

class FileReceiverBackup {

    public DatagramSocket socket;
    public DatagramPacket pkt;

    public static void main(String[] args) throws Exception {

        // check if the number of command line argument is 1
        if (args.length != 1) {
            System.out.println("Usage: java FileReceiver port");
            System.exit(1);
        }

        new FileReceiverBackup(args[0]); // args[0] is port number
    }

    public FileReceiverBackup(String localPort) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(localPort));
        byte[] rcvBuffer = new byte[1000];

        System.out.println("running on port " + localPort);

        String fileName = getFileName(serverSocket);

        FileOutputStream fos = new FileOutputStream(fileName, false);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        int counter = 0;

        while (true){
            DatagramPacket receivedPacket = new DatagramPacket(rcvBuffer, rcvBuffer.length);
            serverSocket.receive(receivedPacket);
            System.out.println("received packet" + counter);
            counter++;

            if (isEmptyPacket(receivedPacket)){
                System.out.println("packet length: " + receivedPacket.getLength());
                System.exit(1);
            } else {
                bos.write(receivedPacket.getData(), 0, receivedPacket.getLength());
                bos.flush();
            }
        }
    }

    private String getFileName(DatagramSocket serverSocket){
        byte[] buffer = new byte[1000];
        DatagramPacket fileNamePkt = new DatagramPacket(buffer, buffer.length);
        try {
            serverSocket.receive(fileNamePkt);
            return new String(fileNamePkt.getData(), 0, fileNamePkt.getLength()); //note the constructor difference
        } catch (Exception e){
            System.out.println("File name is not received");
            return null;
        }
    }

    private boolean isEmptyPacket(DatagramPacket pkt){
        if (pkt.getLength() == 0){
            return true;
        }

        return false;
    }

    // possibly send an empty packet to signal the end of file transfer
    // java.nio has byte buffer
}
