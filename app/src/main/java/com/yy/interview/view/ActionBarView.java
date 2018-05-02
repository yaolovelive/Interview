package com.yy.interview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.interview.R;


public class ActionBarView extends RelativeLayout {
    private Button btnLeft,btnRight;
    private TextView tvTitle;

    private int colorLeft;
    private Drawable backgroundLeft;
    private String textLeft;
    private float widthLeft,heightLeft;

    private int colorRight;
    private Drawable backgroundRight;
    private String textRight;
    private float widthRight,heightRight;

    private String textTitle;
    private float sizeTitle;
    private int colorTitle;

    private LayoutParams rightParams,leftParams,titleParams;

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ActionBarView);

        colorLeft = ta.getColor(R.styleable.ActionBarView_leftTextColor,0);
        backgroundLeft = ta.getDrawable(R.styleable.ActionBarView_leftBackground);
        textLeft = ta.getString(R.styleable.ActionBarView_leftText);
        widthLeft = ta.getDimension(R.styleable.ActionBarView_leftWidth,LayoutParams.WRAP_CONTENT);
        heightLeft = ta.getDimension(R.styleable.ActionBarView_leftHeight,LayoutParams.WRAP_CONTENT);
        
        colorRight = ta.getColor(R.styleable.ActionBarView_rightTextColor,0);
        backgroundRight = ta.getDrawable(R.styleable.ActionBarView_rightBackground);
        textRight = ta.getString(R.styleable.ActionBarView_rightText);
        widthRight = ta.getDimension(R.styleable.ActionBarView_rightWidth,LayoutParams.WRAP_CONTENT);
        heightRight = ta.getDimension(R.styleable.ActionBarView_rightHeight,LayoutParams.WRAP_CONTENT);

        textTitle = ta.getString(R.styleable.ActionBarView_titleText);
        sizeTitle = ta.getDimension(R.styleable.ActionBarView_titleTextSize,0);
        colorTitle = ta.getColor(R.styleable.ActionBarView_titleTextColor,0);

        ta.recycle();
        btnLeft = new Button(context);
        btnRight = new Button(context);
        tvTitle = new TextView(context);
        
        btnLeft.setText(textLeft);
        btnLeft.setTextColor(colorLeft);
        btnLeft.setBackground(backgroundLeft);

        btnRight.setText(textRight);
        btnRight.setTextColor(colorRight);
        btnRight.setBackground(backgroundRight);
//        btnRight.setGravity(Gravity.RIGHT);

        tvTitle.setText(textTitle);
        tvTitle.setTextSize(sizeTitle);
        tvTitle.setTextColor(colorTitle);
        tvTitle.setGravity(Gravity.CENTER);

        leftParams = new LayoutParams((int)widthLeft,(int)heightLeft);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
        leftParams.addRule(RelativeLayout.CENTER_VERTICAL,TRUE);
        addView(btnLeft,leftParams);

        rightParams = new LayoutParams((int)widthRight,(int)heightRight);
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL,TRUE);
        addView(btnRight,rightParams);

        titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(tvTitle,titleParams);
    }

    public void setBtnLeftOnClickListener(OnClickListener onClickListener){
        btnLeft.setOnClickListener(onClickListener);
    }

    public void setBtnRightOnClickListener(OnClickListener onClickListener){
        btnRight.setOnClickListener(onClickListener);
    }

}
