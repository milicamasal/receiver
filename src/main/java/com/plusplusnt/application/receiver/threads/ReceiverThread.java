package com.plusplusnt.application.receiver.threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.plusplusnt.application.receiver.converter.Converter;

public class ReceiverThread implements Runnable {
	private static ReceiverThread instance;
	private List<int[]> unsentPackets;
	private Socket socket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private static final Logger logger = LogManager.getLogger(ReceiverThread.class);

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataInputStream getDataInputStream() {
		return dataInputStream;
	}

	public void setDataInputStream(DataInputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
	}

	public DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}

	public void setDataOutputStream(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	public List<int[]> getUnsentPackets() {
		return unsentPackets;
	}

	public void setUnsentPackets(List<int[]> packets) {
		this.unsentPackets = packets;
	}

	private ReceiverThread() {
		this.unsentPackets = new ArrayList<int[]>();
	}

	public void attach(int[] packet) {
		unsentPackets.add(packet);

	}

	public void detach(int[] packet) {
		unsentPackets.remove(packet);

	}

	public synchronized static ReceiverThread getInstance() {
		if (instance == null)
			instance = new ReceiverThread();
		return instance;

	}

	public void run() {
		try {
			connectToServer();
			notifyServerOfUnsentPackets();

			while (true) {
				int[] packet = receivePacket();
				handleReceivedPacket(packet);

			}
		} catch (UnknownHostException e) {
			logger.error("Unexpected error  " + e.getMessage());
		} catch (IOException e) {
			logger.info("Socket is closed");
		}

	}

	private void notifyServerOfUnsentPackets() {
		new Thread(new CancelThread()).start();
	}

	private void handleReceivedPacket(int[] packet) {
		SenderThread senderThread = new SenderThread(this, packet);
		new Thread(senderThread).start();
	}

	private int[] receivePacket() throws IOException {
		int packetID = Converter.readAndConvertToIntNextFourBytes(getDataInputStream());
		logger.info("Received packet with header " + packetID);
		int bufferSize = assignBufferSize(packetID);

		int[] packet = new int[bufferSize];
		packet[0] = packetID;
		readBytesFromPacket(bufferSize, packet);
		logger.info("Received packet " + Arrays.toString(packet));
		return packet;
	}

	private void connectToServer() throws UnknownHostException, IOException {
		Properties properties = new Properties();
		properties.load(ReceiverThread.class.getClassLoader().getResourceAsStream("config.properties"));
		int serverPort = Integer.parseInt(properties.getProperty("serverPort"));
		InetAddress host = InetAddress.getByName(properties.getProperty("host"));

		setSocket(new Socket(host, serverPort));
		logger.info("Connecting to server on port " + socket.getRemoteSocketAddress());
		setDataInputStream(new DataInputStream(socket.getInputStream()));
		setDataOutputStream(new DataOutputStream(socket.getOutputStream()));
	}

	private int assignBufferSize(int packetID) {
		int bufferSize = 0;
		if (packetID == 1)
			bufferSize = 4;
		else if (packetID == 2)
			bufferSize = 3;
		return bufferSize;
	}

	private int[] readBytesFromPacket(int bufferSize, int[] packet) throws IOException {
		for (int i = 1; i < bufferSize; i++) {
			packet[i] = Converter.readAndConvertToIntNextFourBytes(getDataInputStream());
		}
		return packet;
	}

}
