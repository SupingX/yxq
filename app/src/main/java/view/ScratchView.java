package view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.laputa.yxq.R;

/**
 * 刮刮卡
 * Created by Administrator on 2016/9/6.
 */
public class ScratchView extends View {

    private static final int DEFAULT_MASK_COLOR = Color.YELLOW ;
    private static final int DEFAULT_ERASE_WIDTH = 60;
    private static final int DEFAULT_MAX_PRECENT = 60;
    private Paint maskPaint;
    private Bitmap maskBitmap;
    private Paint erasePaint;
    private int scaledTouchSlop;
    private Canvas maskCanvas;
    private Rect rect;
    private Path erasePath;
    private int maxPrecent = DEFAULT_MAX_PRECENT;
    private int currentPrecent = 0;
    private int pixels[];
    /**
     * 水印
     */
    private BitmapDrawable waterMark ;
    private float mStartX;
    private float mStartY;
    private Paint bitmapPaint;
    private boolean isCompeted = false;

    public ScratchView(Context context) {
        super(context);
        TypedArray typeArray = context.obtainStyledAttributes(R.styleable.ScratchView);
        init(typeArray);
    }

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs,R.styleable.ScratchView);
        init(typeArray);
    }

    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.obtainStyledAttributes(attrs,R.styleable.ScratchView,defStyleAttr,0);
        init(typeArray);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typeArray = context.obtainStyledAttributes(attrs,R.styleable.ScratchView,defStyleAttr,defStyleRes);
        init(typeArray);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureSize(widthMeasureSpec);
        int measureHeight = measureSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth,measureHeight);
    }

    private int measureSize(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            size = specSize;
        } else {
            if(specMode == MeasureSpec.AT_MOST){
                size = Math.min(size,specSize);
            }

        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(maskBitmap,0,0,maskPaint);
        canvas.drawText("Leaf small valoit",100,100,maskPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.e("ScratchView"  ,"--ACTION_DOWN--");
                startErase(event.getX(),event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("ScratchView"  ,"--ACTION_MOVE--");
                erase(event.getX(),event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                Log.e("ScratchView"  ,"--ACTION_UP--");
                stopErase();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }



    /**
     * 开始擦除
     * @param x x
     * @param y y
     */
    private void startErase(float x, float y) {
        erasePath.reset();
        erasePath.moveTo(x,y);
        this.mStartX = x;
        this.mStartY = y;
    }
    private void stopErase() {
        mStartX = 0;
        mStartY = 0;
        erasePath.reset();
    }

    /**
     * 擦除
     * @param x x
     * @param y y
     */
    private void erase(float x, float y) {

        int dx = (int) Math.abs(x - mStartX);
        int dy = (int) Math.abs(y-mStartY);

        if (dx >= scaledTouchSlop || dy >= scaledTouchSlop){
            Log.e("ScratchView"  ,"--擦除--");
            this.mStartX = x;
            this.mStartY = y;
            erasePath.lineTo(this.mStartX,this.mStartY);
            maskCanvas.drawPath(erasePath,erasePaint);

            onEraseNew();

            erasePath.reset();
            erasePath.moveTo(mStartX,mStartY);
        }
    }



    private void init(TypedArray typedArray ) {
        setClickable(true);
        int taskColor = DEFAULT_MASK_COLOR;
        float eraseWidth = DEFAULT_ERASE_WIDTH;
        int waterMarkResId = 0;


        if (typedArray != null) {
            maxPrecent = typedArray.getInt(R.styleable.ScratchView_maxPrecent,DEFAULT_MAX_PRECENT);
            taskColor = typedArray.getColor(R.styleable.ScratchView_maskColor, DEFAULT_MASK_COLOR);
            eraseWidth = typedArray.getDimension(R.styleable.ScratchView_eraseWidth, DEFAULT_ERASE_WIDTH);
            waterMarkResId = typedArray.getResourceId(R.styleable.ScratchView_waterMark,-1);
            typedArray.recycle();
        }
        maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setDither(true);
        setMaskColor(taskColor);

        erasePaint = new Paint();
        erasePaint.setAntiAlias(true);
        erasePaint.setDither(true);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));// 设置擦除效果
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setStrokeCap(Paint.Cap.ROUND);
        setEraseWidth(eraseWidth);

        bitmapPaint =  new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        setWaterMark(waterMarkResId);

        ViewConfiguration viewConfiguration = android.view.ViewConfiguration.get(getContext());
        scaledTouchSlop = viewConfiguration.getScaledTouchSlop();

        erasePath = new Path();
    }





    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMask(w,h);
    }

    public void createMask(int w, int h) {
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        rect = new Rect(0,0,w,h);
        //  这一步是做什么的?  新建一个图片,在上面画东西? 然后再在canvas里画这张图片!?
        maskCanvas.drawRect(rect,maskPaint);


        if (waterMark != null){
            Rect waterRect = new Rect(rect);
            waterMark.setBounds(waterRect);
            waterMark.draw(maskCanvas);
        }

        pixels = new int[w * h];
    }

    public void reset(){

        int width = getWidth();
        int height = getHeight();
        createMask(width,height);
        currentPrecent = 0;
        isCompeted = false;
        onEraseNew();
        invalidate();

    }

    public void clear(){
        int width = getWidth();
        int height = getHeight();
        maskBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        Rect rect = new Rect(0,0,width,height);
        maskCanvas.drawRect(rect,erasePaint);
        currentPrecent = 0;
        onEraseNew();
        invalidate();




    }

    private void  onEraseNew(){
        int width = getWidth();
        int height = getHeight();

        maskBitmap.getPixels(pixels,0,width,0,0,width,height);
        float erasePixelCount = 0; //擦除的像素个数
        float totalPixelCount = width * height;

        for (int pos = 0;pos<totalPixelCount;pos++ ) {
            if (pixels[pos] == 0){ //像素值为0
                erasePixelCount ++ ;
            }
        }
        if(erasePixelCount >= 0 && totalPixelCount > 0){
            currentPrecent = Math.round(erasePixelCount * 100 / totalPixelCount);
            Log.e("ScratchView","--> percent ; " + currentPrecent);
            Log.e("ScratchView","--> maxPrecent ; " + maxPrecent);
            onPrecentUpdate();
            if(currentPrecent >= maxPrecent){
                onPrecentCompleted();
            }
        }
    }

    /*private void onErase() {
        int width = getWidth();
        int height = getHeight();
        new AsyncTask<Integer,Integer,Boolean>(){

            @Override
            protected Boolean doInBackground(Integer... params) {
                int width = params[0];
                int height = params[1];
                maskBitmap.getPixels(pixels,0,width,0,0,width,height);
                float erasePixelCount = 0; //擦除的像素个数
                float totalPixelCount = width * height;

//                if (!isCompeted){
                    for (int pos = 0;pos<totalPixelCount;pos++ ) {
                        if (pixels[pos] == 0){ //像素值为0
                            erasePixelCount ++ ;
                        }
                    }
//                }

                int percent = 0;
                if(erasePixelCount >= 0 && totalPixelCount > 0){
                    percent = Math.round(erasePixelCount * 100 / totalPixelCount);
                    publishProgress(percent);
                }

                Log.e("ScratchView","--> percent ; " + percent);
                Log.e("ScratchView","--> maxPrecent ; " + maxPrecent);
                return percent >= maxPrecent;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (aBoolean && !isCompeted){
                    isCompeted = true;
                    if(mOnEraseListener !=null){
                        mOnEraseListener.onCompleted(ScratchView.this);
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                currentPrecent = values[0];
                onPrecentUpdate();
            }
        }.execute(width,height);

    }*/

    private void onPrecentUpdate() {
        if (mOnEraseListener!= null && !isCompeted ){
            mOnEraseListener.onProgress(currentPrecent);
        }
    }

    private void onPrecentCompleted() {
        if (mOnEraseListener!= null && !isCompeted ){
            mOnEraseListener.onCompleted(this);
        }
    }


    /**
     * 设置最大的擦除比例
     *
     * @param maxPrecent 百分比 [0,100]
     */
    public void setMaxPrecent(int maxPrecent){
        if (maxPrecent > 100 || maxPrecent <0 ){
            return ;
        }
        this.maxPrecent = maxPrecent;
    }



    /**
     * 设置蒙版颜色
     * @param maskColor 颜色
     */
    public void setMaskColor(int maskColor){
        this.maskPaint.setColor(maskColor);

//        if(rect != null && maskCanvas !=null){
//            maskCanvas.drawRect(rect,maskPaint);
//        }
    }

    /**
     * 设置橡皮擦大小
     * @param eraseWidth 大小
     */
    public void setEraseWidth(float eraseWidth) {
        this.erasePaint.setStrokeWidth(eraseWidth);
    }


    /**
     * 设置水印图标
     *
     * @param resId  图标资源id，-1表示去除水印
     */
    public void setWaterMark(int resId){
        if (resId == -1){
            waterMark = null;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resId);
            waterMark = new BitmapDrawable(bitmap);
            waterMark.setTileModeXY(Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
        }
    }


    private  OnEraseListener mOnEraseListener;
    public void setOnEraseListener(OnEraseListener mOnEraseListener){
        this.mOnEraseListener = mOnEraseListener;
    }

    public static interface OnEraseListener {
        /**
         * 擦除进度
         * @param precent [0,100]
         */
        public void onProgress(int precent);

        /**
         * 擦除完成
         * @param view view
         */
        public void onCompleted(View view);
    }
}
