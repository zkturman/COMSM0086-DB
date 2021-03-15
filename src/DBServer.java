import DBObjects.*;

import java.io.*;
import java.net.*;

public class DBServer {
        Database workingDatabase = null;
        public DBServer(int portNumber)
        {
            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                System.out.println("Server Listening");
                while(true) processNextConnection(serverSocket);
            } catch(IOException ioe) {
                System.err.println(ioe);
            }
        }

        private void processNextConnection(ServerSocket serverSocket)
        {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.println("Connection Established");
                while(true) processNextCommand(socketReader, socketWriter);
            } catch(IOException ioe) {
                System.err.println(ioe);
            } catch(NullPointerException npe) {
                System.out.println("Connection Lost");
                npe.printStackTrace();
            }
        }

        private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException
        {
            if (workingDatabase != null){
                socketWriter.write("Current database " + workingDatabase.getObjectName() + '\n');
            }
            String incomingCommand = socketReader.readLine();
            System.out.println("Received message: " + incomingCommand);
            DBStatement dbStatement = new DBStatement(workingDatabase);
            dbStatement.performStatement(incomingCommand);
            this.workingDatabase = dbStatement.getWorkingDatabase();
            //return string of some sort for errors and success
            socketWriter.write("[OK] Thanks for your message: " + incomingCommand);
            socketWriter.write("\n" + ((char)4) + "\n");
            socketWriter.flush();
        }

        public static void main(String args[])
        {
            DBServer server = new DBServer(8888);
        }

}
