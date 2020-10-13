package clientPilot4Java;

import java.io.IOException;
import java.util.Random;

import meshFHE.funcLib.*;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int period = 1000;
		//SSKey key = LocalFuctionLib.genKey(new Random(), period);
		//System.out.println(key);
		GMap g = LocalFuctionLib.loadG("D:/BaiduNetdiskDownload/GFDF/DF635683432243530954", period);
		System.out.println(g);
		SSKey key = LocalFuctionLib.loadKey("F:/Career/Groups/Own/Projects/sf/shaftstop_key.txt");
		key = new SSKey(period, key.f1, key.f2, g, key.zi);
		System.out.println(key);
		LocalFuctionLib.calKeyZP(new Random(), key, g);
		LocalFuctionLib.calMapZZ(new Random(), key, g);
		double d1 = 12.3;
		double d2 = 5;
		Cipher cipher1 = CryptLib.Encrypt(new Random(), d1, key);
		Cipher cipher2 = CryptLib.Encrypt(new Random(), d2, key);
		Cipher cipher3 = ServerFunctionOperate.GetCipherMultiplyResult(cipher1, cipher2, g, period);
		double result = CryptLib.Decrypt(cipher3, key);
		System.out.println(result);

	}
	
}
