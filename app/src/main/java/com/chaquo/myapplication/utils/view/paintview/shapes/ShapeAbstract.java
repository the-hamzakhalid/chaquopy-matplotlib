package com.chaquo.myapplication.utils.view.paintview.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.chaquo.myapplication.utils.view.paintview.listener.Shapable;
import com.chaquo.myapplication.utils.view.paintview.listener.ShapesInterface;
import com.chaquo.myapplication.utils.view.paintview.painttools.FirstCurrentPosition;


public class ShapeAbstract implements ShapesInterface {

	protected Shapable paintTool = null;
	protected FirstCurrentPosition firstCurrentPos;
	Path mPath;
	protected float x1 = 0;
	protected float y1 = 0;
	protected float x2 = 0;
	protected float y2 = 0;

	ShapeAbstract(Shapable paintTool) {
		assert(paintTool!=null);
		this.paintTool = paintTool;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		firstCurrentPos = paintTool.getFirstLastPoint();
		mPath = paintTool.getPath();
		x1 = firstCurrentPos.firstX;
		y1 = firstCurrentPos.firstY;
		x2 = firstCurrentPos.currentX;
		y2 = firstCurrentPos.currentY;
	}

}
