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
	public String hideString(String payload, String cover_filename)
	{
		//Byte array to store the payload
		byte[] payloadBytes = payload.getBytes();
		//Convert the payload into a BitSet, containing the bits of the payload
		BitSet payloadBits = BitSet.valueOf(payloadBytes);
		
		//Stores the length of the payload in bytes and converts it to an array of 4 bytes
		int payloadLength = payloadBytes.length;
		byte[] payloadSize = {
				(byte)(payloadLength >>> 24),
				(byte)(payloadLength >>> 16),
				(byte)(payloadLength >>> 8),
				(byte)(payloadLength)
		};
		
		//Converts the 4 byte array into a BitSet, containing the bits of the integer
		BitSet lengthBits = BitSet.valueOf(payloadSize);
		
		//Array to store the bytes of the image
		byte[] imgBytes = readImage(cover_filename);
		
		//Variables to hold the value of the bit of the BitSets and counter for iterating through the BitSets
		int counter = 0;
		int bit = 0;
		//Loop to hide the length of the payload in the first 4 bytes of the picture
		int payloadStart = START_POS + sizeBitsLength;
		for(int i = START_POS; i < START_POS + sizeBitsLength; i++)
		{
			//As BitSets hold the values of the bits as booleans, this converts the boolean to an integer value 
			//(1 or 0 for true or false respectively)
			if(lengthBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int lengthByte = swapLsb(bit, (int)imgBytes[i]);
			//Sets the byte in the image to the altered byte
			imgBytes[i] = (byte)lengthByte;
			//Increments the BitSet counter
			counter++;
		}
		
		//resets the counter for the BitSets
		counter = 0;
		//resets the bit for the bitset
		bit = 0;
		//Loop to hide the payload after the 4 length bytes
		for(int i = payloadStart; i < payloadStart + payloadBits.length(); i++)
		{
			if(payloadBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int newByte = swapLsb(bit, (int)imgBytes[i]);
			
			imgBytes[i] = (byte)newByte;
			counter++;
		}

		String outputFileName = "stego_" + cover_filename;
		writeImage(imgBytes, outputFileName);
		
		return outputFileName;
	} 
	
	/**
	The extractString method should extract a string which has been hidden in the stegoimage
	@param the name of the stego image 
	@return a string which contains either the message which has been extracted or 'Fail' which indicates the extraction
	was unsuccessful
	*/
	public String extractString(String stego_image)
	{
		//Array to store the bytes of the stego image
		byte[] stegPixels = readImage(stego_image);
		
		//Variables to hold the value of the bit of the BitSet and the counter for iterating through the BitSet
		int bit = 0;
		int counter = 0;
		
		//BitSets to hold the bits for the payload and the payload's size
		BitSet stringBits = new BitSet();
		BitSet lengthBits = new BitSet();
		
		//Loop for extracting the payload size from the first 4 bytes of the image
		for(int i = START_POS; i < START_POS + sizeBitsLength; i++)
		{
			if(lengthBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int getSizeBit = getLSB(stegPixels[i]);
			if(bit != getSizeBit)
				lengthBits.flip(counter);
			
			counter++;
		}
		
		//Array to convert the bits of the payloadSize BitSet into an array of 4 bytes
		byte[] lengthBytes = lengthBits.toByteArray();
		//Converts the 4 byte array into a value, giving the length of the payload in bytes
		int payloadLength = ((lengthBytes[0] & 0xFF) << 24) | ((lengthBytes[1] & 0xFF) << 16) |
		          ((lengthBytes[2] & 0xFF) << 8)  | (lengthBytes[3] & 0xFF);
		
		
		System.out.println(payloadLength);
		//Stores teh start point for the extraction to begin
		int extractStart = START_POS + sizeBitsLength;
		counter = 0; //Restarts the counter for the bitset
		bit = 0; //resets the value of the bit
		
		//Loop for extracting the hidden string
		for(int i = extractStart; i < extractStart + (payloadLength*byteLength-1); i++)
		{
			if(stringBits.get(counter) == false)
				bit = 0;
			else
				bit = 1;
			
			int getBit = getLSB(stegPixels[i]);
			
			if(bit != getBit)
				stringBits.flip(counter);
			
			counter++;
		}
		
		//Converts the BitSet containing the payload bits into an array of bytes
		byte[] stringBytes = stringBits.toByteArray();
		//Converts the byte array containing the payload back into a string
		String extracted = new String(stringBytes);
		
		System.out.println(extracted);
		return extracted;
	}

	
	/**
	The hideFile method hides any file (so long as there's enough capacity in the image file) in a cover image

	@param file_payload - the name of the file to be hidden, you can assume it is in the same directory as the program
	@param cover_image - the name of the cover image file, you can assume it is in the same directory as the program
	@return String - either 'Fail' to indicate an error in the hiding process, or the name of the stego image written out as a
	result of the successful hiding process
	*/
	public String hideFile(String file_payload, String cover_image)
	{
		FileReader fr = new FileReader(file_payload);
		byte[] imgBytes = readImage(cover_image);
		//System.out.println("Image size Bytes:  " + imgBytes.length);
		System.out.println( "File Size: " + fr.getFileSize());
		int payloadLength = fr.getFileSize() + sizeBitsLength + extBitsLength;
		System.out.println("Payload size: " + payloadLength);
		
		
		for(int i = START_POS; i < START_POS + payloadLength; i++)
		{
			int newByte = swapLsb(fr.getNextBit(), (int)imgBytes[i]);
			imgBytes[i] = (byte)newByte;
		}
		
		String outputFileName = "file_stego_" + cover_image;
		writeImage(imgBytes, outputFileName);
		
		return outputFileName;
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
		byte[] stegBytes = readImage(stego_image);
		int upToSize = START_POS + sizeBitsLength;
		int upToExt = upToSize + extBitsLength;
		int bit = 0, counter = 0;
		
		/**
		 * Gets the payload size in bits; stored in payloadLength 
		 */
		BitSet sizeBits = new BitSet();
		for(int i = START_POS; i < upToSize; i++)
		{
			if(sizeBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int lsb = getLSB(stegBytes[i]);
			if(bit != lsb)
				sizeBits.flip(counter);
			
			counter++;
		}
		byte[] sizeBytes = sizeBits.toByteArray();
		int payloadSize = ((sizeBytes[0] & 0xFF) << 24) | ((sizeBytes[1] & 0xFF) << 16) |
				((sizeBytes[2] &0xFF) << 8) | ((sizeBytes[3] & 0xFF));
		System.out.println("Payload size: " + payloadSize);
		
		
		/**
		 * Gets the payload's file extention as a string; stored in fileExt.
		 */
		BitSet extBits = new BitSet();
		bit = 0; counter = 0;
		for(int i = upToSize; i < upToExt; i++)
		{
			if(extBits.get(counter) == true)
				bit = 1;
			else
				bit = 0;
			
			int lsb = getLSB(stegBytes[i]);
			if(bit != lsb)
				extBits.flip(counter);
			
			counter++;
		}
		
		byte[] extBytes = extBits.toByteArray();
		char[] extChars = new char[extBitsLength/8];
		for(int i = 0; i < extChars.length; i++)
		{
			if(i >= extBytes.length)
				extChars[i] = 0;
			else
				extChars[i] = (char)extBytes[i];
		}
		String fileExt = new String(extChars);
		System.out.println("File Ext: " + fileExt);
		
		
		/**
		 * Gets the payload as a byte array; Stored in payloadBytes
		 */
		BitSet payloadBits = new BitSet();
		bit = 0; counter = 0;
		for(int i = upToExt; i < payloadSize; i++)
		{
			if(payloadBits.get(counter) == true)
				bit = 1; 
			else
				bit = 0;
			
			int lsb = getLSB(stegBytes[i]);
			if(bit != lsb)
				payloadBits.flip(counter);
			
			counter++;
		}
		byte[] payloadBytes = payloadBits.toByteArray();
		String outputFileName = "Extracted_File" + fileExt;
		
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
	
	/**
	 * Method to write out the new steganography image
	 * @param img the byte array containing the bytes of the image
	 * @param outputName the name to call the output file
	 * @return the new altered image.
	 */
	public BufferedImage writeImage(byte[] img, String outputName)
	{
		try
		{
			imgOut = ImageIO.read(new ByteArrayInputStream(img));
			ImageIO.write(imgOut, "bmp", new File(outputName));
			return imgOut;
		}
		catch(IOException e)
		{
			System.out.println("Error: Cannot create file");
			return null;
		}
	}
	
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
	 * Main Method to call the program 
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		Steg s = new Steg();
		//s.hideFile("Test2.txt", "lena.bmp");
		s.extractFile("file_stego_lena.bmp");
		
	}
}
