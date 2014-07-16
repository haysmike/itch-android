itch-android
============

A Play Newsstand-style app for browsing subreddit galleries on Imgur

Layout
------
- Multiple infinite-scrolling ListViews in a swipeable ViewPager
- Header with tab indicator, which appears to be shared between lists. The tab indicator sticks to the top of the screen so it can still be used when the list is scrolled down.

Hurdles
-------
- JANK! if you play with Play Newsstand for a few minutes, you'll see the header disconnect from the lists and snap back into place.
- The ListView-in-a-ScrollView approach isn't worth the trouble.
- FragmentStatePagerAdapter's knowledge of the current view is limited, and ViewPager doesn't know about current fragment/position.
- onScroll() calls happen after fragment and activity lifecycle hooks, making it hard to know when views have settled.
- There's one edge case that requires some UX thought: when the user moves from a list that is scrolled to the top (showing the header) to a list that is already scrolled down. Rather than glitching, animate the header to the right position.

### ListView Issues
- Only one OnScrollListener per ListView. OnScrollListeners are much more convenient than OnTouchListeners, and sync much better. Add an additional scroll listener to the fragment or subclass ListView.
- ListView manages its own scrolling. Putting a ListView inside a ScrollView seems simple logically, and it *is* possible with some complicated OnTouchListener logic... but i've never gotten it to feel right. The one hack i found that feels decent involves making the ListView expand indefinitely as contents are added, which has the nasty side effect of causing it to not recycle views. So you'll probably want to stick with a ViewPager...
- scrollTo() and translateY() don't work for scrolling offscreen lists. These are methods on *View*, and will cause the container to move, not scroll the contents.
- The smoothScroll methods don't stick when the ListView becomes the focused child of the ViewPager. The list will jump back to the top after being added.
- setSelection() and setSelectionFromTop() can be used to update the offscreen lists. This updates the ListViews' internal states without causing extra scroll events. See the updateListViews() method.
- Similarly, ListViews won't tell you their scroll position in pixels. You can calculate it (count * item size + header size) but we really just need to know how much of the header is visible. Use listView.getFirstVisiblePosition() and view.getTop() on the item.
- ListViews can't scroll if their contents aren't long enough. One fix is to use a tall footer to ensure all the lists can scroll. See ScreenHeightLayout.java.
- ListViews don't seem to respect the height of their headers' contents - empty FrameLayouts will get squashed. Set a minHeight to fix.

### FragmentStatePagerAdapter Issues
- Fragments get lots of onActivityCreated calls. In a production app you'll want to use persistence and caching to avoid hitting the network every time the user swipes. FragmentStatePagerAdapter will save and restore your fragments' states - you can handle this like [Configuration Changes](https://github.com/thecodepath/android_guides/wiki/Handling-Configuration-Changes#saving-and-restoring-fragment-state).
- It's unclear in the adapter when the view will be created. setPrimaryItem() gets called a lot and is a fine place to update state.
- A more formalized way of storing state would be to keep track of all of your fragments using an ItemInfo array (very common pattern in Android's internal views) or a [sparse array](https://github.com/thecodepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter#dynamic-viewpager-fragments).
- Depending on what you're doing, the isViewFromObject() method on the adapter and getFocusedChild() on the ViewPager may also come in handy.
- There are many approaches, but as long as you know the current view before you update any scroll positions you'll probably be fine.

### Additional Notes
- GridView doesn't support headers/footers, and doesn't have a setSelectionFromTop() method. You can simulate a GridView by using ListView with LinearLayouts in the individual list items. This will complicate your adapter a bit.
- Translation and scroll are opposites! moving the header up means scrolling the list down.
- Animations on Android have two issues that make it harder to translate our header:
  - setFillAfter(true) has some weird side-effects, so persist the translation using an AnimationListener.
  - onAnimationEnd() gets called before the animation ends (wat), so you'll need to call clearAnimation() to avoid flicker.

Image Issues
------------
- Text on images can look bad or be unreadable. Use a gradient to make the text visible. See overlay_gradient.xml and styles.xml, and note the use of padding to adjust the fade.
- Images don't load immediately, and static placeholders are boring, so generate a placeholder drawable using info from the model we're loading. See ImgurPost.getColorDrawable() for an example.
- Use centerCrop to make sure you don't leave empty space while maintaining the image's aspect ratio.
- Images take up a lot of memory. Make sure you resize them in the image loader. Resizing them based on the view is tricky, because the view often hasn't been measured by the time you want to fetch images.
- Keep in mind that designing shorter list items means more items will be visible at a time.

Other Styling Tricks
--------------------
- Scroll bars and default overscroll indicators look really bad in this type of layout. Turn off scroll bars and consider implementing pull-to-refresh.
- Use a PageTransformer to add a more 3-dimensional feel to the ViewPager.
- Use an xml drawable for tab indicator states. See tab_selector_unselected_pressed.xml.
- Use a font glyph for the app icon. See IconFont.java (you may want to move letter "strings" to strings.xml to give them a name). You can use textView.setTypeFace(IconFont.getTypeFace()) to display your glyphs elsewhere.
- Use a consistent aspect ratio for images by overriding onMeasure. See AspectImageView.java.

Instructions
------------
1. Point Android Studio at the top-level build.gradle file.
1. Add Secrets.java with your imgur client id:
```java
package io.alpacafarm.itch;

public class Secrets {
      public static final String IMGUR_CLIENT_ID = "YOUR CLIENT ID HERE";
}
```

Included Libraries
------------------
- [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)
- [android-async-http](https://github.com/loopj/android-async-http)
- [Picasso](https://github.com/loopj/android-async-http)

Credits
-------
- [NotBoringActionBar](https://github.com/flavienlaurent/NotBoringActionBar)
- [Android-ParallaxHeaderViewPager](https://github.com/kmshack/Android-ParallaxHeaderViewPager)
