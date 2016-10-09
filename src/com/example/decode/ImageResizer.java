package com.example.decode;

import java.io.FileDescriptor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class ImageResizer {
	
	public ImageResizer(){
		
	}
	
	
	public Bitmap decodeBitmapFromResources(Resources res,int resId,int reqWidth,int reqHeight){
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize=calculateInSamoleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds=false;
		
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public Bitmap decodeBitmapFromFileDescripter(FileDescriptor descriptor,int reqWidth,int reqHeight){
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFileDescriptor(descriptor, null, options);
		options.inSampleSize=calculateInSamoleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds=false;
		
		return BitmapFactory.decodeFileDescriptor(descriptor, null, options);
	}
	
	public Bitmap decodeBitmapFromBtyes(byte [] bytes,int reqWidth,int reqHeight){
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inSampleSize=calculateInSamoleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds=false;
		
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}
	
	//inSampleSize
	public int calculateInSamoleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
		if(reqHeight==0||reqWidth==0){
			return 1;
		}
		
		int width=options.outWidth;
		int height=options.outHeight;
		
		int inSampleSize=1;
		if(width>reqWidth||height>reqHeight){
			int halfWidth=width/2;
			int halfHeight=height/2;
			while(halfWidth/inSampleSize>=reqWidth&&halfHeight/inSampleSize>=reqHeight){
				inSampleSize *=2;
			}
		}
		
		return inSampleSize;
	}
}
