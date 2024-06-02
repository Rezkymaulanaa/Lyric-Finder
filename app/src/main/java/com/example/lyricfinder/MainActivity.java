package com.example.lyricfinder;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lyricfinder.Fragment.SearchFragment;
import com.example.lyricfinder.Fragment.ArtistFragment;
import com.example.lyricfinder.Fragment.ProfilFragment;
import com.example.lyricfinder.Fragment.SongFragment;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity implements ProfilFragment.ProfilFragmentListener {

    private MeowBottomNavigation meowBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meowBottomNavigation = findViewById(R.id.meow_bottom_navigation);

        // Add navigation items
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_audiotrack_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_group_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_search_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.baseline_person_24));

        // Set default fragment
        loadFragment(new SongFragment());

        // Set listener for navigation item click
        meowBottomNavigation.setOnClickMenuListener(model -> {
            Fragment fragment = null;
            switch (model.getId()) {
                case 1:
                    fragment = new SongFragment();
                    break;
                case 2:
                    fragment = new ArtistFragment();
                    break;
                case 3:
                    fragment = new SearchFragment();
                    break;
                case 4:
                    fragment = new ProfilFragment();
                    break;
            }
            loadFragment(fragment);
            return null;
        });

        // Set listener for default selected item
        meowBottomNavigation.setOnShowListener(model -> {
            Fragment fragment = null;
            switch (model.getId()) {
                case 1:
                    fragment = new SongFragment();
                    break;
                case 2:
                    fragment = new ArtistFragment();
                    break;
                case 3:
                    fragment = new SearchFragment();
                    break;
                case 4:
                    fragment = new ProfilFragment();
                    break;
            }
            loadFragment(fragment);
            return null;
        });

        // Set the first selected item
        meowBottomNavigation.show(1, true);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

    @Override
    public void onFavoriteUpdated() {
        // Handle the update here
        // This method will be called whenever the favorites are updated in ProfileFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ProfilFragment) {
            ((ProfilFragment) currentFragment).onFavoriteUpdated();
        }
    }
}
