package pl.com.jolszew.chat_server.commandHandlers;

import pl.com.jolszew.chat_server.ServerThread;

public class LogoutCommandHandler implements CommandHandler{

	public void handleCommand(String message, ServerThread serverThread) {
		String nickname = message.substring(8, message.length());
		serverThread.getServer().userLogout(nickname);
		serverThread.getServer().eraseNameFromServer(nickname);
		
	}

}
