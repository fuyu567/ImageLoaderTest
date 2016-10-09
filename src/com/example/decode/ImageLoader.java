package com.example.decode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.dislrucache.DiskLruCache;
import com.example.mylocalimageloader.R;
import com.example.utill.Utills;

public class ImageLoader {
	
	private LruCache<String, Bitmap> mMemoryCache;
	private Context mContext;
	private DiskLruCache mDiskLruCache;
	private AtomicBoolean atomicBoolean=new AtomicBoolean(false);
	private ImageResizer imageResizer=new ImageResizer();
	
	private static final long DISK_CACHE_SIZE=1024*1024*50;
	private static final int DISK_CACHE_INDEX=0;
	private static final int BUFFER_SIZE=8*1024;
	
	private static final int MESSAGE_POST_BITMAP=0;
	
	private static final int TAG_KEY_URI=R.id.image;
	private static final int CPU_COUNT=Runtime.getRuntime().availableProcessors();
	private static final int COREPOOLSIZE=CPU_COUNT+1;
	private static final int MAXIMUMPOOLSIZE=2*CPU_COUNT+1;
	private static final long KEEPALIVETIME=10L;
	private static final ThreadFactory sThreadFactory=new ThreadFactory() {
		private final AtomicInteger mCount=new AtomicInteger(1);
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ImageLoader#"+mCount.getAndIncrement());
		}
	};
	
	public static final Executor THREAD_POOL_EXECUTOR=new ThreadPoolExecutor(COREPOOLSIZE, MAXIMUMPOOLSIZE, KEEPALIVETIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);
	
	public ImageLoader(Context context){	
		mContext=context.getApplicationContext();
		int maxMemory=(int)Runtime.getRuntime().maxMemory()/1024;
		int cacheSize=maxMemory/8;
		mMemoryCache=new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes()*value.getHeight()/1024;
			}
			
			
			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
				//recycle and so on
			}
		};
		
		File diskCacheDir=Utills.diskCacheDir(mContext, "bitmap");
		if(!diskCacheDir.exists()){
			diskCacheDir.mkdirs();
		}
		
		if(getUsableSpace(diskCacheDir)<DISK_CACHE_SIZE){
			try {
				mDiskLruCache=DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE,100);
				atomicBoolean.set(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private long getUsableSpace(File file){
		if(Build.VERSION.SDK_INT>=VERSION_CODES.GINGERBREAD){
			return file.getUsableSpace();
		}
		StatFs statFs=new StatFs(file.getPath());
		return (long)statFs.getBlockSize()*(long)statFs.getAvailableBlocks();
	}
	
	public void addBitmapToMemoryCache(String key,Bitmap bitmap){
		if(mMemoryCache.get(key)==null){
			mMemoryCache.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromMemoryCache(String key){
		return mMemoryCache.get(key);
	}
	
	private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws Exception{
		
		if(Looper.myLooper()==Looper.getMainLooper()){
			throw new Exception("can not visit network from UI thread");
		}
		
		if(mDiskLruCache==null){
			return null;
		}
		
		String key=Utills.hashKeyFromUrl(url);
		DiskLruCache.Editor editor=mDiskLruCache.edit(key);
		if(editor!=null){
			OutputStream outputStream=editor.newOutputStream(DISK_CACHE_INDEX);
			if(downloadUrlToStream(url, outputStream)){
				editor.commit();
			}else{
				editor.abort();
			}
			mDiskLruCache.flush();
		}
		
		return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
	}
	
	private boolean downloadUrlToStream(String urlString,OutputStream outputStream){
		Log.i("fuyu", "downloadUrlToStream");
		HttpURLConnection httpURLConnection = null;
		BufferedOutputStream out=null;;
		BufferedInputStream in=null;
		try {
			URL url=new URL(urlString);
			httpURLConnection=(HttpURLConnection)url.openConnection();
			httpURLConnection.connect();
			httpURLConnection.setReadTimeout(4*1000);
			in=new BufferedInputStream(httpURLConnection.getInputStream(), BUFFER_SIZE);
			out=new BufferedOutputStream(outputStream, BUFFER_SIZE);
			
			int b=-1;
			while((b=in.read())!=-1){
				out.write(b);
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
			try {
				if (out != null && in != null) {
					out.close();
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	private Bitmap loadBitmapFromDiskCache(String url,int reqWidth,int reqHeight) throws Exception{
		
		if(Looper.myLooper()==Looper.getMainLooper()){
			throw new Exception("can not visit network from UI thread");
		}
		
		if(mDiskLruCache==null){
			return null;
		}
		
		Bitmap bitmap=null;
		String key=Utills.hashKeyFromUrl(url);
		DiskLruCache.Snapshot snapshot=mDiskLruCache.get(key);
		if(snapshot!=null){
			FileInputStream fileInputStream=(FileInputStream)snapshot.getInputStream(DISK_CACHE_INDEX);
			FileDescriptor fileDescriptor=fileInputStream.getFD();
			bitmap=imageResizer.decodeBitmapFromFileDescripter(fileDescriptor, reqWidth, reqHeight);
			if(bitmap!=null){
				addBitmapToMemoryCache(key, bitmap);
			}
		}
		
		return bitmap;
	}
	
	private byte [] changeToBytes(InputStream in){
		 ByteArrayOutputStream output = new ByteArrayOutputStream();
		    byte[] buffer = new byte[4*1024];
		    int n = 0;
		    try {
				while (-1 != (n = in.read(buffer))) {
				    output.write(buffer, 0, n);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		    return output.toByteArray();
	}

	private Bitmap downloadBitmapFromUrl(String urlString,int reqWidth,int reqHeight){
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream=null;
		try {
			URL url=new URL(urlString);
			String key=Utills.hashKeyFromUrl(urlString);
			httpURLConnection=(HttpURLConnection)url.openConnection();
			inputStream=httpURLConnection.getInputStream();
			byte [] bytes=changeToBytes(inputStream);
			Bitmap bitmap=imageResizer.decodeBitmapFromBtyes(bytes, reqWidth, reqHeight);
			if(bitmap!=null){
				addBitmapToMemoryCache(key, bitmap);
			}
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
			try {
				if (inputStream!= null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	} 
	
	//synchronized loadbitmap
	public Bitmap loadBitmap(String url,int reqWidth,int reqHeight){
		String key=Utills.hashKeyFromUrl(url);
		Bitmap bitmap=getBitmapFromMemoryCache(key);
		if(bitmap!=null){
			return bitmap;
		}
		try {
			bitmap=loadBitmapFromDiskCache(url, reqWidth, reqHeight);
			if(bitmap!=null){
				return bitmap;
			}
			bitmap=loadBitmapFromHttp(url, reqWidth, reqHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(bitmap==null&&!atomicBoolean.get()){
			bitmap=downloadBitmapFromUrl(url, reqWidth, reqHeight);
		}
		
		return bitmap;
	}
	
	@SuppressLint("HandlerLeak") private android.os.Handler mHandler=new android.os.Handler(Looper.getMainLooper()){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LoaderResult loaderResult=(LoaderResult)msg.obj;
			ImageView imageView=loaderResult.imageView;
			String uri=(String)imageView.getTag(TAG_KEY_URI);
			if(uri.equals(loaderResult.uri)){
				imageView.setImageBitmap(loaderResult.bitmap);
			}else{
				
			}
		}
		
	};
	
	//not synchronized loadbitmap
	public void bindBitmap(final String uri,final ImageView imageView,final int reqWidth,final int reqHeight){
		Bitmap bitmap=getBitmapFromMemoryCache(uri);
		if(bitmap!=null){
			imageView.setImageBitmap(bitmap);
			return;
		}
		imageView.setTag(TAG_KEY_URI, uri);
		Runnable loadBitmapTask=new Runnable() {
			
			@Override
			public void run() {
				Bitmap bitmap=loadBitmap(uri, reqWidth, reqHeight); 
				if(bitmap!=null){
					LoaderResult loaderResult=new LoaderResult(imageView, uri, bitmap);
					mHandler.obtainMessage(MESSAGE_POST_BITMAP, loaderResult).sendToTarget();
				}
			}
		};
		THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
	}
	
	private static class LoaderResult{
		public ImageView imageView;
		public String uri;
		public Bitmap bitmap;
		
		public LoaderResult(ImageView imageView,String uri,Bitmap bitmap){
			this.imageView=imageView;
			this.uri=uri;
			this.bitmap=bitmap;
		}
	}
	
	
}
