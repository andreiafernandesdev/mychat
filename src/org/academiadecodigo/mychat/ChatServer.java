package org.academiadecodigo.mychat;

import java.io.*;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded tcp chat server
 */
public class ChatServer {

    public static final int DEFAULT_PORT = 8080;

    /**
    Thread-safe implementation of a growable array of objects
     */
    private Vector<ServerWorker> queue = new Vector<>();

    // 2 mains: chat server and client
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
            ChatServer chatServer = new ChatServer();
            chatServer.establishConnection(port);
        } catch (NumberFormatException ex) {
            System.out.println("Usage: java ChatServer [port_number]");
            System.exit(1);
        }
    }

    /**
     * Starts the chat server on a specified port
     *
     * @param port the tcp port to bind to
     */
    private void establishConnection(int port) {
        try {
            // binding the socket to port
            System.out.println("Binding to port " + port + ", please wait for more information.");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started: " + serverSocket);
            dispatch(serverSocket);

        } catch (IOException ex) {
            System.out.println("Unable to start server on port " + port);
        }
    }

    /**
     * Delegation to server worker
     * @param serverSocket represents server connection
     */
    private void dispatch(ServerSocket serverSocket) {
            try {

                /**
                 * Creates new threads as needed, reusing previously constructed threads when available
                 */
                ExecutorService cachedPool = Executors.newCachedThreadPool();

                while (true) {
                    ServerWorker serverWorker = new ServerWorker(serverSocket.accept(), this);
                    queue.add(serverWorker);
                    System.out.println(Thread.currentThread().getName() + "There are #" + queue.size() + " client(s) online!");
                    cachedPool.submit(serverWorker);

                    if (queue.isEmpty()) {
                        serverSocket.close();
                    }
                }

                    } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public Vector<ServerWorker> getQueue() {
        return queue;
    }

}