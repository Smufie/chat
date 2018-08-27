package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class AddCommandHandler implements CommandHandler{

	public void handleCommand(String message, ServerThread serverThread) {
		String nickname = message.substring(5, message.length());
		serverThread.getServer().addUserToDatabase(nickname, serverThread.getThreadID());
		
	}

}
