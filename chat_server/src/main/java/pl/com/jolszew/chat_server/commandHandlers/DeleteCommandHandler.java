package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class DeleteCommandHandler implements CommandHandler{

	public void handleCommand(String message, ServerThread serverThread) {
		String nickname = message.substring(8, message.length());
		serverThread.getServer().deleteUserFromDatabase(nickname, serverThread.getThreadID());
		
	}

}
