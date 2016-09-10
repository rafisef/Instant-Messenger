/**
 * Created by Rafael on 4/20/2016.
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// CLIENT'S PROGRAM THAT IS RUN FROM A PERSONAL COMPUTER TO COMMUNICATE WITH ANOTHER COMPUTER THROUGH A SERVER

public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private JButton reconnectButton;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection; // the connection itself

    // CONSTRUCTOR

    public Client(String host){
        super("Client mofo!");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand());
                userText.setText("");
            }
        });
        add(userText,BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow),BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
        reconnectButton = new JButton("reconnect");
        add(reconnectButton,BorderLayout.SOUTH);
        reconnectButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        reconnect(); // runs startRunning()
                        //ableToType(true);
                        showMessage("\n reconnected \n");
                    }
                }
        );
    }

    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client terminated the connection");
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally {
            closeCrap();
        }
    }

    // CONNECT TO SERVER
    private void connectToServer() throws IOException{
        showMessage("Attempting connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP),6789); // connecting to server IP address and its' port number
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    // SET UP STREAMS TO SEND AND RECEIVE MESSAGES
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Dude your streams are now good to go! \n");
    }

    //WHILE CHATTING WITH SERVER
    private void whileChatting() throws IOException{
        ableToType(true);
        do {
            try{
                message = (String) input.readObject();
                showMessage("\n " + message);
            }catch (ClassNotFoundException classNotfoundException){
                showMessage("\n I don't know that object type");
            }
        }while (!message.equals("Server - END"));
    }

    // CLOSE THE STREAMS AND SOCKETS
    private void closeCrap(){
        showMessage("\n closing crap down...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    // SEND MESSAGES TO SERVER
    private void sendMessage(String message){
        try{
            output.writeObject("CLIENT - " + message); // sends your message to the server which then sends it to the other client
            output.flush();
            showMessage("\n CLIENT - " + message); // actually updates the GUI so that both parties can view the messages
        }catch (IOException ioException){
            chatWindow.append("\n Something messed up sending message hoss!");
        }
    }

    // CHANGE/UPDATE chatWindow
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);

                    }
                }
        );
    }

    // GIVES USER PERMISSION TO TYPE INTO THE TEXTBOX
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }
    private void reconnect(){
        startRunning();
    }
}
