import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {
    private static final int PORT = 4545;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception {
        System.out.println("Server online");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }


    /**
     *
     * THE THREAD CLASS
     *
     */

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }


        public void run() {
            ChatClient c = new ChatClient();
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("JOIN");
                    name = in.readLine();
                    out.println("LIST" + name);

                    if (name == null) {
                        c.J_ER(2);
                    }
                    synchronized (names) {
                        if (!names.contains(name)) { //TODO IMPLEMENT USERNAME RESTRICTIONS
                            names.add(name);
                            out.println("J_OK");
                            writers.add(out);
                            //out.println("LIST");
                            break;
                        }else{
                            c.J_ER(1);
                        }
                    }
                }

                while (true) {
                    String input = in.readLine();
                    System.out.println(name + input);
                    if (input == null) {
                        return;

                    }
                    for (PrintWriter writer : writers) {
                        writer.println("DATA " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {

                if (name != null) {
                    names.remove(name);

                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    System.out.println("closing ioreaders/iowriters");
                    in.close();
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
