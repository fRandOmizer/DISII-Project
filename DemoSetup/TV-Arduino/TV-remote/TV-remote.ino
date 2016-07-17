//Turn Samsung TV on/off repeatedly to test program. IR LED connected to pin 3.


#include <IRremote.h>


#define POWER         0xE0E040BF
#define channel_up    0xE0E048B7
#define channel_down  0xE0E008F7
#define volume_up     0xE0E0E01F
#define volume_down   0xE0E0D02F
#define mute          0xE0E0F00F


#define SAMSUNG_BITS  32

int serialData = 0;
IRsend irsend;
 
void setup()
{
  Serial.begin(9600);
  pinMode (3, OUTPUT);  //output as used in library
}//end of setup


void loop()
{
  if (Serial.available() == 1) //check for input
  {serialData = Serial.read();   

    switch (serialData) 
    {
        case 48:
            irsend.sendSAMSUNG(POWER, SAMSUNG_BITS);
            break;
          
        case 49:
            irsend.sendSAMSUNG(volume_up, SAMSUNG_BITS);
            break;

        case 50:
            irsend.sendSAMSUNG(volume_down, SAMSUNG_BITS);
            break;
        case 51:
            irsend.sendSAMSUNG(channel_up, SAMSUNG_BITS);
        break;
        case 52:
            irsend.sendSAMSUNG(channel_down, SAMSUNG_BITS);
        break;
        case 53:
            irsend.sendSAMSUNG(mute, SAMSUNG_BITS);
        break;
         
        default:
        break;
    }
    
  }
}//end of loop

