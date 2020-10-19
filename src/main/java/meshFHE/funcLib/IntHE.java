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
			System.out.println("WARN�������ļ������ڣ���ʹ��Ĭ�������ļ�");
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
			System.out.println("ERROR�������ֵ䲻���ڣ�");
			e.printStackTrace();
		}
	}
    
	/**
	 * �����û���ʶ����˽Կ���־û�˽Կ
	 * @param uid �û���ʶ
	 * @return ˽Կ����
	 * @throws IOException ˽Կ�ļ�����ʧ��
	 */
    public static SSKey genKey(String uid) throws IOException {
		SSKey defaultKey = LocalFuctionLib.loadKey(IntHE.class.getResourceAsStream("/shaftstop_key"));
		String fileName = KEY_DIR + File.separator + uid;
		return LocalFuctionLib.genKey(new Random(), period, defaultKey, fileName);
	}
    
    /**
     * �����û���ʶɾ��˽Կ
     * @param uid �û���ʶ
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
     * �����û���ʶ��˽Կ���������ֵ䲢�־û�
     * @param key ˽Կ
     * @param uid �û���ʶ
     * @return �����ֵ�
     * @throws IOException �����ֵ��ļ�����ʧ��
     */
	public static GMap genGMap(SSKey key, String uid) throws IOException {
		String fileName = GMAP_DIR + File.separator + uid;
		return LocalFuctionLib.genGMap(new Random(), period, key, fileName);
	}
	

    /**
     * �����û���ʶɾ�������ֵ�
     * @param uid �û���ʶ
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
	 * �����û���ʶ��ȡ˽Կ
	 * @param uid �û���ʶ
	 * @return ˽Կ
	 * @throws IOException ˽Կ�ļ������쳣
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
	 * �����û���ʶ��ȡ�����ֵ�
	 * @param uid �û���ʶ
	 * @return �����ֵ�
	 * @throws IOException �����ֵ��ļ������쳣
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
	 * ����
	 * @param m ����
	 * @param key ˽Կ
	 * @return ����
	 */
	public static Cipher encrypt(double m, SSKey key) {
		return CryptLib.Encrypt(new Random(), m, key);
	}
	
	/**
	 * ����
	 * @param m ����
	 * @param uid �û���ʶ
	 * @return ����
	 */
	public static Cipher encrypt(double m, String uid) throws Exception {
		try {
			return CryptLib.Encrypt(new Random(), m, getKey(uid));
		} catch (IOException e) {
			throw new Exception("key not exists");
		}
	}

	/**
	 * ����
	 * @param c ����
	 * @param key ˽Կ
	 * @return ����
	 */
	public static double decrypt(Cipher c, SSKey key) {
		return CryptLib.Decrypt(c, key);
	}
	
	/**
	 * ����
	 * @param c ����
	 * @param uid �û���ʶ
	 * @return ����
	 * @throws Exception ˽Կ������
	 */
	public static double decrypt(Cipher c, String uid) throws Exception {
		try {
			return decrypt(c, getKey(uid));
		} catch (IOException e) {
			throw new Exception("key not exists");
		}	
	}

	/**
	 * ���ļӷ�����
	 * @param c1 ����1 
	 * @param c2 ����2
	 * @param gmap �����ֵ�
	 * @return ����������
	 */
	public static Cipher add(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, 1, c1, c2, gmap, period);
	}
	
	/**
	 * ���ļӷ�����
	 * @param c1 ����1
	 * @param c2 ����2
	 * @param uid �û���ʶ
	 * @return ����������
	 * @throws Exception �����ֵ䲻����
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
	 * ���ļ�������
	 * @param c1 ����1 
	 * @param c2 ����2
	 * @param gmap �����ֵ�
	 * @return ����������
	 */
	public static Cipher substract(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherAddResult(1, -1, c1, c2, gmap, period);
	}
	
	/**
	 * ���ļ�������
	 * @param c1 ����1
	 * @param c2 ����2
	 * @param uid �û���ʶ
	 * @return ����������
	 * @throws Exception �����ֵ䲻����
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
	 * ���ĳ˷�����
	 * @param c1 ����1 
	 * @param c2 ����2
	 * @param gmap �����ֵ�
	 * @return ����������
	 */
	public static Cipher multiply(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.GetCipherMultiplyResult(c1, c2, gmap, period);
	}
	
	/**
	 * ���ĳ˷�����
	 * @param c1 ����1
	 * @param c2 ����2
	 * @param uid �û���ʶ
	 * @return ����������
	 * @throws Exception �����ֵ䲻����
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
	 * ���ıȽ�����
	 * @param c1 ����1 
	 * @param c2 ����2
	 * @param gmap �����ֵ�
	 * @return �ȽϽ�� "true"�����ڣ� "none":��ȣ�"false"��С��
	 */
	public static String compare(Cipher c1, Cipher c2, GMap gmap) {
		return ServerFunctionOperate.Compare(substract(c1, c2, gmap), x0, x1, y0, y1, gmap, period);
	}
	
	
	/**
	 * ���ıȽ�����
	 * @param c1 ����1
	 * @param c2 ����2
	 * @param uid �û���ʶ
	 * @return �ȽϽ�� "true"�����ڣ� "none":��ȣ�"false"��С��
	 * @throws Exception �����ֵ䲻����
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
	 * ����ת��
	 * @param c ����
	 * @param gmap1 Դ�����ֵ�
	 * @param gmap2 Ŀ�������ֵ�
	 * @return ת���������
	 */
	public static Cipher transfer(Cipher c, GMap gmap1, GMap gmap2) {
		return ServerFunctionOperate.Transfer(c, gmap1, gmap2, period);
	}
	
	/**
	 * ����ת��
	 * @param c ����
	 * @param uid1 Դ�û���ʶ
	 * @param uid2 Ŀ���û���ʶ
	 * @return ת���������
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
