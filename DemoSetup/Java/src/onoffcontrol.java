package IRTV;

import java.io.IOException;
import java.io.OutputStream;
	
	
public class onoffcontrol 
{

	OutputStream output;
	
	onoffcontrol(OutputStream stream)
	{
		output = stream;
	}
	
	public void turnonoff()
	{
		try {
    		output.write('0');
		} catch (IOException e) {
			System.out.println("Fehler beim Ein- und Ausschalten");
		
		}
	}
}
