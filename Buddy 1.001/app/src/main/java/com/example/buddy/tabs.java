package com.example.buddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class tabs extends AppCompatActivity {
     BottomNavigationView bview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        bview= findViewById(R.id.bottomNavigationView);
        bview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_home)
                {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft= fm.beginTransaction();
                    ft.add(R.id.container,new fragment_a());
                    ft.commit();
                    ft.add(R.id.container,new fragment_a());
                }
                else if(id ==R.id.nav_profile)
                {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft= fm.beginTransaction();
                    ft.add(R.id.container,new fragment_b());
                    ft.commit();
                    ft.replace(R.id.container,new fragment_b());
                }
                else if(id ==R.id.nav_auction)
                {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft= fm.beginTransaction();
                    ft.add(R.id.container,new fragment_c());
                    ft.commit();
                    ft.replace(R.id.container,new fragment_c());
                }
                else
                {

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft= fm.beginTransaction();
                    ft.add(R.id.container,new fragment_d());
                    ft.commit();
                    ft.replace(R.id.container,new fragment_d());
                }
                return true;
            }
        });
        bview.setSelectedItemId(R.id.nav_home);
    }
}