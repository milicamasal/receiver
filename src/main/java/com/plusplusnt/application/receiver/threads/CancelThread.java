package com.plusplusnt.application.receiver.threads;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CancelThread implements Runnable {
	private static final Logger logger = LogManager.getLogger(CancelThread.class);

	public void run() {
		Iterator<int[]> iterator = ReceiverThread.getInstance().getUnsentPackets().iterator();

		while (iterator.hasNext()) {
			int[] packet = iterator.next();
			try {
				notifyServerOfUnsentPacket(iterator, packet);
			} catch (SocketException e) {
				logger.error("Socket closed while attemping to return unsent packet " + Arrays.toString(packet));
			} catch (IOException e) {
				logger.error("Unexpected error  " + e.getMessage());
			}

		}

	}

	private void notifyServerOfUnsentPacket(Iterator<int[]> iterator, int[] packet) throws IOException {
		logger.info("Attemping to return unsent packet " + Arrays.toString(packet));
		ReceiverThread.getInstance().getDataOutputStream()
				.writeUTF("Delay time for packet " + Arrays.toString(packet) + " has expired!");
		logger.info("Server is successfully notified of unsent packet " + Arrays.toString(packet));
		iterator.remove();
	}
}