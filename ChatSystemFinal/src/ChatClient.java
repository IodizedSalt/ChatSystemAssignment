import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.*;

public class ChatClient {
    private static HashSet<String> users = new HashSet<String>();
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Better than Facebook");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JTextArea activeClients = new JTextArea(1, 20);
    String newUser;
    JButton disconnectBtn = new JButton("Disconnect");
    JLabel userNameLabel = new JLabel("");
    JPanel panel = new JPanel();
    private int jer = 0;
    private final int PORT = 4545;
    //private final int PORT =12456;
    String serverAddress;
    public ChatClient() {

        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        activeClients.setEditable(false);
        frame.getContentPane().add(textField, "North");
        textField.setText("Enter text here");


        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.getContentPane().add(new JScrollPane(activeClients), "West");
        panel.add(userNameLabel, BorderLayout.NORTH);
        panel.add(disconnectBtn, BorderLayout.CENTER);
        frame.getContentPane().add(panel, "East");
        //frame.pack();
        frame.setSize(700, 500);

        // Add Listeners
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textField.getText().equals("Enter text here"))
                {
                    textField.setText("");
                }
            }
        });
        disconnectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent eventDis) {
                QUIT();
            }
        });
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        }); //enter press
    }

    private String getServerAddress() {
        serverAddress = JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the Server:",
                "IPCONFIG",
                JOptionPane.QUESTION_MESSAGE);

        return serverAddress;
    }

    //TODO MAKE J_ER HERE
    public void J_ER(int jer){
        switch (jer) {
            case 1:
                JOptionPane.showMessageDialog(
                        frame,
                        "Error 400 \n" + "400 - Duplicate username detected. Please try again",
                        "Error 400",
                        JOptionPane.ERROR_MESSAGE);
            case 2:
                JOptionPane.showMessageDialog(
                        frame,
                        "Error 406 \n" + "406 - Not Acceptable +\n " + "Max 12 chars long + \n" + "Letters, digits , '-' '_' allowed",
                        "Error 406",
                        JOptionPane.ERROR_MESSAGE);
            case 3: //TODO MAKE A DIFFERENT ERROR HERE
                JOptionPane.showMessageDialog(
                        frame,
                        "Error \n" + "400 - Duplicate username detected. Please try again",
                        "Error 400",
                        JOptionPane.ERROR_MESSAGE);
        }



    }


    private String JOIN() {
        newUser = JOptionPane.showInputDialog(
                frame,
                "Enter a userName",
                "Username selection",
                JOptionPane.PLAIN_MESSAGE);
        return newUser;
    }

    private void QUIT(){
        frame.dispose();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    private void IMAV(){
//        JOptionPane.showMessageDialog(
//                frame,
//                "Timeout Error, please Reconnect",
//                "Timeout",
//                JOptionPane.ERROR_MESSAGE);
//        QUIT();

    }

    private void run() throws IOException {
        String serverAddress = String.valueOf(getServerAddress());
        Socket socket = new Socket(serverAddress, PORT);
        //socket.setSoTimeout(10000);
        //socket.setKeepAlive(true);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        try {
            while (true) {
                String line = in.readLine();
                if (line.startsWith("JOIN")) {
                    out.println(JOIN());
                    userNameLabel.setText("User: " + newUser + "\n");
                    //messageArea.append("JOIN " + newUser + " " + serverAddress + ":" + PORT + "\n");
                    //activeClients.append();
                } else if (line.startsWith("J_OK")) {
                    messageArea.append(newUser + " from " + serverAddress + ":" + PORT + " has connected" + "\n");
                    textField.setEditable(true);
                } else if (line.startsWith("DATA")) {
                    messageArea.append(line.substring(4) + "\n");
                } else if (line.startsWith("LIST")) { //TODO LIST
                    users.add(line.substring(4));
                    activeClients.setText(users.toString().replace(",", "").replace("[", "").replace("]", "\n"));
                }
            }
        } catch (SocketTimeoutException ste) {
            IMAV();
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