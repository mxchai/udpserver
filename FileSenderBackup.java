// Author: Chai Ming Xuan

import java.net.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * You may assume that underlying transmission channel is perfect and all data will be received in good order.
 *
 */

class FileSenderBackup {

    public DatagramSocket socket;
    public DatagramPacket pkt;

    public static void main(String[] args) throws Exception {

        // check if the number of command line argument is 4
        if (args.length != 4) {
            System.out.println("Usage: java FileSender <path/filename> "
                    + "<rcvHostName> <rcvPort> <rcvFileName>");
            System.exit(1);
        }

        new FileSender(args[0], args[1], args[2], args[3]);
    }

    public FileSenderBackup(String fileToOpen, String host, String port, String rcvFileName) throws Exception {
        // Declarations
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(host);
        int portNumber = Integer.parseInt(port);

        // Opening the file
        byte[] buffer = new byte[1000];

        // File input
        FileInputStream fis = new FileInputStream(fileToOpen);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // Send filename packet
        byte[] fileName = rcvFileName.getBytes();
        DatagramPacket fileNamePkt = new DatagramPacket(fileName, fileName.length, serverAddress, portNumber);
        clientSocket.send(fileNamePkt);

        // Ok the read statement does have side effect of filling the buffer.
        // bis.read(buffer) returns an int in the range of 0 to 255, which I assume is the packet length
        int counter = 0;
        int len;
        while ((len = bis.read(buffer)) > 0) {
            DatagramPacket sendPkt = new DatagramPacket(buffer, len, serverAddress, portNumber);
            clientSocket.send(sendPkt);
            Thread.sleep(5);
            System.out.println("packet sent " + counter + ", packet length: " + len);
            counter++;
        }
        bis.close();

        DatagramPacket emptyPkt = new DatagramPacket(buffer, 0, serverAddress, portNumber);
        clientSocket.send(emptyPkt);

//        for (int i = 0; i < 4; i++){
//            bis.read(buffer);
//            DatagramPacket sendPkt = new DatagramPacket(buffer, buffer.length, serverAddress, portNumber);
//            clientSocket.send(sendPkt);
//        }
//        bis.close();


        // UDP transmission is unreliable. Sender may overrun
        // receiver if sending too fast, giving packet lost as a result.
        // In that sense, sender may need to pause once in a while.
        // E.g. Thread.sleep(1); // pause for 1 millisecond

        /*
        // File output and writing (shouldn't be here)
        FileOutputStream fos = new FileOutputStream(rcvFileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        // Need to change this such that the read bits are sent over UDP 1000 bits at a time
        // instead of writing it to bos straightaway
        int len;
        while ((len = bis.read(buffer)) > 0){
            bos.write(buffer, 0, len);
        }

        bis.close();
        bos.close();*/
    }
}
