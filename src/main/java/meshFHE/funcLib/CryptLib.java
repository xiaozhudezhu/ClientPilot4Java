package meshFHE.funcLib;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CryptLib
{
	public static Cipher Encrypt(java.util.Random r, double plain, SSKey key)
	{
		Cipher cipher = new Cipher();
		List<Double> parts = Depart(r, plain, key.stage);
		double ctp = 0;
		cipher.cipherItems[0].add(0d);
		cipher.cipherItems[1].add((double) r.nextInt(key.period - 1)); // + r.NextDouble());
		cipher.cipherItems[2].add((double) r.nextInt(key.period - 1)); // + r.NextDouble());
		cipher.multiPow.add(new ArrayList<Integer>(java.util.Arrays.asList(new Integer[] { 0, 0 })));
		for (int i = key.stage - 1; i >= 1; i--)
		{
			double xi = r.nextInt(key.period - 1); // + r.NextDouble();
			double yi = r.nextInt(key.period - 1); // + r.NextDouble();
			double ai = parts.get(i) / (LocalFuctionLib.get2BezierY(xi, key.f1) / LocalFuctionLib.get2BezierY(yi, key.f2) * key.zi.get(i));
			ctp += parts.get(i);
			cipher.cipherItems[0].add(ai);
			cipher.cipherItems[1].add(xi);
			cipher.cipherItems[2].add(yi);
			cipher.addMultiPow(key.stage, i, 1);
		}
		double x0 = r.nextInt(key.period - 1); // + r.NextDouble();
		double y0 = r.nextInt(key.period - 1); // + r.NextDouble();
		double a0 = (plain - ctp) / (LocalFuctionLib.get2BezierY(x0, key.f1) / LocalFuctionLib.get2BezierY(y0, key.f2) * key.zi.get(0));
		cipher.cipherItems[0].add(a0);
		cipher.cipherItems[1].add(x0);
		cipher.cipherItems[2].add(y0);
		cipher.addMultiPow(key.stage, 0, 1);
		cipher.cipherItems[3].add(0d);
		return cipher;
	}
	private static ArrayList<Double> Depart(java.util.Random r, double plain, int pn)
	{
		ArrayList<Double> parts = new ArrayList<Double>();
		int pr1 = r.nextInt(80);
		int pr2 = r.nextInt(80);
		double part1 = plain * (double)pr1 / 100d + r.nextInt(10);
		double part2 = plain * (double)pr2 / 100d + r.nextInt(10);
		parts.add(part1);
		parts.add(part2);
		return parts;
	}
	public static double Decrypt(Cipher cipher, SSKey key)
	{
		double plain = 0;
		for (int i = 0; i < cipher.multiPow.size(); i++)
		{
			double ai = cipher.cipherItems[0].get(i);
			double xi = cipher.cipherItems[1].get(i);
			double yi = cipher.cipherItems[2].get(i);
			List<Integer> cmp = cipher.multiPow.get(i);
			double z = 1;
			for (int j = 0; j < cmp.size(); j++)
			{
				z = z * Math.pow(key.zi.get(j), cmp.get(j));
			}
			plain += ai * LocalFuctionLib.get2BezierY(xi, key.f1) / LocalFuctionLib.get2BezierY(yi, key.f2) * z;
		}
		//plain += cipher.cipherItems[3][0];
		BigDecimal bg = new BigDecimal(plain).setScale(key.accuracy, RoundingMode.UP);
		return bg.doubleValue();
	}
	public static double DecryptNoPower(double a1, double x1, double y1, double a2, double x2, double y2, SSKey key)
	{
		double plain = 0;
		plain += a1 * LocalFuctionLib.get2BezierY(x1, key.f1) / LocalFuctionLib.get2BezierY(y1, key.f2) * key.zi.get(1);
		plain += a2 * LocalFuctionLib.get2BezierY(x2, key.f1) / LocalFuctionLib.get2BezierY(y2, key.f2) * key.zi.get(0);
		BigDecimal bg = new BigDecimal(plain).setScale(key.accuracy, RoundingMode.UP);
		return bg.doubleValue();
	}
	public static double DecryptPart(double a1, double x1, double y1, SSKey key)
	{
		double plain = 0;
		plain += a1 * LocalFuctionLib.get2BezierY(x1, key.f1) / LocalFuctionLib.get2BezierY(y1, key.f2);
		return plain; // Math.Round(plain, key.accuracy);
	}
	public static double[] EncryptPartList(java.util.Random r, Double z, SSKey key)
    {
		double[] result = new double[3];
        result[0] = 0d;
        result[1] = (double) r.nextInt(1000);
        result[2] = (double) r.nextInt(1000);
        result[0] = z * LocalFuctionLib.get2BezierY(result[1], key.f2) / LocalFuctionLib.get2BezierY(result[1], key.f1);
        return result;
    }
	public static String EncryptPart(java.util.Random r, double z, SSKey key)
	{
		double x1 = r.nextInt(1000);
		double y1 = r.nextInt(1000);
		double a = z * LocalFuctionLib.get2BezierY(y1, key.f2) / LocalFuctionLib.get2BezierY(x1, key.f1);
		return a + "," + x1 + "," + y1;
	}
	public static List<Cipher> Encrypt(java.util.Random r, String plain, SSKey key)
	{
		List<Cipher> result = new ArrayList<Cipher>();
		byte[] pbs = plain.getBytes();
		for (byte pb : pbs)
		{
			result.add(Encrypt(r, (double)pb, key));
		}
		return result;
	}
	public static String DecryptString(List<Cipher> cipherList, SSKey key)
	{
		String result = "";
		byte[] bytes = new byte[cipherList.size()];
		int i = 0;
		for(Cipher c : cipherList)
		{
			bytes[i] = (byte)Decrypt(c, key);
			i ++;
		}
		result = new String(bytes);
		return result;
	}
}