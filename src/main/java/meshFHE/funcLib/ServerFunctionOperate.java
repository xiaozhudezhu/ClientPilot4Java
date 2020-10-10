package meshFHE.funcLib;

import java.util.ArrayList;
import java.util.List;

//using System.IO.Compression;

public class ServerFunctionOperate
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
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
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
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
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
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
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
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
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
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
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}