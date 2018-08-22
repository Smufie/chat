package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class LoginCommandHandler implements CommandHandler{

	public void handleCommand(String message, ServerThread serverThread) {
		String nickname = message.substring(7, message.length());
		serverThread.getServer().nameAlreadyExist(nickname, serverThread.getThreadID());		
	}

}
