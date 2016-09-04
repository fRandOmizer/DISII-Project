package com.distopia.raspiserver;

import javax.swing.*;

import com.distopia.raspiserver.RaspiServer.OnMessageReceived;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class extends the Thread class so we can receive and send messages at the
 * same time
 */
public class RaspiServer extends Thread
{

  public static final int SERVERPORT = 4444;
  private boolean running = false;
  private PrintWriter mOut;
  private OnMessageReceived messageListener;

  public static void main(String[] args)
  {

    // opens the window where the messages will be received and sent
    RaspiServerGUI frame = new RaspiServerGUI();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

  }

  /**
   * Constructor of the class
   * 
   * @param messageListener
   *          listens for the messages
   */
  public RaspiServer(OnMessageReceived messageListener)
  {
    this.messageListener = messageListener;
  }

  /**
   * Method to send the messages from server to client
   * 
   * @param message
   *          the message sent by the server
   */
  public void sendMessage(String message)
  {
    if (mOut != null && !mOut.checkError())
    {
      mOut.println(message);
      mOut.flush();
    }
  }

  @Override
  public void run()
  {
    super.run();

    running = true;

    try
    {
      System.out.println("S: Connecting...");

      // create a server socket. A server socket waits for requests to come in
      // over the network.
      ServerSocket serverSocket = new ServerSocket(SERVERPORT);

      // while for multiple clients
      while(running)
      {
      
      // create client socket... the method accept() listens for a connection to
      // be made to this socket and accepts it.
      Socket client = serverSocket.accept();
      ServerHelper serv = new ServerHelper(client, running, mOut, messageListener);
      serv.start();
      }

    }
    catch (Exception e)
    {
      System.out.println("S: Error");
      e.printStackTrace();
    }

  }

  // Declare the interface. The method messageReceived(String message) will must
  // be implemented in the ServerBoard
  // class at on startServer button click
  public interface OnMessageReceived
  {
    public void messageReceived(String message);
  }

}

class ServerHelper extends Thread
{

  private Socket socket = null;
  private boolean running = false;
  private PrintWriter mOut;
  private OnMessageReceived messageListener;

 

  public ServerHelper(Socket socket, boolean running, PrintWriter mOut, OnMessageReceived messageListener)
  {
    super("Server Helper");    
    this.socket = socket;
    this.running = running;
    this.mOut = mOut;
    this.messageListener = messageListener;
  }

  public void run()
  {
    // Read input and process
    System.out.println("S: Receiving...");

    try
    {

      // sends the message to the client
      mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

      // read the message received from client
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // in this while we wait to receive messages from client (it's an
      // infinite loop)
      // this while it's like a listener for messages
      while (running)
      {
        String message = in.readLine();

        if (message != null && messageListener != null)
        {
          // call the method messageReceived from ServerBoard class
          messageListener.messageReceived(message);
        }
      }

    }
    catch (Exception e)
    {
      System.out.println("S: Error");
      e.printStackTrace();
    }
    finally
    {
      try
      {
        socket.close();
      }
      catch (IOException e)
      {
        System.out.println("S: Error while closing socket");
        e.printStackTrace();
      }
      System.out.println("S: Done.");
    }

  }

}