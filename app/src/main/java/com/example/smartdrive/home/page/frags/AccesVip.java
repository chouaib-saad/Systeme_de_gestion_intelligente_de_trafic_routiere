package com.example.smartdrive.home.page.frags;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.smartdrive.R;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class AccesVip extends Fragment {


    //emeregency switch and text declaration
    SwitchCompat emergencySwitch;
    TextView isActivatedText,userName;

    //lottie animation declaration
    LottieAnimationView animationView ;


    //layout animation declaration
    LinearLayout switchLayout;

    //initialise progress bar
    ProgressBar progressBar;


    //firebase firestore reference
    FirebaseFirestore db;



    //emergency Logo
    LinearLayout emergencyLogo_layout;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.acces_vip, container, false);


        //emeregency switch and text hook
        emergencySwitch = v.findViewById(R.id.emergencySwitch);
        isActivatedText = v.findViewById(R.id.isActivatedText);

        //create a new instance of progressbar
        progressBar = new ProgressBar(getContext());



        //lottie animation initialisation
        animationView = v.findViewById(R.id.emergencyLogo);

        //layout animation declaration
        switchLayout = v.findViewById(R.id.switchLayout);

        //emergency Logo
        emergencyLogo_layout = v.findViewById(R.id.emergencyLogo_layout);



        //userName Initialisation
        userName = v.findViewById(R.id.userName);


        //transition to the lineair layouts to take effects
        switchLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


        //set the user picture and profile name
        setUserName(v);

        //set the emergency state
        setTheEmergencyState();



        emergencySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    isActivatedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
                    isActivatedText.setText("Mode d'urgence active");

                    //animation change
                    animationView.setAnimation(R.raw.emergency_on);
                    animationView.setRepeatCount(LottieDrawable.INFINITE);
                    animationView.playAnimation();


                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("emergencyState", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEmergency", true);
                    editor.apply();


                }else {

                    isActivatedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                    isActivatedText.setText("Mode d'urgence desactive");

                    //animation change
                    animationView.setAnimation(R.raw.emergency_off);
                    animationView.cancelAnimation();
                    animationView.setProgress(0.52f);

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("emergencyState", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEmergency", false);
                    editor.apply();


                }


            }
        });



        return v;



    }   //onCreated end





    @SuppressLint("SetTextI18n")
    private void setTheEmergencyState(){

        //initialise sharedPreference state
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("emergencyState", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("isEmergency",false)){

            emergencySwitch.setChecked(true);
            isActivatedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
            isActivatedText.setText("Mode d'urgence active");

            //animation change
            animationView.setAnimation(R.raw.emergency_on);
            animationView.setRepeatCount(LottieDrawable.INFINITE);
            animationView.playAnimation();


        }else{

            emergencySwitch.setChecked(false);
            isActivatedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            isActivatedText.setText("Mode d'urgence desactive");

            //animation change
            animationView.setAnimation(R.raw.emergency_off);
            animationView.cancelAnimation();
            animationView.setProgress(0.52f);



        }
    }




    private void setUserName(View v){

        progressBar.show();

        //profile data hook
        userName = v.findViewById(R.id.userName);

        //database initialisation
        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(getUserUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String UserName = documentSnapshot.getString("UserName");

                        assert UserName != null;
                        userName.setText("M. "+UserName.toLowerCase()+",");
                    }

                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        progressBar.dismiss();

                    }
                });








    }       //end set home Coordinate






    //getUser Uid
    private String getUserUid(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return   currentUser.getUid();
    }





}   // end class