package meshFHE.funcLib;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class IntHE {
	
	private static String x0 = "92";
	private static String x1 = "93";
    private static String y0 = "89";
    private static String y1 = "90";
    
    private static int period = 1000;
    
    private static GMap g;
    
    //private static String DEFAULT_KEY_FILE = "F:/Career/Groups/Own/Projects/sf/shaftstop_key.txt";

    private static String DEFAULT_GMAP_FILE = "D:/BaiduNetdiskDownload/GFDF/DF635683432243530954";
    
    private static String KEY_DIR = getPath() + "/shaftstop/key";
    private static String GMAP_DIR = getPath() + "/shaftstop/gmap";
    
    private static Map<String, SSKey> keyCache = new MaxSizeHashMap<String, SSKey>(100);
    private static Map<String, GMap> gCache = new MaxSizeHashMap<String, GMap>(100);

	static {
		Properties properties = new Properties();
		try {
			properties.load(IntHE.class.getResourceAsStream("/shaftstop.properties"));
			String keyDir = properties.getProperty("KEY_DIR");
			String gmapDir = properties.getProperty("GMAP_DIR");
			if(keyDir != null && !keyDir.equals(""))
				KEY_DIR = properties.getProperty("KEY_DIR");
			if(gmapDir != null && !gmapDir.equals(""))
				GMAP_DIR = properties.getProperty("GMAP_DIR");
		} catch (IOException e) {
			System.out.println("WARN：配置文件不存在，将使用默认配置文件");
		}
		File keyDir = new File(KEY_DIR);
		if(!keyDir.exists())
			keyDir.mkdirs();
		File gMapDir = new File(GMAP_DIR);
		if(!gMapDir.exists())
			gMapDir.mkdirs();
		try {
			g = LocalFuctionLib.loadG(DEFAULT_GMAP_FILE, period);
		} catch (IOException e) {
			System.out.println("ERROR：运算字典不存在！");
			e.printStackTrace();
		}
	}
    
	/**
	 * 根据用户标识生成私钥并持久化私钥
	 * @param uid 用户标识
	 * @return 私钥内容
	 * @throws IOException 私钥文件生成失败
	 */
    public static SSKey genKey(String uid) throws IOException {
		SSKey defaultKey = LocalFuctionLib.loadKey(IntHE.class.getResourceAsStream("/shaftstop_key"));
		String fileName = KEY_DIR + File.separator + uid;
		return LocalFuctionLib.genKey(new Random(), period, defaultKey, fileName);
	}
    
    /**
     * 根据用户标识删除私钥
     * @param uid 用户标识
     */
    public static void removeKey(String uid) {
		String fileName = KEY_DIR + File.separator + uid;
		File file = new File(fileName);
		if(file.exists())
			file.delete();
		if(keyCache.containsKey(uid))
			keyCache.remove(uid);
    }
    

    /**
     * 根据用户标识和私钥生成运算字典并持久化
     * @param key 私钥
     * @param uid 用户标识
     * @return 运算字典
     * @throws IOException 运算字典文件生成失败
     */
	public static GMap genGMap(SSKey key, String uid) throws IOException {
		String fileName = GMAP_DIR + File.separator + uid;
		return LocalFuctionLib.genGMap(new Random(), period, key, fileName);
	}
	

    /**
     * 根据用户标识删除运算字典
     * @param uid 用户标识
     */
    public static void removeGMap(String uid) {
		String fileName = GMAP_DIR + File.separator + uid;
		File file = new File(fileName);
		if(file.exists())
			file.delete();
		if(gCache.containsKey(uid))
			gCache.remove(uid);
    }
    
	/**
	 * 根据用户标识获取私钥
	 * @param uid 用户标识
	 * @return 私钥
	 * @throws IOException 私钥文件访问异常
	 */
	public static SSKey getKey(String uid) throws IOException {
		SSKey key = keyCache.get(uid);
		if(key == null) {
			key = LocalFuctionLib.loadKey(KEY_DIR + File.separator + uid);
			keyCache.put(uid, key);
		}
		return key;
	}
	
	/**
	 * 根据用户标识获取运算字典
	 * @param uid 用户标识
	 * @return 运算字典
	 * @throws IOException 运算字典文件访问异常
	 */
	public static GMap getGMap(String uid) throws IOException {
		GMap gmap = gCache.get(uid);
		if(gmap == null) {
			gmap = LocalFuctionLib.loadGMap(GMAP_DIR + File.separator + uid);
			if(g != null) {
				gmap.G = g.G;
				gmap.xs = g.xs;
				gmap.ys = g.ys;
			}
			gCache.put(uid, gmap);
		}		
		return gmap;
	}

	/**
	 * 加密
	 * @param m 明文
	 * @param key 私钥
	 * @return 密文
	 */
	public static Cipher encrypt(double m, SSKey key) {
		return CryptLib.Encrypt(new Random(), m, key);
	}
	
	/**
	 * 加密
	 * @param m 明文
	 * @param uid 用户标识
	 * @return 密文
	 */
	public static Cipher encrypt(double m, String uid) throws Exception {
		try {
			return CryptLib.Encrypt(new Random(), m, getKey(uid));
		} catch (IOException e) {
			throw new Exception("key not exists");
		}
	}

	/**
	 * 解密
	 * @param c 密文
	 * @param key 私钥
	 * @return 明文
	 */
	public static double decrypt(Cipher c, SSKey key) {
		return CryptLib.Decrypt(c, key);
	}
	
	/**
	 * 解密
	 * @param c 密文
	 * @param uid 用户标识
	 * @return 明文
	 * @throws Exception 私钥不存在
	 */
	public static double decrypt(Cipher c, String uid) throws Exception {
		try {
			return decrypt(c, getKey(uid));
		} catch (IOException e) {
			throw new Exception("key not exists");
		}	
	}

	/**
	 * 密文加法运算
	 * @param c1 密文1 
	 * @param c2 密文2
	 * @param gmap 运算字典
	 * @return 运算后的密文
	 */
	public static Cipher add(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, 1, c1, c2, gmap, period);
	}
	
	/**
	 * 密文加法运算
	 * @param c1 密文1
	 * @param c2 密文2
	 * @param uid 用户标识
	 * @return 运算后的密文
	 * @throws Exception 运算字典不存在
	 */
	public static Cipher add(Cipher c1, Cipher c2, String uid) throws Exception {
		GMap g = null;
		try {
			g = getGMap(uid);
		} catch (IOException e) {
			throw new Exception("map not exists");
		}
		return add(c1, c2, g);
	}

	/**
	 * 密文减法运算
	 * @param c1 密文1 
	 * @param c2 密文2
	 * @param gmap 运算字典
	 * @return 运算后的密文
	 */
	public static Cipher substract(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, -1, c1, c2, gmap, period);
	}
	
	/**
	 * 密文减法运算
	 * @param c1 密文1
	 * @param c2 密文2
	 * @param uid 用户标识
	 * @return 运算后的密文
	 * @throws Exception 运算字典不存在
	 */
	public static Cipher substract(Cipher c1, Cipher c2, String uid) throws Exception {
		GMap g = null;
		try {
			g = getGMap(uid);
		} catch (IOException e) {
			throw new Exception("map not exists");
		}
		return substract(c1, c2, g);
	}

	/**
	 * 密文乘法运算
	 * @param c1 密文1 
	 * @param c2 密文2
	 * @param gmap 运算字典
	 * @return 运算后的密文
	 */
	public static Cipher multiply(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherMultiplyResult(c1, c2, gmap, period);
	}
	
	/**
	 * 密文乘法运算
	 * @param c1 密文1
	 * @param c2 密文2
	 * @param uid 用户标识
	 * @return 运算后的密文
	 * @throws Exception 运算字典不存在
	 */
	public static Cipher multiply(Cipher c1, Cipher c2, String uid) throws Exception {
		GMap g = null;
		try {
			g = getGMap(uid);
		} catch (IOException e) {
			throw new Exception("map not exists");
		}
		return multiply(c1, c2, g);
	}

	/**
	 * 密文比较运算
	 * @param c1 密文1 
	 * @param c2 密文2
	 * @param gmap 运算字典
	 * @return 比较结果 "true"：大于； "none":相等；"false"：小于
	 */
	public static String compare(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.Compare(substract(c1, c2, gmap), x0, x1, y0, y1, gmap, period);
	}
	
	
	/**
	 * 密文比较运算
	 * @param c1 密文1
	 * @param c2 密文2
	 * @param uid 用户标识
	 * @return 比较结果 "true"：大于； "none":相等；"false"：小于
	 * @throws Exception 运算字典不存在
	 */
	public static String compare(Cipher c1, Cipher c2, String uid) throws Exception {
		GMap g = null;
		try {
			g = getGMap(uid);
		} catch (IOException e) {
			throw new Exception("map not exists");
		}
		return compare(c1, c2, g);
	}

	/**
	 * 密文转换
	 * @param c 密文
	 * @param gmap1 源运算字典
	 * @param gmap2 目标运算字典
	 * @return 转换后的密文
	 */
	public static Cipher transfer(Cipher c, GMap gmap1, GMap gmap2) {
		return ServerFunctionOperate.Transfer(c, gmap1, gmap2, period);
	}
	
	/**
	 * 密文转换
	 * @param c 密文
	 * @param uid1 源用户标识
	 * @param uid2 目标用户标识
	 * @return 转换后的密文
	 * @throws Exception
	 */
	public static Cipher transfer(Cipher c, String uid1, String uid2) throws Exception {
		GMap g1, g2 = null;
		try {
			g1 = getGMap(uid1);
			g2 = getGMap(uid2);
		} catch (IOException e) {
			throw new Exception("map not exists");
		}
		return transfer(c, g1, g2);
	}
	
	
	private static String getPath()
	{
		String path = IntHE.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(System.getProperty("os.name").contains("dows"))
		{
			path = path.substring(1,path.length());
		}
		if(path.contains("jar"))
		{
			path = path.substring(0,path.lastIndexOf("."));
			return path.substring(0,path.lastIndexOf("/"));
		}
		return path.replace("target/classes/", "");
	}

}
