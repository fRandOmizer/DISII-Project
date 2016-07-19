package com.distopia.raspiserver;

import java.io.IOException;
import java.io.OutputStream;

public class channelcontrol
{

  OutputStream output;

  channelcontrol(OutputStream stream)
  {
    output = stream;
  }

  public void increase()
  {
    try
    {
      output.write('3');
    }
    catch (IOException e)
    {
      System.out.println("Fehler beim Ein- und Ausschalten");

    }
  }

  public void decrease()
  {
    try
    {
      output.write('4');
    }
    catch (IOException e)
    {
      System.out.println("Fehler beim Ein- und Ausschalten");

    }
  }
}
