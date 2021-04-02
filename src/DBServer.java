import DBException.DBException;
import DBObjects.*;

import java.io.*;
import java.net.*;

public class DBServer {
        DBDatabase workingDatabase = null;
        public DBServer(int portNumber)
        {
            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                System.out.println("Server Listening");
                while(true) processNextConnection(serverSocket);
            } catch(IOException ioe) {
                System.err.println("File handling exception.");
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
                System.err.println("File handling exception.");
                ioe.printStackTrace();
            } catch(NullPointerException npe) {
                System.out.println("Connection Lost");
                npe.printStackTrace();
            }
        }

        private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException
        {
            String incomingCommand = socketReader.readLine();
            System.out.println("Received message: " + incomingCommand);
            DBStatement dbStatement = new DBStatement(workingDatabase);
            try{
                dbStatement.performStatement(incomingCommand);
                socketWriter.write("[OK] Processed: " + incomingCommand + System.lineSeparator());
                String returnMessage = dbStatement.getReturnMessage();
                if (returnMessage != null){
                    socketWriter.write(returnMessage);
                }
            }
            catch(DBException de){
                socketWriter.write("[ERROR] " + de.toString());
            }
            socketWriter.write("\n" + ((char)4) + "\n");
            this.workingDatabase = dbStatement.getWorkingDatabase();

            socketWriter.flush();
        }

        public static void main(String[] args)
        {
            DBServer server = new DBServer(8888);
        }

}
