package redisson.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSACoder {

	// 非对称密钥算法
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 密钥长度，DH算法的默认密钥长度是1024 密钥长度必须是64的倍数，在512到65536位之间
	 */
	private static final int KEY_SIZE = 1024;

	// 公钥
	private static final String PUBLIC_KEY = "RSAPublicKey";

	// 私钥
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	private static final int MAX_ENCRYPT_BLOCK = 117;

	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * 初始化密钥对
	 *
	 * @return Map 甲方密钥的Map
	 */
	public static Map<String, Object> initKey() throws Exception {
		// 实例化密钥生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		// 初始化密钥生成器
		keyPairGenerator.initialize(KEY_SIZE);
		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 甲方公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 甲方私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 将密钥存储在map中
		Map<String, Object> keyMap = new HashMap<String, Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 私钥加密
	 *
	 * @param data 待加密数据
	 * @param key  密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥加密
	 *
	 * @param data 待加密数据
	 * @param key  密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 初始化公钥
		// 密钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		// 数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		// 对数据分段解密
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] cache;
		int inputLen = data.length;
		int offSet = 0;
		int i = 0;
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 私钥解密
	 *
	 * @param data 待解密数据
	 * @param key  密钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 对数据分段解密
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] cache;
		int inputLen = data.length;
		int offSet = 0;
		int i = 0;
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 取得私钥
	 *
	 * @param keyMap 密钥map
	 * @return byte[] 私钥
	 */
	public static byte[] getPrivateKey(Map<String, Object> keyMap) {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return key.getEncoded();
	}

	/**
	 * 取得公钥
	 *
	 * @param keyMap 密钥map
	 * @return byte[] 公钥
	 */
	public static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return key.getEncoded();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 初始化密钥
		// 生成密钥对
		Map<String, Object> keyMap = RSACoder.initKey();
		// 公钥
		byte[] publicKey = RSACoder.getPublicKey(keyMap);

		// 私钥
		byte[] privateKey = RSACoder.getPrivateKey(keyMap);
		System.out.println("公钥：/n" + Base64.encodeBase64String(publicKey));
		System.out.println("私钥：/n" + Base64.encodeBase64String(privateKey));
//
//        System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
//        String str = "RSA密码交换算法";
//        System.out.println("/n===========甲方向乙方发送加密数据==============");
//        System.out.println("原文:" + str);
//        //甲方进行数据的加密
//        byte[] code1 = RSACoder.encryptByPrivateKey(str.getBytes(), privateKey);
//        System.out.println("加密后的数据：" + Base64.encodeBase64String(code1));
//        System.out.println("===========乙方使用甲方提供的公钥对数据进行解密==============");
//        //乙方进行数据的解密
//        byte[] decode1 = RSACoder.decryptByPublicKey(code1, publicKey);
//        System.out.println("乙方解密后的数据：" + new String(decode1) + "/n/n");
//
//        System.out.println("===========反向进行操作，乙方向甲方发送数据==============/n/n");
//
//        str = "乙方向甲方发送数据RSA算法";
//
//        System.out.println("原文:" + str);
//
//        //乙方使用公钥对数据进行加密
//        byte[] code2 = RSACoder.encryptByPublicKey(str.getBytes(), publicKey);
//        System.out.println("===========乙方使用公钥对数据进行加密==============");
//        System.out.println("加密后的数据：" + Base64.encodeBase64String(code2));
//
//        System.out.println("=============乙方将数据传送给甲方======================");
//        System.out.println("===========甲方使用私钥对数据进行解密==============");
//
//        //甲方使用私钥对数据进行解密
//        byte[] decode2 = RSACoder.decryptByPrivateKey(code2, privateKey);
//
//        System.out.println("甲方解密后的数据：" + new String(decode2));

//    	String str = "X9z6MWFERDNhXZJtn16CeKI7RVGSq5wcHKoGt7Dzofq+WzcTn5hsXF9d3gBGLAt2qE/YxiFV3ca4MH0QLzo5UQ==";
//    	String privateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqXg9cInSteK2x5Ja2EQFuA0y/md0U49VrNAHKvUUnWC/VK+J4vYuu7wYq9U+VBM6el3lm5V9bnzQj7zX2ytEyQIDAQABAkAP2prM02ft8hatVui+wKZUUI/Lsvvz8T3Pm+p/v0u9aS7RdZ360hyYeUCFJnpYoIeDfkCzIVyDF/MJ7jr+vCGBAiEA/73pUSbvPh+yHUxM9OqUyJEHMKij5UjmoN70fSymKLECIQCppAjCwh6Y+6oSJ5Sq0niimM2TR0lLdzLNx7HkpEjjmQIgOke2HvdHeBnTBlg4BWxcAaUDRXR4/Sxy2mBUyR3es9ECICUs92aG1+G6tQiJeAD/YsRvLA3sf1l0Y8PI0WlDv11xAiEAnlk0O7wbmaA8o9tsUNJ0uP4SkjJBRABduJbNafH9WzY=";
//    	
//    	byte[] decode2 = RSACoder.decryptByPrivateKey(Base64.decodeBase64(str), Base64.decodeBase64(privateKey));
//    	System.out.println("甲方解密后的数据：" + new String(decode2));
	}

}
