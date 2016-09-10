/**
 * Created by Rafael on 4/20/2016.
 */
import javax.swing.JFrame;

public class ServerTest{
    public static void main(String[] args){
        Server sally = new Server();
        sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes the program when we exited out of it "clicking the x"
        sally.startRunning(); // starts the server code
    }
}
