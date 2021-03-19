package com.plusplusnt.application.receiver.converter;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class Converter {
	public static byte[] convertIntArrayToByteArray(int[] intArray) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);
		return byteBuffer.array();
	}

	public static int readAndConvertToIntNextFourBytes(DataInputStream dataInputStream) throws IOException {
		return Integer.reverseBytes(dataInputStream.readInt());

	}
}
