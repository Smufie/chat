package pl.com.jolszew.chat_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.com.jolszew.chat_server.commandHandlers.AddCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.AlertCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.AllCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.CommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.CommandsCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.DeleteCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.IgnoreCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.LoginCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.LogoutCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.OnlineCommandHandler;
import pl.com.jolszew.chat_server.commandHandlers.PrivateCommandHandler;

public class ServerThread extends Thread {
	protected Socket socket;
	public static int PORT = 1200;
	private Server server;
	private DataOutputStream dout;
	private DataInputStream din;
	private final int threadID;
	protected String senderNickname;
	private HashMap<String, CommandHandler> hmap = new HashMap<String, CommandHandler>();
	final static Logger logger = LogManager.getLogger(ServerThread.class);
	
	public ServerThread(Server server, Socket socket, int threadID) {
		this.server = server;
		this.socket = socket;
		this.threadID = threadID;
		fillHashMapWithCommandHandlers();
	}



	public void fillHashMapWithCommandHandlers() {
		hmap.put("/login", new LoginCommandHandler());
		hmap.put("/logout", new LogoutCommandHandler());
		hmap.put("/all", new AllCommandHandler());
		hmap.put("/priv", new PrivateCommandHandler());
		hmap.put("/online", new OnlineCommandHandler());
		hmap.put("/commands", new CommandsCommandHandler());
		hmap.put("/alert", new AlertCommandHandler());
		hmap.put("", new IgnoreCommandHandler());
		hmap.put("/add", new AddCommandHandler());
		hmap.put("/delete", new DeleteCommandHandler());
	}
	
	
	
	@Override
	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			while (true) {
				String message = din.readUTF();
				String command = getCommand(message);
				boolean isCommandValid = hmap.containsKey(command);
				if (isCommandValid) {
					CommandHandler commandHandler = hmap.get(command);
					commandHandler.handleCommand(message, this);					
				} else {
					server.displayCommandError(senderNickname);					
				}
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
			
		} catch (Throwable e) {
			
			
		}
	}

	public void send(String messageFromClient) {
		try {
			dout.writeUTF(messageFromClient);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server getServer() {
		return server;
	}

	public int getThreadID() {
		return threadID;
	}

	public String getSenderNickname() {
		return senderNickname;
	}

	public static String getCommand(String text) {
	    int index = text.indexOf(' ');
	    if (index > -1) { 
	      return text.substring(0, index); 
	    } else {
	      return text; 
	    }
	  }
}