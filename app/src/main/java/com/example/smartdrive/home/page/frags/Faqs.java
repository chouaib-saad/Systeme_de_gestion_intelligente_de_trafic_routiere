package com.example.smartdrive.home.page.frags;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdrive.R;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;


public class Faqs extends Fragment {


    //this to on click on items
    CardView cardview1,cardview2,cardview3,cardview4,cardview5,cardview6,cardview7,cardview8,cardview9;

    //text description
    TextView textview1_details,textview2_details,textview3_details,textview4_details,textview5_details,textview6_details,textview7_details,textview8_details,textview9_details;

    //Lineair layout contains the title and the details both
    LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4,linearLayout5,linearLayout6,linearLayout7,linearLayout8,linearLayout9;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.faqs, container, false);



        //views hooks initialisation
        viewsInit(v);

        //LineairLayout Transition Initialisation
        LineairLayoutTransitionInit();


        //cardView Transition Initialisation
        cardViewTransitionInit();



        cardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview1_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout1,new AutoTransition());
                textview1_details.setVisibility(v);

                resetStateForOthers(cardview1);

            }});





        cardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview2_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout2,new AutoTransition());
                textview2_details.setVisibility(v);

                resetStateForOthers(cardview2);


            }});



        cardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview3_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout3,new AutoTransition());
                textview3_details.setVisibility(v);

                resetStateForOthers(cardview3);


            }});



        cardview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview4_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout4,new AutoTransition());
                textview4_details.setVisibility(v);

                resetStateForOthers(cardview4);


            }});




        cardview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview5_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout5,new AutoTransition());
                textview5_details.setVisibility(v);

                resetStateForOthers(cardview5);


            }});


        cardview6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview6_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout6,new AutoTransition());
                textview6_details.setVisibility(v);

                resetStateForOthers(cardview6);



            }});



        cardview7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview7_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout7,new AutoTransition());
                textview7_details.setVisibility(v);

                resetStateForOthers(cardview7);



            }});



        cardview8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview8_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout8,new AutoTransition());
                textview8_details.setVisibility(v);

                resetStateForOthers(cardview8);



            }});




        cardview9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (textview9_details.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(linearLayout9,new AutoTransition());
                textview9_details.setVisibility(v);

                resetStateForOthers(cardview9);



            }});







        return v;

    }   //onCreate end



































    @SuppressLint("NonConstantResourceId")
    private void resetStateForOthers(View v){

        switch (v.getId()){

            case R.id.cardview1:
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview2:
                textview1_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview3:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview4:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview5:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview6:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview7:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;
            case R.id.cardview8:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);
                break;

            case R.id.cardview9:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                break;
            default:
                textview1_details.setVisibility(View.GONE);
                textview2_details.setVisibility(View.GONE);
                textview3_details.setVisibility(View.GONE);
                textview4_details.setVisibility(View.GONE);
                textview5_details.setVisibility(View.GONE);
                textview7_details.setVisibility(View.GONE);
                textview6_details.setVisibility(View.GONE);
                textview8_details.setVisibility(View.GONE);
                textview9_details.setVisibility(View.GONE);

        }
    }








public void  cardViewTransitionInit(){

     //transition to the lineair layouts to take effects
    cardview1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview5.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview6.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview7.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview8.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    cardview9.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
}




public void LineairLayoutTransitionInit(){

    //transition to the lineair layouts to take effects
    linearLayout1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout5.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout6.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout7.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout8.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    linearLayout9.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


}




private void viewsInit(View v){

    //cardviews hooks (onclick listener function)
    cardview1 = v.findViewById(R.id.cardview1);
    cardview2 = v.findViewById(R.id.cardview2);
    cardview3 = v.findViewById(R.id.cardview3);
    cardview4 = v.findViewById(R.id.cardview4);
    cardview5 = v.findViewById(R.id.cardview5);
    cardview6 = v.findViewById(R.id.cardview6);
    cardview7 = v.findViewById(R.id.cardview7);
    cardview8 = v.findViewById(R.id.cardview8);
    cardview9 = v.findViewById(R.id.cardview9);

    //text viewd hooks
    textview1_details = v.findViewById(R.id.textview1_details);
    textview2_details = v.findViewById(R.id.textview2_details);
    textview3_details = v.findViewById(R.id.textview3_details);
    textview4_details = v.findViewById(R.id.textview4_details);
    textview5_details = v.findViewById(R.id.textview5_details);
    textview6_details = v.findViewById(R.id.textview6_details);
    textview7_details = v.findViewById(R.id.textview7_details);
    textview8_details = v.findViewById(R.id.textview8_details);
    textview9_details = v.findViewById(R.id.textview9_details);



    //Lineair layouts hooks
    linearLayout1 = v.findViewById(R.id.linearLayout1);
    linearLayout2 = v.findViewById(R.id.linearLayout2);
    linearLayout3 = v.findViewById(R.id.linearLayout3);
    linearLayout4 = v.findViewById(R.id.linearLayout4);
    linearLayout5 = v.findViewById(R.id.linearLayout5);
    linearLayout6 = v.findViewById(R.id.linearLayout6);
    linearLayout7 = v.findViewById(R.id.linearLayout7);
    linearLayout8 = v.findViewById(R.id.linearLayout8);
    linearLayout9 = v.findViewById(R.id.linearLayout9);



}


}  //class end