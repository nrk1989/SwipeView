/**
 * SwipeView.java
 * 
 * A SwipeView for the adapter class.
 *
 * @category   View
 * @package    com.rk.lib.view
 * @version    1.0
 * @author     Rajkumar.N (Email: rkmail1989@gmail.com)
 * @license    http://www.apache.org/licenses/LICENSE-2.0 
 */
package com.android.lib.view;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * A SwipeView for the adapter class.
 */
public class SwipeView extends FrameLayout {

	private View mFocusedView;
	private View mFocusedViewLike;
	private View mFocusedViewNope;

	private int mFocusedViewWidth;
	private float mPreviousAlpha = 0.0f;

	private Integer mLikeResource = 0;
	private Integer mNopeResource = 0;

	private final static int MAX_ELEMENTS = 3;
	private final static long DELAY_SCROLL_RUNNABLE = 1;
	private final static int SCROLL_LENGTH = 5;
	private int mScrolledPixelsX;
	private int mScrolledPixelsY;
	private int mNeedToScrollX;
	private int mNeedToScrollY;
	private int mTotalScrolledX;
	private int mTotalScrolledY;
	private int mScrollLengthX = SCROLL_LENGTH;
	private int mScrollLengthY = SCROLL_LENGTH;

	private boolean enableTouchSwipe = true;

	private Context mContext;

	private ScrollMode mScrollModeX = ScrollMode.NONE;
	private ScrollMode mScrollModeY = ScrollMode.NONE;
	private ScrollDirection mScrollDirection = ScrollDirection.NONE;

	private enum ScrollMode {
		LEFT, RIGHT, TOP, BOTTOM, NONE
	};

	private enum ScrollDirection {
		IN, OUT, NONE
	};

	private int paddingX[] = { 0, 10, 20 };
	private int paddingYTop[] = { 0, 10, 20 };
	private int paddingYBottom[] = { 20, 10, 0 };

	private OnCardSwipedListener mOnCardSwipedListener;

	/**
	 * The listener used to detects whether the cards liked or disliked.
	 * 
	 * @see OnCardSwipedEvent
	 */
	public interface OnCardSwipedListener {
		/**
		 * Call back method triggered when the view is liked.
		 */
		public void onLikes();

		/**
		 * Call back method triggered when the view is disliked.
		 */
		public void onDisLikes();

		/**
		 * Call back method triggered when the view is tapped at single click.
		 */
		public void onSingleTap();

	}

	/**
	 * Instantiates a new swipe view.
	 * 
	 * @param context
	 *            the context
	 * @param likeResource
	 *            the like view id
	 * @param nopeResource
	 *            the nope view id
	 * @param cardSwipeListener
	 *            the card swipe listener
	 */
	public SwipeView(Context context, Integer likeResource,
			Integer nopeResource, OnCardSwipedListener cardSwipeListener) {
		super(context);
		mContext = context;
		mLikeResource = likeResource;
		mNopeResource = nopeResource;
		mOnCardSwipedListener = cardSwipeListener;

		float density = getResources().getDisplayMetrics().density;
		for (int i = 0; i < paddingX.length; i++) {
			paddingX[i] = (int) (paddingX[i] * density);
			paddingYTop[i] = (int) (paddingYTop[i] * density);
			paddingYBottom[i] = (int) (paddingYBottom[i] * density);
		}

		final GestureDetector gestureDetector = new GestureDetector(mContext,
				simpleOnGestureListener);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getChildCount() > 0) {
					if (mScrollDirection != ScrollDirection.NONE) {
						return false;
					}

					if (!enableTouchSwipe) {
						return false;
					}

					gestureDetector.onTouchEvent(event);
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						if (getChildCount() > 0) {
							mFocusedView = getChildAt(getChildCount() - 1);
							mFocusedViewLike = mFocusedView
									.findViewById(mLikeResource);
							mFocusedViewNope = mFocusedView
									.findViewById(mNopeResource);
							mFocusedViewWidth = mFocusedView.getWidth();
							// Updates the padding the focused view.
							mFocusedView.setPadding(paddingX[0], 0,
									paddingX[0], 0);
						}
						resetScrollingValues();
						break;
					}

					case MotionEvent.ACTION_UP: {
						// Reset the padding the of child
						alignCardsPadding();
						// Detects scroll mode in X
						if (mScrolledPixelsX < 0) {
							mScrollModeX = ScrollMode.LEFT;
							mTotalScrolledX = -mScrolledPixelsX;
						} else {
							mScrollModeX = ScrollMode.RIGHT;
							mTotalScrolledX = mScrolledPixelsX;
						}
						// Detects scroll mode in Y
						if (mScrolledPixelsY < 0) {
							mScrollModeY = ScrollMode.BOTTOM;
							mTotalScrolledY = -mScrolledPixelsY;
						} else {
							mScrollModeY = ScrollMode.TOP;
							mTotalScrolledY = mScrolledPixelsY;
						}
						detectSwipe();
						break;
					}

