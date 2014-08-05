// Program arguments <iteration number> <adjusted URI>

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.*;

class PaddingOracle {
	static int code = 0;
	static String modifiedBlock = "58b1ffb4210a580f748b4ac714c001bd";
	static String adjustedCipher = "";
	static List<String> origList = new ArrayList<String>();
	static List<String> answerList = new ArrayList<String>();

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static void main(String[] args) throws Exception {
		String cipherStr = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";
		byte[] cipher = new String(cipherStr.toCharArray()).getBytes("UTF-8");
		byte[] iv = new byte[32];
		System.arraycopy(cipher, 0, iv, 0, iv.length);
		byte[] b1 = new byte[32]; 	
		System.arraycopy(cipher, 32, b1, 0, iv.length);
		byte[] b2 = new byte[32]; 
		System.arraycopy(cipher, 64, b2, 0, iv.length);
		byte[] b3 = new byte[32]; 
		System.arraycopy(cipher, 96, b3, 0, iv.length);	
		String guess;
		
		int iteration = Integer.valueOf(args[0]);
		
		for( int i = 0; i < 16; i++ ) {
			for( int j = 0; j < 16; j++ ) {
				guess = PaddingOracle.indexToString( i, j );
				// Maybe turn the following into a method:
				String result = PaddingOracle.modifiedBlock.substring(0,PaddingOracle.modifiedBlock.length()-((iteration)*2))+ PaddingOracle.xorHex(PaddingOracle.xorHex(guess, "0" + Integer.toHexString(iteration) ), 
							PaddingOracle.modifiedBlock.substring(PaddingOracle.modifiedBlock.length()-((iteration)*2),PaddingOracle.modifiedBlock.length()-((iteration-1)*2))) 
							+ args[1]; // adjusted bytes from last iterations
				
				if( PaddingOracle.sendRequest( result + new String(b2, "UTF-8") ) == 404 ) {
					System.out.println( "Guess: " + hexArray[i] + hexArray[j] );
					System.out.println( "The original cipher byte was: " + PaddingOracle.modifiedBlock.substring(PaddingOracle.modifiedBlock.length()-((iteration)*2),
								PaddingOracle.modifiedBlock.length()-((iteration-1)*2)) ); // the original cipher byte
					System.out.println( "The adjusted cipher byte is: " + PaddingOracle.xorHex(PaddingOracle.xorHex(guess,  "0" + Integer.toHexString(iteration) ), 
							PaddingOracle.modifiedBlock.substring(PaddingOracle.modifiedBlock.length()-((iteration)*2),PaddingOracle.modifiedBlock.length()-((iteration-1)*2))));	// the resultant cipher byte
				}
			}
		}
	}
	
	// Auxiliary method to update all previous cipher bytes; for automation.
	// Takes the URI and returns it modified for the next iteration; call after every iteration provided the block of end cipher has been adjusted.
	// adjustedCipher is allowed to be as long as possible.
	public static String updateCipher( int iteration ) {
		int num = iteration - 1; // the number for the last block
		if( num == 0 ) return PaddingOracle.modifiedBlock; // if first iteration, return the same string
		
		String s = PaddingOracle.modifiedBlock.substring(0, PaddingOracle.modifiedBlock.length() - (iteration)) + PaddingOracle.adjustedCipher;
		return s;
	}
	
	// To be recursively called, adjusting the end cipher block.
	public static String adjustCipher( int iteration ) {
		String s = ""; 
		
		for( int i = 0; i < iteration; i++ ) {
			s = s + PaddingOracle.xorHex("0" + Integer.toHexString(iteration+1),PaddingOracle.xorHex(PaddingOracle.origList.get(i),PaddingOracle.answerList.get(i)));
		}
		return s; // not defined yet	
	}
	
	public static String xorHex(String a, String b) {
		char[] chars = new char[a.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = PaddingOracle.toHex(fromHex(a.charAt(i)) ^ PaddingOracle.fromHex(b.charAt(i)));
		}
		return new String(chars);
	}

	private static int fromHex(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		}
		if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		}
		throw new IllegalArgumentException();
	}	

	private static char toHex(int nybble) {
		if (nybble < 0 || nybble > 15) {
			throw new IllegalArgumentException();
		}
		return "0123456789ABCDEF".charAt(nybble);
	}
	
	// Auxiliary method that assumes hexArray is the array to index from, takes two indexed bits from the array to make a byte from.
	public static String indexToString( int first, int second ) throws Exception {
		char[] c = { hexArray[first], hexArray[second] };
		return new String(c);
	}
	
	// Auxiliary method to test the URL for 404 (good padding) or a 405 (bad padding), given the cipher/string to request with. Returns the response code (403/404).
	public static int sendRequest( String test ) throws Exception {
		URL url;
		HttpURLConnection connection = null;
		
		try {
			url = new URL( "http://crypto-class.appspot.com/po?er=" + test );
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.connect();
			PaddingOracle.code = connection.getResponseCode();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return PaddingOracle.code;
	}
}

// URL is 128 bits, therefore 4 blocks of 32 bits.