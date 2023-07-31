package com.example.smartdrive.home.page.frags;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdrive.R;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;


public class Informations extends Fragment {


    //inisialise data reference
    DatabaseReference trafficRef;

    ImageView car_route1,car_route2,car_route3,car_route4;
    TextView count_route1,count_route2,count_route3,count_route4;
    TextView route1_txt,route2_txt,route3_txt,route4_txt;


    //layouts animation
    LinearLayout logo_layout1,logo_layout2,logo_layout3,logo_layout4;
    LinearLayout route1,route2,route3,route4;


    //initialise markers
    LinearLayout carMark1,carMark2,carMark3,carMark4;
    ImageView ic_mark1,ic_mark2,ic_mark3,ic_mark4;


    //date layout and text dec
    ConstraintLayout title_date_layout;
    TextView date;



    //lane text view
    TextView voie_textview;

    //initialise the progressbar
    ProgressBar progressBar;


    //route  database and fstore reference
    DatabaseReference laneNumber ;
    FirebaseFirestore fStore;


    private Boolean isMarkClicked = false;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //progressbar initialising
        progressBar = new ProgressBar(getContext());

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.informations, container, false);


        //inisialise data reference
        trafficRef = FirebaseDatabase.getInstance().getReference().child("traffic");






        //Views hook
        //images logos
        car_route1 = v.findViewById(R.id.car_route1);
        car_route2 = v.findViewById(R.id.car_route2);
        car_route3 = v.findViewById(R.id.car_route3);
        car_route4 = v.findViewById(R.id.car_route4);



        //count TextView
        count_route1 = v.findViewById(R.id.count_route1);
        count_route2 = v.findViewById(R.id.count_route2);
        count_route3 = v.findViewById(R.id.count_route3);
        count_route4 = v.findViewById(R.id.count_route4);



        //text view route N
        route1_txt = v.findViewById(R.id.route1_txt);
        route2_txt = v.findViewById(R.id.route2_txt);
        route3_txt = v.findViewById(R.id.route3_txt);
        route4_txt = v.findViewById(R.id.route4_txt);


        //markers hook
        carMark1 = v.findViewById(R.id.carMark1);
        carMark2 = v.findViewById(R.id.carMark2);
        carMark3 = v.findViewById(R.id.carMark3);
        carMark4 = v.findViewById(R.id.carMark4);


        //markers image hook
        ic_mark1 = v.findViewById(R.id.ic_mark1);
        ic_mark2 = v.findViewById(R.id.ic_mark2);
        ic_mark3 = v.findViewById(R.id.ic_mark3);
        ic_mark4 = v.findViewById(R.id.ic_mark4);


        voie_textview = v.findViewById(R.id.voie_textview);


        //start the progress bar and close in on the onChange data
        progressBar.show();


        //initialise the firebase elements
        laneNumber = FirebaseDatabase.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();


        //initialise the isVipInZone State (first time set it to false)
        laneNumber.child("voie").setValue(null);




        LineairLayoutGlobalTransitionInit(v);
        LineairLayoutTransitionInit(v);
        titleDateLayoutTransitionInit(v);
        setMarkAnimate();
        trafficListener();
        setTheDateTime(v);



        carMark1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getTheVipZoneState() != null) {

                    if(!isMarkClicked){


                        ic_mark1.setVisibility(View.VISIBLE);
                        ic_mark2.setVisibility(View.INVISIBLE);
                        ic_mark3.setVisibility(View.INVISIBLE);
                        ic_mark4.setVisibility(View.INVISIBLE);

                        voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
                        voie_textview.setText("toi maintenant sur la route numero 1");
                        voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));

                        isMarkClicked = true;



                        //return to the SmartFeux activity and set the specifique(map)
                        openCountDown(1);



                    }else{

                        snackBar("Vous avez deja choisi votre voie",2000,R.color.baseGreen,R.drawable.ic_error);

                    }



                }else {



                    voie_textview.setText("Vous n'etes pas a l'interieur des feux"+"\n"+"ou un utilisateur vip !");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
                    startVibratingAnimation();



                }

            }
        });

        carMark2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(getTheVipZoneState() != null) {


                    if ((!isMarkClicked)){

                    ic_mark1.setVisibility(View.INVISIBLE);
                    ic_mark2.setVisibility(View.VISIBLE);
                    ic_mark3.setVisibility(View.INVISIBLE);
                    ic_mark4.setVisibility(View.INVISIBLE);

                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
                    voie_textview.setText("toi maintenant sur la route numero 2");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.green_settings));

                        isMarkClicked = true;

                        //return to the SmartFeux activity and set the specifique(map)
                        openCountDown(2);

                    }else{

                        snackBar("Vous avez deja choisi votre voie",2000,R.color.baseGreen,R.drawable.ic_error);

                    }

                }else{


                    voie_textview.setText("Vous n'etes pas a l'interieur des feux"+"\n"+"ou un utilisateur vip !");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
                    startVibratingAnimation();

                }


            }
        });


        carMark3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {


                if(getTheVipZoneState() != null) {

                    if(!isMarkClicked) {

                        ic_mark1.setVisibility(View.INVISIBLE);
                        ic_mark2.setVisibility(View.INVISIBLE);
                        ic_mark3.setVisibility(View.VISIBLE);
                        ic_mark4.setVisibility(View.INVISIBLE);

                        voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
                        voie_textview.setText("toi maintenant sur la route numero 3");
                        voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));

                       isMarkClicked = true;

                        //return to the SmartFeux activity and set the specifique(map)
                        openCountDown(3);

                }else{

                    snackBar("Vous avez deja choisi votre voie",2000,R.color.baseGreen,R.drawable.ic_error);


                }

                }else{

                    voie_textview.setText("Vous n'etes pas a l'interieur des feux"+"\n"+"ou un utilisateur vip !");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
                    startVibratingAnimation();

                }

            }
        });

        carMark4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {


                if(getTheVipZoneState() != null) {


                    if(!isMarkClicked){

                    ic_mark1.setVisibility(View.INVISIBLE);
                    ic_mark2.setVisibility(View.INVISIBLE);
                    ic_mark3.setVisibility(View.INVISIBLE);
                    ic_mark4.setVisibility(View.VISIBLE);

                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_settings));
                    voie_textview.setText("toi maintenant sur la route numero 4");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.green_settings));

                        isMarkClicked = true;

                        //return to the SmartFeux activity and set the specifique(map)
                        openCountDown(4);

                    }else{

                        snackBar("Vous avez deja choisi votre voie",2000,R.color.baseGreen,R.drawable.ic_error);

                    }

                }else{

                    voie_textview.setText("Vous n'etes pas a l'interieur des feux"+"\n"+"ou un utilisateur vip !");
                    voie_textview.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
                    startVibratingAnimation();

                }
            }
        });







        return v;

    }       //onCreate end






























    //set car counts
    @SuppressLint("SetTextI18n")
    private void setCarCount(int laneNumber, String route){
        TextView countText;
        switch(laneNumber) {
            case 1:
                countText = count_route1;
                break;
            case 2:
                countText = count_route2;
                break;
            case 3:
                countText = count_route3;
                break;
            case 4:
                countText = count_route4;
                break;
            default:
                return; // Invalid lane number
        }
        countText.setText("X" + route);
    }







    private void updateLaneData(String vehicleCount, ImageView carImageView, TextView countTextView, TextView routeTextView) {

        if (!isVisible()) {
            return; // Fragment is hidden, exit the method
        }

        if (vehicleCount != null && vehicleCount.equals("0")) {
            carImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.chartGreen2));
            countTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.chartGreen2));
            routeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.chartGreen2));
        } else if (vehicleCount != null && Integer.parseInt(vehicleCount) >= 1 && Integer.parseInt(vehicleCount) < 4) {

            carImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_marker));
            countTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_marker));
            routeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_marker));
        } else if (vehicleCount != null && Integer.parseInt(vehicleCount) >= 4) {

            carImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            countTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            routeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));

        }
    }

    //traffic firebase data change listener
    private void trafficListener() {
        ValueEventListener trafficListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    },1000);


                    DataSnapshot lane1Snapshot = dataSnapshot.child("lane1");
                    DataSnapshot lane2Snapshot = dataSnapshot.child("lane2");
                    DataSnapshot lane3Snapshot = dataSnapshot.child("lane3");
                    DataSnapshot lane4Snapshot = dataSnapshot.child("lane4");

                    String lane1VehicleCount = lane1Snapshot.child("vehicle-count").getValue(String.class);
                    String lane2VehicleCount = lane2Snapshot.child("vehicle-count").getValue(String.class);
                    String lane3VehicleCount = lane3Snapshot.child("vehicle-count").getValue(String.class);
                    String lane4VehicleCount = lane4Snapshot.child("vehicle-count").getValue(String.class);

                    setCarCount(1, lane1VehicleCount);
                    setCarCount(2, lane2VehicleCount);
                    setCarCount(3, lane3VehicleCount);
                    setCarCount(4, lane4VehicleCount);

                    updateLaneData(lane1VehicleCount, car_route1, count_route1, route1_txt);
                    updateLaneData(lane2VehicleCount, car_route2, count_route2, route2_txt);
                    updateLaneData(lane3VehicleCount, car_route3, count_route3, route3_txt);
                    updateLaneData(lane4VehicleCount, car_route4, count_route4, route4_txt);
                }else {

                    progressBar.dismiss();
                    snackBar("un erreur ce produit", 2000, R.color.baseRed, R.drawable.ic_warning);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackBar("erreur : " + databaseError.getMessage(), 2000, R.color.baseRed, R.drawable.ic_warning);
                progressBar.dismiss();
            }


        };

        trafficRef.addValueEventListener(trafficListener);
    }//traffic listener end








    //Lineair Layout Transition Init
    private void LineairLayoutTransitionInit(View v){


        //lineair layouts hooks
        logo_layout1 = v.findViewById(R.id.logo_layout1);
        logo_layout2 = v.findViewById(R.id.logo_layout2);
        logo_layout3 = v.findViewById(R.id.logo_layout3);
        logo_layout4 = v.findViewById(R.id.logo_layout4);


        //transition to the lineair layouts to take effects
        logo_layout1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        logo_layout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        logo_layout3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        logo_layout4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }


    private void titleDateLayoutTransitionInit(View v){

        //date layout and text hook
        title_date_layout = v.findViewById(R.id.title_date_layout);
        //transition to the lineair layouts to take date-layout
        title_date_layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }




    //Lineair Layout Global Transition Init
    private void LineairLayoutGlobalTransitionInit(View v){

        //global lineair layouts hooks
        route1 = v.findViewById(R.id.route1);
        route2 = v.findViewById(R.id.route2);
        route3 = v.findViewById(R.id.route3);
        route4 = v.findViewById(R.id.route4);


        //transition to the lineair layouts to take effects
        route1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        route2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        route3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        route4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }


    //set mark animate
    private void setMarkAnimate(){
        //transition to the lineair layouts to take effects
        carMark1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        carMark2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        carMark3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        carMark4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }






    //snack-bar message function
    private void snackBar(String message, int time, int backgroundColor, int icon) {

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







    private void updateDateTime() {
        // Get the current time and day


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("fr", "FR")); // Set the Locale to French

        String currentTime = timeFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());

        // Set the current time and day to the TextView
        date.setText(currentTime + " " + currentDay);
    }

    private void setTheDateTime(View v) {
        date = v.findViewById(R.id.date);
        updateDateTime();

        // Create a Handler and a Runnable to update the date and time periodically
        final Handler handler = new Handler();
        final Runnable updateDateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000); // Schedule the next update after 1 second
            }
        };

        // Start updating the date and time when the view is created
        handler.postDelayed(updateDateTimeRunnable, 0);
    }




    //get The Vip Zone State from the map activity ( smartFeux)
    private Boolean getTheVipZoneState(){

        // Retrieve the arguments
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("isVipInZone")) {
            return bundle.getBoolean("isVipInZone");
        }else {
            return null;
        }

    }


    //vibrationAnimation
    private void startVibratingAnimation() {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(voie_textview, "translationX", 0, 10, -10, 0);
        animator.setDuration(100);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();

        // Stop the animation after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.cancel();
            }
        }, 260);
    }




    private void returnToTheSmartFeuxFrag() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Get the FragmentManager
                FragmentManager fragmentManager = getParentFragmentManager();

                // Check if there are any fragments in the back stack
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    // Pop the topmost fragment from the back stack
                    fragmentManager.popBackStack();
                }

            }
        }, 1000);
    }








    //countdown  dialog
    private void openCountDown(int voie){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout_dialog = LayoutInflater.from(requireContext()).inflate(R.layout.green_light_countdown_dialog, null);
        builder.setView(layout_dialog);

        ConstraintLayout btn_annuler = layout_dialog.findViewById(R.id.btn_annuler);
        LottieAnimationView lottie_countdown = layout_dialog.findViewById(R.id.lottie_countdoun);

        //set the lottie progress to 0.6 and start it manually
//        lottie_countdown.setProgress(0.6f);
        // Set the minimum and maximum progress values
        lottie_countdown.setMinProgress(0.53f);
        lottie_countdown.setMaxProgress(1.0f);
        lottie_countdown.playAnimation();

        // Create an AtomicBoolean to track if the animation was canceled
        AtomicBoolean animationCanceled = new AtomicBoolean(false);



        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);


        // Set up Lottie animation listener
        lottie_countdown.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation ended, show toast

                if (!animationCanceled.get()) {
                    dialog.dismiss();
                    returnToTheSmartFeuxFrag();
                    laneNumber.child("voie").setValue(voie);

                //Toast.makeText(getContext(), "Countdown animation ended", Toast.LENGTH_SHORT).show();

                }

            }
            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation canceled

                // Animation canceled, set the flag to true
                animationCanceled.set(true);
                returnToTheSmartFeuxFrag();

                if(laneNumber != null){
                    laneNumber.child("voie").setValue(null);
                }

                //Toast.makeText(getContext(), "Countdown animation Canceled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeated
            }
        });


        //btn annuler
        btn_annuler.setOnClickListener(view -> {

            lottie_countdown.cancelAnimation();
            dialog.dismiss();

        });


        //show dialog
        dialog.show();
    }









}       //class end