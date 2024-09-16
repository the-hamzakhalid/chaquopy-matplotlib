package com.chaquo.myapplication.utils.view.paintview.listener;


import android.graphics.Path;

import com.chaquo.myapplication.utils.view.paintview.painttools.FirstCurrentPosition;


public interface Shapable {
	Path getPath();

	FirstCurrentPosition getFirstLastPoint();

	void setShap(ShapesInterface shape);
}
