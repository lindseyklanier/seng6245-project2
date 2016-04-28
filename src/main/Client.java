package main;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;

/**
 * Instant Messaging Client
 * By: Lindsey Lanier
 * Spring 2016
 */
public class Client {

	//Swing variables
	private static JTextField ipAddressTextField;
	private static JTextField portNumberTextField;
	private static JTextField usernameTextField;
	private static JTextField chatField;
	private static JLabel lblDisconnected;
	private static JTextArea chatArea;
	
	//Client & Server variables
	private static String localhost = "localhost";
	private static String connectionStatus = "DISCONNECTED"; // by default
	private static ServerSocket ip = null;
	private static Socket socket = null;
	private static BufferedReader bufferedReader_IN = null;
	private static PrintWriter printWriter_OUT = null;
	private static StringBuffer appendToChat = new StringBuffer("");
	private static StringBuffer sendToChat = new StringBuffer("");

	// Instant Message Client created with Swing usinig the Eclipse plug-in -
	// WindowBuilder
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frmInstantMessagingV = new JFrame("HelloWorldSwing");
		frmInstantMessagingV.setTitle("Instant Messaging Client");
		frmInstantMessagingV.getContentPane().setLayout(null);

		JLabel lblHostname = new JLabel("IP Address:");
		lblHostname.setBounds(19, 11, 56, 14);
		frmInstantMessagingV.getContentPane().add(lblHostname);

		JLabel lblPortNumber = new JLabel("Port number:");
		lblPortNumber.setBounds(12, 36, 63, 14);
		frmInstantMessagingV.getContentPane().add(lblPortNumber);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(23, 61, 52, 14);
		frmInstantMessagingV.getContentPane().add(lblUsername);

		ipAddressTextField = new JTextField();
		ipAddressTextField.setBounds(85, 8, 86, 20);
		frmInstantMessagingV.getContentPane().add(ipAddressTextField);
		ipAddressTextField.setColumns(10);

		portNumberTextField = new JTextField();
		portNumberTextField.setBounds(85, 33, 86, 20);
		frmInstantMessagingV.getContentPane().add(portNumberTextField);
		portNumberTextField.setColumns(10);

		usernameTextField = new JTextField();
		usernameTextField.setBounds(85, 58, 86, 20);
		frmInstantMessagingV.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);

		chatArea = new JTextArea();
		chatArea.setBounds(10, 86, 467, 202);
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		frmInstantMessagingV.getContentPane().add(chatArea);				
		
		// Get the text typed in by the user and pass it to the sendToChat StringBuffer object
		chatField = new JTextField();		
		chatField.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				String s = chatField.getText();
	               if (!s.equals("")) {
	            	   appendToChat.append("You: " + s + "\n");	                  
	            	   chatField.selectAll();
	                  // Send the string
	            	   sendToChat.append(s + "\n");
	               }
			}
		});
		chatField.setBounds(12, 299, 465, 20);
		frmInstantMessagingV.getContentPane().add(chatField);
		chatField.setColumns(10);

		JLabel titleLabel = new JLabel("IM Client by Lindsey Lanier");
		titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
		titleLabel.setBounds(290, 5, 199, 45);
		frmInstantMessagingV.getContentPane().add(titleLabel);

		//This label will get updated once the users connect to the server
		lblDisconnected = new JLabel("Disconnected.");
		lblDisconnected.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblDisconnected.setBounds(401, 61, 78, 14);
		frmInstantMessagingV.getContentPane().add(lblDisconnected);

		//Change the connection status to "Connecting" once the user clicks "Connect"
		JButton btnNewButton = new JButton("Connect");
		btnNewButton.setBounds(181, 8, 89, 72);
		frmInstantMessagingV.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				connectionStatus="CONNECTING";
			}
			});
		

		frmInstantMessagingV.setVisible(true);
	}

	/**
	 * Start a GUI chat client.
	 */
	public static void main(String[] args) {        
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
				
		String text;
		
		while (true){
			try { 
	            Thread.sleep(10); //Continue checking for status update every 10 milliseconds
	         }
	         catch (Exception e) {}
		
		switch (connectionStatus) {
		case "CONNECTING":
			try {			
				//Get server details and print them to the console
				String ipAddress = ipAddressTextField.getText();
				int portNumber = Integer.parseInt(portNumberTextField.getText());
				String userName = usernameTextField.getText();
				System.out.println("The connection details are: IPAddress=" + ipAddress + " Port= " + portNumber + " Username= " + userName);
				
				if(userName.equals("user1")) //user1 has to login first to start the server, always
				{
					System.out.println("Trying to connect as user1");
					ip = new ServerSocket(portNumber);
					socket = ip.accept();
					System.out.println("user1 has opened the server");
				}				
				// Other clients can connect to localhost once the first user has logged in
				else {
					System.out.println("another user is trying to connect");
					socket = new Socket(ipAddress, portNumber);
					System.out.println("another user has connected");
				}

				bufferedReader_IN = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				printWriter_OUT = new PrintWriter(socket.getOutputStream(), true);
				lblDisconnected.setText("Connected!");
				connectionStatus = "CONNECTED"; //change the status to connected!
			}

			catch (Exception e1) {
				System.out.println("Something went wrong when trying to connect! " + e1);
				//Close and Dispose everything!
				try {
					if (ip != null) 
					{ 
						ip.close();  			         	
					}
				}
				catch (IOException e2) { ip = null; }

				try {
					if (socket != null) 
					{ 
						socket.close();  			         	
					}
				}
				catch (IOException e3) { socket = null; }

				try {
					if (bufferedReader_IN != null) 
					{ 
						bufferedReader_IN.close();  			         	
					}
				}
				catch (IOException e4) { bufferedReader_IN = null; }

				connectionStatus = "DISCONNECTED";
			}
			break;

		case "CONNECTED":
			try {
				//System.out.println("We are connected and now trying to chat with each other.");
				
				// Send a message to the other user
				if (sendToChat.length() != 0) {
					printWriter_OUT.print(sendToChat); 
					printWriter_OUT.flush();
					sendToChat.setLength(0);
					connectionStatus = "";
				}

				// Receive a message from the other user
				if (bufferedReader_IN.ready()) {
					text = bufferedReader_IN.readLine();
					if ((text != null) &&  (text.length() != 0)) {
						appendToChat.append("Friend: " + text + "\n");
						connectionStatus = "";
					}
				}
							      
				//System.out.println(appendToChat.toString());
				
			}
			catch (IOException e6) {
				//Close and Dispose everything!
				try {
					if (ip != null) 
					{ 
						ip.close();  			         	
					}
				}
				catch (IOException e1) { ip = null; }

				try {
					if (socket != null) 
					{ 
						socket.close();  			         	
					}
				}
				catch (IOException e1) { socket = null; }

				try {
					if (bufferedReader_IN != null) 
					{ 
						bufferedReader_IN.close();  			         	
					}
				}
				catch (IOException e1) { bufferedReader_IN = null; }
				printWriter_OUT.close();

				connectionStatus = "DISCONNECTED";
			}
			break;

		default: break;
		}
		}
	
	}
}
