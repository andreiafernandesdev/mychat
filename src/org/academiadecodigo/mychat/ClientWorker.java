package org.academiadecodigo.mychat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Delegation of responsibility from Client to ClientWorker
 * Works to the client by handling incoming messages from the server
 */

public class ClientWorker implements Runnable {
    private final String clientName;
    private final Socket clientSocket;
    private BufferedReader in;
    private BufferedReader readSystemIn;
    private PrintWriter out;

    /**
     * @param clientSocket represents the connected client
     * @param clientName   identifies the client
     */

    public ClientWorker(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        openStreams();
    }

    private void openStreams() {
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (!clientSocket.isClosed()) {

            String incomingMessage;

            try {

                incomingMessage = in.readLine();

                if (incomingMessage != null) {
                    System.out.println(incomingMessage);

                } else {
                    System.out.println("Connection closed, exiting...");
                        close();
                    }

                } catch(IOException e){
                    throw new RuntimeException(e);
                }
            }
        }


    public void close() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
