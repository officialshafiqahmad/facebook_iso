package com.example.facebook_iso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.example.facebook_iso.adapters.PostsListAdapter;
import com.example.facebook_iso.entities.Post;
import com.example.facebook_iso.menuHandler.MenuHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Feed_Page extends AppCompatActivity {

    private RecyclerView lstPosts;
    private PostsListAdapter adapter;
    private MenuHandler menuHandler;
    private List<Post> posts;

    private String user_name;
    private Uri user_photo;
    private String userFullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_page);

        user_name = "@" + current_user.getInstance().get_Current().getUserName();
        user_photo = current_user.getInstance().get_Current().getProfilePic();
        String userFirstName = current_user.getInstance().get_Current().getFirstName();
        String userLastName = current_user.getInstance().get_Current().getLastName();
        userFullName = userFirstName + " " + userLastName;

        lstPosts = findViewById(R.id.lstPosts);
        adapter = new PostsListAdapter(this);
        lstPosts.setAdapter(adapter);

        lstPosts.setLayoutManager(new LinearLayoutManager(this));

        posts = new ArrayList<>();
        loadJson();
        adapter.setPosts(posts);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        adapter.setUserName(user_name);
        adapter.setUserPhoto(user_photo);
    }

    private void loadJson() {
        try {
            InputStream inputStream = getAssets().open("allPosts.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;
            int max;
            String title, author, author_photo, description, date, img;
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            max = jsonArray.length();
            for (int i = 0; i < max; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                title = jsonObject.getString("title");
                author = "@" + jsonObject.getString("author");
                author_photo = jsonObject.getString("author_photo");
                description = jsonObject.getString("description");
                date = jsonObject.getString("date");
                img = jsonObject.getString("img");
                Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + img);
                Uri imageAuthorUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + author_photo);
                // Create the post with the correct image resource
                posts.add(new Post(title, author, imageAuthorUri,description, date, imageUri, lstPosts, adapter));
            }


        } catch (Exception e) {
            Log.e("TAG", "loadJson: error " + e);
        }


    }

    private void showPopupMenu(View view) {
        View menuView = getLayoutInflater().inflate(R.layout.menu, null);
        PopupWindow popupWindow = new PopupWindow(menuView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAsDropDown(view);
        menuHandler = new MenuHandler(menuView, lstPosts, adapter, user_name, popupWindow, userFullName, user_photo);
    }

}