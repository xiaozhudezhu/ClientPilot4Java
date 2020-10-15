package meshFHE.funcLib;

import java.util.Random;

public class IntHE {
	
	static String x0 = "92";
    static String x1 = "93";
    static String y0 = "89";
    static String y1 = "90";
    
    static int period = 1000;

	public static SSKey genKey() {
		return LocalFuctionLib.genKey(new Random(), period);
	}

	public static GMap genGMap(SSKey key) {
		return null;
	}

	public static Cipher encrypt(double m, SSKey key) {
		return CryptLib.Encrypt(new Random(), m, key);
	}

	public static double decrypt(Cipher c, SSKey key) {
		return CryptLib.Decrypt(c, key);
	}

	public static Cipher add(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, 1, c1, c2, gmap, period);
	}

	public static Cipher substract(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, -1, c1, c2, gmap, period);
	}

	public static Cipher multiply(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherMultiplyResult(c1, c2, gmap, period);
	}

	public static String compare(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.Compare(substract(c1, c2, gmap), x0, x1, y0, y1, gmap, period);
	}

	public static Cipher transfer(Cipher c, GMap gmap1, GMap gmap2) {
		return ServerFunctionOperate.Transfer(c, gmap1, gmap2, period);
	}

}
