package com.example.facebook_iso.editHandler;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.facebook_iso.R;
import com.example.facebook_iso.adapters.PostsListAdapter;
import com.example.facebook_iso.EditPost;
import com.example.facebook_iso.entities.Post;

import java.util.List;

public class ThreeDots {

    private static final int REQUEST_SELECT_PHOTO = 1;
    private final View menuView;
    private final PostsListAdapter adapter;
    private final RecyclerView lstPosts;
    private Post post;

    private final PopupWindow popupWindow;

    public ThreeDots(View menuView, PostsListAdapter adapter, RecyclerView lstPosts, Post post, PopupWindow popupWindow) {
        this.menuView = menuView;
        this.adapter = adapter;
        this.lstPosts = lstPosts;
        this.post = post;
        this.popupWindow =popupWindow;
        setupMenu();
    }

    private void setupMenu() {
        int indexOfPost = adapter.getPosts().indexOf(post);
        if (indexOfPost != -1) {
            lstPosts.scrollToPosition(indexOfPost);
        }
        ImageButton editButton = menuView.findViewById(R.id.EditButton);
        TextView editText = menuView.findViewById(R.id.EditText);
        ImageButton deleteButton = menuView.findViewById(R.id.DeleteButton);
        TextView deleteText = menuView.findViewById(R.id.DeleteText);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOperation(v);
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOperation(v);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOperation();
            }
        });

        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOperation();
            }
        });
    }

    private void editOperation(View view) {
        popupWindow.dismiss();
        EditPost editPost = new EditPost(adapter, lstPosts, post, view);
    }

    private void deleteOperation() {
        popupWindow.dismiss();
        List<Post> posts = adapter.getPosts();
        int indexOfPost = posts.indexOf(post);
        posts.remove(indexOfPost);
        adapter.setPosts(posts);
    }
}