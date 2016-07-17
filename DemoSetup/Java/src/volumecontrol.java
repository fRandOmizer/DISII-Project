package IRTV;

import java.io.IOException;
import java.io.OutputStream;
	
public class volumecontrol 
{		

		OutputStream output;
		
		volumecontrol(OutputStream stream)
		{
			output = stream;
		}
		
		public void turnonoff()
		{
			try {
	    		output.write('5');
			} catch (IOException e) {
				System.out.println("Fehler beim Ein- und Ausschalten");
			
			}
		}
		
		public void increase()
		{
			try {
	    		output.write('1');
			} catch (IOException e) {
				System.out.println("Fehler beim Ein- und Ausschalten");
			
			}
		}
		
		public void decrease()
		{
			try {
	    		output.write('2');
			} catch (IOException e) {
				System.out.println("Fehler beim Ein- und Ausschalten");
			
			}
		}
}
