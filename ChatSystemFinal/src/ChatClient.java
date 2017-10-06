import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class ChatClient {
    private static HashSet<String> users = new HashSet<String>();
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("!Better than Facebook");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JTextArea activeClients = new JTextArea(1, 20);
    String newUser;
    JButton disconnectBtn = new JButton("Disconnect");
    JLabel userNameLabel = new JLabel("");
    JPanel panel = new JPanel();
    private final int PORT = 4545;
    String serverAddress;
    ChatServer cs = new ChatServer();

    public ChatClient() {

        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        activeClients.setEditable(false);
        textField.setText("Enter text here");

        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.getContentPane().add(new JScrollPane(activeClients), "West");
        panel.add(userNameLabel, BorderLayout.NORTH);
        panel.add(disconnectBtn, BorderLayout.CENTER);
        frame.getContentPane().add(panel, "East");
        frame.setSize(700, 500);

        //Listeners
        textField.addMouseListener(new MouseAdapter() {  //REMOVES TEXTFIELD TEXT ON CLICK
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textField.getText().equals("Enter text here"))
                {
                    textField.setText("");
                }
            }
        });
        disconnectBtn.addActionListener(new ActionListener() { //HANDLES DISCONNECT FUNCTION
            @Override
            public void actionPerformed(ActionEvent eventDis) {
                QUIT();
            }
        });
        textField.addActionListener(new ActionListener() {  //HANDLES ENTER KEY PRESSED FUNCTION
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getServerAddress() { //Throws dialog for user to enter IPAddress
        serverAddress = JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the Server:",
                "IPCONFIG",
                JOptionPane.QUESTION_MESSAGE);
        if(serverAddress == null){
            QUIT();
        }

        return serverAddress;
    }

    public void J_ER(int jer){
        switch (jer) {
            case 1:
                JOptionPane.showMessageDialog(
                        frame,
                        "Error 400 \n" + "400 - Duplicate username detected. Please try again",
                        "Error 400",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("Error 400");
                break;
            case 2:
                JOptionPane.showMessageDialog(
                        frame,
                        "Error 406 \n" + "406 - Not Acceptable \n " + "Max 12 chars long  \n" + "Letters, digits , '-' '_' allowed",
                        "Error 406",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("Error 406");
                break;
        }
    } //J_ER handlers
    private String JOIN() {
        newUser = JOptionPane.showInputDialog(
                frame,
                "Enter a userName",
                "Username selection",
                JOptionPane.PLAIN_MESSAGE);

        if(newUser == null){
            QUIT();
        }
        return newUser;
    }

    private void QUIT(){
        String QUITmsg = (" from " + serverAddress + ":" + PORT + " has disconnected");
        out.println(QUITmsg);
        frame.dispose();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    private void IMAV() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                String IMAVmsg = "User: " + newUser + " is still alive";
                out.println(IMAVmsg);
            }
        },60000, 60000);



    }

    private void run() throws IOException {
        String serverAddress = String.valueOf(getServerAddress());
        Socket socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        try {
            while (true) {
                String line = in.readLine();
                if (line.startsWith("JOIN")) {
                    out.println(JOIN());

                } else if (line.startsWith("J_OK")) {
                    messageArea.append(newUser + " from " + serverAddress + ":" + PORT + " has connected" + "\n");
                    IMAV();
                    userNameLabel.setText("User: " + newUser+ " " );
                    textField.setEditable(true);
                } else if (line.startsWith("DATA")) {
                    messageArea.append(line.substring(4) + "\n");
                } else if (line.startsWith("LIST")) { //TODO LIST~ fix  the getter method
                    //cs.getNames();
//                    users.add(line.substring(4));
//                    activeClients.setText(users.toString().replace(",", "").replace("[", "").replace("]", "\n"));
                    activeClients.setText(cs.getNames().toString());

                }
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }


}