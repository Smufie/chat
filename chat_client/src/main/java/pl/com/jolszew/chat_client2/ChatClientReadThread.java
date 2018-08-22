package pl.com.jolszew.chat_client2;

import java.io.DataInputStream;
import java.io.IOException;

public class ChatClientReadThread extends Thread {

	private DataInputStream din;
	private ChatListener listener;

	public ChatClientReadThread(DataInputStream din, ChatListener listener) {
		this.din = din;
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String message = din.readUTF();
				listener.messageArrived(message);
			}
		} catch (IOException e) {
			listener.error(e.getMessage());
		}

	}

	public void stopListening() {
		try {
			din.close();
		} catch (IOException e) {
			listener.error(e.getMessage());
		}
	}

}
