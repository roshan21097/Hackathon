package com.vinay.hackathon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int RC_SIGN_IN = 11;
    List<AuthUI.IdpConfig> providers;
    ListView listView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseListAdapter<details> detailsFirebaseListAdapter;
    ArrayList<String> store_data;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        listView = (ListView)findViewById(R.id.list_view);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("database");

        detailsFirebaseListAdapter = new FirebaseListAdapter<details>(this,details.class,R.layout.details,
               databaseReference ) {
            @Override
            protected void populateView(View v, details model, int position) {
                TextView textView1 = (TextView)v.findViewById(R.id.data_textView1);
                textView1.setText(model.getTopic());
                TextView textView2 = (TextView)v.findViewById(R.id.data_textView2);
                textView2.setText(model.getMessage());

            }
        };
        listView.setAdapter(detailsFirebaseListAdapter);



        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        show_sign_in_option();
        bottomNav.setOnNavigationItemReselectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }

    private void show_sign_in_option(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                Toast.makeText(this,user.getDisplayName(),Toast.LENGTH_SHORT).show();


                // ...
            } else {
                Toast.makeText(this,"Sign In failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sign_out(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        show_sign_in_option();
                    }
                });
    }


    private BottomNavigationView.OnNavigationItemReselectedListener navListener =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new SettingFragment();
                            break;
                        case R.id.nav_help:
                            selectedFragment = new HelpFragment();
                            break;
                    }
                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
            };
}