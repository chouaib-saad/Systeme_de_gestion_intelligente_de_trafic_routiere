package com.example.smartdrive.home.page.frags;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdrive.R;
import com.example.smartdrive.Signin;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.Objects;

public class ChangePassword extends Fragment {

    //progressbar hook
    ProgressBar progressBar;


    //progressBar
    android.widget.ProgressBar progressBarButton;

    //firebase Auth initialising
    private FirebaseAuth mAuth;

    //fields
    EditText ancien_password_field,nouveau_password_field,retaper_password_field;
    ConstraintLayout modifyButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.change_password, container, false);


        progressBar = new ProgressBar(getContext());


        //buttons hook
        modifyButton = v.findViewById(R.id.modifyButton);

        //progressBar hook
        progressBarButton = v.findViewById(R.id.progressbar);

        //fields hooks
        ancien_password_field = v.findViewById(R.id.ancien_password_field);
        nouveau_password_field = v.findViewById(R.id.nouveau_password_field);
        retaper_password_field = v.findViewById(R.id.retaper_password_field);



        //animate layout
        modifyButton.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set the progressBarButton visible
                progressBarButton.setVisibility(View.VISIBLE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (!validateOldPassword() || !validatePassword() || !validateRetypePassword()) {
                            progressBarButton.setVisibility(View.GONE);
                            return;

                        } else {

                            modifyPassword();

                        }

                    }


                }, 1000);


            }



        });





        return v;


    }    //OnCreate End



















    //modify password function
    private void modifyPassword(){


        //initialise the mAuth instance of the FirebaseAuth class
        mAuth = FirebaseAuth.getInstance();



                String oldPassword = ancien_password_field.getText().toString().trim();
                String newPassword = nouveau_password_field.getText().toString().trim();

                // Check if old password is correct
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()), oldPassword);
                mAuth.getCurrentUser().reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {



                                    //ancien field normal state
                                    ancien_password_field.setError(null);
                                    ancien_password_field.setTextColor(getResources().getColor(R.color.grey));

                                    // Old password is correct, change to new password
                                        mAuth.getCurrentUser().updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            snackBar(getString(R.string.mot_de_passe_changee_avec_succes), 3000, R.color.baseGreen, R.drawable.ic_warning);


                                                            //set the progressBarButton visibility GONE
                                                            progressBarButton.setVisibility(View.GONE);


                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    progressBar.show();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            //logout here.. and re-sign in
                                                                            progressBar.dismiss();
                                                                            FirebaseAuth.getInstance().signOut();
                                                                            startActivity(new Intent(getContext(), Signin.class));
                                                                            requireActivity().finish();

                                                                        }
                                                                    },2500);
                                                                }
                                                                },3200);




//                                                            finish();
                                                        } else {

                                                            //set the progressBarButton visibility GONE
                                                            progressBarButton.setVisibility(View.GONE);

                                                            snackBar(getString(R.string.impossible_de_modifier_le_mot_de_passe), 2000, R.color.baseRed, R.drawable.ic_error);

                                                            //set the progressBarButton visibility GONE
                                                        }
                                                    }
                                                });

                                } else {

                                    //the old password is incorrect

                                    //set the progressBarButton visibility GONE

                                    //set the progressBarButton visibility GONE
                                    progressBarButton.setVisibility(View.GONE);

                                    ancien_password_field.requestFocus();
                                    ancien_password_field.setError(getString(R.string.l_ancien_mot_de_passe_est_incorrect));
                                    ancien_password_field.setTextColor(getResources().getColor(R.color.red));
                                }
                            }
                        });

    }





    private Boolean validateOldPassword(){
        String val = ancien_password_field.getText().toString();


        if (val.trim().isEmpty()) {
            ancien_password_field.requestFocus();
            ancien_password_field.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
            return false;

        } else {
            ancien_password_field.setError(null);
            ancien_password_field.setTextColor(getResources().getColor(R.color.grey));
            return true;
        }

    }




    private Boolean validatePassword() {
        String val = nouveau_password_field.getText().toString();


        if (val.trim().isEmpty()) {
            nouveau_password_field.requestFocus();
            nouveau_password_field.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
            return false;
        }
//        else if(val.trim().length()<=6){
//            password.requestFocus();
//            password.setError("password is to");
//            password.setTextColor(getResources().getColor(R.color.red));
//            return false;
//        }
        else if (val.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$") || val.trim().length() <= 7) {
            nouveau_password_field.requestFocus();
            nouveau_password_field.setTextColor(getResources().getColor(R.color.red));
            nouveau_password_field.setError(getString(R.string.le_mot_de_passe_est_trop_faible));
            return false;
        } else {
            nouveau_password_field.setError(null);
            nouveau_password_field.setTextColor(getResources().getColor(R.color.grey));
            return true;
        }

    }





    private Boolean validateRetypePassword() {

        String newPassword = nouveau_password_field.getText().toString().trim();
        String val = retaper_password_field.getText().toString().trim();

        if (val.trim().isEmpty()) {
            retaper_password_field.requestFocus();
            retaper_password_field.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
            return false;
        } else if (!retaper_password_field.getText().toString().equals(newPassword)) {
            retaper_password_field.requestFocus();
            retaper_password_field.setError(getString(R.string.les_mots_de_passe_ne_correspondent_pas));
            retaper_password_field.setTextColor(getResources().getColor(R.color.red));
            return false;
        } else {
            retaper_password_field.setError(null);
            retaper_password_field.setTextColor(getResources().getColor(R.color.grey));
            return true;
        }

    }



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



}       //class end