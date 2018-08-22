package pl.com.jolszew.chat_client2;

public interface ChatListener {
	
	public void messageArrived(String message);
	
	public void connectionEstablished();
	
	public void connectionClosed();

	public void error(String message);
	
}