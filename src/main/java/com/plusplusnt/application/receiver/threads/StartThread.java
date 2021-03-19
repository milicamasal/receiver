package com.plusplusnt.application.receiver.threads;

import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartThread implements Runnable {
	private static ReceiverThread receiverThread = ReceiverThread.getInstance();
	private static final Logger logger = LogManager.getLogger(StartThread.class);
	private boolean activeApplication = true;

	public boolean isActiveApplication() {
		return activeApplication;
	}

	public void setActiveApplication(boolean activeApplication) {
		this.activeApplication = activeApplication;
	}

	public void run() {
		startReceivingPackets();

		while (true) {
			Scanner scanner = new Scanner(System.in);
			String userInput = "";
			if (isActiveApplication()) {
				System.out.println("Enter stop in order to stop the application");
				userInput = scanner.nextLine();
				if (userInput.equals("stop")) {
					stopReceivingPackets();
					setActiveApplication(false);
				}
			}
			if (!isActiveApplication()) {
				System.out.println("Enter start in order to start the application");
				userInput = scanner.nextLine();
				if (userInput.equals("start")) {
					startReceivingPackets();
					setActiveApplication(true);
				}
			}
		}
	}

	private void stopReceivingPackets() {
		try {
			receiverThread.getSocket().close();
			receiverThread.getDataInputStream().close();
			receiverThread.getDataOutputStream().close();
			logger.info("Stopped receiving packets from the server");
		} catch (IOException e) {
			logger.error("Unexpected exception: " + e.getMessage());
		}
	}

	private void startReceivingPackets() {
		new Thread(receiverThread).start();
		logger.info("Started receiving packets from the server");
	}
}
