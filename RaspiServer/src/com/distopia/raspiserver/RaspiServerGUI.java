package com.distopia.raspiserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RaspiServerGUI extends JFrame
{
  private JTextArea messagesArea;
  private JButton sendButton;
  private JTextField message;
  private JButton startServer;
  private RaspiServer mServer;
  
  private IRTV tv; 
  
  public RaspiServerGUI()
  {

    super("RaspiServer");
    
    tv = new IRTV();

    JPanel panelFields = new JPanel();
    panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.X_AXIS));

    JPanel panelFields2 = new JPanel();
    panelFields2.setLayout(new BoxLayout(panelFields2, BoxLayout.X_AXIS));

    // here we will have the text messages screen
    messagesArea = new JTextArea();
    messagesArea.setColumns(30);
    messagesArea.setRows(10);
    messagesArea.setEditable(false);

    sendButton = new JButton("Send");
    sendButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // get the message from the text view
        String messageText = message.getText();
        // add message to the message area
        messagesArea.append("\n" + messageText);
        // send the message to the client
        mServer.sendMessage(messageText);
        // clear text
        message.setText("");
      }
    });

    startServer = new JButton("Start");
    startServer.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // disable the start button
        startServer.setEnabled(false);

        // creates the object OnMessageReceived asked by the TCPServer
        // constructor
        mServer = new RaspiServer(new RaspiServer.OnMessageReceived()
        {
          @Override
          // this method declared in the interface from TCPServer class is
          // implemented here
          // this method is actually a callback method, because it will run
          // every time when it will be called from
          // TCPServer class (at while)
          public void messageReceived(String message)
          {
            messagesArea.append("\n " + message);
            System.out.println("Received command: " + message);

            try
            {
              // lights
              if (message.contentEquals("LightOn"))
              {
                Runtime.getRuntime().exec("./lightOn.sh");
                System.out.println("Lights are turned on");
              }
              else if (message.contentEquals("LightOff"))
              {
                Runtime.getRuntime().exec("./lightOff.sh");
                System.out.println("Lights are turned off");
              }
              // tv
              else if (message.contentEquals("TVOn"))
              {
                tv.turnon();
                System.out.println("TV is turned on");
              }
              else if (message.contentEquals("TVOff"))
              {
                tv.turnoff();
                System.out.println("TV is turned off");
              }
              else if (message.contains("TVSetVolume"))
              {
                int value = getMessageValue(message);
                tv.setvolume(value);
                System.out.println("TV volume set to" + Integer.toString(value));
              }
              else if (message.contains("TVSetChannel"))
              {
                int value = getMessageValue(message);
                setTVChannel(value);
                System.out.println("TV channel set to" + Integer.toString(value));
              }
              else if (message.contains("TVChannelUp"))
              {
                tv.increachannel();
                System.out.println("TV channel up");
              }
              else if (message.contains("TVChannelDown"))
              {
                tv.decreasechannel();
                System.out.println("TV channel down");
              }
              // speaker
              else if (message.contains("SpeakerSetVolume"))
              {
                int value = getMessageValue(message);
                Runtime.getRuntime().exec("./setVolume.sh " + value + "%");
                System.out.println("Speaker volume set to " + Integer.toString(value));
              }
              // shutter
            }
            catch (IOException e)
            {
              System.out.println("IO Error while exec commands");
            }
          }
        });
        mServer.start();

      }
    });

    // the box where the user enters the text (EditText is called in Android)
    message = new JTextField();
    message.setSize(200, 20);

    // add the buttons and the text fields to the panel
    panelFields.add(messagesArea);
    panelFields.add(startServer);

    panelFields2.add(message);
    panelFields2.add(sendButton);

    getContentPane().add(panelFields);
    getContentPane().add(panelFields2);

    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    setSize(300, 170);
    setVisible(true);
  }
  
  private int getMessageValue(String message)
  {
    String value = message.substring(message.indexOf(":")+1);
    value = value.trim();
    return Integer.parseInt(value);
  }
  
  private void setTVChannel(int channel)
  {
    int curCh = tv.getchannel();
    if (curCh < channel) 
    {     
      for (int i = curCh; i < channel; i++)
      {
        tv.increachannel();
      }
    }
    else if (curCh > channel)
    {
      for (int i = curCh; i > channel; i--)
      {
        tv.decreasechannel();
      }
    }
  }
  
  int map(int x, int in_min, int in_max, int out_min, int out_max)
  {
   return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }
}