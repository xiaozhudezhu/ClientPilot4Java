package clientPilot4Java;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import meshFHE.funcLib.*;

public class Test {
	
	static String x0 = "92";
    static String x1 = "93";
    static String y0 = "89";
    static String y1 = "90";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//int period = 1000;
		//SSKey key = LocalFuctionLib.genKey(new Random(), period, null);
		//System.out.println(key);
		/*GMap g = LocalFuctionLib.loadG("D:/BaiduNetdiskDownload/GFDF/DF635683432243530954", period);
		System.out.println(g);
		SSKey key = LocalFuctionLib.loadKey("F:/Career/Groups/Own/Projects/sf/shaftstop_key.txt");
		key = new SSKey(period, key.f1, key.f2, g, key.zi);
		key.G = g;
		System.out.println(key);
		LocalFuctionLib.calKeyZP(new Random(), key, g);
		LocalFuctionLib.calMapZZ(new Random(), key, g);
		for(int i = 0; i < 100; i ++) {
			double d1 = new Random().nextInt(100);
			double d2 = new Random().nextInt(100);
			System.out.println("d1,d2:" + d1 + "," + d2);
			Cipher cipher1 = CryptLib.Encrypt(new Random(), d1, key);
			Cipher cipher2 = CryptLib.Encrypt(new Random(), d2, key);
			Cipher cipher3 = IntHE.add(cipher1, cipher2, g);
			double result = CryptLib.Decrypt(cipher3, key);
			System.out.println("add:" + result);
			cipher3 = IntHE.substract(cipher1, cipher2, g);
			result = CryptLib.Decrypt(cipher3, key);
			System.out.println("substract:" + result);
			cipher3 = IntHE.multiply(cipher1, cipher2, g);
			result = CryptLib.Decrypt(cipher3, key);
			System.out.println("multiply:" + result);
			String r = IntHE.compare(cipher1, cipher2, g);
			System.out.println("compare:" + r);
		}*/
		
		String uid = UUID.randomUUID().toString();

		SSKey key1 = IntHE.genKey(uid);
		SSKey key2 = IntHE.getKey(uid);

		GMap gmap1 = IntHE.genGMap(key1, uid);
		GMap gmap2 = IntHE.getGMap(uid);
		
		/*String uid = "3f1a57a8-9978-4d38-aa29-946f2e3014d0";
		String uid2 = "38fe9608-66d3-45f4-a7ef-ebf8e71158ba";
		for(int i = 0; i < 100; i ++) {
			double d1 = new Random().nextInt(100);
			double d2 = new Random().nextInt(100);
			System.out.println("d1,d2:" + d1 + "," + d2);
			Cipher cipher1 = IntHE.encrypt(d1, uid);
			Cipher cipher2 = IntHE.encrypt(d2, uid);
			Cipher cipher3 = IntHE.add(cipher1, cipher2, uid);
			double result = IntHE.decrypt(cipher3, uid);
			System.out.println("add:" + result);
			cipher3 = IntHE.substract(cipher1, cipher2, uid);
			result = IntHE.decrypt(cipher3, uid);
			System.out.println("substract:" + result);
			cipher3 = IntHE.multiply(cipher1, cipher2, uid);
			result = IntHE.decrypt(cipher3, uid);
			System.out.println("multiply:" + result);
			String r = IntHE.compare(cipher1, cipher2, uid);
			System.out.println("compare:" + r);
			
			Cipher cipher4 = IntHE.transfer(cipher3, uid, uid2);
			result = IntHE.decrypt(cipher4, uid2);
			System.out.println("transfer:" + result);

		}*/
		System.out.println("");
		
	}
	
}
