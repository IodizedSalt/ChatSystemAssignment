import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer {
    private static final int PORT = 4545;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static HashSet<String> getNames() {
        return names;
    }

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

        private boolean userNameRestrictions(){
            Pattern p = Pattern.compile("[$&+,:;=?@#|'<>.^*()%!]");

            Matcher m = p.matcher(name);
            boolean b = m.find();
            System.out.println(b);
            if(b==true){
                return true;
            }else {
                return false;

            }
        }
        public void run() {
            ChatClient c = new ChatClient();
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                JoinLoop:
                while (true) {
                    out.println("JOIN");
                    name = in.readLine();
                    if (name == null || userNameRestrictions() || name.length()>12) {
                        c.J_ER(2);
                        continue JoinLoop;
                    }else {
                    synchronized (names) {
                        if (names.contains(name)) {
                            c.J_ER(1);
                            continue JoinLoop;
                        } else {
                            names.add(name);
                            out.println("J_OK");
                            writers.add(out);
                            out.println("LIST" + name);
                            break;
                        }
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
