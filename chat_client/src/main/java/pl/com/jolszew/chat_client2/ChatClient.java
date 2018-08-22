package pl.com.jolszew.chat_client2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 
 * 
 * This class is used to connect with server through Socket.
 * It also contains methods allowing you to login/logout from the chat.
 *
 */
public class ChatClient {

	private ChatListener listener;
	private DataOutputStream dout;
	private Socket s;
	private ChatClientReadThread readThread;

	public void addChatListener(ChatListener listener) {
		this.listener = listener;
	}

	public void connect(String ip, String port) {
		try {
			s = new Socket(ip, Integer.parseInt(port));
			dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());

			readThread = new ChatClientReadThread(din, listener);
			readThread.start();

			connectionSucces();
		} catch (Exception e) {
			error(e.getMessage());
		}

	}

	public void login(String nickname) {
		this.send("/login " + nickname);
	}

	public void logout(String nickname) {
		this.send("/logout " + nickname);
	}

	public void send(String message) {
		try {
			dout.writeUTF(message);
			dout.flush();
		} catch (Exception e) {
			error(e.getMessage());
		}
	}

	public void disconnect() {
		try {
			readThread.stopListening();
			s.close();
			connectionTerminated();
		} catch (IOException e) {
			error(e.getMessage());
		}
	}

	private void error(String message) {
		if (listener != null) {
			listener.error(message);
		}
	}

	private void connectionSucces() {
		if (listener != null) {
			listener.connectionEstablished();
		}
	}

	private void connectionTerminated() {
		if (listener != null) {
			listener.connectionClosed();
		}
	}
}
