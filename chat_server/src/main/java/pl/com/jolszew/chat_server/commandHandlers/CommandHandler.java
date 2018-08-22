package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public interface CommandHandler {
	public void handleCommand (String message, ServerThread serverThread);
}
