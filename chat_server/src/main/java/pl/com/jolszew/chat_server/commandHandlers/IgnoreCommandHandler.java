package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class IgnoreCommandHandler implements CommandHandler{

	
	public void handleCommand(String message, ServerThread serverThread) {
		serverThread.getServer().ignore(serverThread.getSenderNickname());
		
	}

}
