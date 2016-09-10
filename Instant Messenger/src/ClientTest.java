/**
 * Created by Rafael on 4/20/2016.
 */

import javax.swing.JFrame;

// In order for this to work you need to provide all the people in your chat room the IP address of your server
public class ClientTest {
    public static void main(String[] args){
        Client charlie;
        //charlie = new Client(ServerIP)
        charlie = new Client("127.0.0.1"); // IP address is local host meaning the computer I am at right now. A way to test without having a personal server
        charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        charlie.startRunning();
    }
}
