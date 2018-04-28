import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SocketServer extends JFrame {
	private database db;
	private JTabbedPane tabbedPane;
	private JPanel panel1;
	private JPanel panel2;
	private ResultSet result;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private Calendar cal;
	private SimpleDateFormat sdf;
	private int count;
	private int id;

	// write file
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;
	// panel 1
	private JLabel lbl_status;
	private JLabel lbl_picture;
	private JButton btn_login;
	private JButton btn_exit;
	private JLabel lbl_user;
	private JLabel lbl_pass;
	private JTextField tf_user;
	private JPasswordField pf_pass;

	// panel 2
	private JTextField userText;
	private JTextArea chatWindow;
	private JButton btn_send;
	private JLabel lbl_status_2;
	private JButton btn_clear;
	private JButton btn_file;

	public SocketServer() {
		super("Nghia Messenger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		try {
			file = new File("history.txt");
			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		db = new database();
		db.initialize();

		tabbedPane = new JTabbedPane();
		tabbedPane.setSize(500, 500);

		panel1 = new JPanel();
		tabbedPane.addTab("Login", null, panel1, "Login to use this program");
		panel1.setLayout(null);

		lbl_user = new JLabel("Username");
		lbl_user.setBounds(101, 197, 61, 14);
		panel1.add(lbl_user);

		tf_user = new JTextField();
		tf_user.setBounds(101, 222, 241, 20);
		panel1.add(tf_user);

		lbl_pass = new JLabel("Password");
		lbl_pass.setBounds(101, 253, 76, 14);
		panel1.add(lbl_pass);

		pf_pass = new JPasswordField();
		pf_pass.setBounds(101, 278, 241, 20);
		panel1.add(pf_pass);

		lbl_picture = new JLabel("");
		lbl_picture.setIcon(new ImageIcon(getClass().getResource("hcmutrans.png")));
		lbl_picture.setBounds(174, 11, 120, 150);
		panel1.add(lbl_picture);

		count = 0;
		btn_login = new JButton("Log in");
		btn_login.setBounds(101, 321, 111, 23);
		btn_login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				result = db.excSelect("SELECT * FROM account");
				if (count == 0) {
					try {
						while (result.next()) {
							if (result.getString(2).equals(tf_user.getText())) {
								if (result.getString(3).equals(pf_pass.getText())) {
									id = result.getInt(1);
									loggedIn_Status(true);
									tabbedPane.setEnabledAt(1, true);
									btn_login.setText("Disconnect");
									loadData();
									count++;
									break;
								}
							} else {
								lbl_status.setText("Your username or password is incorrect, please try again!");
								lbl_status.setIcon(new ImageIcon(getClass().getResource("error.png")));
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					loggedIn_Status(false);
					tabbedPane.setEnabledAt(1, false);
					btn_login.setText("Log in");
					count--;
				}
			}
		});
		panel1.add(btn_login);

		btn_exit = new JButton("Exit");
		btn_exit.setBounds(231, 321, 111, 23);
		btn_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		panel1.add(btn_exit);

		lbl_status = new JLabel("Status:");
		lbl_status.setBounds(10, 399, 459, 23);
		panel1.add(lbl_status);

		panel2 = new JPanel();
		tabbedPane.addTab("Server", null, panel2, "Chat window");
		panel2.setLayout(null);
		setSize(500, 500);

		btn_send = new JButton(new ImageIcon(getClass().getResource("send.png")));
		btn_send.setBounds(390, 10, 89, 23);
		btn_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(userText.getText());
				userText.setText("");
			}
		});
		panel2.add(btn_send);
		panel2.add(new JScrollPane());

		userText = new JTextField();
		userText.setEditable(false);
		userText.setBounds(10, 11, 370, 20);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		panel2.add(userText);

		chatWindow = new JTextArea();
		chatWindow.setBounds(10, 42, 370, 362);
		panel2.add(chatWindow);

		lbl_status_2 = new JLabel("Waiting for connection...");
		lbl_status_2.setBounds(10, 418, 469, 14);
		panel2.add(lbl_status_2);

		btn_clear = new JButton("");
		btn_clear.setBounds(390, 78, 89, 23);
		btn_clear.setIcon(new ImageIcon(getClass().getResource("eraser.png")));
		btn_clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chatWindow.setText("");
			}
		});
		panel2.add(btn_clear);

		btn_file = new JButton("");
		btn_file.setBounds(390, 44, 89, 23);
		btn_file.setIcon(new ImageIcon(getClass().getResource("folder.png")));
		panel2.add(btn_file);

		getContentPane().add(tabbedPane);
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setVisible(true);
		setVisible(true);
	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					// Trying to connect and have conversation
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					lbl_status_2.setText("\n Server ended the connection! ");
				} finally {
					closeConnection();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		connection = server.accept();
		setEnabled(true);
		lbl_status_2.setText("Connected ");
	}

	// get stream to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}

	// during the chat conversation
	private void whileChatting() throws IOException {
		String message = null;
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
				db.excUpdate("\n" + message, id);
				bw.write("\n" + message);

				cal = Calendar.getInstance();
				sdf = new SimpleDateFormat("hh:mm:ss");
				String time = sdf.format(cal.getTime());
				showMessage("\n" + time);
				db.excUpdate("\n" + time, id);
				bw.write("\n" + time);
			} catch (ClassNotFoundException classNotFoundException) {
				lbl_status_2.setText("The user has sent an unknown object!");
			}
		} while (!message.equals("CLIENT: END"));
	}

	public void closeConnection() {
		lbl_status_2.setText("\n Closing Connections... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
			bw.close();
			fw.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// Send a mesage to the client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER: " + message);
			output.flush();
			db.excUpdate("\nSERVER: " + message, id);
			showMessage("\nSERVER: " + message);
			bw.write("\nSERVER: " + message);

			cal = Calendar.getInstance();
			sdf = new SimpleDateFormat("hh:mm:ss");
			String time = sdf.format(cal.getTime());
			showMessage("\n" + time);
			db.excUpdate("\n" + time, id);
			bw.write("\n" + sdf.format(cal.getTime()));
		} catch (IOException ioException) {
			lbl_status_2.setText("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}

	// update chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}

	private void loadData() {
		result = db.excSelect(String.format("SELECT history FROM account WHERE id='%d'", id));
		try {
			while (result.next()) {
				showMessage(result.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loggedIn_Status(boolean b) {
		if (b) {
			lbl_status.setText("Logged in!");
			lbl_status.setIcon(new ImageIcon(getClass().getResource("tick.png")));
			tf_user.setEnabled(false);
			pf_pass.setEnabled(false);
		} else {
			lbl_status.setText("Status:");
			lbl_status.setIcon(null);
			tf_user.setEnabled(true);
			pf_pass.setEnabled(true);
		}
	}
}
