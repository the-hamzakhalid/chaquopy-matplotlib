
package com.chaquo.myapplication.utils.view.paintview;

import static com.chaquo.myapplication.utils.Context_bitmap_extKt.bitampToByteArray;
import static com.chaquo.myapplication.utils.Context_bitmap_extKt.duplicateBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chaquo.myapplication.utils.view.paintview.listener.PaintViewCallBack;
import com.chaquo.myapplication.utils.view.paintview.listener.QuickRemoveListener;
import com.chaquo.myapplication.utils.view.paintview.listener.Shapable;
import com.chaquo.myapplication.utils.view.paintview.listener.ShapesInterface;
import com.chaquo.myapplication.utils.view.paintview.listener.ToolInterface;
import com.chaquo.myapplication.utils.view.paintview.listener.UndoCommand;
import com.chaquo.myapplication.utils.view.paintview.painttools.Eraser;
import com.chaquo.myapplication.utils.view.paintview.painttools.PlainPen;
import com.chaquo.myapplication.utils.view.paintview.shapes.Curv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class PaintView extends View implements UndoCommand {

    boolean canvasIsCreated = false;

    private Canvas mCanvas = null;

    private ToolInterface mCurrentPainter = null;

    QuickRemoveListener quickRemoveListener;

    private Bitmap mBitmap = null;

    private Bitmap mOrgBitMap = null;

    private int mBitmapWidth = 0;

    private int mBitmapHeight = 0;

    private int mBackGroundColor = PaintConstants.DEFAULT.BACKGROUND_COLOR;

    private Paint mBitmapPaint = null;

    private paintPadUndoStack mUndoStack = null;

    private int mPenColor = PaintConstants.DEFAULT.PEN_COLOR;

    private int mPenSize = PaintConstants.PEN_SIZE.SIZE_4;

    private int mEraserSize = PaintConstants.ERASER_SIZE.SIZE_1;

    int mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;

    private PaintViewCallBack mCallBack = null;

    private int mCurrentShapeType = 0;

    private ShapesInterface mCurrentShape = null;

    private Style mStyle = Style.STROKE;

    private boolean isTouchUp = false;

    private int mStackedSize = 50;

    ToolInterface tool = null;


    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        mCanvas = new Canvas();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mUndoStack = new paintPadUndoStack(this, mStackedSize);

        mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;

        mCurrentShapeType = PaintConstants.SHAP.CURV;
        creatNewPen();
    }

    public void setCallBack(PaintViewCallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        isTouchUp = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCanvas.setBitmap(mBitmap);
                creatNewPen();
                mCurrentPainter.touchDown(x, y);
                mUndoStack.clearRedo();
                mCallBack.onTouchDown();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPainter.touchMove(x, y);
                if (mPaintType == PaintConstants.PEN_TYPE.ERASER) {
                    mCurrentPainter.draw(mCanvas);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentPainter.hasDraw()) {
                    mUndoStack.push(mCurrentPainter);
                    if (mCallBack != null) {

                        mCallBack.onHasDraw();
                    }
                }
                mCurrentPainter.touchUp(x, y);

                mCurrentPainter.draw(mCanvas);
                invalidate();
                isTouchUp = true;
                if (MyConstants.DoingQuickRemove) {
                    quickRemoveListener.removeQuick();
                }
                break;
        }
        return true;
    }


    private void setShape() {
        if (mCurrentPainter instanceof Shapable) {
            switch (mCurrentShapeType) {
                case PaintConstants.SHAP.CURV:
                    mCurrentShape = new Curv((Shapable) mCurrentPainter);
                    break;
                default:
                    break;
            }
            ((Shapable) mCurrentPainter).setShap(mCurrentShape);
        }
    }

    @Override
    public void onDraw(Canvas cv) {
        cv.drawColor(mBackGroundColor);

        cv.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        if (!isTouchUp) {
            if (mPaintType != PaintConstants.PEN_TYPE.ERASER) {
                mCurrentPainter.draw(cv);
            }
        }
    }

    void creatNewPen() {
        switch (mPaintType) {
            case PaintConstants.PEN_TYPE.PLAIN_PEN:
                tool = new PlainPen(mPenSize, mPenColor, mStyle);
                break;
            case PaintConstants.PEN_TYPE.ERASER:
                tool = new Eraser(mEraserSize);
                break;
            default:
                break;
        }
        mCurrentPainter = tool;
        setShape();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!canvasIsCreated) {
            mBitmapWidth = w;
            mBitmapHeight = h;
            creatCanvasBitmap(w, h);
            canvasIsCreated = true;
        }
    }


    public void setForeBitMap(Bitmap bitmap) {
        if (bitmap != null) {
            recycleMBitmap();
            recycleOrgBitmap();
        }
        mBitmap = duplicateBitmap(bitmap, getWidth(), getHeight());
        mOrgBitMap = duplicateBitmap(mBitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        invalidate();
    }


    private void recycleOrgBitmap() {
        if (mOrgBitMap != null && !mOrgBitMap.isRecycled()) {
            mOrgBitMap.recycle();
            mOrgBitMap = null;
        }
    }

    private void recycleMBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }


    public Bitmap getSnapShoot() {

        setDrawingCacheEnabled(true);
        buildDrawingCache(true);
        Bitmap bitmap = getDrawingCache(true);
        Bitmap bmp = duplicateBitmap(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        setDrawingCacheEnabled(false);
        return bmp;
    }

    public void clearAll(boolean clearBackground) {
        if (clearBackground) {
            recycleMBitmap();
            recycleOrgBitmap();
            creatCanvasBitmap(mBitmapWidth, mBitmapHeight);
        } else {
            if (mOrgBitMap != null) {
                mBitmap = duplicateBitmap(mOrgBitMap);
                mCanvas.setBitmap(mBitmap);
            } else {
                creatCanvasBitmap(mBitmapWidth, mBitmapHeight);
            }
        }
        mUndoStack.clearAll();
        invalidate();
    }

    private void creatCanvasBitmap(int w, int h) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
    }


    public void setCurrentPainterType(int type) {
        switch (type) {
            case PaintConstants.PEN_TYPE.BLUR:
            case PaintConstants.PEN_TYPE.PLAIN_PEN:
            case PaintConstants.PEN_TYPE.EMBOSS:
            case PaintConstants.PEN_TYPE.ERASER:
                mPaintType = type;
                break;
            default:
                mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;
                break;
        }
    }

    public void setCurrentShapType(int type) {
        switch (type) {
            case PaintConstants.SHAP.CURV:
            case PaintConstants.SHAP.LINE:
            case PaintConstants.SHAP.RECT:
            case PaintConstants.SHAP.CIRCLE:
            case PaintConstants.SHAP.OVAL:
            case PaintConstants.SHAP.SQUARE:
            case PaintConstants.SHAP.STAR:
                mCurrentShapeType = type;
                break;
            default:
                mCurrentShapeType = PaintConstants.SHAP.CURV;
                break;
        }
    }


    public int getCurrentPainter() {
        return mPaintType;
    }


    public void setPenSize(int size) {
        mPenSize = size;
    }


    public void setEraserSize(int size) {
        mEraserSize = size;
    }


    public int getPenSize() {
        return mPenSize;
    }

    public void resetState() {
        setCurrentPainterType(PaintConstants.PEN_TYPE.PLAIN_PEN);
        setPenColor(PaintConstants.DEFAULT.PEN_COLOR);
        setBackGroundColor(PaintConstants.DEFAULT.BACKGROUND_COLOR);
        mUndoStack.clearAll();
    }

    public void setBackGroundColor(int color) {
        mBackGroundColor = color;
        invalidate();
    }

    public int getBackGroundColor() {
        return mBackGroundColor;
    }

    public void setPenColor(int color) {
        mPenColor = color;
    }

    public int getPenColor() {
        return mPenColor;
    }


    protected void setTempForeBitmap(Bitmap tempForeBitmap) {
        if (null != tempForeBitmap) {
            recycleMBitmap();
            mBitmap = duplicateBitmap(tempForeBitmap);
            if (null != mBitmap && null != mCanvas) {
                mCanvas.setBitmap(mBitmap);
                invalidate();
            }
        }
    }

    public void setPenStyle(Style style) {
        mStyle = style;
    }

    public byte[] getBitmapArry() {
        return bitampToByteArray(mBitmap);
    }

    @Override
    public void undo() {
        if (null != mUndoStack) {
            mUndoStack.undo();
        }
    }

    @Override
    public void redo() {
        if (null != mUndoStack) {
            mUndoStack.redo();
        }
    }

    @Override
    public boolean canUndo() {
        return mUndoStack.canUndo();
    }

    @Override
    public boolean canRedo() {
        return mUndoStack.canRedo();
    }

    @Override
    public String toString() {
        return "mPaint" + mCurrentPainter + mUndoStack;
    }

    public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }


    public class paintPadUndoStack {
        private int m_stackSize = 0;

        private PaintView mPaintView = null;

        private ArrayList< ToolInterface > mUndoStack = new ArrayList< ToolInterface >();

        private ArrayList< ToolInterface > mRedoStack = new ArrayList< ToolInterface >();

        private ArrayList< ToolInterface > mOldActionStack = new ArrayList< ToolInterface >();

        public paintPadUndoStack(PaintView paintView, int stackSize) {
            mPaintView = paintView;
            m_stackSize = stackSize;
        }


        public void push(ToolInterface penTool) {
            if (null != penTool) {

                if (mUndoStack.size() == m_stackSize && m_stackSize > 0) {
                    ToolInterface removedTool = mUndoStack.get(0);
                    mOldActionStack.add(removedTool);
                    mUndoStack.remove(0);
                }

                mUndoStack.add(penTool);
            }
        }

        public void clearAll() {
            mRedoStack.clear();
            mUndoStack.clear();
            mOldActionStack.clear();
        }

        public void undo() {
            if (canUndo() && null != mPaintView) {
                ToolInterface removedTool = mUndoStack.get(mUndoStack.size() - 1);
                mRedoStack.add(removedTool);
                mUndoStack.remove(mUndoStack.size() - 1);

                if (null != mOrgBitMap) {

                    mPaintView.setTempForeBitmap(mPaintView.mOrgBitMap);
                } else {
                    mPaintView.creatCanvasBitmap(mPaintView.mBitmapWidth, mPaintView.mBitmapHeight);
                }

                Canvas canvas = mPaintView.mCanvas;

                // First draw the removed tools from undo stack.
                for (ToolInterface paintTool : mOldActionStack) {
                    paintTool.draw(canvas);
                }

                for (ToolInterface paintTool : mUndoStack) {
                    paintTool.draw(canvas);
                }

                mPaintView.invalidate();
            }
        }

        public void redo() {
            if (canRedo() && null != mPaintView) {
                ToolInterface removedTool = mRedoStack.get(mRedoStack.size() - 1);
                mUndoStack.add(removedTool);
                mRedoStack.remove(mRedoStack.size() - 1);

                if (null != mOrgBitMap) {
                    mPaintView.setTempForeBitmap(mPaintView.mOrgBitMap);
                } else {
                    mPaintView.creatCanvasBitmap(mPaintView.mBitmapWidth, mPaintView.mBitmapHeight);
                }

                Canvas canvas = mPaintView.mCanvas;
                for (ToolInterface sketchPadTool : mOldActionStack) {
                    sketchPadTool.draw(canvas);
                }
                for (ToolInterface sketchPadTool : mUndoStack) {
                    sketchPadTool.draw(canvas);
                }

                mPaintView.invalidate();
            }
        }

        public boolean canUndo() {
            return (mUndoStack.size() > 0);
        }

        public boolean canRedo() {
            return (mRedoStack.size() > 0);
        }

        public void clearRedo() {
            mRedoStack.clear();
        }

        @Override
        public String toString() {
            return "canUndo" + canUndo();
        }
    }

    @SuppressLint("WrongThread")
    public Bitmap saveSignature() {

        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        Paint rect_paint = new Paint();
//        rect_paint.setStyle(Paint.Style.FILL);
//        rect_paint.setColor(Color.rgb(0, 0, 0));
//        canvas.drawRect(0, 0, width, height, rect_paint); // that's painting the whole canvas in the chosen color.
        this.draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory() + "/sign.png");

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void setBrushSize(int brushSize) {
        mPenSize = brushSize;
        mEraserSize = brushSize;
        creatNewPen();
    }

    public void setListener(QuickRemoveListener value) {
        this.quickRemoveListener = value;
    }


    private BitmapDrawable bitmapDrawable;

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        if (bitmapDrawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * bitmapDrawable.getIntrinsicHeight() / bitmapDrawable.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


}
