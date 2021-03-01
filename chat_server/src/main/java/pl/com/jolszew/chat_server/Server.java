package pl.com.jolszew.chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jan
 * 
 *         Server for my chat.
 *
 */
public class Server {

	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost:5432/jdbc";
	static final String USER = "postgres";
	private ArrayList<ServerThread> threads = new ArrayList<>();
	private int threadID = 0;
	final static Logger logger = LogManager.getLogger(Server.class);

	public Server() {
		ServerSocket ss = null;
		Socket socket = null;
		try {
			ss = new ServerSocket(ServerThread.PORT);

			while (true) {
				socket = ss.accept();

				logger.warn("Client connected : " + socket.getInetAddress() + ", " + socket.getPort());

				ServerThread serverThread = new ServerThread(this, socket, threadID);
				newClientConnection(serverThread);
				serverThread.start();

				threadID++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void newClientConnection(ServerThread serverThread) {
		threads.add(serverThread);
	}

//	private void removeClientConnection(ServerThread serverThread) {
//		threads.remove(serverThread);
//	}
	
	public static void main(String[] args) throws IOException {
		new Server();
	}

	public void allMessage(String messageToAll, String senderNickname) {
		for (ServerThread thread : threads) {
			thread.send(senderNickname + ": " + messageToAll + "\n");
		}
		logger.info("User " + senderNickname + " sent '" + messageToAll + "' to all users. ");
	}

	public void isLoginPossible(String nickname, int threadID) {
		boolean isUserWithThatNameOnline = true;
		boolean isUserOnServerDatabase = false;
		for (int i = 0; i < threads.size(); i++) {
			if (nickname.equals(threads.get(i).senderNickname)) {
				threads.get(threadID).send("(error) User with this name is already connected. \n");
				isUserWithThatNameOnline = false;
				logger.warn("Logging by user " + nickname + " unsuccessful.");
				break;
			}
		}
		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
		entityManager.clear();
		Query query = entityManager.createQuery("SELECT c FROM ChatUsers c");
		@SuppressWarnings("unchecked")
		List<ChatUsers> list = query.getResultList();
		for (ChatUsers l : list) {
			if (nickname.equals(l.getUsername())) {
				isUserOnServerDatabase = true;
			}
		}
		entityManager.close();

		if (!isUserOnServerDatabase) {
			threads.get(threadID).send("(error) You're not signed in. \n");
			logger.warn("Logging by user " + nickname + " unsuccessful.");
		}

		if (isUserWithThatNameOnline && isUserOnServerDatabase) {
			for (ServerThread thread : threads) {
				threads.get(threadID).senderNickname = nickname;
				thread.send("User '" + nickname + "' has logged in. \n");
				logger.warn("Logging by user " + nickname + " successful.");
			}
		}
	}

	public void eraseNameFromServer(String nickname) {
		for (int i = 0; i < threads.size(); i++) {
			if (nickname.equals(threads.get(i).senderNickname)) {
				threads.get(i).senderNickname = null;
			}
		}
	}

	public void sendDirectMessageToReceiver(String direct_message, String senderNickname) {
		int name_position = direct_message.indexOf(" ");
		String nickname = direct_message.substring(0, name_position);
		direct_message = direct_message.replace(nickname + " ", "");
		boolean isReceiverOnline = false;
		int memory = 0;
		logger.trace("User " + senderNickname + " tries to send message to user " + nickname + ". ");
		for (int i = 0; i < threads.size(); i++) {
			if (nickname.equals(threads.get(i).senderNickname)) {
				threads.get(i).send("(priv) " + senderNickname + ": " + direct_message + "\n");
				isReceiverOnline = true;
			}

			if (senderNickname.equals(threads.get(i).senderNickname)) {
				memory = i;
			}

		}
		if (!isReceiverOnline) {
			threads.get(memory).send("User '" + nickname + "' is not online. \n");
			logger.info("User " + senderNickname + " failed to send message to user " + nickname + ". ");
		} else {
			threads.get(memory).send("(sent to '" + nickname + "')" + ": " + direct_message + "\n");
			logger.info("User " + senderNickname + " sent '" + direct_message + "' to user " + nickname + ". ");
		}
	}

	public void sendAlert(String direct_message, String senderNickname) {
		String nickname = direct_message.replace("\n", "");
		int memory = 0;
		boolean isReceiverOnline = false;
		for (int i = 0; i < threads.size(); i++) {
			if (nickname.equals(threads.get(i).senderNickname)) {
				threads.get(i).send("(alert) '" + senderNickname + "' alerts you! \n");
				isReceiverOnline = true;
			}

			if (senderNickname.equals(threads.get(i).senderNickname)) {
				memory = i;
			}
		}
		if (isReceiverOnline) {
			threads.get(memory).send("You've alerted '" + nickname + "'! \n");
			logger.info("User " + senderNickname + " successfully alerted user " + nickname + ". ");
		} else {
			threads.get(memory).send("User '" + nickname + "' is not online.");
			logger.info("User " + senderNickname + " failed to alert user " + nickname + ". ");
		}
	}

	public void displayCommandError(String senderNickname) {
		logger.info("User " + senderNickname + " haven't used proper command. ");
		for (int i = 0; i < threads.size(); i++) {
			if (senderNickname.equals(threads.get(i).senderNickname)) {
				threads.get(i).send("Wrong command, type '/commands' to display all commands. \n");
			}
		}
	}

	public void commandsRequest(String senderNickname) {
		logger.trace("User " + senderNickname + " asked for commands. ");
		for (int i = 0; i < threads.size(); i++) {
			if (senderNickname.equals(threads.get(i).senderNickname)) {
				threads.get(i)
						.send("List of commands: \n" + "(/all) sends a message to all users \n"
								+ "(/online) shows list of all users, that are currently online \n"
								+ "(/priv nickname) sends direct message to user defined by nickname \n"
								+ "(/alert nickname) alerts user defined by nickname \n");
			}
		}
		logger.info("User " + senderNickname + " received list of commands. ");
	}

	public void onlineUsersRequest(String senderNickname) {
		logger.trace("User " + senderNickname + " asks for list of online users. ");
		for (int i = 0; i < threads.size(); i++) {
			if (senderNickname.equals(threads.get(i).senderNickname)) {
				threads.get(i).send("Users that are currently online: ");
				for (int y = 0; y < threads.size(); y++) {
					if (y != 0) {
						threads.get(i).send(", ");
					}
					threads.get(i).send(threads.get(y).senderNickname);
				}
				threads.get(i).send("\n");
			}
		}
		logger.info("User " + senderNickname + " received list of online users. ");
	}

	public void userLogout(String nickname) {
		for (ServerThread thread : threads) {
			thread.send("User " + nickname + " logged out. \n");
		}
		logger.warn("User " + nickname + " logged out. ");
	}

	public void ignore(String senderNickname) {
		logger.info("User " + senderNickname + " sent empty message.");

	}

	public void addUserToDatabase(String nickname, int threadID) {
		logger.info("Trying to add user " + nickname + ".");
		String senderNickname = threads.get(threadID).getSenderNickname();
		if (!senderNickname.equals("Janex")) {
			logger.error("User " + threads.get(threadID).getSenderNickname() + " tried to use command (add) without permission. ");
			threads.get(threadID).send("You don't have permission. \n");
		} else {
			boolean nameAlreadyExistsInDatabase = false;
			ChatUsers chatUsers = new ChatUsers();
			EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
			entityManager.clear();
			Query query = entityManager.createQuery("SELECT c FROM ChatUsers c "); //TODO use where and delete for
			@SuppressWarnings("unchecked")
			List<ChatUsers> list = query.getResultList();
			for (ChatUsers l : list) {
				if (nickname.equals(l.getUsername())) {
					nameAlreadyExistsInDatabase = true;
				}
			}
			if (nameAlreadyExistsInDatabase) {
				threads.get(threadID).send("User " + nickname + " is already added to database. \n");
				logger.warn("User " + nickname + " is already added to database. ");
			} else {
				chatUsers.setNickname(nickname);
				entityManager.getTransaction().begin();
				entityManager.persist(chatUsers);
				entityManager.getTransaction().commit();
				threads.get(threadID).send("User " + nickname + " succesfully added to database. \n");
				logger.warn("User " + nickname + " succesfully added to database. ");
				
			}
			entityManager.close();
		}
			
		}

	public void deleteUserFromDatabase(String nickname, int threadID) {
		if (!threads.get(threadID).senderNickname.equals("Janex")) {
			logger.error("User " + threads.get(threadID).senderNickname + " tried to use command (delete) without permission. ");
			threads.get(threadID).send("You don't have permission. \n");
		} else {
			logger.info("Trying to delete user " + nickname + ".");
			boolean nameAlreadyExistsInDatabase = false;
			int position = 0;
			ChatUsers chatUsers = new ChatUsers();
			EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
			entityManager.clear();
			Query query = entityManager.createQuery("SELECT c FROM ChatUsers c");
			@SuppressWarnings("unchecked")
			List<ChatUsers> list = query.getResultList();
			for (ChatUsers l : list) {
				if (nickname.equals(l.getUsername())) {
					nameAlreadyExistsInDatabase = true;
					position = l.getId();
					break;
				}
			}
			if (nameAlreadyExistsInDatabase) {
				chatUsers = entityManager.find(chatUsers.getClass(), position);
				entityManager.getTransaction().begin();
				entityManager.remove(chatUsers);
				entityManager.getTransaction().commit();
				threads.get(threadID).send("User " + nickname + " succesfully deleted from database. \n");
				logger.warn("User " + nickname + " succesfully deleted from database. ");
			} else {
				threads.get(threadID).send("User " + nickname + " doesn't exist. \n");
				logger.warn("User " + nickname + " doesn't exist. ");
			}
			entityManager.close();
			
		}				
	}
}
