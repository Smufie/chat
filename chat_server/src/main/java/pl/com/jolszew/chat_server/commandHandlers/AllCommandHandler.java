package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class AllCommandHandler implements CommandHandler {

	@Override
	public void handleCommand(String message, ServerThread serverThread) {
		String allMessage = message.substring(5, message.length());
		serverThread.getServer().allMessage(allMessage, serverThread.getSenderNickname());

	}

}
