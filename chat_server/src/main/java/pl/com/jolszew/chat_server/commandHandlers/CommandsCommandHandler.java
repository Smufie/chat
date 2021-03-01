package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class CommandsCommandHandler implements CommandHandler{

	@Override
	public void handleCommand(String message, ServerThread serverThread) {
		serverThread.getServer().commandsRequest(serverThread.getSenderNickname());
		
	}

}
