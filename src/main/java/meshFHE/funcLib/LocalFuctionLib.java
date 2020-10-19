package meshFHE.funcLib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class LocalFuctionLib
{
	public static List<List<Double>> genFPoints(java.util.Random r, int max, int period)
	{
		return genFPoints(r, max, period, 0);
	}
	public static List<List<Double>> genFPoints(java.util.Random r, int max, int period, int baseline)
	{
		return genFPoints(r, max, period, baseline, 1);
	}
	public static List<List<Double>> genFPoints(java.util.Random r, int max, int period, int baseline, double k0)
	{
		return genFPoints(r, max, period, baseline, k0, 1, 1);
	}
	public static List<List<Double>> genFPoints(java.util.Random r, int max, int period, int baseline, double k0, double scaleX, double scaleY)
	{
		List<Double> kList = new ArrayList<Double>();
		List<List<Double>> points = LocalFuctionLib.genPointList(r, max, period, baseline, k0, kList, scaleX, scaleY);
		List<Double> yList = new ArrayList<Double>();
		double ymin = 1000;
		double ymax = -1000;
		double ysum = 0;
		for (double x = 1; x < points.get(points.size() - 1).get(0); x++)
		{
			double y = LocalFuctionLib.get2BezierY(x, points);
			yList.add(y);
			if (y < ymin)
			{
				ymin = y;
			}
			if (y > ymax)
			{
				ymax = y;
			}
			ysum += y;
		}
		//double yavg = ysum / yList.size();
		double kmin = 10;
		double kmax = 0;
		double ksum = 0;
		double ksum2 = 0;
		for (double k : kList)
		{
			ksum += k;
			double k2 = Math.abs(k);
			ksum2 += k2;
			if (k2 > kmax)
			{
				kmax = k2;
			}
			if (k2 < kmin)
			{
				kmin = k2;
			}
		}
		double kav = ksum / kList.size();
		double kav2 = ksum2 / kList.size();
		double kdev = 0;
		double kdev2 = 0;
		int c10 = 0;
		int c20 = 0;
		int c90 = 0;
		List<Integer> cList = java.util.Arrays.asList(new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		for (double k : kList)
		{
			double k2 = Math.abs(k);
			kdev = Math.pow(k - kav, 2);
			kdev2 = Math.pow(k2 - kav2, 2);
			if (kmax - k2 <= kmax * 0.1)
			{
				c10++;
				cList.set(0, cList.get(0) + 1);
			}
			else if (kmax - k2 <= kmax * 0.2)
			{
				c20++;
				cList.set(1, cList.get(1) + 1);
			}
			else if (kmax - k2 <= kmax * 0.3)
			{
				cList.set(2, cList.get(2) + 1);
			}
			else if (kmax - k2 <= kmax * 0.4)
			{
				cList.set(3, cList.get(3) + 1);
			}
			else if (kmax - k2 <= kmax * 0.5)
			{
				cList.set(4, cList.get(4) + 1);
			}
			else if (kmax - k2 <= kmax * 0.6)
			{
				cList.set(5, cList.get(5) + 1);
			}
			else if (kmax - k2 <= kmax * 0.7)
			{
				cList.set(6, cList.get(6) + 1);
			}
			else if (kmax - k2 <= kmax * 0.8)
			{
				cList.set(7, cList.get(7) + 1);
			}
			else if (kmax - k2 <= kmax * 0.9)
			{
				cList.set(8, cList.get(8) + 1);
			}
			else if (kmax - k2 <= kmax * 1)
			{
				cList.set(9, cList.get(9) + 1);
			}
			if (k2 <= kmin * 1.2)
			{
				c90++;
			}
		}
		kdev = Math.sqrt(kdev) / kList.size();
		kdev2 = Math.sqrt(kdev2) / kList.size();
		//double r10 = (double)c10 / (double)kList.size();
		//double r20 = (double)(c10 + c20) / (double)kList.size();
		//double r90 = (double)c90 / (double)kList.size();
		ArrayList<Double> crList = new ArrayList<Double>();
		for (int c : cList)
		{
			crList.add((double)c / (double)points.size());
		}
		List<Double> ycList = java.util.Arrays.asList(new Double[] { 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d });
		double ygap = ymax - ymin;
		for (double y : yList)
		{
			if (y - ymin <= ygap * 0.1)
			{
				ycList.set(0, ycList.get(0) + 1);
			}
			else if (y - ymin <= ygap * 0.2)
			{
				ycList.set(1, ycList.get(1) + 1);
			}
			else if (y - ymin <= ygap * 0.3)
			{
				ycList.set(2, ycList.get(2) + 1);
			}
			else if (y - ymin <= ygap * 0.4)
			{
				ycList.set(3, ycList.get(3) + 1);
			}
			else if (y - ymin <= ygap * 0.5)
			{
				ycList.set(4, ycList.get(4) + 1);
			}
			else if (y - ymin <= ygap * 0.6)
			{
				ycList.set(5, ycList.get(5) + 1);
			}
			else if (y - ymin <= ygap * 0.7)
			{
				ycList.set(6, ycList.get(6) + 1);
			}
			else if (y - ymin <= ygap * 0.8)
			{
				ycList.set(7, ycList.get(7) + 1);
			}
			else if (y - ymin <= ygap * 0.9)
			{
				ycList.set(8, ycList.get(8) + 1);
			}
			else if (y - ymin <= ygap * 1)
			{
				ycList.set(9, ycList.get(9) + 1);
			}
		}
		double crmax = 0;
		double crmin = 1;
		double crsum = 0;
		double crcnt = 0;
		for (double cr : crList)
		{
			crsum += cr;
			if (cr < crmin)
			{
				crmin = cr;
			}
			if (cr > crmax)
			{
				crmax = cr;
			}
			if (cr > 0)
			{
				crcnt++;
			}
		}
		double cravg = crsum / crcnt;
		double crdev = 0;
		for (double cr : crList)
		{
			crdev = Math.pow(cr - cravg, 2);
		}
		crdev = Math.sqrt(crdev) / crcnt;

		if (!(kmax < 1 && crdev < 0.01 && points.get(points.size() - 1).get(0) == period))
		{
			points = genFPoints(r, max, period, baseline, k0, scaleX, scaleY);
		}
		return points;
	}
	public static List<List<Double>> genPointList(java.util.Random r, int max, int period, ArrayList<Double> kList)
	{
		return genPointList(r, max, period, 0, kList);
	}
	public static List<List<Double>> genPointList(java.util.Random r, int max, int period, int baseline, ArrayList<Double> kList)
	{
		return genPointList(r, max, period, baseline, 1, kList);
	}
	public static List<List<Double>> genPointList(java.util.Random r, int max, int period, int baseline, double k0, ArrayList<Double> kList)
	{
		return genPointList(r, max, period, baseline, k0, kList, 1, 1);
	}
	public static List<List<Double>> genPointList(java.util.Random r, int max, int period, int baseline, double k0, List<Double> kList, double scaleX, double scaleY)
	{
		double times = 1;
		scaleX = scaleX * 1 / times;
		period = period * (int)times;
		double x0 = 0;
		double y0 = baseline;
		List<Double> point0 = java.util.Arrays.asList(new Double[] { x0, y0 });
		List<List<Double>> points = new ArrayList<List<Double>>();
		points.add(point0);
		double x1 = 20;
		double y1 = x1 * k0 + baseline;
		points.add(java.util.Arrays.asList(new Double[] { x1, y1 }));
		x0 = x1;
		y0 = y1;
		for (int i = 0; i < max; i++)
		{
			double k = 0;
			if (i > 0)
			{
				k = (y0 - y1) / (x0 - x1);
				double d = r.nextDouble() + 0.3;
				x1 = x0 - x1 > 100 || y0 - y1 > 100 ? x0 + (x0 - x1) * 0.3 : x0 + (x0 - x1) * d;
				y1 = x0 - x1 > 100 || y0 - y1 > 100 ? y0 + (y0 - y1) * 0.3 : y0 + (y0 - y1) * d;
				x0 = x1;
				y0 = y1;
				points.add(java.util.Arrays.asList(new Double[] { x1, y1 }));
			}
			else
			{
				k = k0;
			}
			x0 += 20 + r.nextInt(60);
			y0 = k > 0 ? y0 + (x0 - x1) * k - 10 - r.nextInt(40) : y0 + (x0 - x1) * k + 10 + r.nextInt(40);
			if (i == max - 1)
			{
				//x0 += 50;
				double xtemp = x0;
				if (x0 < period)
				{
					x0 = period;
				}
				y0 = baseline;
				double tx0 = points.get(points.size() - 2).get(0);
				double ty0 = points.get(points.size() - 2).get(1);
				double tx1 = points.get(points.size() - 1).get(0);
				double ty1 = points.get(points.size() - 1).get(1);
				double tx2 = x0;
				double ty2 = y0;
				double k2 = k0;
				double k1 = (ty1 - ty0) / (tx1 - tx0);
				if (k1 > 0 && k0 > 0 || k1 < 0 && k0 < 0)
				{
					x0 = xtemp + 20;
					points.add(java.util.Arrays.asList(new Double[] { x0, y0 }));
					k = (points.get(points.size() - 1).get(1) - points.get(points.size() - 2).get(1)) / (points.get(points.size() - 1).get(0) - points.get(points.size() - 2).get(0));
					kList.add(k);
					double d = r.nextDouble() + 0.3;
					x1 = x0 + (x0 - x1) * d;
					y1 = y0 + (y0 - y1) * d;
					x0 = x1;
					points.add(java.util.Arrays.asList(new Double[] { x1, y1 }));
					if (x0 < period)
					{
						x0 = period;
					}
					y0 = baseline;
					tx0 = points.get(points.size() - 2).get(0);
					ty0 = points.get(points.size() - 2).get(1);
					tx1 = points.get(points.size() - 1).get(0);
					ty1 = points.get(points.size() - 1).get(1);
					tx2 = x0;
					ty2 = y0;
					k1 = (ty1 - ty0) / (tx1 - tx0);
					double y3 = (tx2 - ty2 / k2 - tx0 + ty0 / k1) / (1 / k1 - 1 / k2);
					double x3 = tx2 + (y3 - ty2) / k2;
					points.get(points.size() - 1).set(0, x3);
					points.get(points.size() - 1).set(1, y3);
				}
				else
				{
					double y3 = (tx2 - ty2 / k2 - tx0 + ty0 / k1) / (1 / k1 - 1 / k2);
					double x3 = tx2 + (y3 - ty2) / k2;
					points.get(points.size() - 1).set(0, x3);
					points.get(points.size() - 1).set(1, y3);
				}

			}
			else if (i == max - 2)
			{
				x0 += 50;
				y0 = baseline;
			}
			else if (Math.abs(y0 - baseline) >= 80)
			{
				x0 += 50;
				y0 = baseline + (y0 - baseline) / 2;
			}
			else if (i < max - 2 && x0 >= period - 500)
			{
				max = i + 3;
			}
			points.add(java.util.Arrays.asList(new Double[] { x0, y0 }));
			k = (points.get(points.size() - 1).get(1) - points.get(points.size() - 2).get(1)) / (points.get(points.size() - 1).get(0) - points.get(points.size() - 2).get(0));
			kList.add(k);
		}
		for (List<Double> point : points)
		{
			//swinginwind
			point.set(0, Math.floor((point.get(0) * scaleX)));
			point.set(1, point.get(1) * scaleY);
		}
		return points;
	}
	public static double get2BezierY(double x, List<List<Double>> points)
	{
		while (true)
		{
			if (x < points.get(0).get(0))
			{
				x += points.get(points.size() - 1).get(0);
			}
			else if (x > points.get(points.size() - 1).get(0))
			{
				x -= points.get(points.size() - 1).get(0);
			}
			else
			{
				break;
			}
		}

		double y = 0;
		double x1 = 0;
		int p1i = 0;
		double x3 = 0;
		int p3i = 0;
		for (int i = 0; i < points.size(); i += 2)
		{
			List<Double> point = points.get(i);
			double xc = point.get(0);
			if (xc >= x)
			{
				x3 = xc;
				p3i = i;
				break;
			}
			else
			{
				x1 = xc;
				p1i = i;
			}
		}
		double x2 = points.get(p1i + 1).get(0);
		double t = 0;
		double delta = Math.pow((2 * x2 - 2 * x1), 2) - 4 * (x1 - 2 * x2 + x3) * (x1 - x);
		
		if (delta >= 0)
		{
			t = (2 * x1 - 2 * x2 + Math.sqrt(delta)) / (2 * (x1 - 2 * x2 + x3));
			double y1 = points.get(p1i).get(1);
			double y2 = points.get(p1i + 1).get(1);
			double y3 = points.get(p3i).get(1);
			y = Math.pow((1 - t), 2) * y1 + 2 * t * (1 - t) * y2 + Math.pow(t, 2) * y3;
			
		}
		else
		{
			throw new RuntimeException("negative delta");
		}
		if(Double.isNaN(y) || Double.isInfinite(y)) {
			return 0d;
		}
		BigDecimal bg = new BigDecimal(y).setScale(5, RoundingMode.UP);
		return bg.doubleValue();
	}
	public static double F(List<List<Double>> ps, double x)
	{
		if (ps.isEmpty())
		{
			return 1; // x;
		}
		return get2BezierY(x, ps);
	}
	public static double F(List<List<Double>> ps1, List<List<Double>> ps2, double x, double y)
	{
		if (ps1.isEmpty())
		{
			return 1; // x + y;
		}
		return get2BezierY(x, ps1) + get2BezierY(y, ps2);
	}
	public static double h1(double x, double y, int period)
	{
		double res = x + y + 1 >= period ? x + y + 1 - period : x + y + 1;
		return res;
	}
	public static double h2(double x, double y, int period)
	{
		double res = x + y + 2 >= period ? x + y + 2 - period : x + y + 2;
		return res;
	}
	public static double h3(double x, double y, int period)
	{
		double res = x + y + 3 >= period ? x + y + 3 - period : x + y + 3;
		return res;
	}
	public static double h4(double x, double y, int period)
	{
		double res = x + y + 4 >= period ? x + y + 4 - period : x + y + 4;
		return res;
	}
	public static double h5(double x, double y, int period)
	{
		double res = x + y + 5 >= period ? x + y + 5 - period : x + y + 5;
		return res;
	}
	public static double h4r(double x, double y, int period)
    {
        double res = y - x - 4 < 0 ? y - x - 4 + period : y - x - 4;
        return res;
    }
    public static double h5r(double x, double y, int period)
    {
        double res = y - x - 5 < 0 ? y - x - 5 + period : y - x - 5;
        return res;
    }
	public static SSKey genKey(java.util.Random r, int period, SSKey defaultKey, String fileName) throws IOException
	{
		File f = new File(fileName);
		if(f.exists())
			return null;
		int max = 120;
		int time = 1;
		double time1 = 1;
		SSKey key = null;
		if(defaultKey != null) {
			key = new SSKey(r, period, defaultKey.f1, defaultKey.f2);
		}
		else {
			List<List<Double>> f1 = LocalFuctionLib.genFPoints(r, max, period * time, 300, 0.1, 1 / (double)time, 1 / (double)time);
			List<List<Double>> f2 = LocalFuctionLib.genFPoints(r, max, period * time, 300, -0.1, 1 / (double)time1, 1 / (double)time);
			key = new SSKey(r, period, f1, f2);
		}
        String keyStr = JSON.toJSONString(key);
        f.createNewFile();
    	FileOutputStream fs = new FileOutputStream(f);
    	byte[] b = keyStr.getBytes();
        fs.write(b);
        fs.close();
		return key;
	}
	public static GMap genGMap(java.util.Random r, int period, SSKey key, String fileName) throws IOException {
		File f = new File(fileName);
		if(f.exists())
			return null;
		GMap g = new GMap();
		LocalFuctionLib.calKeyZP(r, key, g);
		LocalFuctionLib.calMapZZ(r, key, g);
		String gStr = JSON.toJSONString(g);
        f.createNewFile();
    	FileOutputStream fs = new FileOutputStream(f);
    	byte[] b = gStr.getBytes();
        fs.write(b);
        fs.close();
		return g;
	}
	public static void calMapZZ(java.util.Random r, SSKey key, GMap map)
    {
        // 在生成完私钥和字典加载后执行
        map.z11 = CryptLib.Encrypt(r, key.zi.get(0) * key.zi.get(0), key);
        map.z12 = CryptLib.Encrypt(r, key.zi.get(0) * key.zi.get(1), key);
        map.z22 = CryptLib.Encrypt(r, key.zi.get(1) * key.zi.get(1), key);
    }
    public static void calKeyZP(java.util.Random r, SSKey key, GMap map)
    {
        map.zp1 = CryptLib.EncryptPartList(r, key.zi.get(0), key);
        map.zp2 = CryptLib.EncryptPartList(r, key.zi.get(1), key);
    }
    
    public static SSKey loadKey(InputStream is) throws IOException {
	    ByteArrayOutputStream result = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = is.read(buffer)) != -1) {
	    	result.write(buffer, 0, length);
	    }
        String str = result.toString();
        SSKey key = JSON.parseObject(str, SSKey.class);
        return key;
    }
    
    public static SSKey loadKey(String fileName) throws IOException {
        File f = new File(fileName);
    	FileInputStream fs = new FileInputStream(f);
    	byte[] b = new byte[(int) f.length()];
        fs.read(b);
        fs.close();
        String str = new String(b);
        SSKey key = JSON.parseObject(str, SSKey.class);
        return key;
    }
    
    public static GMap loadGMap(String fileName) throws IOException {
        File f = new File(fileName);
    	FileInputStream fs = new FileInputStream(f);
    	byte[] b = new byte[(int) f.length()];
        fs.read(b);
        fs.close();
        String str = new String(b);
        GMap map = JSON.parseObject(str, GMap.class);
        return map;
    }
    
    public static GMap loadG(String fileName, int period) throws IOException
    {
        GMap g = new GMap(period);
        for (int i = 0; i < 6; i++)
        {
            FileInputStream fs = new FileInputStream(fileName + "_" + i + ".gmd");
            for (int j = 0; j < period / GMap.step; j++)
            {
                if (i == 0)
                {
                    g.G[j] = new double[(int)(period / GMap.step)][];
                }
                for (int k = 0; k < period / GMap.step; k++)
                {
                    if (i == 0)
                    {
                        g.G[j][k] = new double[24];
                    }
                    byte[] gs1 = new byte[8];
                    byte[] gs2 = new byte[8];
                    byte[] gs3 = new byte[8];
                    byte[] gs4 = new byte[8];
                    fs.read(gs1, 0, 8);
                    fs.read(gs2, 0, 8);
                    fs.read(gs3, 0, 8);
                    fs.read(gs4, 0, 8);
                    double gv1 = bytes2Double(gs1);
                    double gv2 = bytes2Double(gs2);
                    double gv3 = bytes2Double(gs3);
                    double gv4 = bytes2Double(gs4);
                    g.G[j][k][i * 4] = gv1;
                    g.G[j][k][i * 4 + 1] = gv2;
                    g.G[j][k][i * 4 + 2] = gv3;
                    g.G[j][k][i * 4 + 3] = gv4;
                }
            }
            fs.close();
        }
        FileInputStream fs2 = new FileInputStream(fileName + "_6.gmd");
        for (int i = 0; i < period / GMap.step; i++)
        {
            byte[] xib = new byte[8];
            fs2.read(xib, 0, 8);
            g.xs[i] = bytes2Double(xib);
        }
        fs2.close();
        fs2 = new FileInputStream(fileName + "_7.gmd");
        for (int i = 0; i < period / GMap.step; i++)
        {
            byte[] yib = new byte[8];
            fs2.read(yib, 0, 8);
            g.ys[i] = bytes2Double(yib);
        }
        fs2.close();
        return g;
    }
    
    public static double bytes2Double(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
    }
    
    
}