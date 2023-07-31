package com.example.smartdrive;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdrive.Utility.NetworkChangeListener;
import com.example.smartdrive.Utility.ProgressBar;
import com.example.smartdrive.home.page.frags.TermesConditions;
import com.example.smartdrive.home.page.frags.Home;
import com.example.smartdrive.home.page.frags.Informations;
import com.example.smartdrive.home.page.frags.Settings;
import com.example.smartdrive.home.page.frags.SmartFeux;
import com.example.smartdrive.home.page.frags.Statistics;
import com.example.smartdrive.home.page.frags.AccesVip;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class VipScreen extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    //double back button to finish
    private  boolean doubleBackToExitPressedOnce = false;




    //backstack fragments
    FragmentManager fragmentManager = getSupportFragmentManager();


    //initialise progress bar
    ProgressBar progressBar;

    //currentFragment
    private Fragment currentFragment;


    //connection verification class
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //for fragments positions
    private static final int POS_CLOSE = 0;
    private static final int POS_HOME = 1;
    private static final int POS_ACCESVIP = 2;
    private static final int POS_INFORMATIONS = 3;
    private static final int POS_SMARTFEUX = 4;
    private static final int POS_STATISTICS = 5;
    private static final int POS_CONDITIONS_UTILISATION = 6;
    private static final int POS_SETTINGS = 7;
    private static final int POS_FakeItem = 8;
    private static final int POS_LOGOUT = 9;


    Toolbar toolbar;


    //string to pass item name and icon
    private String[] screenTitles;
    private Drawable[] screenIcons;

    //sliding root navigation
    SlidingRootNav slidingRootNav;


    //is vip in the zone ref
    DatabaseReference isVipInZone ;


    @Override
    public void onBackPressed() {

        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu();
        }else if(backStackCount > 0){
            fragmentManager.popBackStack();
        }

        else{

            if(doubleBackToExitPressedOnce){
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Appuyez a nouveau pour quitter ", Toast.LENGTH_SHORT).show();

            //time for second click
            int SECOND_CLICK_DELAY = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, SECOND_CLICK_DELAY);

        }
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vip_screen);

        //progressbar hook
        progressBar = new ProgressBar(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(100)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();




        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();



        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_ACCESVIP),
                createItemFor(POS_INFORMATIONS),
                createItemFor(POS_SMARTFEUX),
                createItemFor(POS_STATISTICS),
                createItemFor(POS_CONDITIONS_UTILISATION),
                createItemFor(POS_SETTINGS),
                createItemFor(POS_FakeItem),
                createItemFor(POS_LOGOUT)
        ));

        adapter.setListener(this);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.setSelected(POS_HOME);



        setUserTypeTitle();




        //initialise the firebase elements
        isVipInZone = FirebaseDatabase.getInstance().getReference();




    }  //onCreate end


















    private String[] loadScreenTitles() {

        return getResources().getStringArray(R.array.id_activityVipTitles);
    }



    private Drawable[] loadScreenIcons() {

        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityVipIcons);
        Drawable[] icons = new Drawable[ta.length()];

        for(int i = 0 ; i < ta.length() ; i++){

            int id = ta.getResourceId(i,0);
            if((id != 0)){
                icons[i] = ContextCompat.getDrawable(this,id);
            }

        }
        ta.recycle();
        return icons;
    }


    private  DrawerItem createItemFor(int position){

        //icon color
        return new SimpleItem(screenIcons[position],screenTitles[position])
                .withIconTint(color(R.color.baseGreen))
                .withSelectedIconTint(R.color.baseRed)
                .withTextTint(R.color.baseGreen)
                .withSelectedTextTint(R.color.baseGreen);

    }


    @ColorInt
    private int  color(@ColorRes int res){
        return ContextCompat.getColor(this,res);
    }




    @Override
    public void onItemSelected(int position) {

        int backStackCount = fragmentManager.getBackStackEntryCount();
        //check the backstack
        if(backStackCount > 0) {
            //delete the cache fragments
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Check if fragment already exists
        Fragment fragment = null ;

        switch (position) {
            case POS_HOME:
                fragment = new Home();
                toolbar.setTitle("Accueil");
                break;
            case POS_ACCESVIP:
                    fragment = new AccesVip();
                toolbar.setTitle("Acces VIP");
                break;
            case POS_INFORMATIONS:
                    fragment = new Informations();
                toolbar.setTitle("Informations");
                break;
            case POS_SMARTFEUX:
                    fragment = new SmartFeux();
                toolbar.setTitle("Smart Feux");
                break;
            case POS_STATISTICS:
                fragment = new Statistics();
                toolbar.setTitle("Statistiques");
                break;
            case POS_CONDITIONS_UTILISATION:
                fragment = new TermesConditions();
                toolbar.setTitle("Termes & Conditions");
                break;
            case POS_SETTINGS:
                    fragment = new Settings();
                toolbar.setTitle("Parametres");
                break;
            case POS_LOGOUT:
                logout();
                break;
            default:
                // Do nothing
                break;
        }

        // Hide previous fragment
        if (fragment != null) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            if (!fragment.isAdded()) {

                transaction.add(R.id.container, fragment);
            }

            transaction.show(fragment); // Show the selected fragment
            currentFragment = fragment;
            transaction.commit();

        }


        slidingRootNav.closeMenu();

    }






    private void logout(){

        progressBar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.dismiss();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Signin.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                VipScreen.super.finish();

            }},2500);


        // Google sign out
        GoogleSignIn.getClient(getApplicationContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut();



    }







        private void setUserTypeTitle(){

            //from the drawer_menu file
            TextView textView = findViewById(R.id.userTypeTitle);
            textView.setText("Smart Drive +");

        }


    @Override
    protected void onDestroy() {
        //initialise the isVipInZone State (first time set it to false)
        isVipInZone.child("isVipInZone").setValue(false);
        super.onDestroy();
    }


    @Override
    protected void onPause() {
    //isVipInZone.child("isVipInZone").setValue(false);
        super.onPause();
    }


} //class end