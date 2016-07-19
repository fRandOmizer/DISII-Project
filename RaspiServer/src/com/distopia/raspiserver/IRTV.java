package com.distopia.raspiserver;

import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class IRTV
{

  public OutputStream output;
  int volume;
  int channel;
  boolean onoff;
  boolean mute;
  onoffcontrol onoffc;
  volumecontrol volumec;
  channelcontrol channelc;

  public IRTV()
  {
    output = this.setupCOM();
    onoffc = new onoffcontrol(output);
    volumec = new volumecontrol(output);
    channelc = new channelcontrol(output);

    volume = 10;
    onoff = false;
    mute = false;
    channel = 1;

  }

  private OutputStream setupCOM()
  {
    // Find all available ports
    SerialPort[] computerPorts = SerialPort.getCommPorts();

    // Select COMPort
    SerialPort selectedPort = null;

    for (SerialPort p : computerPorts)
    {
      // if (p.getSystemPortName().equals("COM3")) //for windows...
      // if (p.getSystemPortName().equals("/dev/ttyAMA0")) //for pi rx tx
      if (p.getSystemPortName().equals("/dev/ttyUSB0")) // for pi USB
      {
        selectedPort = p;
      }
    }

    // Try to open port
    if (selectedPort == null)
    {
      System.out.println("Failed to find port");
      return null;

    }
    else if (selectedPort.openPort())
    {
      System.out.println(selectedPort.getSystemPortName() + " successfully opened.");

    }
    else
    {
      System.out.println(selectedPort.getSystemPortName() + " failed to open.");
      return null;
    }

    // Set port to scanner mode
    selectedPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

    OutputStream output = selectedPort.getOutputStream();
    System.out.println("Port ini complete");
    return output;

  }

  public boolean getonoff()
  {
    return onoff;
  }

  public int getvolume()
  {
    return volume;
  }

  public int getchannel()
  {
    return channel;
  }

  public void setonoff(boolean on)

  {
    if (onoff != on)
    {
      onoff = on;
    }

  }

  public void turnon()

  {
    if (onoff == false)
    {
      onoffc.turnonoff();
      onoff = true;
    }
  }

  public void turnoff()
  {
    if (onoff == true)
    {
      onoffc.turnonoff();
      onoff = false;
    }
  }

  public void mute()
  {
    volumec.turnonoff();
  }

  public void increasevolume()

  {
    volumec.increase();
    volume += 1;
  }

  public void decreasevolume()
  {
    if (volume > 0)
    {
      volumec.decrease();
      volume -= 1;
    }
  }

  public void increachannel()
  {
    channelc.increase();
    channel += 1;
  }

  public void decreasechannel()
  {
    if (channel > 0)
    {
      channelc.decrease();
      channel -= 1;
    }
  }

  public void setvolume(int toset)
  {
    if (toset > volume)
    {
      for (int i = (toset - volume); i > 0; i--)
      {
        volumec.decrease();
      }
    }
    else
    {
      for (int i = (volume - toset); i > 0; i--)
      {
        volumec.increase();
      }
    }
    volume = toset;

  }

}
