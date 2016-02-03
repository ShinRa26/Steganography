import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

public class Steg 
{
	/**
	 * A constant to hold the number of bits per byte
	 */
	private final int byteLength=8;

	/**
	 * A constant to hold the number of bits used to store the size of the file extracted
	 */
	protected final int sizeBitsLength=32;
	/**
	 * A constant to hold the number of bits used to store the extension of the file extracted
	 */
	protected final int extBitsLength=64;

	/**
	 * Instance variable to hold the image.
	 */
	private byte[] img; 
	
	/**
	 * Starting position for hiding the payload in the picture (Skips the header of the bmp)
	 */
	private final int START_POS = 54;
	
	/**
	 * Instance variable to store the Image for output
	 */
	private BufferedImage imgOut;
	
	/**
	 * Variable to hold the number of pixels needed for the String operations
	 */
	private int stringPixelsNeeded;
	
	/**
	* Default constructor to create a steg object, doesn't do anything - so we actually don't need to declare it explicitly. Oh well. 
	*/

	public Steg(){}
	
	/**
	A method for hiding a string in an uncompressed image file such as a .bmp or .png
	You can assume a .bmp will be used
	@param cover_filename - the filename of the cover image as a string 
	@param payload - the string which should be hidden in the cover image.
	@return a string which either contains 'Fail' or the name of the stego image which has been 
	written out as a result of the successful hiding operation. 
	You can assume that the images are all in the same directory as the java files
	*/
	//TODO you must write this method
	public String hideString(String payload, String cover_filename)
	{
		pixelsNeededString(payload);
		System.out.println(pixelsNeededString(payload));
		byte[] payloadBytes = payload.getBytes();
		BitSet payloadBits = BitSet.valueOf(payloadBytes);
				
		byte[] imgBytes = readImage(cover_filename);
		
		int counter = 0;
		
		for(int i = START_POS; i < START_POS + (stringPixelsNeeded*3); i++)
		{
			int bit = 0;
			
			if(payloadBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int newByte = swapLsb(bit, (int)imgBytes[i]);
			
			imgBytes[i] = (byte)newByte;
			counter++;
		}

		String outputFileName = "stego_" + cover_filename;

		try
		{
			imgOut = ImageIO.read(new ByteArrayInputStream(imgBytes));
			ImageIO.write(imgOut, "bmp", new File(outputFileName));
		}
		catch(IOException e)
		{
			return "Fail";
		}

		
		return outputFileName;
	} 
	
	//TODO you must write this method
	/**
	The extractString method should extract a string which has been hidden in the stegoimage
	@param the name of the stego image 
	@return a string which contains either the message which has been extracted or 'Fail' which indicates the extraction
	was unsuccessful
	*/
	public String extractString(String stego_image)
	{
		byte[] stegPixels = readImage(stego_image);
		
		for(int i = START_POS; i < START_POS + (stringPixelsNeeded*3); i++)
		{
			
		}

		return "";
	}

	//TODO you must write this method
	/**
	The hideFile method hides any file (so long as there's enough capacity in the image file) in a cover image

	@param file_payload - the name of the file to be hidden, you can assume it is in the same directory as the program
	@param cover_image - the name of the cover image file, you can assume it is in the same directory as the program
	@return String - either 'Fail' to indicate an error in the hiding process, or the name of the stego image written out as a
	result of the successful hiding process
	*/
	public String hideFile(String file_payload, String cover_image)
	{
		return "";
	}

	//TODO you must write this method
	/**
	The extractFile method hides any file (so long as there's enough capacity in the image file) in a cover image

	@param stego_image - the name of the file to be hidden, you can assume it is in the same directory as the program
	@return String - either 'Fail' to indicate an error in the extraction process, or the name of the file written out as a
	result of the successful extraction process
	*/
	public String extractFile(String stego_image)
	{
	
		return "";
	}

	/**
	 * Method to get the image file and convert the image pixels into an array of integers
	 * @param imageName the name of the image
	 * @return the image represented as an integer array
	 */
	public byte[] readImage(String imageName)
	{	
		try
		{
			File image = new File(imageName);
			FileInputStream in = new FileInputStream(image);
			img = new byte[(int) image.length()];
			in.read(img);
			in.close();
			return img;
		}
		catch(IOException e)
		{
			System.out.println("No file");
			return null;
		}
	}
	
	//TODO you must write this method
	/**
	 * This method swaps the least significant bit with a bit from the filereader
	 * @param bitToHide - the bit which is to replace the lsb of the byte of the image
	 * @param byt - the current byte
	 * @return the altered byte
	 */
	public int swapLsb(int bitToHide,int byt)
	{	
		if(bitToHide != (byt&0x1))
		{
			if(bitToHide == 0)
				byt&=~0x1;
			else
				byt |= 0x1;
		}
		return byt;
	}
	
	/**
	 * Method to get the LSB of a byte
	 * @param byteIn the byte 
	 * @return the least significant bit of the byte
	 */
	public int getLSB(byte byteIn)
	{
		int lsb = byteIn&0x1;
		return lsb;
	}
	
	
	/**
	 * Method to calculate the number of pixels needed for a string payload
	 * @param payload the String containing the payload
	 * @return the number of pixels needed to hide the payload
	 */
	public int pixelsNeededString(String payload)
	{
		//Converting payload into a string of binary digits
		byte[] payloadBytes = payload.getBytes();
		BitSet payloadBits = BitSet.valueOf(payloadBytes);
		
		int bitsPerPixel = 3;
		int numBits = payloadBits.length();
		
		//Calculating the number of pixels needed
		if(numBits % bitsPerPixel == 0)
		{
			stringPixelsNeeded = numBits/bitsPerPixel;
			return stringPixelsNeeded;
		}
		else
		{
			stringPixelsNeeded = numBits/bitsPerPixel + 1;
			return stringPixelsNeeded;
		}
	}
	
	/**
	 * Main Method to call the program 
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		Steg s = new Steg();
		s.extractString("stego_tiger.bmp");
	}
}
