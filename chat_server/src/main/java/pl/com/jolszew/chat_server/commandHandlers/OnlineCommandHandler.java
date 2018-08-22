package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class OnlineCommandHandler implements CommandHandler {

	@Override
	public void handleCommand(String message, ServerThread serverThread) {
		serverThread.getServer().onlineUsersRequest(serverThread.getSenderNickname());

	}

}
