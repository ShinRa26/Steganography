#author Dr Rosanne English 19.06.13
from PIL import Image 

print "stego program"
img = Image.new( 'RGB', (255,255), "black") # create a new black image
NUM_HEADER_PIX=12 # use the first 12 pixels for storing length
#of message

#asks the user for the name of the image to be used as a cover
#works
def getCover():
	print "what image do you want to use as a cover?"
	imageName = raw_input()
	return imageName
#asks the user for a sentence to hide
#works 
def getPayload():
	print "what text do you want to hide?"
	toHide = raw_input()
	return toHide

def loadImage(imName):
    img= Image.open(imName)
    img= img.convert('RGB')
    #img.save('loaded.bmp')
    return img

#loads the image into memory and returns the pixel map
#works 
def getPixelMap(img):
	#img= Image.open(imName)
	#img= img.convert('RGB')
	pixels=img.load()
	#img.save('loaded.bmp')
	return pixels

#converts a string of ascii characters to a binary string
#works    
def asciiToBinary(payload):
        #print 'payload is ' + payload
        #bin(ord('a'))[2:].zfill(8) #takes a character and changes it to ascii
        #then changes it to binary, bin returns leading 0b decorator so
        #[2:] deducts this, zfill(8) gives leading 0s to make to 8 bits
        #bin takes the ascii value and switches to binary
        index = 0
        binaryPayload = ''
        while index < len(payload):
                letter = payload[index]
                #print letter
                nextBits= bin(ord(letter))[2:].zfill(8)
                binaryPayload +=nextBits
                index +=1
        return binaryPayload

#converts a string of binary back to ASCII
#works
def binaryToAscii(extracted):
        #print 'extracted ' + extracted
        bitsPerByte=8
        splits=[extracted[x:x+bitsPerByte] for x in range(0,len(extracted),bitsPerByte)]
        #print splits
        decoded=''
        for byte in splits:
                decoded+=chr(int(byte,2))
        return decoded

def hide(img, payload):
        #print Image.open(coverIm).size
        #get height, width and pixel map from cover
        #pixelMap=getPixelMap(coverIm)
        pixels=getPixelMap(img)
        width, height =img.size
        
        #print "width is " , w
        #print "height is " , h
        #calculate number of rows needed so you
        #don't go through extra rows
        pixelsNeeded = calculatePixelsNeeded(payload)
        rowsNeeded = 0
        
        payloadInBits=asciiToBinary(payload)
        currentPayloadBit = 0
        numBitsToHide = getNumBits(payloadInBits)
        if (pixelsNeeded % width == 0):
                rowsNeeded = pixelsNeeded /  width
        else:
                rowsNeeded = pixelsNeeded /  width + 1

       #first go through the first 12 pixels and store the
       #num of pixels
        bitCount=0#keeps count of the number of bits in the payloadSize string
   
        #get the number of bits to hide, put it in a binary
        #format, drop the 0b which bin method gives
        #pad it out with 0s on the LHS till it's size 36 bits
        payloadSize= bin(numBitsToHide)[2:].zfill(NUM_HEADER_PIX*3)
        print 'payload size is ', payloadSize
        #now get the first 12 pixels, assumes image has width >=12
        for col in range (0, NUM_HEADER_PIX):
            #get each pixel out
            r, g, b = pixels[col,0]
            #print r, g, b
            for i in range(1,4):
                    sizeBit=getBit(payloadSize, bitCount)
                    bitCount=bitCount + 1
                    #print 'current size bit is ' , bitCount , sizeBit
                    if(i ==1):
                            r=flipBits(r,sizeBit)
                    if(i == 2):
                            g=flipBits(g,sizeBit)
                    if(i ==3):
                            b = flipBits(b,sizeBit)
            #print 'altered' , r, g, b
            pixels[col,0]= (r,g, b)#replace value
                
        for h in range (0, rowsNeeded):#go through the rows needed
            for w in range(0, width):#go through the columns
                #if we are in header pixel then skip it
                if(h == 0 and w < NUM_HEADER_PIX):
                        r , g, b = pixels[w,h]
                        #print r, g, b
                else:        
                        r , g, b = pixels[w,h]
                        #print r, g, b
            #get red value flip last bit to match next bit in payload
                        for i in range (0,3):
                            if(currentPayloadBit < numBitsToHide):
                                if(i == 0):
                                    r=flipBits(r,getBit(payloadInBits, currentPayloadBit))
                                    currentPayloadBit = currentPayloadBit + 1
                                if(i == 1):
                                    g=flipBits(g,getBit(payloadInBits, currentPayloadBit))
                                    currentPayloadBit = currentPayloadBit + 1
                                if(i == 2):
                                    b=flipBits(b,getBit(payloadInBits, currentPayloadBit))
                                    currentPayloadBit = currentPayloadBit + 1
                            else:
                                break
                        #print 'altered' , r, g, b
                        pixels[w,h]= (r,g, b)#replace value
        writeImage(img)
        
