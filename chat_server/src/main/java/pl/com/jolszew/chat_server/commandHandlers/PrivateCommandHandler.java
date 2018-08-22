package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class PrivateCommandHandler implements CommandHandler {

	@Override
	public void handleCommand(String message, ServerThread serverThread) {
		String direct_message = message.substring(6, message.length());
		serverThread.getServer().sendDirectMessageToReceiver(direct_message, serverThread.getSenderNickname());

	}

}
