package ar.codeslu.plax.story;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import xute.storyview.StoryModel;
import xute.storyview.StoryPreference;

public class StoryView extends View {
    public static final int START_ANGLE = 270;
    public static int ANGEL_OF_GAP = 15;
    public static final String PENDING_INDICATOR_COLOR = "#009988";
    public static final String VISITED_INDICATOR_COLOR = "#33009988";
    private int mStoryImageRadiusInPx;
    private int mStoryIndicatorWidthInPx;
    private int mSpaceBetweenImageAndIndicator;
    private int mPendingIndicatorColor;
    private int mVisistedIndicatorColor;
    private int mViewWidth;
    private int mViewHeight;
    private int mIndicatoryOffset;
    private int mIndicatorImageOffset;
    private Resources resources;
    private ArrayList<StoryModel> storyImageUris;
    private Paint mIndicatorPaint;
    private int indicatorCount;
    private int indicatorSweepAngle;
    private Bitmap mIndicatorImageBitmap;
    private Rect mIndicatorImageRect;
    private Context mContext;
    StoryPreference storyPreference;
    TypedArray ta;

    public StoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        ta = context.obtainStyledAttributes(attrs, xute.storyview.R.styleable.StoryView, 0, 0);
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            Global.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            mStoryImageRadiusInPx = (int) Math.round(displaymetrics.widthPixels *0.06667);
            mStoryIndicatorWidthInPx = (int) Math.round(displaymetrics.widthPixels *0.006);
            mSpaceBetweenImageAndIndicator = (int) Math.round(displaymetrics.widthPixels *0.006);
            mPendingIndicatorColor = ta.getColor(xute.storyview.R.styleable.StoryView_pendingIndicatorColor, Color.parseColor(PENDING_INDICATOR_COLOR));
            mVisistedIndicatorColor = ta.getColor(xute.storyview.R.styleable.StoryView_visitedIndicatorColor, Color.parseColor(VISITED_INDICATOR_COLOR));
        }
        catch (NullPointerException e)
        {

        }
        finally {
            ta.recycle();
        }
        prepareValues();
    }

    private void init(Context context) {
        this.mContext = context;
        storyPreference = new StoryPreference(context);
        resources = context.getResources();
        storyImageUris = new ArrayList<>();
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.STROKE);
        mIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void prepareValues() {
        mViewHeight = 2 * (mStoryIndicatorWidthInPx + mSpaceBetweenImageAndIndicator + mStoryImageRadiusInPx);
        mViewWidth = mViewHeight;
        mIndicatoryOffset = mStoryIndicatorWidthInPx / 2;
        mIndicatorImageOffset = mStoryIndicatorWidthInPx + mSpaceBetweenImageAndIndicator;
        mIndicatorImageRect = new Rect(mIndicatorImageOffset, mIndicatorImageOffset, mViewWidth - mIndicatorImageOffset, mViewHeight - mIndicatorImageOffset);
    }

    public void resetStoryVisits(){
        storyPreference.clearStoryPreferences();
    }

    public void setImageUris(ArrayList<StoryModel> imageUris,Context conn) {
        this.storyImageUris = imageUris;
        this.indicatorCount = imageUris.size();
        calculateSweepAngle(indicatorCount,conn);
        invalidate();
        loadFirstImageBitamp(conn);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            navigateToStoryPlayerPage();
            return true;
        }
        return true;
    }

    public void navigateToStoryPlayerPage() {
        Intent intent = new Intent(mContext, StoryPlayer.class);
        intent.putParcelableArrayListExtra(StoryPlayer.STORY_IMAGE_KEY,storyImageUris);
        mContext.startActivity(intent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mIndicatorPaint.setColor(mPendingIndicatorColor);
        mIndicatorPaint.setStrokeWidth(mStoryIndicatorWidthInPx);
        int startAngle = START_ANGLE + ANGEL_OF_GAP / 2;
        for (int i = 0; i < indicatorCount; i++) {
            mIndicatorPaint.setColor(getIndicatorColor(i));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawArc(mIndicatoryOffset, mIndicatoryOffset, mViewWidth - mIndicatoryOffset, mViewHeight - mIndicatoryOffset, startAngle, indicatorSweepAngle - ANGEL_OF_GAP / 2, false, mIndicatorPaint);
            }
            startAngle += indicatorSweepAngle + ANGEL_OF_GAP / 2;
        }
        if (mIndicatorImageBitmap != null) {
            canvas.drawBitmap(mIndicatorImageBitmap, null, mIndicatorImageRect, null);
        }
    }

    private int getIndicatorColor(int index) {
        if(storyImageUris.size() >0)
        return storyPreference.isStoryVisited(storyImageUris.get(index).imageUri) ? mVisistedIndicatorColor : mPendingIndicatorColor;
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingStart() + getPaddingEnd() + mViewWidth;
        int height = getPaddingTop() + getPaddingBottom() + mViewHeight;
        int w = resolveSizeAndState(width, widthMeasureSpec, 0);
        int h = resolveSizeAndState(height, heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
    }

    private void loadFirstImageBitamp(Context conn) {
        RequestOptions options = new RequestOptions();
        options.circleCrop();
        if (storyImageUris.size() > 0)
        {
        Glide.with(conn)
                .asBitmap()
                .placeholder(R.drawable.placeholder_grey)
               .apply(options)
                .load(storyImageUris.get(0).imageUri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mIndicatorImageBitmap = resource;
                        invalidate();
                    }
                });
    }
    }

    public void calculateSweepAngle(int itemCounts,Context conn) {
        if (itemCounts == 1) {
            ANGEL_OF_GAP = 0;
        }
        if(itemCounts > 0)
        this.indicatorSweepAngle = (360 / itemCounts) - ANGEL_OF_GAP / 2;
        invalidate();
        loadFirstImageBitamp(conn);


    }

    private int getPxFromDp(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.getDisplayMetrics());
    }
}
