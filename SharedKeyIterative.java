import java.math.*;
import java.util.*;
import java.math.BigInteger;

class SharedKeyIterative {
	static BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084171");
	static BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979459063929499736583746670572176471460312928594829675428279466566527115212748467589894601965568");
	static BigInteger h = new BigInteger("3239475104050450443565264378728065788649097520952449527834792452971981976143292558073856937958553180532878928001494706097394108577585732452307673444020333");
	
	public static void main( String[] args ) {
		HashMap leftSide = populateLeft();
	}
	
	// Populates the left side hash map with SharedKey.h/g^x1 for x1 in [0,2^20], using iterative powers.
	private static HashMap populateLeft() {
		BigInteger buffer = h;
		BigInteger gInv = SharedKey.g.modInverse(SharedKey.p);
		HashMap hm = new HashMap();
		for( int i = 0; i < Math.pow(2,20); i++ ) {
			buffer = buffer.multiply(gInv);
			hm.put(buffer.hashCode(), buffer);
			System.out.println( i );
		}
		return hm;
	}
}