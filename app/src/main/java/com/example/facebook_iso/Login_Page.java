package com.example.facebook_iso;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class Login_Page extends AppCompatActivity {
    private logininfo_list list;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        current_user.getInstance().set_current(null);
        list = logininfo_list.getInstance();
        TextView username_text = (TextView) findViewById(R.id.username);
        TextView password_text = (TextView) findViewById(R.id.password);

        Button btnsingup = findViewById(R.id.btnsignup);
        btnsingup.setOnClickListener(v -> {
            Intent signupi =new Intent(this, signup_page.class);
            startActivity(signupi);
        });
        Button btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(v -> {
            String username = username_text.getText().toString();
            String pass = password_text.getText().toString();
            loginInfo user = list.find_by_user(username);
            if (user!=null){
                current_user.getInstance().set_current(user);
                if (check_password(pass)){
                    Toast.makeText(this, "valid password",
                            Toast.LENGTH_SHORT).show();
                    Intent i =new Intent(this, Feed_Page.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(this, "Invalid password",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Invalid username",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("Main Activity","onCreate");
    }
    private Boolean check_password(String pass){
        String realPass = current_user.getInstance().get_Current().getPassword();
        if (pass.equals(realPass)){
            return true;
        }
        return false;
    }

}