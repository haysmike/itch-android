itch-android
============

a Play Newsstand-style app for browsing subreddit galleries on Imgur

layout
------
- multiple swipeable ListViews
- header with tab indicator appears to be shared between lists
- we'll use a placeholder header on the ListViews to give the appearance of a shared header

hurdles
-------
- JANK! if you play with Play Newsstand for a few minutes, you'll see the header disconnect from the lists
- using a FragmentStatePagerAdapter and a ViewPager, neither of which has knowledge of both the current fragment and current view
- onScroll calls happen after fragment and activity lifecycle hooks, making it hard to know when views have settled

### ListView issues:
- scrollTo and translateY don't work. these are methods on *View*, and will cause the container to move, not scroll the contents
- smoothScroll\* causes even more onScroll events to fire, making it even harder to tell when the view is settled
- ListView headers want to wrap contents - FrameLayouts get squashed. set a minHeight to fix.
- ListViews won't scroll if their contents aren't loaded. we'll use a tall footer to ensure all the views can scroll
- we'll use setSelectionFromTop to update the off-screen fragments. this avoids the extra scroll events, so we can use the onScrollStateChanged hook to tell when the view has been scrolled manually.

### FragmentStatePagerAdapter issues:
- fragments get lots of onActivityCreated calls. make sure caching is enabled or you'll be hitting the network way too much.
- unclear when the view is created. it's definitely there in finishUpdate, when the transaction is committed... turns out it's also available sometimes in setPrimaryItem.
- this just requires careful state management and experimentation. we'll be updating state (current view and fragment) in setPrimaryItem, and update offscreen views in finishUpdate.

### additional notes
- GridView doens't support headers/footers - can use ListView and LinearLayouts in the individual list items to simulate
- translation and scroll go in opposite directions! moving the header up means scrolling the list down.

styling with images
-------------------
- just lists of images... what's so hard about that?
- text on images often looks very bad. use a gradient to make the text visible. see overlay_gradient.xml
- images don't load immediately, and static placeholders are boring. we'll generate a placeholder drawable using info from the model we're loading. see ImgurPost.java
- images take up a lot of memory. make sure you resize them in the image loader. keep in mind that smaller list items means more items will be visible at a time.
- use centerCrop to make sure you don't leave empty space.

other styling tricks
--------------------
- using an xml drawable for tab indicator states. see tab_selector_unselected_pressed.xml
- using a font glyph for the app icon! see IconFont.java (you may want to move letter "strings" to strings.xml to give them a name)
- use a consistent aspect ratio for images by overriding onMeasure. see AspectImageView.java

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


