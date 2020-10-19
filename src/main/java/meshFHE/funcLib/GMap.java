package meshFHE.funcLib;

import java.util.ArrayList;
import java.util.List;

public class GMap
{
	public double[][][] G;
	public double[] xs, ys;
	public static double step = 1;
	public Cipher z11, z12, z22;
    public double[] zp1, zp2;
    
    public GMap() {
    	
    }

	public GMap(int period)
	{
		G = new double[(int)(period / step)][][];
		xs = new double[(int)(period / step)];
		ys = new double[(int)(period / step)];
	}
	public final void setMatchingMethod(double x, double y, int index, int period, int method)
	{
		int xi = getIndex(x, xs);
		int yi = getIndex(y, ys);
		G[xi][yi][index * 4 + 3] = method;
	}
	public final double getGValue(double x, double y, int index, int period, String mode)
	{
		double res = 0;
		x = getXInPeriod(x, period);
		y = getXInPeriod(y, period);
		double[] gs = getG(x, y, index);
		if (containX(x) && containY(y))
		{
			res = gs[0];
		}
		else
		{
			if (mode.equals("STATIC"))
			{
				res = GetCurveValueStatic(x, y, period, index);
			}
			else if(mode.equals("OPT"))
			{
				res = GetCurveValueOpt(x, y, period, index);
			}
			else if(mode.equals("ZXS"))
			{
				res = GetCurveValueZXS(x, y, period, index);
			}
			else if (mode.equals("QNB"))
			{
				res = GetCurveValueQNB(x, y, period, index);
			}
			else if (mode.equals("SDB"))
			{
				res = GetCurveValueSDB(x, y, period, index);
			}
			else if (mode.equals("SDBC"))
			{
				res = GetCurveValueSDBC(x, y, period, index);
			}
			else if (mode.equals("QXS"))
			{
				res = GetCurveValueQXS(x, y, period, index);
			}
			else if(mode.equals("QXW"))
			{
				res = GetCurveValueQXW(x, y, period, index);
			}
			else if(mode.equals("FXQX"))
			{
				res = GetCurveValueFXQX(x, y, period, index);
			}
			else
			{
				res = GetCurveValueZXS(x, y, period, index);
			}
		}
		return res;
	}
	public final double[] getGPart(int index)
	{
		double[] part = new double[xs.length * ys.length * 4];
		for (int i = 0; i < xs.length; i++)
		{
			for (int j = 0; j < ys.length; j++)
			{
				for (int k = 0; k < 4; k++)
				{
					part[i * xs.length * 4 + j * 4 + k] = G[i][j][index * 4 + k];
				}
			}
		}
		return part;
	}
	///#region G的动态局部拟合
	private double[] getG(double x, double y, int index)
	{
		double[] gs = new double[] { 0, 0, 0, 0 };
		int xi = getIndex(x, xs);
		int yi = getIndex(y, ys);
		for (int i = 0; i < 4; i++)
		{
			gs[i] = G[xi][yi][index * 4 + i];
		}
		return gs;
	}
	/*private double[] getG(double x, double y, int index, int period)
	{
		double[] gs = new double[] { 0, 0, 0, 0 };
		x = getXInPeriod(x, period);
		y = getXInPeriod(y, period);
		int xi = getIndex(x, xs);
		int yi = getIndex(y, ys);
		for (int i = 0; i < 4; i++)
		{
			gs[i] = G[xi][yi][index * 4 + i];
		}
		return gs;
	}*/
	private double getXInPeriod(double x, int period)
	{
		while (true)
		{
			if (x < 0)
			{
				x += period;
			}
			else if (x >= period)
			{
				x -= period;
			}
			else
			{
				break;
			}
		}
		return x;
	}
	private int getIndex(double x, double[] xs)
	{
		int index = (int)(x / step);
		return index;
	}
	private boolean containX(double x)
	{
		for(double t : xs) {
			if(t == x)
				return true;
		}
		return false;
	}
	//swinginwind
	private boolean containY(double y)
	{
		for(double t : ys) {
			if(t == y)
				return true;
		}
		return false;
	}
	private double GetCurveValueStatic(double x, double y, int period, int index)
	{
		double res = GetCurveValueZXS(x, y, period, index);
		return res;
	}
	private double GetCurveValueOpt(double x, double y, int period, int index)
	{
		double res = 0;
		int opt = (int)getG(x, y, index)[3];
		opt = 0;
		if (opt == 0)
		{
			res = GetCurveValueZXS(x, y, period, index);
		}
		else if (opt == 1)
		{
			res = GetCurveValueQNB(x, y, period, index);
		}
		else if (opt == 2)
		{
			res = GetCurveValueSDB(x, y, period, index);
		}
		else if (opt == 3)
		{
			res = GetCurveValueSDBC(x, y, period, index);
		}
		else if (opt == 4)
		{
			res = GetCurveValueQXS(x, y, period, index);
		}
		else if (opt == 5)
		{
			res = GetCurveValueQXW(x, y, period, index);
		}
		else if (opt == 6)
		{
			res = GetCurveValueFXQX(x, y, period, index);
		}
		return res;
	}
	// 三点补偿
	private double GetCurveValueSDBC(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);
		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		double x0f2 = x0 + 2 * step < period ? x0 + 2 * step : x0 - period + 2 * step;
		double y0f2 = y0 + 2 * step < period ? y0 + 2 * step : y0 - period + 2 * step;
		px1.add(x0);
		px1.add(x0 + step);
		px1.add(x0 + 2 * step);
		px2.add(y0);
		px2.add(y0 + step);
		px2.add(y0 + 2 * step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs20 = getG(x0f2, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);
		double[] gs21 = getG(x0f2, y0f1, index);
		double[] gs02 = getG(x0, y0f2, index);
		double[] gs12 = getG(x0f1, y0f2, index);
		double[] gs22 = getG(x0f2, y0f2, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		ArrayList<Double> cpy3 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy1.add(gs20[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		cpy2.add(gs21[0]);
		cpy3.add(gs02[0]);
		cpy3.add(gs12[0]);
		cpy3.add(gs22[0]);
		py1.add(cpy1);
		py1.add(cpy2);
		py1.add(cpy3);

		for (int j = 0; j < 3; j++)
		{
			double res1 = GetSDBCMatchingValue(x, px1, py1.get(j));
			py2.add(res1);
		}
		double res2 = GetSDBCMatchingValue(y, px2, py2);

		result = res2;
		return result;
	}
	// 直线
	private double GetCurveValueZXS(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);
		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		px1.add(x0);
		px1.add(x0 + step);
		px2.add(y0);
		px2.add(y0 + step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		py1.add(cpy1);
		py1.add(cpy2);

		for (int j = 0; j < 2; j++)
		{
			double res1 = GetLineMatchingValue(x, px1, py1.get(j));
			py2.add(res1);
		}
		double res2 = GetLineMatchingValue(y, px2, py2);

		result = res2;
		return result;
	}
	// 切线上
	private double GetCurveValueQXS(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);
		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> pk1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();
		ArrayList<Double> pk2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		px1.add(x0);
		px1.add(x0 + step);
		px2.add(y0);
		px2.add(y0 + step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		py1.add(cpy1);
		py1.add(cpy2);

		ArrayList<Double> cpk1 = new ArrayList<Double>();
		ArrayList<Double> cpk2 = new ArrayList<Double>();
		cpk1.add(gs00[1]);
		cpk1.add(gs10[1]);
		cpk2.add(gs01[1]);
		cpk2.add(gs11[1]);
		pk1.add(cpk1);
		pk1.add(cpk2);

		//pk2.Add((gs00[2] + gs10[2]) / 2);
		//pk2.Add((gs01[2] + gs11[2]) / 2);
		pk2.add((gs10[2] - gs00[2]) * (y - y0) / step + gs00[2]);
		pk2.add((gs11[2] - gs01[2]) * (y - y0) / step + gs01[2]);

		for (int j = 0; j < 2; j++)
		{
			double res1 = GetQXMatchingValue(x, px1, py1.get(j), pk1.get(j));
			py2.add(res1);
		}
		double res2 = GetQXMatchingValue(y, px2, py2, pk2);

		result = res2;
		return result;
	}
	// 切线内贝塞尔
	private double GetCurveValueQNB(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);
		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> pk1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();
		ArrayList<Double> pk2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		px1.add(x0);
		px1.add(x0 + step);
		px2.add(y0);
		px2.add(y0 + step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		py1.add(cpy1);
		py1.add(cpy2);

		ArrayList<Double> cpk1 = new ArrayList<Double>();
		ArrayList<Double> cpk2 = new ArrayList<Double>();
		cpk1.add(gs00[1]);
		cpk1.add(gs10[1]);
		cpk2.add(gs01[1]);
		cpk2.add(gs11[1]);
		pk1.add(cpk1);
		pk1.add(cpk2);

		//pk2.Add((gs00[2] + gs10[2]) / 2);
		//pk2.Add((gs01[2] + gs11[2]) / 2);
		pk2.add((gs10[2] - gs00[2]) * (y - y0) / step + gs00[2]);
		pk2.add((gs11[2] - gs01[2]) * (y - y0) / step + gs01[2]);

		for (int j = 0; j < 2; j++)
		{
			double res1 = GeQNBMatchingValue(x, px1, py1.get(j), pk1.get(j));
			py2.add(res1);
		}
		double res2 = GeQNBMatchingValue(y, px2, py2, pk2);

		result = res2;
		return result;
	}
	// 三点贝塞尔
	private double GetCurveValueSDB(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);
		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();

		double x0f1 = x0 - step >= 0 ? x0 - step : x0 + period - step;
		double x0f2 = x0 + 2 * step < period ? x0 + 2 * step : x0 - period + 2 * step;
		double y0f1 = y0 - step >= 0 ? y0 - step : y0 + period - step;
		double y0f2 = y0 + 2 * step < period ? y0 + 2 * step : y0 - period + 2 * step;
		px1.add(x0 - step);
		px1.add(x0);
		px1.add(x0 + 2 * step);
		px2.add(y0 - step);
		px2.add(y0);
		px2.add(y0 + 2 * step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs20 = getG(x0f2, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);
		double[] gs21 = getG(x0f2, y0f1, index);
		double[] gs02 = getG(x0, y0f2, index);
		double[] gs12 = getG(x0f1, y0f2, index);
		double[] gs22 = getG(x0f2, y0f2, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		ArrayList<Double> cpy3 = new ArrayList<Double>();
		cpy1.add(gs11[0]);
		cpy1.add(gs01[0]);
		cpy1.add(gs21[0]);
		cpy2.add(gs10[0]);
		cpy2.add(gs00[0]);
		cpy2.add(gs20[0]);
		cpy3.add(gs12[0]);
		cpy3.add(gs02[0]);
		cpy3.add(gs22[0]);
		py1.add(cpy1);
		py1.add(cpy2);
		py1.add(cpy3);

		for (int j = 0; j < 3; j++)
		{
			double res1 = GetBezierMatchingValue(x, px1, py1.get(j));
			py2.add(res1);
		}
		double res2 = GetBezierMatchingValue(y, px2, py2);

		result = res2;
		return result;
	}
	//3个点贝塞尔拟合
	private double GetBezierMatchingValue(double x, ArrayList<Double> px, ArrayList<Double> py)
	{
		double y = 0;
		double x0 = px.get(0);
		double xt = px.get(1);
		double x2 = px.get(2);
		double y0 = py.get(0);
		double yt = py.get(1);
		double y2 = py.get(2);
		double t = 0.36;
		double x1 = ((1 - t) * (1 - t) * x0 + t * t * x2 - xt) / (-2 * t * (1 - t));
		double y1 = ((1 - t) * (1 - t) * y0 + t * t * y2 - yt) / (-2 * t * (1 - t));
		List<List<Double>> points = new ArrayList<List<Double>>();
		points.add(java.util.Arrays.asList(new Double[] { x0, y0 }));
		points.add(java.util.Arrays.asList(new Double[] { x1, y1 }));
		points.add(java.util.Arrays.asList(new Double[] { x2, y2 }));
		y = LocalFuctionLib.get2BezierY(x, points);
		return y;
	}
	//2个点，直线
	private double GetLineMatchingValue(double x, List<Double> px, List<Double> py)
	{
		double y = 0;
		double r = (x - px.get(0)) / (px.get(1) - px.get(0));
		y = (py.get(1) - py.get(0)) * r + py.get(0);
		return y;
	}
	//3个点，考虑第三点补偿弧度
	private double GetSDBCMatchingValue(double x, List<Double> px, List<Double> py)
	{
		double y = 0;
		double r = (x - px.get(0)) / (px.get(1) - px.get(0));
		y = (py.get(1) - py.get(0)) * r + py.get(0);
		double k = (py.get(1) - py.get(0)) / (px.get(1) - px.get(0));
		double k2 = (py.get(2) - py.get(0)) / (px.get(2) - px.get(0));
		y = (-(k2 - k) / (24 * (r - 0.5) * (r - 0.5) + 4) + k) * (x - px.get(0)) + py.get(0);
		return y;
	}
	//2个点，考虑切线，求切线焦点，贝塞尔曲线
	private double GeQNBMatchingValue(double x, List<Double> px, List<Double> py, List<Double> pk)
	{
		double y = 0;
		double x0 = px.get(0);
		double x1 = px.get(1);
		double y0 = py.get(0);
		double y1 = py.get(1);
		double k0 = pk.get(0);
		double k1 = pk.get(1);
		if (k1 == k0)
		{
			return GetLineMatchingValue(x, px, py);
		}
		double y2 = (k0 * k1 * (x1 - x0) + k1 * y0 - k0 * y1) / (k1 - k0);
		double x2 = k0 == 0 ? (y2 - y1) / k1 + x1 : (y2 - y0) / k0 + x0;
		List<List<Double>> points = new ArrayList<List<Double>>();
		points.add(java.util.Arrays.asList(new Double[] { x0, y0 }));
		points.add(java.util.Arrays.asList(new Double[] { x2, y2 }));
		points.add(java.util.Arrays.asList(new Double[] { x1, y1 }));
		try
		{
			y = LocalFuctionLib.get2BezierY(x, points);
		}
		catch (RuntimeException exp)
		{
			return GetLineMatchingValue(x, px, py);
		}
		return y;
	}
	//2个点，靠近切线
	private double GetQXMatchingValue(double x, ArrayList<Double> px, ArrayList<Double> py, ArrayList<Double> pk)
	{
		double y = 0;
		double x0 = px.get(0);
		double x1 = px.get(1);
		double y0 = py.get(0);
		double y1 = py.get(1);
		double k0 = pk.get(0);
		double k1 = pk.get(1);
		if (k1 == k0)
		{
			return GetLineMatchingValue(x, px, py);
		}
		double y2 = (k0 * k1 * (x1 - x0) + k1 * y0 - k0 * y1) / (k1 - k0);
		double x2 = k0 == 0 ? (y2 - y1) / k1 + x1 : (y2 - y0) / k0 + x0;
		if (x <= x2)
		{
			y = k0 == 0 ? y0 : (x - x0) * k0 + y0;
		}
		else
		{
			y = k1 == 0 ? y1 : y1 - (x1 - x) * k1;
		}
		return y;
	}
	// 切线外三角形
	public final double GetCurveValueQXW(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);

		List<Double> px1 = new ArrayList<Double>();
		List<List<Double>> py1 = new ArrayList<List<Double>>();
		List<List<Double>> pk1 = new ArrayList<List<Double>>();
		List<Double> px2 = new ArrayList<Double>();
		List<Double> py2 = new ArrayList<Double>();
		List<Double> pk2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		px1.add(x0);
		px1.add(x0 + step);
		px2.add(y0);
		px2.add(y0 + step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);

		List<Double> cpy1 = new ArrayList<Double>();
		List<Double> cpy2 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		py1.add(cpy1);
		py1.add(cpy2);

		List<Double> cpk1 = new ArrayList<Double>();
		List<Double> cpk2 = new ArrayList<Double>();
		cpk1.add(gs00[1]);
		cpk1.add(gs10[1]);
		cpk2.add(gs01[1]);
		cpk2.add(gs11[1]);
		pk1.add(cpk1);
		pk1.add(cpk2);

		//pk2.Add((gs00[2] + gs10[2]) / 2);
		//pk2.Add((gs01[2] + gs11[2]) / 2);
		pk2.add((gs10[2] - gs00[2]) * (y - y0) / step + gs00[2]);
		pk2.add((gs11[2] - gs01[2]) * (y - y0) / step + gs01[2]);

		for (int j = 0; j < 2; j++)
		{
			double res1 = GetLineMatchingValue3(x, px1, py1.get(j), pk1.get(j));
			py2.add(res1);
		}
		double res2 = GetLineMatchingValue3(y, px2, py2, pk2);

		result = res2;
		return result;
	}
	// 反向切线外三角形
	public final double GetCurveValueFXQX(double x, double y, int period, int index)
	{
		double result = 0;
		double x0 = (double)((int)(x / step) * step);
		double y0 = (double)((int)(y / step) * step);

		ArrayList<Double> px1 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> py1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> pk1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> px2 = new ArrayList<Double>();
		ArrayList<Double> py2 = new ArrayList<Double>();
		ArrayList<Double> pk2 = new ArrayList<Double>();

		double x0f1 = x0 + step < period ? x0 + step : x0 - period + step;
		double y0f1 = y0 + step < period ? y0 + step : y0 - period + step;
		px1.add(x0);
		px1.add(x0 + step);
		px2.add(y0);
		px2.add(y0 + step);

		double[] gs00 = getG(x0, y0, index);
		double[] gs10 = getG(x0f1, y0, index);
		double[] gs01 = getG(x0, y0f1, index);
		double[] gs11 = getG(x0f1, y0f1, index);

		ArrayList<Double> cpy1 = new ArrayList<Double>();
		ArrayList<Double> cpy2 = new ArrayList<Double>();
		cpy1.add(gs00[0]);
		cpy1.add(gs10[0]);
		cpy2.add(gs01[0]);
		cpy2.add(gs11[0]);
		py1.add(cpy1);
		py1.add(cpy2);

		ArrayList<Double> cpk1 = new ArrayList<Double>();
		ArrayList<Double> cpk2 = new ArrayList<Double>();
		cpk1.add(gs00[1]);
		cpk1.add(gs10[1]);
		cpk2.add(gs01[1]);
		cpk2.add(gs11[1]);
		pk1.add(cpk1);
		pk1.add(cpk2);

		//pk2.Add((gs00[2] + gs10[2]) / 2);
		//pk2.Add((gs01[2] + gs11[2]) / 2);
		pk2.add((gs10[2] - gs00[2]) * (y - y0) / step + gs00[2]);
		pk2.add((gs11[2] - gs01[2]) * (y - y0) / step + gs01[2]);

		for (int j = 0; j < 2; j++)
		{
			double res1 = GetLineMatchingValue4(x, px1, py1.get(j), pk1.get(j));
			py2.add(res1);
		}
		double res2 = GetLineMatchingValue4(y, px2, py2, pk2);

		result = res2;
		return result;
	}
	//2个点，切线交点上方，三角形
	public final double GetLineMatchingValue3(double x, List<Double> px, List<Double> py, List<Double> pk)
	{
		double y = 0;
		double x0 = px.get(0);
		double x1 = px.get(1);
		double y0 = py.get(0);
		double y1 = py.get(1);
		double k0 = pk.get(0);
		double k1 = pk.get(1);
		if (k1 == k0)
		{
			return GetLineMatchingValue(x, px, py);
		}
		double y2 = (k0 * k1 * (x1 - x0) + k1 * y0 - k0 * y1) / (k1 - k0);
		double x2 = k0 == 0 ? (y2 - y1) / k1 + x1 : (y2 - y0) / k0 + x0;
		//double yt = GetLineMatchingValue(x, px, py);
		if (x2 < x0)
		{
			y2 = GetLineMatchingValue(x0, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x2, x1 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y2, y1 })));
			y2 = 2 * y2 - y0;
			y2 = 2 * y2 - y0;
			y2 = 2 * y2 - y0;
			y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x0, x1 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y2, y1 })));
		}
		else if (x2 > x1)
		{
			y2 = GetLineMatchingValue(x1, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x0, x2 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y0, y2 })));
			y2 = 2 * y2 - y1;
			y2 = 2 * y2 - y1;
			y2 = 2 * y2 - y1;
			y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x0, x1 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y0, y2 })));
		}
		else
		{
			double y2t = GetLineMatchingValue(x2, px, py);
			y2 = 2 * y2 - y2t;
			y2 = 2 * y2 - y2t;
			y2 = 2 * y2 - y2t;
			if (x < x2)
			{
				y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x0, x2 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y0, y2 })));
			}
			else if (x == x2)
			{
				y = y2;
			}
			else
			{
				y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x2, x1 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y2, y1 })));
			}
		}
		return y;
	}
	//2个点，切线交点上方，三角形，反向
	public final double GetLineMatchingValue4(double x, ArrayList<Double> px, ArrayList<Double> py, ArrayList<Double> pk)
	{
		double y = 0;
		double x0 = px.get(0);
		double x1 = px.get(1);
		double y0 = py.get(0);
		double y1 = py.get(1);
		double k0 = pk.get(0);
		double k1 = pk.get(1);
		if (k1 == k0)
		{
			return GetLineMatchingValue(x, px, py);
		}
		double y2 = (k0 * k1 * (x1 - x0) + k1 * y0 - k0 * y1) / (k1 - k0);
		double x2 = k0 == 0 ? (y2 - y1) / k1 + x1 : (y2 - y0) / k0 + x0;
		//double yt = GetLineMatchingValue(x, px, py);
		double y2t = GetLineMatchingValue(x2, px, py);
		double y2n = y2 = 2 * y2t - y2;
		//y2n = 2 * y2n - y2;
		if (x < x2)
		{
			y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x0, x2 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y0, y2n })));
		}
		else if (x == x2)
		{
			y = y2;
		}
		else
		{
			y = GetLineMatchingValue(x, new ArrayList<Double>(java.util.Arrays.asList(new Double[] { x2, x1 })), new ArrayList<Double>(java.util.Arrays.asList(new Double[] { y2n, y1 })));
		}
		return y;
	}
	///#endregion
}