def extract(img):
        pixels=getPixelMap(img)
        width, height =img.size
        

        payloadBits = ''
        pixelCount=0 # keep track of the number of pixels used for lsbs
        NUM_HEADER_PIX=12
        sizeBits = ''
        #now get the first 12 pixels, assumes image has width >=12
        for col in range (0, NUM_HEADER_PIX):
            #get each pixel out
            r, g, b = pixels[col,0]
            #print r, g, b
            #get the lsbs and add to a string storing size bits
            sizeBits +=  str(getLSB(r))
            sizeBits +=  str(getLSB(g))
            sizeBits +=  str(getLSB(b))

        #now take the sizeBitsString and turn it back into a number
        #store that number in numPixelsUsed
        #print 'size bits were', sizeBits
        numPixelsUsed=int(sizeBits,2)
        #print 'pixels used were' , numPixelsUsed
        #8 bits per character in the payload
        #numChars=2
        #numBitsPerChar=8
        #numPixelsUsed = numChars * numBitsPerChar
        rowsNeeded = 0

        if (numPixelsUsed % width == 0):
                rowsNeeded = numPixelsUsed /  width
        else:
                rowsNeeded = numPixelsUsed /  width + 1
                
        for h in range (0, rowsNeeded):#go through the rows needed
            for w in range(0, width):#go through the columns
                #if we are in header pixel then skip it
                if(h == 0 and w < NUM_HEADER_PIX):
                        r , g, b = pixels[w,h]
                        #print r, g, b
                else:        
                        r , g, b = pixels[w,h]
                        #print r, g, b
            #get last bit for each of the bytes and add to payloadBits
                        if(pixelCount > numPixelsUsed):
                                break
                        else:
                                payloadBits +=  str(getLSB(r))
                                pixelCount = pixelCount + 1
                        
                        if(pixelCount > numPixelsUsed):
                                break
                        else:
                                payloadBits +=  str(getLSB(g))
                                pixelCount = pixelCount + 1

                        if(pixelCount > numPixelsUsed):
                                break
                        else:
                                payloadBits += str(getLSB(b))
                                pixelCount = pixelCount + 1

        #print 'payload bits are' + payloadBits
        extracted = binaryToAscii(payloadBits)
        print 'the hidden message is ' + extracted
        
def writeImage(img):
        print 'writing stegoimage out, it''s called stego.bmp'
        img.save('stego.bmp')

#returns the length of a string of payload in bits
#works
def getNumBits(payloadInBits):
        return len(payloadInBits)

#returns a specific bit as an int in string of payload bits
#works
def getBit(payloadInBits, bitNum):
        result = int(payloadInBits[bitNum])
        #print 'bit ' , bitNum , 'is ' , result
        return result
    
        
#calulates the number of pixels needed to hide the payload
#works 
def calculatePixelsNeeded(payload):
        payloadInBits=asciiToBinary(payload)
        numBits = len(payloadInBits)
        bitsPerPixel = 3
        if (numBits % bitsPerPixel ==0):
                return numBits/bitsPerPixel
        else:
                return numBits/bitsPerPixel + 1
        
#flip bits takes a byte and flips the last bit to match the bit to hide
#tested and working
def flipBits(byt,bitToHide):
        #print 'flipping bits'
        lsb=byt & 0x1#get the lsb
        if(bitToHide != lsb):
            if(bitToHide ==0):
                #change lsb to 0
                byt &= ~ 0x1;
            else:
                #change lsb to 1
                byt |= 0x1;
        return byt       
        
def getLSB(byt):
        lsb=byt & 0x1#get the lsb
        return lsb

def main():
        print 'do you want to hide a message? put yes to hide'
        answer=raw_input()
        if (answer == 'yes'):  
            coverIm=getCover()
            img = loadImage(coverIm)
            payload = getPayload()
            hide(img , payload)
        else:
            print 'ok, let\'s extract a message'    
            coverIm=getCover()
            img = loadImage(coverIm)        
            extract (img)
       #payloadSize= bin(27)[2:].zfill(36)
       #print payloadSize
       #res=int(payloadSize,2)
       #print res
       
main()

	
	
