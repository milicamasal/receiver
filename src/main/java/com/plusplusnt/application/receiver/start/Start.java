package com.plusplusnt.application.receiver.start;

import com.plusplusnt.application.receiver.threads.StartThread;

public class Start {

	public static void main(String[] args) {
		new Thread(new StartThread()).start();
	}
}
