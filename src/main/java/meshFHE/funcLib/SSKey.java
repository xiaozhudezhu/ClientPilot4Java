package meshFHE.funcLib;

import java.util.ArrayList;
import java.util.List;

public class SSKey
{
	public String UID;
	public List<List<Double>> f1, f2;
	public List<Double> zi = new ArrayList<Double>();
	public int stage, period;
	public GMap G;
	public int accuracy = 0;

	public SSKey(java.util.Random r, int period, List<List<Double>> f1, List<List<Double>> f2, GMap G)
	{
		stage = 2;
		this.period = period;
		this.f1 = f1;
		this.f2 = f2;
		this.G = G;
		for (int i = 0; i < stage; i++)
		{
			zi.add(r.nextInt(100) + r.nextDouble());
		}
	}
	public SSKey(java.util.Random r, int period, List<List<Double>> f1, List<List<Double>> f2, GMap G, List<Double> zi)
	{
		stage = 2;
		this.period = period;
		this.f1 = f1;
		this.f2 = f2;
		this.G = G;
		this.zi = zi;
	}
	public final void RegenZ(java.util.Random r)
	{
		zi = new ArrayList<Double>();
		for (int i = 0; i < stage; i++)
		{
			zi.add(r.nextInt(100) + r.nextDouble());
		}
	}
	@Override
	public String toString()
	{
		String res = "";
		res += zi.get(0) + "," + zi.get(1);
		return res;
	}
	public final String ToString2()
	{
		String res = "("+this.toString()+"),[";
		for (int i = 0; i < f1.size(); i++)
		{
			res += "(" + f1.get(i).get(0) + "," + f1.get(i).get(1) + ")";
		}
		res += "],[";
		for (int i = 0; i < f2.size(); i++)
		{
			res += "(" + f2.get(i).get(0) + "," + f2.get(i).get(1) + ")";
		}
		res += "]";
		return res;
	}
}