package org.academiadecodigo.mychat;

import java.io.*;
import java.net.Socket;

/**
 * Delegation of responsibility from Server to ServerWorker
 * Works to the server by handling client connections
 */
public class ServerWorker implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ChatServer myChatServer;

    /**
     * @param clientSocket represents the client socket connection
     * @param chatServer   establishes a commitment between a chat server and its runnable worker
     */
    public ServerWorker(Socket clientSocket, ChatServer chatServer) {
        this.clientSocket = clientSocket;
        this.myChatServer = chatServer;
        openStreams();
    }

    private void openStreams() {

        try {

            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {

        try {

            //message exchange
            while (!clientSocket.isClosed()) {
                //read client's message
                String line = in.readLine();

                if (line == null) {
                    System.out.println("Client " + Thread.currentThread().getName() + " exiting...");
                    exit();
                    continue;
                }

                System.out.println(Thread.currentThread().getName() + ": " + line);
                //write client message
                broadcast(line);
            }


        } catch (IOException ex) {
            System.out.println("Receiving error on " + Thread.currentThread().getName() + " : " + ex.getMessage());
        }

    }

    public void send(String msg) {
        out.println(msg + "\n");
    }

    public void broadcast(String msg) {

        for (int i = 0; i < myChatServer.getQueue().size(); i++) {

            /**
             * verification to eliminate eco
             * receiver must be != sender
             */
            if (!(myChatServer.getQueue().get(i) == this)) {
                myChatServer.getQueue().get(i).send(msg);
            }
        }
    }

    public void exit() {

        try {
            myChatServer.getQueue().remove(this);
            in.close();
            out.close();
            getClientSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
