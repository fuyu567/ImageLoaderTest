package com.example.imageloader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

}
