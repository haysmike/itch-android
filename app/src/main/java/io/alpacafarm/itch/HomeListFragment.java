package io.alpacafarm.itch;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeListFragment extends Fragment {
    private ListView listView;
    private ImgurPostAdapter adapter;
    private AbsListView.OnScrollListener scrollListener;
    private Picasso picasso;
    private View loadingFooter;
    private String gallery;

    public HomeListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        picasso = Picasso.with(getActivity());

        adapter = new ImgurPostAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int state) {
                if (scrollListener != null) {
                    scrollListener.onScrollStateChanged(view, state);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollListener != null) {
                    scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                if (totalItemCount + listView.getHeaderViewsCount() - (firstVisibleItem + visibleItemCount) < 5) {
                    // do infinite scroll stuff here!
                }
            }
        });

        gallery = getArguments().getString("GALLERY");
        ImgurRestClient.get("gallery" + gallery, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray items = response.getJSONArray("data");
                    ArrayList<ImgurPost> posts = ImgurPost.fromJson(items);
                    adapter.addAll(posts);
                    listView.removeFooterView(loadingFooter);
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "failure retrieving gallery '" + gallery, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.fragment_home_list, container, false);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // placeholder goes under the header in the activity layout
        View placeholderHeader = inflater.inflate(R.layout.view_header_placeholder, listView, false);
        listView.addHeaderView(placeholderHeader);

        // add a tall footer to make sure the view is always scrollable
        loadingFooter = inflater.inflate(R.layout.view_footer_loading, listView, false);
        listView.addFooterView(loadingFooter);

        return listView;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private class ImgurPostAdapter extends ArrayAdapter<ImgurPost> {
        public ImgurPostAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = newView(getContext());
            }
            bindView(convertView, getItem(position));
            return convertView;
        }

        public View newView(Context context) {
            View view = View.inflate(context, R.layout.view_imgur_post_list_item, null);
            ImgurPostHolder holder = new ImgurPostHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.titleView = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);
            return view;
        }

        public void bindView(View view, final ImgurPost imgurPost) {
            ImgurPostHolder holder = (ImgurPostHolder) view.getTag();
            holder.titleView.setText(imgurPost.getTitle());
            ImageView imageView = holder.imageView;
            picasso.load(imgurPost.getLink())
                    .resize(800, 600)   // todo: calculate based on view size
                    .centerCrop()
                    .placeholder(imgurPost.getColorDrawable(getActivity()))
                    .into(imageView);
        }

        private class ImgurPostHolder {
            ImageView imageView;
            TextView titleView;
        }
    }
}
