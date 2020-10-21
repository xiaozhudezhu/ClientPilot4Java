package meshFHE.funcLib;

import java.util.ArrayList;
import java.util.List;

//using System.IO.Compression;

public class ServerFunctionOperate
{
	///#region G的动态局部拟合
	public static List<Double> GetCurveValueOpt(double x, double y, GMap G, int period)
	{
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < 6; i++)
		{
			double res = G.getGValue(x, y, i, period, "STATIC");
			result.add(res);
		}
		return result;
	}
	///#endregion
	///#region 基本操作
	private static List<Integer> addCMP(List<Integer> cmp1, List<Integer> cmp2)
	{
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < cmp1.size(); i++)
		{
			res.add(cmp1.get(i) + cmp2.get(i));
		}
		return res;
	}
	public static Cipher MergeSameMP(Cipher c, GMap G, int period)
	{
		Cipher res = new Cipher();
		res.maxPower = c.maxPower;
		ArrayList<String> mpList = new ArrayList<String>();
		for (int i = 0; i < c.multiPow.size(); i++)
		{
			if (mpList.contains(c.getMultiPowString(i)))
			{
				continue;
			}
			String mps = c.getMultiPowString(i);
			mpList.add(mps);
			List<Integer> indList = c.getMultiPowIndex(mps);
			if (indList.size() == 1)
			{
				res.multiPow.add(c.multiPow.get(indList.get(0)));
				res.addCipherItem(c.getCipherItem(indList.get(0)));
			}
			else
			{
				res.multiPow.add(c.multiPow.get(indList.get(0)));
				double[] cs = c.getCipherItem(indList.get(0));
				for (int j = 1; j < indList.size(); j++)
				{
					cs = GetFunctionAddResult(1, 1, cs, c.getCipherItem(indList.get(j)), G, period);
				}
				res.addCipherItem(cs);
			}
		}
		res.cipherItems[3].add(c.cipherItems[3].get(0));
		return res;
	}
	///#endregion
	///#region 同态加法
	//使用固定拟合策略
	public static double[] GetFunctionAddResult(double p1, double p2, double[] c1, double[] c2, GMap G, int period)
	{
		double[] c = new double[] { 0, 0, 0 };
		double a1 = c1[0] * p1;
		double x1 = c1[1];
		double y1 = c1[2];
		double a2 = c2[0] * p2;
		double x2 = c2[1];
		double y2 = c2[2];
		double u1 = LocalFuctionLib.h3(x1, y2, period);
		double u2 = LocalFuctionLib.h3(x2, y1, period);
		double u3 = LocalFuctionLib.h5(y1, y2, period);
		double u4 = LocalFuctionLib.h1(u1, u2, period);
		double u5 = LocalFuctionLib.h2(u2, u4, period);
		double u6 = LocalFuctionLib.h5(u3, u5, period);
		double a3 = a1 * GetCurveValueOpt(x1, y2, G, period).get(3);
		double a4 = a2 * GetCurveValueOpt(x2, y1, G, period).get(3);
		double a5 = GetCurveValueOpt(y1, y2, G, period).get(5);
		double a6 = a3 * GetCurveValueOpt(u1, u2, G, period).get(0);
		double a7 = a6 * GetCurveValueOpt(u2, u4, G, period).get(2) + (a4 - a3) * GetCurveValueOpt(u2, u4, G, period).get(1);
		double a8 = a5 * GetCurveValueOpt(u3, u5, G, period).get(5);
		c[0] = a7 / a8;
		c[1] = u5;
		c[2] = u6;
		return c;
	}
	public static Cipher GetCipherAddResult(double p1, double p2, Cipher c1, Cipher c2, GMap G, int period)
	{
		Cipher c = new Cipher();
		if (c1.multiPow.size() == c2.multiPow.size())
		{
			for (int i = 0; i < c1.multiPow.size(); i++)
			{
				if (c1.getMultiPowString(i).equals(c2.getMultiPowString(i)))
				{
					List<Integer> cmp = c1.multiPow.get(i);
					c.multiPow.add(cmp);
					c.addCipherItem(GetFunctionAddResult(p1, p2, c1.getCipherItem(i), c2.getCipherItem(i), G, period));
				}
				else
				{
					List<Integer> cmp = c1.multiPow.get(i);
					c.multiPow.add(cmp);
					double[] cp1 = c1.getCipherItem(i);
					double[] cp2 = c2.getCipherItemsByCPM(c1.getMultiPowString(i));
					if (cp2 == null)
					{
						c.addCipherItem(cp1);
					}
					else
					{
						c.addCipherItem(GetFunctionAddResult(p1, p2, cp1, cp2, G, period));
					}
				}
			}
			//c.cipherItems[3].Add(p1 * c1.cipherItems[3][0] + p2 * c2.cipherItems[3][0]);
		}
		else
		{

		}
		return c;
	}
	///#endregion
	///#region 同态乘法
	//使用固定拟合策略
	public static double[] GetFunctionMultiplyResult(double[] c1, double[] c2, GMap G, int period)
	{
		double[] c = new double[] { 0, 0, 0 };
		double a1 = c1[0];
		double x1 = c1[1];
		double y1 = c1[2];
		double a2 = c2[0];
		double x2 = c2[1];
		double y2 = c2[2];
		double u1 = LocalFuctionLib.h4(x1, x2, period);
		double u2 = LocalFuctionLib.h5(y1, y2, period);
		//double a3 = a1 * a2 * G[4][x1][x2][0] / G[5][y1][y2][0];
		double a3 = a1 * a2 * GetCurveValueOpt(x1, x2, G, period).get(4) / GetCurveValueOpt(y1, y2, G, period).get(5);
		c[0] = a3;
		c[1] = u1;
		c[2] = u2;
		return c;
	}
	
	public static double[] GetFunctionSplitResult(double[] c1, double[] c2, GMap G, int period)
    {
        double[] c = new double[] { 0, 0, 0 };
        double a1 = c1[0];
        double x1 = c1[1];
        double y1 = c1[2];
        double a2 = c2[0];
        double x2 = c2[1];
        double y2 = c2[2];
        double u1 = LocalFuctionLib.h4r(x2, x1, period);
        double u2 = LocalFuctionLib.h5r(y2, y1, period);
        //double a3 = a1 * a2 * G[4][x1][x2][0] / G[5][y1][y2][0];
        double a3 = a1 / a2 * GetCurveValueOpt(u2, y2, G, period).get(5) / GetCurveValueOpt(u1, x2, G, period).get(4);
        c[0] = a3;
        c[1] = u1;
        c[2] = u2;
        return c;
    }
	
	public static double[] GetFunctionMultiplyResult(double[] c1, double p1)
	{
		c1[0] = c1[0] * p1;
		return c1;
	}
	public static Cipher GetCipherMultiplyResult(Cipher c1, Cipher c2, GMap G, int period)
	{
		Cipher res = new Cipher();
		double b1 = c1.cipherItems[3].get(0);
		double b2 = c2.cipherItems[3].get(0);
		for (int i = 0; i < c1.multiPow.size(); i++)
		{
			for (int j = 0; j < c2.multiPow.size(); j++)
			{
				List<Integer> cmp1 = c1.multiPow.get(i);
				List<Integer> cmp2 = c2.multiPow.get(j);
				List<Integer> cmpr = addCMP(cmp1, cmp2);
				res.multiPow.add(cmpr);
				double[] cp1 = c1.getCipherItem(i);
				double[] cp2 = c2.getCipherItem(j);
				res.addCipherItem(GetFunctionMultiplyResult(cp1, cp2, G, period));
			}
			List<Integer> cmp1b = c1.multiPow.get(i);
			res.multiPow.add(cmp1b);
			double[] cp1b = c1.getCipherItem(i);
			res.addCipherItem(GetFunctionMultiplyResult(cp1b, b2));
		}
		for (int i = 0; i < c2.multiPow.size(); i++)
		{
			List<Integer> cmp2b = c2.multiPow.get(i);
			res.multiPow.add(cmp2b);
			double[] cp2b = c2.getCipherItem(i);
			res.addCipherItem(GetFunctionMultiplyResult(cp2b, b1));
		}
		res.cipherItems[3].add(b1 * b2);
		res.maxPower = addCMP(c1.maxPower, c2.maxPower);
		res.Sort();
		res = MergeSameMP(res, G, period);
		// 降次
        double[] cp11 = res.getCipherItem(4);
        double[] cp12 = res.getCipherItem(3);
        double[] cp22 = res.getCipherItem(2);
        double[] zc11_1 = G.z11.getCipherItem(2);
        double[] zc11_2 = G.z11.getCipherItem(1);
        double[] zc12_1 = G.z12.getCipherItem(2);
        double[] zc12_2 = G.z12.getCipherItem(1);
        double[] zc22_1 = G.z22.getCipherItem(2);
        double[] zc22_2 = G.z22.getCipherItem(1);
        Cipher rcp1 = new Cipher();
        rcp1.addCipherItem(new double[] { 0, 0, 0 });
        rcp1.addCipherItem(GetFunctionMultiplyResult(cp11, zc11_2, G, period));
        rcp1.addCipherItem(GetFunctionMultiplyResult(cp11, zc11_1, G, period));
        rcp1.maxPower.set(0, 1);
        rcp1.maxPower.set(1, 1);
        rcp1.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 0 }));
        rcp1.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 1 }));
        rcp1.multiPow.add(java.util.Arrays.asList(new Integer[] { 1, 0 }));
        Cipher rcp2 = new Cipher();
        rcp2.addCipherItem(new double[] { 0, 0, 0 });
        rcp2.addCipherItem(GetFunctionMultiplyResult(cp12, zc12_2, G, period));
        rcp2.addCipherItem(GetFunctionMultiplyResult(cp12, zc12_1, G, period));
        rcp2.maxPower.set(0, 1);
        rcp2.maxPower.set(1, 1);
        rcp2.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 0 }));
        rcp2.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 1 }));
        rcp2.multiPow.add(java.util.Arrays.asList(new Integer[] { 1, 0 }));
        Cipher rcp3 = new Cipher();
        rcp3.addCipherItem(new double[] { 0, 0, 0 });
        rcp3.addCipherItem(GetFunctionMultiplyResult(cp22, zc22_2, G, period));
        rcp3.addCipherItem(GetFunctionMultiplyResult(cp22, zc22_1, G, period));
        rcp3.maxPower.set(0, 1);
        rcp3.maxPower.set(1, 1);
        rcp3.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 0 }));
        rcp3.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 1 }));
        rcp3.multiPow.add(java.util.Arrays.asList(new Integer[] { 1, 0 }));
        Cipher res0 = new Cipher();
        res0.maxPower.set(0, 1);
        res0.maxPower.set(1, 1);
        res0 = GetCipherAddResult(1, 1, rcp1, rcp2, G, period);
        res = GetCipherAddResult(1, 1, res0, rcp3, G, period);
		return res;
	}
	public static Cipher GetCipherMultiplyResult(Cipher c1, double p1, GMap G, int period)
	{
		Cipher res = c1;
		//swinginwind
		res.cipherItems[0].set(1, res.cipherItems[0].get(1) * p1);
		res.cipherItems[0].set(2, res.cipherItems[0].get(2) * p1);
		return res;
	}
	///#endregion
	///#region 同态比较
	public static String Compare(Cipher c, String x0, String x1, String y0, String y1, GMap G, int period)
	{
		String result = "none";
		double v00 = GetFunctionAddResult(Double.parseDouble(y0), Double.parseDouble(x0), c.getCipherItem(1), c.getCipherItem(2), G, period)[0];
		double v01 = GetFunctionAddResult(Double.parseDouble(y0), Double.parseDouble(x1), c.getCipherItem(1), c.getCipherItem(2), G, period)[0];
		double v10 = GetFunctionAddResult(Double.parseDouble(y1), Double.parseDouble(x0), c.getCipherItem(1), c.getCipherItem(2), G, period)[0];
		double v11 = GetFunctionAddResult(Double.parseDouble(y1), Double.parseDouble(x1), c.getCipherItem(1), c.getCipherItem(2), G, period)[0];
		if (v00 < 0 && v01 < 0 && v10 < 0 && v11 < 0)
		{
			result = "false";
		}
		else if (v00 > 0 && v01 > 0 && v10 > 0 && v11 > 0)
		{
			result = "true";
		}
		return result;
	}
	public static Cipher ABS(Cipher c, String x0, String x1, String y0, String y1, GMap G, int period)
	{
		Cipher result = c.GetClone();
		String res = Compare(c, x0, x1, y0, y1, G, period);
		if (res.equals("false"))
		{
			result.cipherItems[0].set(1, -1 * result.cipherItems[0].get(1));
			result.cipherItems[0].set(2, -1 * result.cipherItems[0].get(2));
		}
		return result;
	}
	///#endregion
	// 密文转换
    public static Cipher Transfer(Cipher c, GMap G1, GMap G2, int period)
    {
        Cipher result = new Cipher();
        double[] c1 = c.getCipherItem(2);
        double[] c2 = c.getCipherItem(1);
        double[] z1 = G1.zp1;
        double[] z2 = G1.zp2;
        double[] z3 = G2.zp1;
        double[] z4 = G2.zp2;
        double[] c1z1 = GetFunctionMultiplyResult(c1, z1, G1, period);
        double[] c1z3 = GetFunctionSplitResult(c1z1, z3, G2, period);
        double[] c2z2 = GetFunctionMultiplyResult(c2, z2, G1, period);
        double[] c2z4 = GetFunctionSplitResult(c2z2, z4, G2, period);
        result.addCipherItem(new double[] { 0, 0, 0 });
        result.addCipherItem(c2z4);
        result.addCipherItem(c1z3);
        result.maxPower.set(0, 1);
        result.maxPower.set(1, 1);
        result.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 0 }));
        result.multiPow.add(java.util.Arrays.asList(new Integer[] { 0, 1 }));
        result.multiPow.add(java.util.Arrays.asList(new Integer[] { 1, 0 }));
        return result;
    }
    
}