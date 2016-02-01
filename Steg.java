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
	private BufferedImage img; 
	
	private final int START_POS = 12;
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
		int pixelsNeeded = pixelsNeededString(payload);
		
		char[] payloadBytes = payload.toCharArray();
		
		int[][] pixelMap = getPixels(cover_filename);
		
		for(int i = START_POS; i < START_POS+pixelsNeeded; i++)
		{
			int[] convertPixel = convertPixel(pixelMap[i][START_POS]);
		}
		
		return "stego_" + cover_filename;
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
		return null;
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
	public int[][] getPixels(String imageName)
	{	
		try
		{
			img = ImageIO.read(new File(imageName));
			int[][] pixel = new int[img.getWidth()][img.getHeight()];
			for(int i = 0; i < img.getWidth(); i++)
				for(int j = 0; j < img.getHeight(); j++)
					pixel[i][j] = img.getRGB(i, j);

			return pixel;
		}
		catch(IOException e)
		{
			System.out.println("No file");
			return null;
		}
	}
	
	/**
	 * Method to convert a pixel into an array of bytes
	 * @param i the pixel integer
	 * @return the byte array containing the RGB values in bytes
	 */
	public int[] convertPixel(int i)
	{
		int alpha, red, green, blue;
		
		alpha = ((i & 0xFF000000) >>> 24); //Alpha (Not needed...?)
		red = ((i & 0x00FF0000) >>> 16); //Red
		green = ((i & 0x0000FF00) >>> 8); //Green
		blue = (i & 0x000000FF); //Blue
		
		int[] pixelInBytes = {red, green, blue};
		
		return pixelInBytes;
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
	public int getLSB(int byteIn)
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
		String builder = "";
		for(int i = 0; i < payloadBytes.length; i++)
			builder += String.format("%8s", Integer.toBinaryString(payloadBytes[i]).replace(' ', '0'));
		
		//Converting the binary string into individual bits
		char[] bitLength = builder.toCharArray();
		int bitsPerPixel = 3;
		int numBits = bitLength.length;
		
		//if(numBits % bitsPerPixel > img.getHeight() || numBits % bitsPerPixel > img.getWidth())
			//return 0;
		//System.err.println(builder);
		//Calculating the number of pixels needed
		if(numBits % bitsPerPixel == 0)
			return numBits/bitsPerPixel;
		else
			return numBits/bitsPerPixel + 1;
	}
	
	/**
	 * Main Method to call the program 
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		Steg s = new Steg();
		String payload = "This is a message.";
		byte[] pBytes = payload.getBytes();
		for(int i = 0; i < pBytes.length; i++)
			System.out.println(pBytes[i]);
		System.err.println("\n" + s.pixelsNeededString(payload));
		
		
	}
}
