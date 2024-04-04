package com.example.facebook_iso;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.PopupWindow;


import com.example.facebook_iso.adapters.PostsListAdapter;
import com.example.facebook_iso.entities.Post;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPost {

    private final PostsListAdapter adapter;
    private final RecyclerView lstPosts;
    private final String user_name;
    private final Uri user_photo;
    private Uri imagePost;

    private static final int REQUEST_SELECT_PHOTO = 1;
    private Boolean is_camera;
    private final int CAMERA_REQ_CODE = 100;


    public NewPost(PostsListAdapter adapter, RecyclerView lstPosts, View view, String user_name, Uri user_photo) {
        this.adapter = adapter;
        this.lstPosts = lstPosts;
        this.user_name = user_name;
        this.user_photo = user_photo;
        this.is_camera = false;
        this.imagePost = null;
        createNewPost(view);
    }

    public void createNewPost(View view) {
        view = LayoutInflater.from(view.getContext()).inflate(R.layout.newpost, null);
        PopupWindow popupWindowNewPost = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0] + view.getWidth() / 2 - popupWindowNewPost.getWidth() / 2;
        int y = view.getHeight() * 5;
        popupWindowNewPost.showAtLocation(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, x, y);

        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDescription = view.findViewById(R.id.etDescription);



        Button btnChooseImage = view.findViewById(R.id.btnChooseImage);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), picturePost.class);
                v.getContext().startActivity(intent);

            }
        });



        Button btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePost = currentPost.getInstance().getImagePost();
                String setTitle = etTitle.getText().toString();
                String setDescription = etDescription.getText().toString();
                if (imagePost != null){
                    createPost(setTitle, setDescription);
                }
                popupWindowNewPost.dismiss();
            }
        });

        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window without making any changes
                popupWindowNewPost.dismiss();
            }
        });


    }


    private void createPost(String setTitle, String setDescription) {
        String date = getDate();
        Post newPost = new Post(setTitle, user_name, user_photo, setDescription, date, imagePost, lstPosts, adapter);
        adapter.addPost(newPost);
    }


    private String getDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(currentDate);

    }

}