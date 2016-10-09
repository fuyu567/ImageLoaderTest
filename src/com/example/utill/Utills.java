package com.example.utill;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Environment;

public class Utills {
	
	public static File diskCacheDir(Context context,String dirname){
		boolean isMounted=Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED);
		String cachePathString;
		if(isMounted){
			cachePathString=context.getExternalCacheDir().getPath();
		}else{
			cachePathString=context.getCacheDir().getPath();
		}
		return new File(cachePathString+File.separator+dirname);
	}
	
	//url-->md5-->key
	public static String hashKeyFromUrl(String url){
		String cacheKey;
		try {
			MessageDigest mDigest=MessageDigest.getInstance("MD5");
			mDigest.update(url.getBytes());
			cacheKey=bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey=String.valueOf(url.hashCode());
		}
		return cacheKey;
	}
	
	private static String bytesToHexString(byte [] bytes){
		StringBuilder sbBuilder=new StringBuilder();
		for(int i=0;i<bytes.length;i++){
			String hexString=Integer.toHexString(0XFF&bytes[i]);
			if(hexString.length()==1){
				sbBuilder.append('0');
			}
			sbBuilder.append(hexString);
		}
		return sbBuilder.toString();
	}
}
