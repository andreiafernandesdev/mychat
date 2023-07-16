package org.academiadecodigo.mychat;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded tcp chat client
 */
public class Client {

    private Socket clientSocket;
    private Vector<ClientWorker> queue = new Vector<>();

    // 2 mains: server and client
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ChatClient <host> <port>");
            System.exit(1);
        }
        try {
            System.out.println("Trying to establish connection");
            Client client = new Client();
            client.establishConnection(args[0], Integer.parseInt(args[1]));
            System.exit(1);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid port number " + args[1]);
            System.out.print(1);
        }
    }

    private void establishConnection(String host, int serverPort) {

        //the client needs to know the server in order to connect
        try {
            this.clientSocket = new Socket(host, serverPort);
            dispatch(clientSocket);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Delegation to client worker
     *
     * @param clientSocket represents client connection
     */
    private void dispatch(Socket clientSocket) {
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        System.out.println("Enter your username: ");
        try {
            String clientName = new BufferedReader(new InputStreamReader(System.in)).readLine();

            ClientWorker clientWorker = new ClientWorker(clientSocket, clientName);
            queue.add(clientWorker);
            cachedPool.submit(clientWorker);

            System.out.println(Thread.currentThread().getName() + "There are " + queue.size() + " client(s) online!");
            System.out.println("Welcome " + clientName);

            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter consoleOut = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            while (!clientSocket.isClosed()) {

                String myMessage = consoleIn.readLine();

                if (myMessage == null || myMessage.equals("quit")) {
                    break;
                }
                consoleOut.println(clientName + ": " + myMessage);
            }

            if (queue.isEmpty()) {
                clientSocket.close();
                clientWorker.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
