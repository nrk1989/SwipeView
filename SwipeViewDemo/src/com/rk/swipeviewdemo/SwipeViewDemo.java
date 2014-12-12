/**
 * SwipeViewDemo.java
 * 
 * An demo activity for Swipe View.
 *
 * @category   View
 * @package    com.rk.swipeviewdemo
 * @version    1.0
 * @author     Rajkumar.N <rkmail1989@gmail.com>
 * @license    http://www.apache.org/licenses/LICENSE-2.0 
 */
package com.rk.swipeviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rk.lib.view.SwipeView;

public class SwipeViewDemo extends Activity implements
		SwipeView.OnCardSwipedListener {

	private final static int CARDS_MAX_ELEMENTS = 5;

	private FrameLayout contentLayout;
	private SwipeView mSwipeView;

	private int[] bikes = { R.drawable.img_bike_1, R.drawable.img_bike_2,
			R.drawable.img_bike_3, R.drawable.img_bike_4,
			R.drawable.img_bike_5, R.drawable.img_bike_6,
			R.drawable.img_bike_7, R.drawable.img_bike_8, R.drawable.img_bike_9 };

	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe_view_demo);
		contentLayout = (FrameLayout) findViewById(R.id.contentLayout);

		// Add the swipe view
		mSwipeView = new SwipeView(this, R.id.imgSwipeLike, R.id.imgSwipeNope,
				this);
		contentLayout.addView(mSwipeView);

		// Adding the cards initially with the maximum limits of cards.
		for (int i = 0; i < CARDS_MAX_ELEMENTS; i++) {
			addCard(i);
		}
	}

	/**
	 * On clicked view.
	 * 
	 * @param clickedView
	 *            the clicked view
	 */
	public void onClickedView(View clickedView) {
		switch (clickedView.getId()) {
		case R.id.imgDisLike: {
			mSwipeView.dislikeCard();
			break;
		}

		case R.id.imgLike: {
			mSwipeView.likeCard();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onLikes() {
		System.out.println("An Card removed");
		// Add a card if you needed after any previous card swiped
		addCard(0);
	}

	@Override
	public void onDisLikes() {
		System.out.println("An Card removed");
		// Add a card if you needed after any previous card swiped
		addCard(0);
	}

	@Override
	public void onSingleTap() {

	}

	/**
	 * Adds the card to the swipe.
	 */
	private void addCard(int position) {
		final View cardView = LayoutInflater.from(this).inflate(
				R.layout.item_swipe_view, null);
		final ImageView imgBike = (ImageView) cardView
				.findViewById(R.id.imgBike);
		imgBike.setImageResource(bikes[count]);
		count++;
		if (count == bikes.length) {
			count = 0;
		}
		// Add a card to the swipe view.
		mSwipeView.addCard(cardView, position);
	}
}
