package com.example.imageloader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.decode.ImageLoader;
import com.example.mylocalimageloader.R;

public class MainActivity extends Activity {
	GridView gridView=null;
	List<String> uriList=new ArrayList<String>();
	private boolean isGridViewIdle=true;
	private MyAdapter mAdapter=null;
	
	private static final int reqWidth=150;
	private static final int reqHeight=150;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView=(GridView)findViewById(R.id.gridview);
        mAdapter=new MyAdapter(this);
        gridView.setAdapter(mAdapter);
        gridView.setOnScrollListener(new MyScrollListever());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	uriList.add("https://www.baidu.com/img/bd_logo1.png");
    	uriList.add("http://h.hiphotos.baidu.com/image/h%3D360/sign=4882823172c6a7efa626ae20cdfbafe9/f9dcd100baa1cd11dd1855cebd12c8fcc2ce2db5.jpg");
    	uriList.add("http://g.hiphotos.baidu.com/image/h%3D360/sign=e35617c82c34349b6b066883f9eb1521/91ef76c6a7efce1b5b6d3e18ab51f3deb58f659a.jpg");
    	uriList.add("http://a.hiphotos.baidu.com/image/h%3D360/sign=c429bad21dd8bc3ed90800ccb28aa6c8/e7cd7b899e510fb3a78c787fdd33c895d0430c44.jpg");
    	uriList.add("http://e.hiphotos.baidu.com/image/h%3D360/sign=ea96ce4c0e7b020813c939e752d8f25f/14ce36d3d539b600be63e95eed50352ac75cb7ae.jpg");
    	uriList.add("http://h.hiphotos.baidu.com/image/h%3D360/sign=2c82459da31ea8d395227202a70b30cf/43a7d933c895d143b233160576f082025aaf074a.jpg");
    	uriList.add("http://g.hiphotos.baidu.com/image/h%3D360/sign=c794ca82d443ad4bb92e40c6b2035a89/03087bf40ad162d9ec74553b14dfa9ec8a13cd7a.jpg");
    	uriList.add("http://c.hiphotos.baidu.com/image/h%3D360/sign=e802d34e48fbfbedc359307948f1f78e/a8ec8a13632762d045aee6cea3ec08fa513dc62b.jpg");
    	uriList.add("http://e.hiphotos.baidu.com/image/h%3D360/sign=d55e5f39237f9e2f6f351b0e2f32e962/500fd9f9d72a605951f80cc52c34349b023bba01.jpg");
    	uriList.add("http://b.hiphotos.baidu.com/image/h%3D360/sign=09502c35fef2b211fb2e8348fa816511/bd315c6034a85edf94d9b4654c540923dd54751a.jpg");
    	uriList.add("http://b.hiphotos.baidu.com/image/h%3D360/sign=e934d804249759ee555066cd82fa434e/0dd7912397dda1444cfad0bbb0b7d0a20df486cc.jpg");
    	uriList.add("http://f.hiphotos.baidu.com/image/h%3D360/sign=6b72caba79d98d1069d40a37113eb807/838ba61ea8d3fd1f049bfe7c354e251f94ca5fe1.jpg");
    	uriList.add("http://f.hiphotos.baidu.com/image/h%3D300/sign=e50211178e18367ab28979dd1e738b68/0b46f21fbe096b63a377826e04338744ebf8aca6.jpg");
    	uriList.add("http://img4.imgtn.bdimg.com/it/u=819265564,3078214620&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=3297688982,2096967770&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=1260676024,5416250&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=3297688982,2096967770&fm=23&gp=0.jpg");
    	uriList.add("http://img3.imgtn.bdimg.com/it/u=3260071971,3749233702&fm=23&gp=0.jpg");
    	uriList.add("http://img3.imgtn.bdimg.com/it/u=2536762479,935801142&fm=23&gp=0.jpg");
    	uriList.add("http://img5.imgtn.bdimg.com/it/u=3083122628,431784069&fm=23&gp=0.jpg");
    	uriList.add("http://img2.imgtn.bdimg.com/it/u=1551561668,2650343682&fm=23&gp=0.jpg");
    	uriList.add("http://img1.imgtn.bdimg.com/it/u=2777499,431372422&fm=23&gp=0.jpg");
    	uriList.add("http://img1.imgtn.bdimg.com/it/u=724829809,3930900112&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=3786130538,3768617096&fm=23&gp=0.jpg");
    	uriList.add("http://img5.imgtn.bdimg.com/it/u=4022887281,347248236&fm=23&gp=0.jpg");
    	uriList.add("http://img5.imgtn.bdimg.com/it/u=3169375365,459810872&fm=23&gp=0.jpg");
    	uriList.add("http://img4.imgtn.bdimg.com/it/u=1361483400,3236713520&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=3361423803,1933817347&fm=23&gp=0.jpg");
    	uriList.add("http://img5.imgtn.bdimg.com/it/u=2332399485,4141364550&fm=23&gp=0.jpg");
    	uriList.add("http://img0.imgtn.bdimg.com/it/u=1486967430,3285700666&fm=23&gp=0.jpg");
    	uriList.add("http://img2.imgtn.bdimg.com/it/u=1940860739,2136633435&fm=23&gp=0.jpg");
    	//ContentObserver contentObserver=new C
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mAdapter.notifyDataSetChanged();
    }
    
    private class MyAdapter extends BaseAdapter{
    	private ImageLoader imageLoader=null;
    	
    	public MyAdapter(Context context){
    		imageLoader=new ImageLoader(context);
    	}
		@Override
		public int getCount() {
			Log.i("fuyu",uriList.size()+"");
			return uriList.size();
		}

		@Override
		public Object getItem(int position) {
			return uriList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder=null;
			if(null==convertView){
				convertView=LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false);
				viewHolder=new ViewHolder();
				viewHolder.imageView=(ImageView)convertView.findViewById(R.id.image);
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			ImageView imageView=viewHolder.imageView;
			String tag=(String)imageView.getTag();
			String url=(String) getItem(position);
			if(!url.equals(tag)){
				imageView.setImageResource(R.drawable.ic_launcher);
			}
			if(isGridViewIdle){
				imageView.setTag(url);
				imageLoader.bindBitmap(url, imageView, reqWidth, reqHeight);
			}
			return convertView;
		}
		
		private final class ViewHolder{
	    	
	    	public ImageView imageView;
	    	
	    }
    	
    }
   
    
    
    
    private class MyScrollListever implements OnScrollListener{

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
				isGridViewIdle=true;
				mAdapter.notifyDataSetChanged();
			}else{
				isGridViewIdle=false;
			}
		}
    	
    }
}
