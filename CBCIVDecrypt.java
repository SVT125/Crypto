import javax.crypto.*;
import javax.crypto.spec.*;

class CBCIVDecrypt {
	public static void main(String[] args) throws Exception { 
		byte[] barrayIV = CryptoTest.toByte("b4832d0f26e1ab7da33249de7d4afc48e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253" );
		byte[] message = new byte[barrayIV.length];
		byte[] key = CryptoTest.toByte("140b41b22a29beb4061bda66b6747e14");
		byte[] iv = CryptoTest.toByte("5b68629feb8606f9a6667670b75b38a5");
		
		for(int i = 0 ; i < barrayIV.length; i += 16){
			byte[] a = subArray(barrayIV,i, i + 16);
			byte[] b, c;
			if(i == 0){
				c = XOR(iv, AES_dec(key, a));
			}else{
				b = subArray(barrayIV,i - 16, i);
				c = XOR(b, AES_dec(key, a));
			}
			for(int j = 0; j < c.length; j ++){
				message[j + i] = c[j];
			}
		}
		
		CryptoTest.printBytes(message);
	}
	
	public static byte[] subArray(byte[] barr, int start, int end ) throws Exception {
		byte[] sub = new byte[16];
		int subCounter = 0;
		for(int i = start; i < end; i++ ) {
			sub[subCounter] = barr[i];
			subCounter++;
		}
		return sub;
	}
	
	static byte[] AES_dec( byte[] key, byte[] cip) throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keyspec = new SecretKeySpec(key, "AES");

		c.init( c.DECRYPT_MODE, keyspec);

		return( c.doFinal(cip));
	}
	
	static byte[] toByteArray(String s) throws Exception { return new String(s).getBytes("UTF-8"); }
	
	static void toString( byte[] barray ) {
		for( byte b : barray ) 
			System.out.println( b );
	}
	
	public static byte[] XOR(byte[] a, byte[] b){
		if(a.length != b.length) System.out.println("warning: a, b have different lengths");
		byte[] c = new byte[a.length];
		for(int i = 0; i < a.length; i ++){
			c[i] = (byte) (a[i] ^ b[i]);
		}
		return c;
	}
	
    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }
	
	public static void printBytes( byte[] barr ) {
		for( byte b : barr )
			System.out.print( (char)b );
	}
}

// Basic CBC mode encryption needs padding.
// Our implementation uses rand. IV