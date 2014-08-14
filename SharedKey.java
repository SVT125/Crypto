// improve timing with progressive powers. left side multiply by x^-1 iteratively, right side multiply by x^1 iteratively.

import java.math.*;
import java.util.*;
import java.math.BigInteger;

class SharedKey {
	static BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084171");
	static BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979459063929499736583746670572176471460312928594829675428279466566527115212748467589894601965568");
	static BigInteger h = new BigInteger("3239475104050450443565264378728065788649097520952449527834792452971981976143292558073856937958553180532878928001494706097394108577585732452307673444020333");
	
	public static void main( String[] args ) {
		Map<BigInteger,Long> leftSide = populateLeft();
		System.out.println( "Hash mapping finished." );
		checkTable(leftSide);
	}
	
	// Populates the left side hash map with h/g^x1 for x1 in [0,2^20].
	private static Map<BigInteger,Long> populateLeft() {
		//BigInteger buffer = h;
		BigInteger gPow;
		Map<BigInteger,Long> hm = new HashMap<BigInteger,Long>();
		for( long i = 0; i < Math.pow(2,20); i++ ) {
			gPow = SharedKey.g.modPow(BigInteger.valueOf(i),SharedKey.p);
			hm.put( SharedKey.h.multiply(gPow.modInverse(SharedKey.p)).mod(SharedKey.p), i );
		}
		return hm;
	}
	
	// Checks every value of x0 against the hash map.
	private static void checkTable(Map<BigInteger,Long> ls) {
		BigInteger b = BigInteger.valueOf(1048576);
		BigInteger base = SharedKey.g.modPow(b,SharedKey.p);
		for( long i = 0; i < Math.pow(2,20); i++ ) {
			if( ls.containsKey(base.modPow(BigInteger.valueOf(i),SharedKey.p)) ) {
				System.out.println( "Found!" );
				System.out.println( "x0: " + i );
				System.out.println( "x1: " + ls.get(base.modPow(BigInteger.valueOf(i),SharedKey.p) ));
				System.out.println( i* 1048576 + ls.get(base.modPow(BigInteger.valueOf(i),SharedKey.p) ));
				break;
			}
		}
	}
}