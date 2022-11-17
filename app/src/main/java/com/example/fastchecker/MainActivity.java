package com.example.fastchecker;

import android.os.Bundle;
import android.util.Log;

import com.example.fastchecker.ui.check.CheckFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fastchecker.databinding.ActivityMainBinding;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements CheckFragment.hashInterface {

    private ActivityMainBinding binding;

    protected HashMap hashContent = new HashMap();

    HashMap myHash = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Instant start = Instant.now();
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_check)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        loadHashFile();
        Instant end = Instant.now();
        //Log.d("DEBUG", "TOTAL>>>Time for MainActivity class : "+Duration.between(start, end));

    }

    public void loadHashFile() {


        try {

            InputStream myInputStream = getResources().openRawResource(R.raw.betterwords);
            Scanner sc = new Scanner(myInputStream);

            int x = 0;  // not necessary but might need later

            while (sc.hasNextLine()) {

                myHash.put(x, sc.nextLine());
                x++;
            }

            Log.d("DEBUG", "loadHashFile() : Dictionary put into myHash totaling "+myHash.size()+"!");
        } catch (NullPointerException e) {

            Log.d("EXCEPTION", "NullPointer occurred! Index out of bounds!");
        }

        //Log.d("DEBUG", "SUCCESS! myHash is "+myHash.size());
    }


    public HashMap getHash() {
        return myHash;
    }

}