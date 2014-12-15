SwipeView
==========

SwipeView for android to like or dislike the items. A Tinder-like Android library to create the swipeable cards. 

Overview
--------
**SwipeView** is a view that can be used to like/dislike a card from the list of cards placed one behind one.

A card can be liked/disliked by swiping the card or pressing the like and nope buttons.

Usage 
--------

Create a instance:

    SwipeView mSwipeView=new SwipeView(Context, LikeViewId, NopeViewId, SwipeView.OnCardSwipedListener);

Add a card to the SwipeView:

    mSwipeView.addCard(View, Position);

Events
--------
There are three events will be called with OnCardSwipedListener:

1. **onLikes()** - Called when a card has been liked.

2. **onDisLikes()** - Called when a card has been dislike.

3. **onSingleTap()** - Called when a card has been single taped. 

Important:
--------
For the better cards view, the SwipeView can holds the **maximum of 3 cards** at a time. But, you can add one by one after a card has been removed.

Basic Samples
--------

![Cards View](https://github.com/nrk1989/SwipeView/blob/master/ScreenShots/Cards.png)

![Like A Card](https://github.com/nrk1989/SwipeView/blob/master/ScreenShots/Like.png)

![Dislike A Card](https://github.com/nrk1989/SwipeView/blob/master/ScreenShots/Dislike.png)

