/**
 * Created by Rafael on 4/20/2016.
 */
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.message.stream.StreamAttachment;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
    private JTextField userText; // area where the user types in the message
    private JTextArea chatWindow; // where the message history is
    private JButton reconnectButton;
    private ObjectOutputStream output; // data sent away from computer
    private ObjectInputStream input; // data sent to your computer
    private ServerSocket server;
    private Socket connection; // Socket in java is a connection between computers

    //constructor
    public Server(){
        super("Instant Messenger");
        userText = new JTextField(); // creates the user text area
        userText.setEditable(false); // by default before connected to anyone else you cant type in message box
        userText.addActionListener(new ActionListener() { // looks for when "ENTER" is pressed
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand()); // if "ENTER" is pressed send message
                userText.setText(""); //If "ENTER" is pressed set the userText to an empty string
            }
        });
        add(userText,BorderLayout.NORTH); //adds the user text to the top of the Border
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow)); // add a pane for the chatWindow
        setSize(300,150); // Size of the messenger
        setVisible(true); // sets the vsibility of messenger to true


        reconnectButton = new JButton("reconnect");
        add(reconnectButton,BorderLayout.SOUTH);
        reconnectButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        startRunning();
                        showMessage("\n reconnected \n");
                    }
                }
        );
    }


    // SET UP AND RUN THE SERVER

    public void startRunning(){
        try{
            server = new ServerSocket(6789,100); // 6789 arbitrary number[Port number] used for testing(DON'T forget it).Backlog [100] is how many people can be on that server or sit and wait on it. Port number is the location of where the instant messaging app is located, or any application on your computer for that matter
            while(true){
                try{
                    //connect and try to have conversation
                    waitforConnection(); // start and wait for someone to connect with
                    setupStreams(); // once connected, set up stream between computers
                    whileChatting(); // when 2 computers are connected be able to send messages
                }catch(EOFException eofException){
                    showMessage("\n Server ended the connection");
                }finally {
                    closeCrap();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }


    // WAIT FOR CONNECTION, THEN DISPLAY CONNECTION INFORMATION

    private void waitforConnection() throws IOException{
        showMessage("Waiting for someone to connect... \n");
        connection = server.accept(); // once a connection request is available the (dot)accept will accept that connection. ONLY creates a socket then it is actually connected to somoneone. Doesn't make multiple variables while in a loop.
        showMessage("Now connected to " + connection.getInetAddress().getHostName()); //Host name is the IP address
    }

    // GET STREAM TO SEND AND RECEIVE DATA

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream()); //creating the pathway to connect to another computer to send data OUT
        output.flush(); // clean up after you send data out
        input = new ObjectInputStream(connection.getInputStream()); //creating the pathway to connect to another computer to send data IN
        showMessage("\n Streams are now setup! \n");
    }

    // DURING THE CHAT CONVERSATION

    private void whileChatting() throws IOException{
        String message = " You are now connected! ";
        sendMessage(message);
        ableToType(true); // going to allow user to type stuff into the text box
        do {
            //have the conversation
            try{
                message = (String) input.readObject(); // tries to read the message from them
                showMessage("\n" + message); // shows message in text box for both of you to see
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n I don't know wtf that user sent!"); // only will happen if the other person sends something that is not a string object
            }
        }while(!message.equals("CLIENT - END"));
    }

    // CLOSE STREAMS AND SOCKETS AFTER YOU ARE DONE CHATTING

    private void closeCrap(){
        showMessage("\n Closing connections...\n");
        ableToType(false); // makes so that user can't type into the text box
        try{
            output.close(); // closes the output stream
            input.close(); // closes the input stream
            connection.close(); // closes the socket connection
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    // SENDS A MESSAGE TO CLIENT

    private void sendMessage(String message) {
        try{
            output.writeObject("SERVER - " + message); // writes the message to the output object
            output.flush(); // flushes any bytes left over to the client
            showMessage("\n SERVER - " + message); // actually shows the message on the view box so that everyone can see it
        }catch(IOException ioException){
            chatWindow.append("\n ERROR DUDE I CAN'T SEND THAT MESSAGE");
        }
    }

    // UPDATES chatWindow

    private void showMessage(final String text) {
        SwingUtilities.invokeLater( //created a thread that will update the GUI
                new Runnable() {
                    @Override
                    public void run() { // the run method is what will update our GUI
                        chatWindow.append(text); // adds a message to the end of the document, which then gets updated in our chatWindow. NOTE we are only updating our chatWindow
                    }
                }
        );
    }

    // LET THE USER TYPE STUF INTO THEIR BOX

    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() { // updates our GUI
                        userText.setEditable(tof); // sets whether the user can type in box or not and updates it in our GUI
                    }
                }
        );
    }
}
