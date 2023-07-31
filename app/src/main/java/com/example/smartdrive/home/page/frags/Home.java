package com.example.smartdrive.home.page.frags;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartdrive.R;

import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;


public class Home extends Fragment {



    //load data estimated time
    private static final int LOAD_DATA_TIME = 300 ;


    //initialise progress bar
    ProgressBar progressBar;



    //firebase firestore reference
    FirebaseFirestore db;


    //slider declaration
    SliderView sliderView;


    //profile data
    private TextView userName;
    private ImageView userPicture,vipBadge;


    //interface home layout
    ConstraintLayout homeLayout;

    //initialise open profile button layout
    LinearLayout profile_layout;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);


        //settingLayout hook
        homeLayout = v.findViewById(R.id.homeLayout);


        //initialise vip badge
        vipBadge = v.findViewById(R.id.vipBadge);

        //create a new instance of progressbar
        progressBar = new ProgressBar(getContext());


        //initialise open profile layout
        profile_layout = v.findViewById(R.id.profile_layout);

        //inisialise vip badge
        vipBadge = v.findViewById(R.id.vipBadge);



        //slide view initialisation
        initialiseSlider(v);

        //set the user picture and profile name
        setHomeCoordinates(v);



            profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openProfile();

            }
        });


        return v;




    }         //onCreate end













    private void initialiseSlider(View v) {

        sliderView = v.findViewById(R.id.imageSlider);
        ImageSliderAdapter adapter = new ImageSliderAdapter();

        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setIndicatorUnselectedColor(ContextCompat.getColor(requireContext(),R.color.uncelected_slider_color));
        sliderView.setIndicatorSelectedColor(ContextCompat.getColor(requireContext(),R.color.green_settings));
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();



    }






    private void setHomeCoordinates(View v){

        progressBar.show();

        //profile data hook
        userName = v.findViewById(R.id.userName);
        userPicture = v.findViewById(R.id.userPicture);

        //database initialisation
        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(getUserUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String UserName = documentSnapshot.getString("UserName");
                        String UserPicture = documentSnapshot.getString("UserPicture");
                        Boolean UserVipState = documentSnapshot.getBoolean("VIP");


                        userName.setText(UserName);
                        assert UserVipState != null;
                        setVipBadge(UserVipState);



                        if (UserPicture == null){


                            //set Layout visivility
                            homeLayout.setVisibility(View.VISIBLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.dismiss();

                                }
                            },LOAD_DATA_TIME);                      }


                        //successfully add profile picture
                        Picasso.get()
                                .load(UserPicture)
                                //.fit()
                                .centerCrop()
                                .placeholder(R.drawable.default_picture)
                                .error(R.drawable.ic_error)
                                .resize(1600,1600)
                                .into(userPicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();

                                            }
                                        },LOAD_DATA_TIME);

                                        //set Layout visivility
                                        homeLayout.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();
                                            }
                                        },LOAD_DATA_TIME);

                                        //set Layout visibility
                                        homeLayout.setVisibility(View.VISIBLE);
                                        snackBar(e.getMessage(),1000,R.color.baseRed,R.drawable.ic_error);

                                    }

                                });






                    }

                });








    }       //end set home Coordinate






    //getUser Uid
    private String getUserUid(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return   currentUser.getUid();
    }


    private void openProfile() {

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new UserProfile(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }









    private void setVipBadge(Boolean VIP) {
        if (VIP) {

            vipBadge.setColorFilter(getResources().getColor(R.color.gold_color));

        } else {

            vipBadge.setColorFilter(getResources().getColor(R.color.base_grey));
        }
    }






    private void snackBar(String message,int time,int backgroundColor,int icon) {

        CookieBar.build(getActivity())
                .setTitle("Avertissement")
                .setMessage(message)
                .setDuration(time)
                .setTitleColor(R.color.special_white)
                .setBackgroundColor(backgroundColor)
                .setCookiePosition(CookieBar.BOTTOM)
                .setIcon(icon)
                .setAction("D'accord", new OnActionClickListener() {
                    @Override
                    public void onClick() {
                        CookieBar.dismiss(getActivity());
                    }
                })
                .show();
    }







}      //class end
