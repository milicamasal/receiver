package com.plusplusnt.application.receiver.threads;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.plusplusnt.application.receiver.converter.Converter;

public class SenderThread implements Runnable {
	private ReceiverThread receiverThread;
	private int[] packet;
	private static final Logger logger = LogManager.getLogger(SenderThread.class);

	public SenderThread(ReceiverThread receiverThread, int[] packet) {
		super();
		this.receiverThread = receiverThread;
		this.packet = packet;
	}

	public ReceiverThread getReceiverThread() {
		return receiverThread;
	}

	public void setReceiverThread(ReceiverThread receiverThread) {
		this.receiverThread = receiverThread;
	}

	public int[] getPacket() {
		return packet;
	}

	public void setPacket(int[] packet) {
		this.packet = packet;
	}

	public void run() {
		try {
			attemptSendingPacket();

		} catch (SocketException e) {
			handleUnsentPacket();
		} catch (InterruptedException e) {
			logger.error("Unexpected error  " + e.getMessage());
		} catch (IOException e) {
			logger.error("Unexpected error  " + e.getMessage());
		}
	}

	private void handleUnsentPacket() {
		logger.warn("Unable to send packet " + Arrays.toString(packet) + " because socket is closed");
		receiverThread.attach(packet);
		logger.info("Unsent packet " + Arrays.toString(packet) + " added to the unsent list");
	}

	private void attemptSendingPacket() throws InterruptedException, IOException {
		synchronized (this) {
			wait(packet[packet.length - 1] * 1000);
			receiverThread.getDataOutputStream().write(Converter.convertIntArrayToByteArray(packet));
			logger.info("Sent " + Arrays.toString(packet));
		}
	}

}
