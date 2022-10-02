/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import tool.compet.core.DkRunner1;

/**
 * Utility class for views.
 */
public class DkViews {
//	/**
//	 * Use {@link View#setBackground(Drawable)} instead since for now, all modules have required api 16+.
//	 */
//	public static void setBackground(View view, Drawable background) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // api 16+
//			view.setBackground(background);
//		}
//		else {
//			view.setBackgroundDrawable(background);
//		}
//	}

	/**
	 * This provides clip path for canvas since `canvas.clipPath()` only works at api 18+.
	 *
	 * @param view View to be drawn.
	 * @param canvas Canvas of the view.
	 * @param path Path to be clipped.
	 */
	public static void clipPath(View view, Canvas canvas, Path path) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) { // api 17-
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		canvas.clipPath(path);
	}

	public static void setTextSize(TextView tv, float newSizeInPx) {
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSizeInPx);
	}

	public static void makeUnderlineTagClickable(TextView textView, DkRunner1<View> clickCb) {
		makeUnderlineTagClickable(textView, textView.getText().toString(), clickCb);
	}

	public static void makeUnderlineTagClickable(TextView textView, String textInHtml, DkRunner1<View> clickCb) {
		Spanned spanned = Html.fromHtml(textInHtml);
		SpannableStringBuilder builder = new SpannableStringBuilder(spanned);
		UnderlineSpan[] urls = builder.getSpans(0, spanned.length(), UnderlineSpan.class);

		if (urls != null) {
			for (UnderlineSpan span : urls) {
				int start = builder.getSpanStart(span);
				int end = builder.getSpanEnd(span);
				int flags = builder.getSpanFlags(span);

				ClickableSpan clickable = new ClickableSpan() {
					public void onClick(@NonNull View view) {
						if (clickCb != null) {
							clickCb.run(view);
						}
					}
				};

				builder.setSpan(clickable, start, end, flags);
			}
		}

		textView.setText(builder);
		textView.setLinksClickable(true);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public static void applyFont(Context context, TextView tv) {
		tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/customFont"));
	}

	/**
	 * @param bounds For eg,. this.textPaint.getTextBounds(text, 0, text.length, bounds)
	 * @param cx x-axis position which the text will be drawn
	 * @param cy y-axis position which the text will be drawn
	 */
	public static float[] calcTextViewDrawPoint(Rect bounds, float cx, float cy) {
		float halfWidth = (bounds.right - bounds.left) / 2f;
		float halfHeight = (bounds.bottom - bounds.top) / 2f;

		return new float[] {cx - halfWidth - bounds.left, cy + halfHeight - bounds.bottom};
	}

	public static float[] getTextViewDrawPoint(Rect bounds, float leftBottomX, float leftBottomY) {
		return new float[] {leftBottomX - bounds.left, leftBottomY - bounds.bottom};
	}

	/**
	 * Get dimension of a view when it is laid out.
	 *
	 * @param view     target view.
	 * @param callback dimension callback [width, height].
	 */
	public static void getViewDimension(View view, DkRunner1<int[]> callback) {
		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				else {
					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}

				if (callback != null) {
					callback.run(new int[] {view.getMeasuredWidth(), view.getMeasuredHeight()});
				}
			}
		});
	}

	public static Animation translateAnimation(long durationMillis, int fromX, int toX, int fromY, int toY) {
		TranslateAnimation anim = new TranslateAnimation(fromX, toX, fromY, toY);
		anim.setDuration(durationMillis);
		anim.setFillAfter(true);
		return anim;
	}

	public static void tintMenuIcon(Menu menu, int color) {
		for (int i = menu.size() - 1; i >= 0; --i) {
			Drawable drawable = menu.getItem(i).getIcon();

			if (drawable != null) {
				drawable.mutate();
				drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	public static void changeToolbarTextFont(Context context, Toolbar toolbar) {
		for (int i = 0, N = toolbar.getChildCount(); i < N; i++) {
			View view = toolbar.getChildAt(i);

			if (view instanceof TextView) {
				TextView tv = (TextView) view;

				if (tv.getText().equals(toolbar.getTitle())) {
					DkViews.applyFont(context, tv);
					break;
				}
			}
		}
	}

	public static void loadWebviewFromHtml(WebView webView, String htmlContent) {
		webView.loadDataWithBaseURL("",
			htmlContent,
			"text/html",
			"utf-8",
			"");
	}

	public static void decorateProgressBar(ProgressBar pb, int color) {
		pb.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
	}

	public static Bitmap createBitmapFromView(View view) {
		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
		view.buildDrawingCache();

		if (view.getDrawingCache() == null) {
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		view.destroyDrawingCache();

		return bitmap;
	}

	public static void changeBackgroundColor(View view, int argb, float radius, float density) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(argb);
		drawable.setShape(GradientDrawable.RECTANGLE);
		drawable.setCornerRadius(radius * density);

		view.setBackgroundDrawable(drawable);
	}

	public static void setStatusBarColor(Activity act, int colorId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = act.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(colorId);
		}
	}

	/**
	 * 4 borders will be rounded
	 *
	 * @param color {normalColor, pressedColor}
	 */
	public static void injectStateListDrawable(View view, float cornerRadius, int[] color) {
		injectStateListDrawable(view, cornerRadius, color, new boolean[]{true, true, true, true});
	}

	/**
	 * @param color  [normalColor, pressedColor]
	 * @param border [topLeft, topRight, bottomRight, bottomLeft]
	 */
	public static void injectStateListDrawable(View view, float cornerRadius, int[] color, boolean[] border) {
		int normalColor = color[0];
		int pressedColor = color[1];
		float topLeftRad = border[0] ? cornerRadius : 0;
		float topRightRad = border[1] ? cornerRadius : 0;
		float bottomRightRad = border[2] ? cornerRadius : 0;
		float bottomLeftRad = border[3] ? cornerRadius : 0;

		StateListDrawable stateList = new StateListDrawable();

		GradientDrawable pressedDrawable = new GradientDrawable();
		pressedDrawable.setColor(pressedColor);
		pressedDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		GradientDrawable normalDrawable = new GradientDrawable();
		normalDrawable.setColor(normalColor);
		normalDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		stateList.addState(new int[] {android.R.attr.state_pressed}, pressedDrawable);
		stateList.addState(new int[] {}, normalDrawable);

		ViewCompat.setBackground(view, stateList);
	}

	public static void injectGradientDrawable(View view, float cornerRadius, int normalColor) {
		boolean[] border = new boolean[]{true, true, true, true};
		injectGradientDrawable(view, cornerRadius, normalColor, border);
	}

	/**
	 * @param border {left-top, top-right, right-bottom, bottom-left}
	 */
	public static void injectGradientDrawable(View view, float cornerRadius, int normalColor, boolean[] border) {
		float topLeftRad = border[0] ? cornerRadius : 0;
		float topRightRad = border[1] ? cornerRadius : 0;
		float bottomRightRad = border[2] ? cornerRadius : 0;
		float bottomLeftRad = border[3] ? cornerRadius : 0;

		GradientDrawable normalDrawable = new GradientDrawable();
		normalDrawable.setColor(normalColor);
		normalDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		ViewCompat.setBackground(view, normalDrawable);
	}

	public static boolean isInScrollingContainer(View view) {
		ViewParent parent = view.getParent();

		while (parent instanceof ViewGroup) {
			if (((ViewGroup) parent).shouldDelayChildPressedState()) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * Check whether the `event` is touched inside the `view`.
	 *
	 * @param event Touch event.
	 * @param view  Target view.
	 */
	public static boolean isInsideView(MotionEvent event, View view) {
		float x = event.getX();
		float y = event.getY();

		return x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom();
	}

	/**
	 * Check whether the touch is visually inside the view.
	 *
	 * @param w      view's width
	 * @param h      view's height
	 * @param localX touched point in x-axis inside view
	 * @param localY touched point in y-axis inside view
	 * @param slop   obtain by ViewConfiguration.get(context).getScaledTouchSlop()
	 */
	public static boolean isInsideView(float localX, float localY, int w, int h, int slop) {
		return localX >= -slop && localY >= -slop && localX < (w + slop) && localY < (h + slop);
	}

	/**
	 * Calculate rectangle offset (NOT bounds) of descendant at ancestor coordinate space.
	 * Normally, result-offset contains positive left, top values.
	 *
	 * @return Coordinate of left-top point of descendant.
	 * Caller should use `offset.left` and `offset.top`.
	 */
	public static Rect calcDescendantOffsetAtAncestorCoords(ViewGroup ancestor, View descendant) {
		Rect offset = new Rect();
		ancestor.offsetDescendantRectToMyCoords(descendant, offset);
		return offset;
	}

	/**
	 * Calculate rectangle offset (NOT bounds) of ancestor at descendant coordinate space.
	 * Normally, result-offset contains negative left, top values.
	 *
	 * @return Coordinate of left-top point of descendant.
	 * Caller should use `offset.left` and `offset.top`.
	 */
	public static Rect calcAncestorOffsetAtDescendantCoords(ViewGroup ancestor, View descendant) {
		Rect offset = new Rect();
		ancestor.offsetRectIntoDescendantCoords(descendant, offset);
		return offset;
	}

	/**
	 * Calculate bounds of descendant view at ancestor view coordinate space.
	 */
	public static Rect calcDescendantBoundsAtAncestorCoords(ViewGroup ancestor, View descendant) {
		Rect offset = calcDescendantOffsetAtAncestorCoords(ancestor, descendant);
		int left = offset.left;
		int top = offset.top;
		return new Rect(left, top, left + descendant.getWidth(), top + descendant.getHeight());
	}

	/**
	 * Calculate bounds of descendant view at ancestor view coordinate space.
	 */
	public static Rect calcAncestorBoundsAtDescendantCoords(ViewGroup ancestor, View descendant) {
		Rect offset = calcAncestorOffsetAtDescendantCoords(ancestor, descendant);
		int left = offset.left;
		int top = offset.top;
		return new Rect(left, top, left + ancestor.getWidth(), top + ancestor.getHeight());
	}

	public static int getStatusbarHeight(Resources resources, int defaultValue) {
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return defaultValue;
	}
}