					default:
						break;
					}
					return true;
				} else {
					return false;
				}
			}
		});
	}

	private Handler mScrollHandler = new Handler();
	private Runnable mScrollRunnable = new Runnable() {

		@Override
		public void run() {
			if (mScrollDirection == ScrollDirection.OUT) {
				// Scroll Direction outer
				if (mNeedToScrollX > 0 || mNeedToScrollY > 0) {
					// Calculate Scroll in X
					if (mNeedToScrollX < mScrollLengthX) {
						mScrollLengthX = mNeedToScrollX;
						mNeedToScrollX = 0;
					} else {
						mNeedToScrollX = mNeedToScrollX - mScrollLengthX;
					}
					// Calculate Scroll in Y
					if (mNeedToScrollY < mScrollLengthY) {
						mScrollLengthY = mNeedToScrollY;
						mNeedToScrollY = 0;
					} else {
						mNeedToScrollY = mNeedToScrollY - mScrollLengthY;
					}

					// Scrolling the view
					int scrollX = 0;
					int scrollY = 0;
					// Calculate scroll x value
					if (mScrollModeX == ScrollMode.LEFT) {
						scrollX = -mScrollLengthX;
					} else {
						scrollX = mScrollLengthX;
					}
					// Calculate scroll y value
					if (mScrollModeY == ScrollMode.TOP) {
						scrollY = -mScrollLengthY;
					} else {
						scrollY = mScrollLengthY;
					}
					mFocusedView.scrollBy(scrollX, scrollY);
					// Post the delay runnable
					mScrollHandler.postDelayed(mScrollRunnable,
							DELAY_SCROLL_RUNNABLE);
				} else {
					mScrollHandler.removeCallbacks(mScrollRunnable);
					removeView(mFocusedView);
					if (mScrollModeX == ScrollMode.LEFT) {
						mOnCardSwipedListener.onLikes();
					} else if (mScrollModeX == ScrollMode.RIGHT) {
						mOnCardSwipedListener.onDisLikes();
					}
					alignCardsPadding();
				}
			} else if (mScrollDirection == ScrollDirection.IN) {
				// Scroll Direction inner
				if (mTotalScrolledX > 0 || mTotalScrolledY > 0) {
					// Scroll in X
					if (mTotalScrolledX < mScrollLengthX) {
						mScrollLengthX = mTotalScrolledX;
						mTotalScrolledX = 0;
					} else {
						mTotalScrolledX = mTotalScrolledX - mScrollLengthX;
					}
					// Scroll in Y
					if (mTotalScrolledY < mScrollLengthY) {
						mScrollLengthY = mTotalScrolledY;
						mTotalScrolledY = 0;
					} else {
						mTotalScrolledY = mTotalScrolledY - mScrollLengthY;
					}

					// Scrolling the view
					int scrollX = 0;
					int scrollY = 0;
					// Calculate scroll x value
					if (mScrollModeX == ScrollMode.LEFT) {
						scrollX = mScrollLengthX;
					} else {
						scrollX = -mScrollLengthX;
					}
					// Calculate scroll y value
					if (mScrollModeY == ScrollMode.TOP) {
						scrollY = -mScrollLengthY;
					} else {
						scrollY = mScrollLengthY;
					}
					mFocusedView.scrollBy(scrollX, scrollY);
					// Post the delay runnable
					mScrollHandler.postDelayed(mScrollRunnable,
							DELAY_SCROLL_RUNNABLE);

				} else {
					mScrollHandler.removeCallbacks(mScrollRunnable);
					mScrollDirection = ScrollDirection.NONE;
				}
			}
		}
	};

	/** The simple on gesture listener. */
	private final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mOnCardSwipedListener.onSingleTap();
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (mFocusedView != null) {
				mScrolledPixelsX = mScrolledPixelsX + (int) distanceX;
				mScrolledPixelsY = mScrolledPixelsY + (int) distanceY;
				mFocusedView.scrollBy((int) distanceX, (int) distanceY);
				// Calculate Alpha and Update alpha for the like/nope images
				final float alpha = ((float) mScrolledPixelsX)
						/ ((float) mFocusedViewWidth);
				if (alpha > 0) {
					// Enable or disable the visibility of like/nope
					mFocusedViewNope.setVisibility(View.VISIBLE);
					mFocusedViewLike.setVisibility(View.GONE);
					setAlpha(mFocusedViewNope, mPreviousAlpha, alpha);
					mPreviousAlpha = alpha;
				} else {
					// Enable or disable the visibility of like/nope
					mFocusedViewNope.setVisibility(View.GONE);
					mFocusedViewLike.setVisibility(View.VISIBLE);
					setAlpha(mFocusedViewLike, mPreviousAlpha, -alpha);
					mPreviousAlpha = -alpha;
				}
			}
			return true;
		};
	};

	/**
	 * Add cards as child view to swipe view.
	 * 
	 * @param view
	 *            - The view reference indicates the view going to be added.
	 * @param position
	 *            - The position of the view.
	 */
	public void addCard(final View view, int position) {
		if (getChildCount() <= MAX_ELEMENTS && position < MAX_ELEMENTS) {
			final LinearLayout viewLayout = new LinearLayout(mContext);
			viewLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			viewLayout.addView(view);
			viewLayout.setPadding(paddingX[position], paddingYTop[position],
					paddingX[position], paddingYBottom[position]);
			addView(viewLayout, 0);
		}
	}

	/**
	 * Removes the focused card in the card stack.
	 */
	public void removeFocusedCard() {
		removeView(mFocusedView);
		alignCardsPadding();
	}

	/**
	 * Aligns the swipe view's child padding.
	 */
	private void alignCardsPadding() {
		for (int i = 0, j = getChildCount() - 1; j >= 0; i++, j--) {
			getChildAt(j).setPadding(paddingX[i], paddingYTop[i], paddingX[i],
					paddingYBottom[i]);
		}
		mScrollDirection = ScrollDirection.NONE;
	}

	/**
	 * Resets the local scrolled values.
	 */
	private void resetScrollingValues() {
		mPreviousAlpha = 0.0f;
		mNeedToScrollX = 0;
		mScrolledPixelsX = 0;
		mTotalScrolledX = 0;
		mNeedToScrollY = 0;
		mScrolledPixelsY = 0;
		mTotalScrolledY = 0;
		mScrollLengthX = SCROLL_LENGTH;
		mScrollLengthY = SCROLL_LENGTH;
		mScrollModeX = ScrollMode.NONE;
		mScrollModeY = ScrollMode.NONE;
	}

	/**
	 * Reset focused view during onConfigChanges.
	 */
	public void resetFocuedView() {
		if (getChildCount() > 0) {
			final View mFocusedView = getChildAt(getChildCount() - 1);
			final View mFocusedViewLike = mFocusedView
					.findViewById(mLikeResource);
			final View mFocusedViewNope = mFocusedView
					.findViewById(mNopeResource);
			// Hides the like and nope when the focused view get reseted
			setAlpha(mFocusedViewLike, 0.0f, 0.0f);
			setAlpha(mFocusedViewNope, 0.0f, 0.0f);
			// Reset focused view scrolls
			mFocusedView.scrollTo(0, 0);
		}
	}

	/**
	 * Detect swipe view action.
	 */
	private void detectSwipe() {
		int imageHalf = mFocusedView.getWidth() / 2;
		mNeedToScrollX = mFocusedView.getWidth() - mTotalScrolledX;
		if (mScrollDirection == ScrollDirection.NONE) {
			if (mNeedToScrollX < imageHalf) {
				mScrollDirection = ScrollDirection.OUT;
			} else {
				mScrollDirection = ScrollDirection.IN;

				// Hides the like and nope when the scroll direction is inside
				setAlpha(mFocusedViewLike, 0.0f, 0.0f);
				setAlpha(mFocusedViewNope, 0.0f, 0.0f);
			}
		}
		mScrollHandler.post(mScrollRunnable);
	}

	/**
	 * Likes the card view.
	 */
	public void likeCard() {
		if (getChildCount() > 0) {
			mFocusedView = getChildAt(getChildCount() - 1);
			mFocusedViewLike = mFocusedView.findViewById(mLikeResource);
			mFocusedViewNope = mFocusedView.findViewById(mNopeResource);
			if (mScrollDirection != ScrollDirection.NONE) {
				return;
			}
			resetScrollingValues();
			mScrollDirection = ScrollDirection.OUT;
			mScrollModeX = ScrollMode.LEFT;
			// Show the like image
			mFocusedViewLike.setVisibility(View.VISIBLE);
			setAlpha(mFocusedViewLike, 0.0f, 1.0f);
			detectSwipe();
		}
	}

	/**
	 * Dislikes the card view.
	 */
	public void dislikeCard() {
		if (getChildCount() > 0) {
			mFocusedView = getChildAt(getChildCount() - 1);
			mFocusedViewLike = mFocusedView.findViewById(mLikeResource);
			mFocusedViewNope = mFocusedView.findViewById(mNopeResource);
			if (mScrollDirection != ScrollDirection.NONE) {
				return;
			}
			resetScrollingValues();
			mScrollDirection = ScrollDirection.OUT;
			mScrollModeX = ScrollMode.RIGHT;
			// Show the dislike image
			mFocusedViewNope.setVisibility(View.VISIBLE);
			setAlpha(mFocusedViewNope, 0.0f, 1.0f);
			detectSwipe();
		}
	}

	/**
	 * Enable or disable the swipe view touchable.
	 * 
	 * @param touchable
	 *            - The boolean reference indicates the enable or disable
	 *            touchable of swipe view.
	 */
	public void setTouchable(boolean touchable) {
		enableTouchSwipe = touchable;
	}

	/**
	 * Sets the alpha for the view..
	 * 
	 * @param view
	 *            the view reference indicates the alpha going to set
	 * @param fromAlpha
	 *            the float refers the from alpha
	 * @param toAlpha
	 *            the float refers the to alpha
	 */
	public static void setAlpha(final View view, final float fromAlpha,
			final float toAlpha) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			final AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha,
					toAlpha);
			alphaAnimation.setDuration(0);
			alphaAnimation.setFillAfter(true);
			view.startAnimation(alphaAnimation);
		} else {
			view.setAlpha(toAlpha);
		}
	}
}
