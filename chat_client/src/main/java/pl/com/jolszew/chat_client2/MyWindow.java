package pl.com.jolszew.chat_client2;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * @author jolszew
 * 
 *         MyWindow class allows to connect to server, by using simple swing
 *         GUI. You can specify IP, port of the server and your nickname. It
 *         also displays other users messages. Window contains few buttons and
 *         text areas.
 * 
 * 
 *
 */
public class MyWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	ChatClient chatClient = new ChatClient();

	private JTextField message_field = new JTextField(90);
	static JTextField nickname_field = new JTextField("test", 22);
	private JTextField ip_field = new JTextField("localhost", 17);
	private JTextField port_field = new JTextField("1200", 6);

	String message_sent;

	private JButton connect_and_disconnect_button;
	private JButton send_button;
	private JTextArea chat_area;
	private JFrame frame;
	private boolean connected = false;
	private boolean logged = false;
	private JButton login_and_logout_button;

	public MyWindow() {
		frame = new JFrame("Chat");
		frame.setSize(430, 570);
		frame.setLocationRelativeTo(null);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		JPanel main_panel = new JPanel();
		frame.add(main_panel);
		EmptyBorder border = new EmptyBorder(5, 5, 5, 5);
		main_panel.setBorder(border);
		main_panel.setLayout(new MigLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setUpperPanel(main_panel);

		setNicknamePanel(main_panel);

		setChatPanel(main_panel);

		setLowerPanel(main_panel);

		addActionsToButtons();

		addSendActionToEnterKey();

		frame.setVisible(true);
		frame.setResizable(false);
	}

	private void addSendActionToEnterKey() {
		int condition = JComponent.WHEN_FOCUSED;

		InputMap iMap = message_field.getInputMap(condition);
		ActionMap aMap = message_field.getActionMap();
		String enter = "enter";
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enter);
		aMap.put(enter, new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				send_button.doClick();

			}
		});
	}

	private void addActionsToButtons() {
		chatClient.addChatListener(new ChatListener() {

			@Override
			public void messageArrived(String newMessage) {
				SwingUtilities.invokeLater(() -> {

					if (newMessage.startsWith("(error)")) {
						send_button.setEnabled(false);
						message_field.setEditable(false);
						logged = false;
						login_and_logout_button.setText(" login ");
						nickname_field.setEditable(true);
					}

					if (newMessage.startsWith("(alert)")) {
						Point currLocation = frame.getLocationOnScreen();
						int iDisplaceXBy = 3;
						int iDisplaceYBy = -3;
						Point position1 = new Point(currLocation.x + iDisplaceXBy, currLocation.y + iDisplaceYBy);
						Point position2 = new Point(currLocation.x - iDisplaceXBy, currLocation.y - iDisplaceYBy);
						SwingUtilities.invokeLater(() -> {

							String alertName = "alercik.wav";
							File mp3file = new File(alertName);
							AudioInputStream audioInputStream = null;
							try {
								audioInputStream = AudioSystem.getAudioInputStream(mp3file.getAbsoluteFile());
								Clip clip = null;
								clip = AudioSystem.getClip();
								clip.open(audioInputStream);
								clip.start();
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						});

						for (int i = 0; i < 5; i++) {

							SwingUtilities.invokeLater(() -> {

								frame.setLocation(position1);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								frame.setLocation(position2);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							});

						}
						frame.setLocation(currLocation);
					}

					chat_area.append(newMessage);
				});
			}

			@Override
			public void connectionEstablished() {
				SwingUtilities.invokeLater(() -> {

					chat_area.append("You have successfully connected to the server. \n");
				});
			}

			@Override
			public void connectionClosed() {
				SwingUtilities.invokeLater(() -> {

					chat_area.append("You have successfully disconnected from the server. \n");
				});
			}

			@Override
			public void error(String message) {
				SwingUtilities.invokeLater(() -> {

					send_button.setEnabled(false);
					message_field.setEditable(false);
				});
			}
		});

		connect_and_disconnect_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ip_field.getText();
				String port = port_field.getText();
				if (connected == false) {
					connected = true;
					logged = false;
					chatClient.connect(ip, port);
					connect_and_disconnect_button.setText("disconnect");
					ip_field.setEditable(false);
					port_field.setEditable(false);
					nickname_field.setEditable(true);
					login_and_logout_button.setEnabled(true);

				} else {
					chat_area.append("Disconnected. \n");
					connected = false;
					if (logged = true) {
						chatClient.logout(nickname_field.getText());
					}
					logged = false;
					chatClient.logout(nickname_field.getText());
					chatClient.disconnect();
					connect_and_disconnect_button.setText(" connect ");
					ip_field.setEditable(true);
					port_field.setEditable(true);
					nickname_field.setEditable(false);
					login_and_logout_button.setEnabled(false);
					login_and_logout_button.setText(" login ");
					send_button.setEnabled(false);
					message_field.setEditable(false);
				}

			}
		});

		send_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (connected && logged) {
					String message = message_field.getText();
					chatClient.send(message);
					message_field.setText("");
				}
			}
		});

		login_and_logout_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (logged == false) {
					chatClient.login(nickname_field.getText());
					nickname_field.setEditable(false);
					login_and_logout_button.setText("logout");
					send_button.setEnabled(true);
					message_field.setEditable(true);
					logged = true;
				} else {
					chatClient.logout(nickname_field.getText());
					nickname_field.setEditable(true);
					login_and_logout_button.setText(" login ");
					send_button.setEnabled(false);
					message_field.setEditable(false);
					logged = false;
				}
			}
		});

	}

	private void setNicknamePanel(JPanel main_panel) {
		JPanel nickname_panel = new JPanel();
		nickname_panel.setLayout(new MigLayout());
		main_panel.add(nickname_panel, "wrap");
		JLabel nickname_label = new JLabel("Your nickname: ");
		nickname_panel.add(nickname_label);
		nickname_panel.add(nickname_field);
		nickname_field.setEditable(false);
		login_and_logout_button = new JButton(" login ");
		nickname_panel.add(login_and_logout_button, "wrap");
		login_and_logout_button.setEnabled(false);
	}

	private void setLowerPanel(JPanel main_panel) {
		JPanel lower_panel = new JPanel();
		lower_panel.setLayout(new MigLayout());
		main_panel.add(lower_panel);
		message_field.setEditable(false);
		lower_panel.add(message_field);
		send_button = new JButton("Send");
		send_button.setEnabled(false);
		lower_panel.add(send_button);
	}

	private void setChatPanel(JPanel main_panel) {
		JPanel chat_panel = new JPanel();
		main_panel.add(chat_panel, "wrap");
		chat_area = new JTextArea();
		Font font = chat_panel.getFont();
		chat_area.setFont(font.deriveFont(12.4f));
		chat_area.setEditable(false);
		JScrollPane editorScrollPane = new JScrollPane(chat_area);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(390, 360));
		chat_panel.add(editorScrollPane);
	}

	private void setUpperPanel(JPanel main_panel) {
		JPanel upper_panel = new JPanel();
		upper_panel.setLayout(new MigLayout());
		main_panel.add(upper_panel, "wrap");
		JLabel ip_label = new JLabel("IP :");
		upper_panel.add(ip_label);
		upper_panel.add(ip_field);
		JLabel port_label = new JLabel("Port :");
		upper_panel.add(port_label);
		upper_panel.add(port_field);
		connect_and_disconnect_button = new JButton(" connect ");
		upper_panel.add(connect_and_disconnect_button, "wrap");
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MyWindow();
			}
		});
	}
}