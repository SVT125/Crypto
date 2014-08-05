import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.*;
import java.math.BigInteger;

class CBCCTRDecrypt {
	public static void main( String[] args ) throws Exception{
		byte[] cipher = CBCCTRDecrypt.toByte("0ec7702330098ce7f7520d1cbbb20fc388d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329");
		byte[] key = CBCCTRDecrypt.toByte("36f18357be4dbd77f050515c73fcf9f2");
		String ivStr = "69dda8455c7dd4254bf353b773304eec";
		byte[] message = new byte[cipher.length];
		
		for(int i = 0 ; i < cipher.length; i += 16){
			byte[] a = CBCCTRDecrypt.inc(ivStr,i/16,16);
			byte[] b, c;

			b = subArray(cipher,i, i + Math.min(cipher.length - i, 16));
			c = XOR(b, AES_enc(key, a));
			
			for(int j = 0; j < c.length; j ++){
				if( j + i < cipher.length) message[j + i] = c[j];
			}
		}
		
		CBCCTRDecrypt.printBytes(message);
	}
	public static byte[] inc(String iv, int d, int len){
		BigInteger iv_b = new BigInteger(iv, 16);
		iv_b = iv_b.add(new BigInteger(new Integer(d).toString()));
		byte[] data = new byte[len];
		byte[] data2 = iv_b.toByteArray();
		int diff = len - data2.length;
		for(int i = 0; i < len; i ++){
			if(i < diff) data[i] = 0;
			else data[i] = data2[i - diff];
		}
		return data;
	}
	static byte[] AES_dec( byte[] key, byte[] cip) throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keyspec = new SecretKeySpec(key, "AES");

		c.init( c.DECRYPT_MODE, keyspec);

		return( c.doFinal(cip));
	}
	
	static byte[] AES_enc( byte[] key, byte[] cip) throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keyspec = new SecretKeySpec(key, "AES");

		c.init( c.ENCRYPT_MODE, keyspec);

		return( c.doFinal(cip));
	}
	
	public static byte[] XOR(byte[] a, byte[] b){
		if(a.length != b.length) System.out.println("warning: a, b have different lengths");
		byte[] c = new byte[a.length];
		for(int i = 0; i < a.length; i ++){
			c[i] = (byte) (a[i] ^ b[i]);
		}
		return c;
	}
	public static void printBytes( byte[] barr ) {
		for( byte b : barr )
			System.out.print( (char)b );
	}
    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }
	public static byte[] subArray(byte[] barr, int start, int end ) throws Exception {
		byte[] sub = new byte[16];
		int subCounter = 0;
		for(int i = start; i < end; i++ ) {
			sub[subCounter++] = barr[i];
		}
		return sub;
	}
}