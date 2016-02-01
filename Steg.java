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
		char[] payloadInAscii = payload.toCharArray();
		int[][] pixelMap = getPixels(cover_filename);
		
		
		
		return "Stego_" + cover_filename;
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
	 * @return the byte array containing the values of the RGB values in bytes
	 */
	public byte[] convertPixel(int i)
	{
		byte alpha, red, green, blue;
		
		alpha = (byte)((i >>> 24)); //Alpha (Not needed...?)
		red = (byte)((i >>> 16)); //Red
		green = (byte)((i >>> 8)); //Green
		blue = (byte)(i); //Blue
		
		byte[] pixelInBytes = {red, green, blue};
		
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
		return 0;
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
		char[] payloadChars = payload.toCharArray();
		String builder = "";
		for(int i = 0; i < payloadChars.length; i++)
			builder += String.format("%8s", Integer.toBinaryString(payloadChars[i]).replace(' ', '0'));
		
		//Converting the binary string into individual bits
		char[] bitLength = builder.toCharArray();
		int bitsPerPixel = 3;
		int numBits = bitLength.length;
		
		System.err.println(builder);
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
		System.out.println(s.pixelsNeededString("This is a message"));
	}
}
