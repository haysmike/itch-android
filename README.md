itch-android
============

A Play Newsstand-style app for browsing subreddit galleries on Imgur

layout
------
- multiple infinite-scrolling ListViews in a swipeable ViewPager
- header with tab indicator, which appears to be shared between lists. the tab indicator sticks to the top of the screen so it can still be used when the list is scrolled down.

hurdles
-------
- JANK! if you play with Play Newsstand for a few minutes, you'll see the header disconnect from the lists and snap back into place.
- the ListView-in-a-ScrollView approach isn't worth the trouble.
- FragmentStatePagerAdapter's knowledge of the current view is limited, and ViewPager doesn't know about current fragment/position.
- onScroll() calls happen after fragment and activity lifecycle hooks, making it hard to know when views have settled.
- there's one edge case that requires some UX thought: when the user moves from a list that is scrolled to the top (showing the header) to a list that is already scrolled down. rather than glitching, animate the header to the right position.

### ListView issues
- only one OnScrollListener per ListView. OnScrollListeners are much more convenient than OnTouchListeners, and sync much better. add an additional scroll listener to the fragment or subclass ListView.
- ListView manages its own scrolling. putting a ListView inside a ScrollView seems simple logically, and it *is* possible with some complicated OnTouchListener logic... but i've never gotten it to feel right. the one hack i found that feels decent involves making the ListView expand indefinitely as contents are added, which has the nasty side effect of causing it to not recycle views. so you'll probably want to stick with a ViewPager...
- scrollTo() and translateY() don't work for scrolling offscreen lists. these are methods on *View*, and will cause the container to move, not scroll the contents.
- the smoothScroll methods don't stick when the ListView becomes the focused child of the ViewPager. the list will jump back to the top after being added.
- setSelection() and setSelectionFromTop() can be used to update the offscreen lists. this updates the ListViews' internal states without causing extra scroll events. see the updateListViews() method.
- similarly, ListViews won't tell you their scroll position in pixels. You can calculate it (count * item size + header size) but we really just need to know how much of the header is visible. use listView.getFirstVisiblePosition() and view.getTop() on the item.
- ListViews can't scroll if their contents aren't long enough. one fix is to use a tall footer to ensure all the lists can scroll. see ScreenHeightLayout.java.
- ListViews don't seem to respect the height of their headers' contents - empty FrameLayouts will get squashed. set a minHeight to fix.

### FragmentStatePagerAdapter issues
- fragments get lots of onActivityCreated calls. in a production app you'll want to use persistence and caching to avoid hitting the network every time the user swipes. FragmentStatePagerAdapter will save and restore your fragments' states - you can handle this like [Configuration Changes](https://github.com/thecodepath/android_guides/wiki/Handling-Configuration-Changes#saving-and-restoring-fragment-state).
- it's unclear in the adapter when the view will be created. setPrimaryItem() gets called a lot and is a fine place to update state.
- a more formalized way of doing this would be to keep track of all of your fragments using an ItemInfo array (very common pattern in Android's internal views) or a [sparse array](https://github.com/thecodepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter#dynamic-viewpager-fragments).
- depending on what you're doing, the isViewFromObject() method on the adapter and getFocusedChild() on the ViewPager may also come in handy.
- there are many approaches, but as long as you know the current view before you update any scroll positions you'll probably be fine.

### additional notes
- GridView doesn't support headers/footers, and doesn't have a setSelectionFromTop() method. you can simulate a GridView by using ListView with LinearLayouts in the individual list items. this will complicate your adapter a bit.
- translation and scroll are opposites! moving the header up means scrolling the list down.
- animations on Android have two issues that make it harder to translate our header:
  - setFillAfter(true) has some weird side-effects, so persist the translation using an AnimationListener.
  - onAnimationEnd() gets called before the animation ends (wat), so you'll need to call clearAnimation() to avoid flicker.

image issues
------------
- text on images can look bad or be unreadable. use a gradient to make the text visible. see overlay_gradient.xml and styles.xml, and note the use of padding to adjust the fade.
- images don't load immediately, and static placeholders are boring, so generate a placeholder drawable using info from the model we're loading. see ImgurPost.getColorDrawable() for an example.
- use centerCrop to make sure you don't leave empty space while maintaining the image's aspect ratio.
- images take up a lot of memory. make sure you resize them in the image loader. resizing them based on the view is tricky, because the view often hasn't been measured by the time you want to fetch images.
- keep in mind that designing shorter list items means more items will be visible at a time.

other styling tricks
--------------------
- scroll bars and overscroll indicators look bad in this type of layout. turn off scroll bars and consider implementing pull-to-refresh.
- use a PageTransformer to add a more 3-dimensional feel to the ViewPager.
- use an xml drawable for tab indicator states. see tab_selector_unselected_pressed.xml.
- use a font glyph for the app icon. see IconFont.java (you may want to move letter "strings" to strings.xml to give them a name). you can use textView.setTypeFace(IconFont.getTypeFace()) to display your glyphs elsewhere.
- use a consistent aspect ratio for images by overriding onMeasure. see AspectImageView.java.

instructions
------------
- point Android Studio at the top-level build.gradle file
- project won't compile until you add Secrets.java with your imgur client id:
```java
package io.alpacafarm.itch;

public class Secrets {
      public static final String IMGUR_CLIENT_ID = "YOUR CLIENT ID HERE";
}
```

included libraries
------------------
- [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)
- [android-async-http](https://github.com/loopj/android-async-http)
- [Picasso](https://github.com/loopj/android-async-http)

credits
-------
- [NotBoringActionBar](https://github.com/flavienlaurent/NotBoringActionBar)
- [Android-ParallaxHeaderViewPager](https://github.com/kmshack/Android-ParallaxHeaderViewPager)
