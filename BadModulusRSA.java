import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.*;

class BadModulusRSA {
	static BigInteger mod1 = new BigInteger( "179769313486231590772930519078902473361797697894230657273430081157732675805505620686985379449212982959585501387537164015710139858647833778606925583497541085196591615128057575940752635007475935288710823649949940771895617054361149474865046711015101563940680527540071584560878577663743040086340742855278549092581");
	static BigInteger mod2 = new BigInteger( "648455842808071669662824265346772278726343720706976263060439070378797308618081116462714015276061417569195587321840254520655424906719892428844841839353281972988531310511738648965962582821502504990264452100885281673303711142296421027840289307657458645233683357077834689715838646088239640236866252211790085787877");
	static BigInteger mod3 = new BigInteger( "720062263747350425279564435525583738338084451473999841826653057981916355690188337790423408664187663938485175264994017897083524079135686877441155132015188279331812309091996246361896836573643119174094961348524639707885238799396839230364676670221627018353299443241192173812729276147530748597302192751375739387929" );
	static BigInteger cipher = new BigInteger( "22096451867410381776306561134883418017410069787892831071731839143676135600120538004282329650473509424343946219751512256465839967942889460764542040581564748988013734864120452325229320176487916666402997509188729971690526083222067771600019329260870009579993724077458967773697817571267229951148662959627934791540" );
	
	public static void main( String[] args ) throws Exception {
		PrintWriter writer = new PrintWriter("Week6Assignments.txt", "UTF-8");
		BadModulusRSA.DependentPrimesAttack( mod1, writer );
		writer.println();
		BadModulusRSA.DependentPrimesAttack2( mod2, writer );
		writer.println();
		BadModulusRSA.DependentPrimesAttack3( mod3, writer );
		writer.println();
		BadModulusRSA.RSAAttack( cipher, mod1, writer );
		writer.close();
	}
	
	// For the attack where p and q are not generated randomly, but are some close primes to some random number R.
	private static void DependentPrimesAttack( BigInteger n, PrintWriter pw ) throws Exception {
		BigInteger a = new BigDecimal(BadModulusRSA.sqrt(n)).toBigInteger().add(new BigInteger("1")); // improvised rounding
		BigInteger x = BadModulusRSA.sqrt(a.pow(2).add(n.negate()));
		
		BigInteger p = a.add(x.negate());
		BigInteger q = a.add(x);
		
		pw.println( "Q1 p is: " + p );
		pw.println( "Q1 q is: " + q );
		pw.println( "Q1 x is: " + x );
	}
	
	// For the attack just as above but the bound is stricter where |p-q| < 2^11 N^.25.
	private static void DependentPrimesAttack2( BigInteger n, PrintWriter pw ) throws Exception {
		outer: for( int i = 72077; i < Math.pow(2,20); i++ ) { // found at iteration 72077.
			//System.out.println(i);
			BigInteger a = BadModulusRSA.sqrt(n).add(new BigInteger(Integer.toString(i))).add(new BigInteger("1"));
			BigInteger x = BadModulusRSA.sqrt(a.pow(2).add(n.negate()));
		
			BigInteger p = a.add(x.negate());
			BigInteger q = a.add(x);			
			if( p.multiply(q).equals(n) ) {
				pw.println( "Q2 p is: " + p );
				pw.println( "Q2 q is: " + q );	
				break outer;
			}
		}
	}
	
	// For the attack where |3p - 2q| < N^.25, using method where A = ceil(sqrt(24N)), E = sqrt(A^2 - 24N) = 3p - 2q = 2q - 3p.
	private static void DependentPrimesAttack3( BigInteger n, PrintWriter pw ) throws Exception { 
		BigInteger a = BadModulusRSA.sqrt( new BigInteger("24").multiply(n) ).add( new BigInteger("1"));
		BigInteger e = BadModulusRSA.sqrt( a.pow(2).add(new BigInteger("24").multiply(n).negate()));
		
		// Test 1
		BigInteger p = a.add( e.negate() ).divide(new BigInteger("6"));
		BigInteger q = a.add( e ).divide(new BigInteger("4"));
		if( p.multiply(q).equals(n) ) {
			pw.println( "Q3 p is: " + p );
			pw.println( "Q3 q is: " + q );
		}
		
		// Test 2
		p = a.add(e).divide(new BigInteger("6"));
		q = a.add(e.negate()).divide(new BigInteger("4"));
		if( p.multiply(q).equals(n) ) {
			pw.println( "Q3 p is: " + p );
			pw.println( "Q3 q is: " + q );
		}		
	}
	
	private static void RSAAttack( BigInteger c, BigInteger n, PrintWriter pw ) throws Exception {
		BigInteger p = new BigInteger( "13407807929942597099574024998205846127479365820592393377723561443721764030073662768891111614362326998675040546094339320838419523375986027530441562135724301");
		BigInteger q = new BigInteger( "13407807929942597099574024998205846127479365820592393377723561443721764030073778560980348930557750569660049234002192590823085163940025485114449475265364281");
		BigInteger phiN = n.add( p.negate() ).add(q.negate()).add( new BigInteger("1"));
		
		BigInteger d = new BigInteger("65537").modInverse(phiN);
		BigInteger PKCStext = c.modPow(d,n);
		
		pw.println( "The PKCS text is: " + PKCStext );
	}
	
	private static BigInteger sqrt(BigInteger n) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		while(b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if(mid.multiply(mid).compareTo(n) > 0) b = mid.subtract(BigInteger.ONE);
			else a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}
}