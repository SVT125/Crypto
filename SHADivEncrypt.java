import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.io.*;

class SHADivEncrypt {
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

	public static void main( String[] args ) throws Exception {
		int leftoverLength = 955;
		Path vid = Paths.get("C:\\Users\\James\\Downloads\\challengevid.mp4");
		byte[] bytevid = Files.readAllBytes(vid);
		int wholeBlockNumber = (bytevid.length - leftoverLength)/ 1024;
		byte[] leftoverVid = Arrays.copyOfRange( bytevid, bytevid.length - leftoverLength, bytevid.length );
		List<Byte> finalHashList = new ArrayList<Byte>();
		finalHashList = SHADivEncrypt.appendArray( finalHashList, leftoverVid);
		PrintWriter writer = new PrintWriter("SHAintermediates.txt", "UTF-8");
		String finalHash = "";
		
		for( int i = 0; i < wholeBlockNumber; i++ ) {
			byte[] sub;
			if( i == 0 )
				sub = Arrays.copyOfRange( bytevid,bytevid.length - leftoverLength - 1024, bytevid.length - leftoverLength); // special case 0
			else
				sub = Arrays.copyOfRange( bytevid, bytevid.length - leftoverLength - ( (i+1) * 1024), bytevid.length - leftoverLength - ( i* 1024));
				
			finalHash = SHADivEncrypt.bytesToHex(SHADivEncrypt.hash(SHADivEncrypt.toByteArray(finalHashList)));				
			writer.println( "Hash " + (i+1) + ": " + finalHash);

			finalHashList = SHADivEncrypt.appendArray( SHADivEncrypt.appendArray(new ArrayList<Byte>(),sub), SHADivEncrypt.hash(SHADivEncrypt.toByteArray(finalHashList)));
		}
		writer.println("FINAL HASH: " + SHADivEncrypt.bytesToHex(SHADivEncrypt.hash(SHADivEncrypt.toByteArray(finalHashList))));
		writer.close();
	}
	public static byte[] hash( byte[] barr ) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(barr);
			return md.digest();
		} catch( Exception e ) { throw new Exception(e); }
	}
	public static byte[] reverse( byte[] barr ) {
		byte temp; // This method is designed specifically for byte conversion
		for( int i = 0; i < Math.floor(barr.length/2); i++ ) {
			temp = barr[barr.length - i - 1];
			barr[barr.length - i - 1] = barr[i];
			barr[i] = temp;
		}
		return barr;
	}
	public static List<Byte> appendArray( List<Byte> original, byte[] appender ) {
		for( int i = 0; i < appender.length; i++ )
			original.add( appender[i] );
		return original;
	}
	public static void print( byte[] out ) {
		for( byte b : out )
			System.out.print( b ); 
	}
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	public static byte[] toByteArray( List<Byte> byteList ) {
		byte[] barr = new byte[byteList.size()];
		for( int i = 0; i < barr.length; i++ ) 
			barr[i] = byteList.get(i);
		return barr;
	}
}