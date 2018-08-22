package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class AlertCommandHandler implements CommandHandler {

	@Override
	public void handleCommand(String message, ServerThread serverThread) {
		String alert = message.substring(7, message.length());
		serverThread.getServer().sendAlert(alert, serverThread.getSenderNickname());
		
	}



}
