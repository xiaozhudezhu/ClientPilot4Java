package meshFHE.funcLib;

import java.util.ArrayList;
import java.util.List;

public class Cipher
{
	
	public List<Double>[] cipherItems = new ArrayList[4];
	public List<List<Integer>> multiPow = new ArrayList<List<Integer>>();
	public List<Integer> maxPower = new ArrayList<Integer>(java.util.Arrays.asList(new Integer[] { 0, 0, 0, 0 }));

	public Cipher() {
		initCipherItem();
	}
	
	private void initCipherItem() {
		cipherItems[0] = new ArrayList<Double>();
		cipherItems[1] = new ArrayList<Double>();
		cipherItems[2] = new ArrayList<Double>();
		cipherItems[3] = new ArrayList<Double>();
	}
	
	public double[] getCipherItem(int index)
	{
		double[] ci = new double[] { cipherItems[0].get(index), cipherItems[1].get(index), cipherItems[2].get(index) };
		return ci;
	}
	public double[] getCipherItemsByCPM(String cpm)
	{
		int index = -1;
		for (int i = 0; i < multiPow.size(); i++)
		{
			String cpmi = getMultiPowString(i);
			if (cpm.equals(cpmi))
			{
				index = i;
				break;
			}
		}
		double[] ci = index >= 0 ? new double[] { cipherItems[0].get(index), cipherItems[1].get(index), cipherItems[2].get(index) } : null;
		return ci;
	}
	public void addCipherItem(double[] ci)
	{
		cipherItems[0].add(ci[0]);
		cipherItems[1].add(ci[1]);
		cipherItems[2].add(ci[2]);
	}
	public void addMultiPow(int stage, int site, int power)
	{
		ArrayList<Integer> cmp = new ArrayList<Integer>();
		for (int i = 0; i < stage; i++)
		{
			cmp.add(0);
		}
		cmp.set(site, power);
		multiPow.add(cmp);
		if (maxPower.get(site) < power)
		{
			maxPower.set(site, power);
		}
	}
	public String getMultiPowString(int index)
	{
		List<Integer> cmp = multiPow.get(index);
		String res = "";
		for (int p : cmp)
		{
			res += (new Integer(p)).toString() + ",";
		}
		return res.substring(0, res.length() - 1);
	}
	public List<Integer> getMultiPowIndex(String cmp)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < multiPow.size(); i++)
		{
			String cmps = "";
			for (int j = 0; j < multiPow.get(i).size(); j++)
			{
				cmps += multiPow.get(i).get(j).toString() + ",";
			}
			cmps = cmps.substring(0, cmps.length() - 1);
			if (cmp.equals(cmps))
			{
				res.add(i);
			}
		}
		return res;
	}
	public void Sort()
	{
		//升幂排列，二元
		List<String> mpList = new ArrayList<String>();
		List<List<Integer>> multiPow = new ArrayList<List<Integer>>();
		List<double[]> ciList = new ArrayList<double[]>();
		int xpm = maxPower.get(0);
		int ypm = maxPower.get(1);
		for (int i = 1; i < xpm + 1; i++)
		{
			for (int j = 0; j < xpm + 1; j++)
			{
				for (int k = 0; k < ypm + 1; k++)
				{
					if (j == 0 && k == 0 || j + k > i)
					{
						continue;
					}
					String cmp = j + "," + k;
					if(!mpList.contains(cmp))
					{
						mpList.add(cmp);
					}
				}
			}
		}
		double B = this.cipherItems[3].get(0);
		for (int i = 0; i < mpList.size(); i++)
		{
			List<Integer> index = getMultiPowIndex(mpList.get(i));
			for (int ci : index)
			{
				multiPow.add(this.multiPow.get(ci));
				ciList.add(this.getCipherItem(ci));
			}
		}
		this.multiPow = multiPow;
		this.initCipherItem();
		for (double[] ci : ciList)
		{
			this.addCipherItem(ci);
		}
		this.cipherItems[3].add(B);
	}
	@Override
	public String toString()
	{
		String res = "";
		for(int i=0;i<cipherItems[0].size();i++)
		{
			res += cipherItems[0].get(i).toString() + "," + cipherItems[1].get(i).toString() + "," + cipherItems[2].get(i).toString() + ",";
		}
		res += "0"; // cipherItems[3][0].ToString();
		return res;
	}
	public String ToShortString()
	{
		String res = "";
		for (int i = 1; i < cipherItems[0].size(); i++)
		{
			res += cipherItems[0].get(i).toString() + "," + cipherItems[1].get(i).toString() + "," + cipherItems[2].get(i).toString() + ",";
		}
		//res += "0";// cipherItems[3][0].ToString();
		res = res.substring(0, res.length() - 1);
		return res;
	}
	public void GenFromValue(double value, int stage)
	{
		for (int i = 0; i < stage + 1; i++)
		{
			cipherItems[0].add(i == 0 ? value : 0);
			cipherItems[1].add(0d);
			cipherItems[2].add(0d);
		}
		//cipherItems[3].Add(value);
	}
	/*public String GetValueToJson()
	{
		String result = "[";
		for(int i=0;i<cipherItems[0].size();i++)
		{
			result += "[" + cipherItems[0].get(i).toString() + "," + cipherItems[1].get(i).toString() + "," + cipherItems[2].get(i).toString() + "],";
		}
		result = result.substring(0, result.length() - 1) + "]";
		return result;
	}
	public String GetValueToJsonWithPower()
	{
		String result = "[";
		for (int i = 0; i < cipherItems[0].size(); i++)
		{
			result += "[[" + cipherItems[0].get(i).toString() + "," + cipherItems[1].get(i).toString() + "," + cipherItems[2].get(i).toString() + "],[" + multiPow.get(i).get(0).toString() + "," + multiPow.get(i).get(1).toString() + "]],";
		}
		result = result.substring(0, result.length() - 1) + "]";
		return result;
	}
	public void GetValueFromJson(List<JToken> res)
	{
		for(java.util.List<JToken> item : res)
		{
			java.util.List<JToken> values = (java.util.List<JToken>)item.get(0);
			java.util.List<JToken> powers = (java.util.List<JToken>)item.get(1);
			cipherItems[0].add((double)values.get(0));
			cipherItems[1].add((double)values.get(1));
			cipherItems[2].add((double)values.get(2));
			ArrayList<Integer> powers2 = new ArrayList<Integer>();
			powers2.add((int)powers.get(0));
			powers2.add((int)powers.get(1));
			multiPow.add(powers2);
		}
	}
	public String GetDeliveryForm()
	{
		return Convert.ToBase64String(Encoding.UTF8.GetBytes(GetValueToJsonWithPower()));
	}
	public void GetValueFromDeliveryForm(String df)
	{
		String jsonString = Encoding.UTF8.GetString(Convert.FromBase64String(df));
		var res = JArray.Parse(jsonString);
		GetValueFromJson((java.util.List<JToken>)res);
	}*/
	public Cipher GetClone()
	{
		Cipher res = new Cipher();
		for (int i = 0; i < cipherItems.length; i++)
		{
			List<Double> cipherItemsElement = cipherItems[i];
			List<Double> new_cipherItemsElement = new ArrayList<Double>();
			for (double cieElement : cipherItemsElement)
			{
				new_cipherItemsElement.add(cieElement);
			}
			res.cipherItems[i] = new_cipherItemsElement;
		}
		for(List<Integer> multiPowElement : multiPow)
		{
			List<Integer> new_multiPowElement = new ArrayList<Integer>();
			for(int mpeElement : multiPowElement)
			{
				new_multiPowElement.add(mpeElement);
			}
			res.multiPow.add(new_multiPowElement);
		}
		for(int maxPowElement : maxPower)
		{
			res.maxPower.add(maxPowElement);
		}
		return res;
	}
}