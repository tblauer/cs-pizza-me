package com.tblauer.pizzame.view.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

	private int _orientation;
	private int _topOrLeftSize = 0;
	private int _bottomOrRightSize = 0;

	public SpacingItemDecoration(int orientation, int topOrLeftSize, int bottomOrRightSize) {
		if (orientation != LinearLayout.HORIZONTAL && orientation != LinearLayout.VERTICAL) {
			throw new IllegalArgumentException("invalid orientation");
		}
		_orientation = orientation;
		_topOrLeftSize = topOrLeftSize;
		_bottomOrRightSize = bottomOrRightSize;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		switch (_orientation) {
			case LinearLayout.VERTICAL:
				if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
					outRect.bottom = _bottomOrRightSize;
					outRect.top = _topOrLeftSize;
				}
				break;
			case LinearLayout.HORIZONTAL:
				if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
					outRect.right = _bottomOrRightSize;
					outRect.left = _topOrLeftSize;
				}
				break;
			default:
				break;
		}
	}
}
