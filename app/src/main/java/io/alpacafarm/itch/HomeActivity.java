package io.alpacafarm.itch;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;

public class HomeActivity extends Activity {
    private ViewPager viewPager;
    private PagerSlidingTabStrip indicator;
    private View header;
    private int actionBarSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("");

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new HomeFragmentPagerAdapter());
        viewPager.setPageTransformer(false, new DepthPageTransformer());
        indicator = (PagerSlidingTabStrip) findViewById(R.id.titles);
        indicator.setViewPager(viewPager);

        header = findViewById(R.id.header_container);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // unlike onCreateOptionsMenu, this gets called after the action bar height is > 0
        actionBarSize = getActionBar().getHeight();
        if (actionBarSize > 0) {
            getActionBar().setIcon(IconFont.getIconDrawable(this, actionBarSize));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private String pages[];
        private HomeListFragment currentFragment;
        private ListView currentView;

        public HomeFragmentPagerAdapter() {
            super(HomeActivity.this.getFragmentManager());

            pages = new String[]{
                    "/r/glitch_art",
                    "/r/woahdude",
                    "/r/brokengifs",
                    "/r/pixelsorting"
            };
        }

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();
            arguments.putString("GALLERY", pages[position]);
            return Fragment.instantiate(HomeActivity.this, HomeListFragment.class.getName(), arguments);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages[position];
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, final Object object) {
            super.setPrimaryItem(container, position, object);

            HomeListFragment fragment = (HomeListFragment) object;
            ListView view = (ListView) fragment.getView();
            if (view != null && view != currentView && view.getHeight() > 0) {
                if (currentFragment != null) {
                    currentFragment.setOnScrollListener(null);
                }

                currentFragment = fragment;
                currentFragment.setOnScrollListener(new FragmentScrollListener());

                currentView = view;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);

            // updates any views that were added
            updateListViews();
        }

        void updateListViews() {
            int childCount = viewPager.getChildCount();
            if (currentView == null || childCount < 2) {
                return;
            }
            for (int i = 0; i < childCount; i++) {
                ListView child = (ListView) viewPager.getChildAt(i);
                if (child != currentView && child.getFirstVisiblePosition() == 0) {
                    int min = actionBarSize + indicator.getHeight();
                    if (currentView.getFirstVisiblePosition() == 0) {
                        // the placeholder header is at least partially visible
                        int offset = Math.max(currentView.getChildAt(1).getTop(), min);
                        child.setSelectionFromTop(1, offset);
                    } else {
                        child.setSelectionFromTop(1, min);
                    }
                }
            }
        }

        private class FragmentScrollListener implements AbsListView.OnScrollListener {
            private boolean touched = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int state) {
                // this doesn't get triggered by layouts
                touched = true;

                if (state == SCROLL_STATE_IDLE) {
                    updateListViews();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount == 0) {
                    return;
                }

                final int translation = calculateHeaderTranslation(view);
                if (touched) {
                    header.clearAnimation();
                    header.setTranslationY(translation);
                } else if (header.getTranslationY() != translation) {
                    TranslateAnimation headerAnimation = new TranslateAnimation(0, 0, header.getTranslationY(), translation);
                    headerAnimation.setDuration(100);
                    headerAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // persist the translation (setFillAfter causes glitches later)
                            header.setTranslationY(translation);

                            // onAnimationEnd gets called before the animation ends
                            header.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    header.startAnimation(headerAnimation);
                }
            }

            private int calculateHeaderTranslation(AbsListView view) {
                int max = -header.getHeight() + indicator.getHeight() + actionBarSize;
                if (view.getFirstVisiblePosition() == 0) {
                    return Math.max(view.getChildAt(0).getTop(), max);
                } else {
                    return max;
                }
            }
        }
    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) {        // [-inf, -1]
                // this page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // (-1,0]
                // scale down and translate opposite the default translation
                view.setAlpha(1 + position);
                view.setTranslationX(pageWidth * -position);

                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else if (position <= 1) { // (0,1]
                // default slide transition sliding in from right
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            }
            // else (1,+Infinity]
        }
    }
}
