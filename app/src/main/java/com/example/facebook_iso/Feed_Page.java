package com.example.facebook_iso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.facebook_iso.adapters.PostsListAdapter;
import com.example.facebook_iso.api_manager.api_manager;
import com.example.facebook_iso.api_manager.api_service;
import com.example.facebook_iso.api_manager.constants;
import com.example.facebook_iso.common.CurrentUserManager;
import com.example.facebook_iso.common.ProgressDialogManager;
import com.example.facebook_iso.common.ResponseHandler;
import com.example.facebook_iso.common.SharedPreferencesManager;
import com.example.facebook_iso.common.UIToast;
import com.example.facebook_iso.entities.Post;
import com.example.facebook_iso.entities.User;
import com.example.facebook_iso.entities.UserClass;
import com.example.facebook_iso.menuHandler.MenuHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import okhttp3.Response;
import okhttp3.internal.Util;


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
        UserClass currentUser = Objects.requireNonNull(CurrentUserManager.getInstance(Feed_Page.this).getCurrentUser()).getUser();
        user_name = "@" + currentUser.getUsername();
        user_photo = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.facebook_photo);
        userFullName = currentUser.getDisplayName();

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

    private String getPosts() {
        final String[] allPosts = new String[1];
        String username = Objects.requireNonNull(SharedPreferencesManager.getObject(Feed_Page.this, keys.currentUser, User.class)).getUser().getUsername();
        String bearerToken = Objects.requireNonNull(SharedPreferencesManager.getObject(Feed_Page.this, keys.currentUser, User.class)).getToken();
        String endpoint = constants.getPosts + "/" + username + "/posts";

        // Debug: Print URL and bearer token before making the API call
        Log.d("Debug123: ", "URL: " + constants.baseUrl + endpoint);
        Log.d("Debug123: ", "Bearer Token: " + bearerToken);

        Future<Response> future = new api_manager(Feed_Page.this).get(endpoint, bearerToken);
        ProgressDialogManager.showProgressDialog(Feed_Page.this, "Getting Posts", "Please wait...");

        // Debug: Print a log to indicate that the asynchronous call is being initiated
        Log.d("Debug123: ", "Asynchronous call initiated");

        // Use a lock object for synchronization
        final Object lock = new Object();

        new Thread(() -> {
            try {
                Response response = future.get();
                ProgressDialogManager.dismissProgressDialog();
                allPosts[0] = ResponseHandler.handleResponse(Feed_Page.this, response, constants.signIn, String.class);
                // Debug: Print a log to indicate successful response handling
                Log.d("Debug123: ", "Response successfully handled" + allPosts[0]);

                // Notify the waiting thread that the response is available
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                ProgressDialogManager.dismissProgressDialog();
                // Debug: Print exception message if an error occurs during response handling
                Log.d("Debug123: ", "Error handling response: " + Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
        }).start();

        // Synchronize on the lock object and wait for the response
        synchronized (lock) {
            try {
                lock.wait(); // This will wait until the lock is notified by the thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Debug: Print a log to indicate the method is returning
        Log.d("Debug123: ", "Returning from getPosts method");
        return allPosts[0];
    }



    private void loadJson() {
        try {
            InputStream inputStream = getAssets().open("allPosts.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String allPosts = new String(buffer, StandardCharsets.UTF_8);
//            String allPosts = getPosts();
            //API CAll
            String bearerToken = Objects.requireNonNull(SharedPreferencesManager.getObject(Feed_Page.this, keys.currentUser, User.class)).getToken();
            String endpoint = constants.getPosts + "/" + user_name + "/posts";
            new api_service(Feed_Page.this).get(endpoint, bearerToken, new api_service.ApiCallback()
            {
                @Override
                public void onSuccess(JSONObject response)
                {

                }

                @Override
                public void onSuccess(JSONArray response)
                {
                    JSONArray jsonArray = null;
                    try
                    {
                        String title, username, profilePic, description, date, img;
                        int max;
                        jsonArray = new JSONArray(response);
                        max = jsonArray.length();
                        for (int i = 0; i < max; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            username = "@" + jsonObject.getString("username");
                            description = jsonObject.getString("description");
                            title = jsonObject.getString("title");
                            date = jsonObject.getString("create_date");
                            img = jsonObject.getString("img");
                            profilePic = jsonObject.getString("profilePic");
                            Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + img);
                            Uri imageAuthorUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + profilePic);
                            posts.add(new Post(title, username, imageAuthorUri,description, date, imageUri, lstPosts, adapter));
                        }
                    }
                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onError(String errorMessage)
                {
                    UIToast.showToast(Feed_Page.this, errorMessage);
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "loadJson: error " + e);
        }
    }

    private void showPopupMenu(View view) {
        @SuppressLint("InflateParams") View menuView = getLayoutInflater().inflate(R.layout.menu, null);
        PopupWindow popupWindow = new PopupWindow(menuView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAsDropDown(view);
        menuHandler = new MenuHandler(menuView, lstPosts, adapter, user_name, popupWindow, userFullName, user_photo);
    }
}
