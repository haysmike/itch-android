package io.alpacafarm.itch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImgurPost {
    private String title;
    private String link;
    private int downs;
    private int ups;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public static ImgurPost fromJson(JSONObject jsonObject) {
        ImgurPost post = new ImgurPost();
        try {
            post.title = jsonObject.getString("title");
            post.link = jsonObject.getString("link");
            post.ups = jsonObject.getInt("ups");
            post.downs = jsonObject.getInt("downs");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return post;
    }

    public static ArrayList<ImgurPost> fromJson(JSONArray jsonArray) {
        ArrayList<ImgurPost> posts = new ArrayList<ImgurPost>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json;
            try {
                json = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            ImgurPost post = ImgurPost.fromJson(json);
            if (post != null) {
                posts.add(post);
            }
        }

        return posts;
    }

    public Drawable getColorDrawable(Context context) {
        // create a drawable based on the upvotes and downvotes, for use as a placeholder
        Rect rect = new Rect(0, 0, 1, 1);
        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.rgb(downs * 32, ups * 24, 12));
        canvas.drawRect(rect, paint);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
