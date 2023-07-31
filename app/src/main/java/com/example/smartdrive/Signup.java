 package com.example.smartdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smartdrive.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

 public class Signup extends AppCompatActivity {

     //connection verification class
     NetworkChangeListener networkChangeListener = new NetworkChangeListener();

     //checkbox
     CheckBox agree_terms;
     ConstraintLayout signup_btn;

     //progressBar
     ProgressBar progressBar;

     //input fields
     EditText name, email, password, conf_password, phone;

     //dropDownMenu for accessLevelType
     AutoCompleteTextView accessLevelType;

     //dropDownMenu
     TextInputLayout dropDownMenu;

     //for registration process we need  :
     FirebaseAuth fAuth;
     FirebaseFirestore fStore;



     @Override
     public void onBackPressed() {
         backRotationAnimation();
     }

     @Override
     protected void onResume() {
         super.onResume();
         //accessLevelType hook
         accessLevelType = findViewById(R.id.accessLevelType);
         //dropDownMenu adapter for userAccessLevel
         String[] userAccessLevelOptions = getResources().getStringArray(R.array.userAccessLevelOptions);
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, userAccessLevelOptions);
         accessLevelType.setAdapter(arrayAdapter);
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
         super.onStop(); }

     @Override
     public void finish() {
         super.finish();
         overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
     }


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //initializing firebase variables :
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //signup_btn hook
        signup_btn = findViewById(R.id.signup_btn);
        //hook fields
        name = findViewById(R.id.name_field);
        email = findViewById(R.id.email_field);
        password = findViewById(R.id.password_field);
        conf_password = findViewById(R.id.retype_password_field);
        phone = findViewById(R.id.phone_field);
        //progressBar hook
        progressBar = findViewById(R.id.progressbar);

        //dropDownMenu hook
         dropDownMenu = findViewById(R.id.dropDownMenu);


         //test : maybe i need this later
//         ((AutoCompleteTextView)dropDownMenu.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
//             @Override
//             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                 String selectedValue = adapterView.getItemAtPosition(i).toString();
//                 Toast.makeText(Signup.this, selectedValue, Toast.LENGTH_SHORT).show();
//
//             }
//         });


        //agree termes button hook
        agree_terms = findViewById(R.id.agree_terms);
        //outlined tint color change
        int [][] states = {{android.R.attr.state_checked}, {}};
        int [] colors = {getResources().getColor(R.color.baseGreen),
                getResources().getColor(R.color.baseRed)};
        agree_terms.setButtonTintList( new ColorStateList(states,colors));



         //transition to the lineair layouts to take effects
         signup_btn.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);





        //signup the user
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {



                        if (!isTermesChecked() | !validateUserAccessLevel() | !validatephone() | !validateConfPassword() | !validatePassword() | !validateEmail() | !validateName()) {
                            progressBar.setVisibility(View.GONE);

                            //account creation
                        } else {



                                // start the user registration process :


                                //get all the values from the edit_text fields
                                String user_name = name.getText().toString();
                                String user_email = email.getText().toString();
                                String user_password = password.getText().toString();
                                String user_phone = phone.getText().toString();


                                //here data save operation..
                                fAuth.createUserWithEmailAndPassword(user_email,user_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        progressBar.setVisibility(View.GONE);

                                        //when the account  created succefully :
                                        Log.d("verif_data", "account created");



                                        //get the user document referance (unique ID) :
                                        //(this methode is available just on the onSuccess method)
                                        FirebaseUser user = fAuth.getCurrentUser();
                                        assert user != null;
                                        String currentUserUid  = user.getUid();

                                        //save the user data on the current user Uid
                                        DocumentReference df = fStore.collection("Users").document(currentUserUid);
                                        Map<String,Object> userInfo = new HashMap<>();
                                        userInfo.put("UserName",user_name);
                                        userInfo.put("UserEmail",user_email);
                                        userInfo.put("UserPhone",user_phone);

                                        //specify if the user is admin
                                        if(getUserAccessLevel().equals("Utilisateur Regulier")){

                                            userInfo.put("REGULAR",true);
                                            userInfo.put("VIP",false);
                                            userInfo.put("VerifState",false);
                                            //save the data
                                            //we can add onSuccessLestener to verify if the user data was created succesfully
                                            df.set(userInfo);
                                            //go to the main activity
                                            startActivity(new Intent(getApplicationContext(),UserScreen.class));
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            Signup.super.finish();


                                        }else if(getUserAccessLevel().equals("Utilisateur VIP")){

                                            //on va passee a l'etat de verification
                                            userInfo.put("REGULAR",false);
                                            userInfo.put("VIP",false);
                                            userInfo.put("VerifState",true);
                                            //save the data
                                            //we can add onSuccessLestener to verify if the user data was created succesfully
                                            df.set(userInfo);
                                            //go to the main activity (dashboard etc..)
                                            startActivity(new Intent(getApplicationContext(),VerificationScreen.class));
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            Signup.super.finish();


                                        }




                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //when the account was not created succefully :

                                        progressBar.setVisibility(View.GONE);
                                        checkFields(Objects.requireNonNull(e.getMessage()));





                                    }
                                });





                            }

                    }
                }, 2000);
            }
        });






    }  //onCreate end






















     public void returnBtn(View view) {backRotationAnimation();}


     private void backRotationAnimation() {
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 findViewById(R.id.log_back).setRotation(60);
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         Signup.super.onBackPressed();
                         findViewById(R.id.log_back).setRotation(120);
                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 findViewById(R.id.log_back).setRotation(180);
                                 new Handler().postDelayed(new Runnable() {
                                     @Override
                                     public void run() {
                                         findViewById(R.id.log_back).setRotation(180);
                                         new Handler().postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 findViewById(R.id.log_back).setRotation(180);
                                                 new Handler().postDelayed(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         findViewById(R.id.log_back).setRotation(180);
                                                         new Handler().postDelayed(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                             }
                                                         }, 100);
                                                     }
                                                 }, 60);
                                             }
                                         }, 60);
                                     }
                                 }, 60);
                             }
                         }, 60);
                     }
                 }, 60);
             }
         }, 60);
     }




     //    **validation functions***
     private Boolean validateName() {
         String val = name.getText().toString();
        /*en peut utiliser aussi val.equals("")
        pour verifier que le champ est vide*/
         if (val.trim().isEmpty()) {
             name.requestFocus();
             name.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
             return false;
         } else if (val.trim().length() <= 6) {
             name.requestFocus();
             name.setError(getString(R.string.le_nom_est_trop_court));
             name.setTextColor(getResources().getColor(R.color.red));
             return false;
         } else {
             name.setError(null);
             name.setTextColor(getResources().getColor(R.color.grey));
             return true;

         }
     }


     private Boolean validateEmail() {
         String val = email.getText().toString();
         /*regic expression: pour Respecter la forme de @ email*/
         String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        /*en peut utiliser aussi val.equals("")
        pour verifier que le champ est vide*/
         if (val.trim().isEmpty()) {
             email.requestFocus();
             email.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
             return false;
         } else if (!val.matches(emailPattern)) {
             email.requestFocus();
             email.setTextColor(getResources().getColor(R.color.red));
             email.setError(getString(R.string.l_email_n_est_pas_valide));
             return false;
         } else {
             email.setError(null);
             email.setTextColor(getResources().getColor(R.color.grey));
             return true;
         }

     }


     private Boolean validatePassword() {
         String val = password.getText().toString();
         //"^" : starting of the screen "$" : end of the screen
         //il faut que le mot de passe contient le suivant:

        /* a verifier
        String passwordVal ="^"+
                //"(?=.*[0-9])" +             //at least 1 digit
                //"(?=.*[a-z])" +             //at least 1 lower case letter
                //"(?=.*[A-Z])" +             //at least 1 upper case letter
                "(?=.*[a-zA-Z])"+           //any letter
                "(?=.*[@#$%^&+=])"+         //at least 1 special caracter
                //"(?=\\s+$)" +             //no white spaces 1
                "\\A\\w{4,20}\\z" +         //no white spaces 2
                ".{4,}" +                   //at least 4 characters
                "$";
        */
        /*en peut utiliser aussi val.equals("")
        pour verifier que le champ est vide*/
         if (val.trim().isEmpty()) {
             password.requestFocus();
             password.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
             return false;
         }
//        else if(val.trim().length()<=6){
//            password.requestFocus();
//            password.setError("password is to");
//            password.setTextColor(getResources().getColor(R.color.red));
//            return false;
//        }
         else if (val.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$") || val.trim().length() <= 7) {
             password.requestFocus();
             password.setTextColor(getResources().getColor(R.color.red));
             password.setError(getString(R.string.le_mot_de_passe_est_trop_faible));
             return false;
         } else {
             password.setError(null);
             password.setTextColor(getResources().getColor(R.color.grey));
             return true;
         }

     }


     private Boolean validateConfPassword() {
         String val = conf_password.getText().toString();
         if (val.trim().isEmpty()) {
             conf_password.requestFocus();
             conf_password.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
             return false;
         } else if (!conf_password.getText().toString().equals(password.getText().toString())) {
             conf_password.requestFocus();
             conf_password.setError(getString(R.string.les_mots_de_passe_ne_correspondent_pas));
             conf_password.setTextColor(getResources().getColor(R.color.red));
             return false;
         } else {
             conf_password.setError(null);
             conf_password.setTextColor(getResources().getColor(R.color.grey));
             return true;
         }

     }


     private Boolean validatephone() {
         String val = phone.getText().toString();
        /*en peut utiliser aussi val.equals("")
        pour verifier que le champ est vide*/
         if (val.trim().isEmpty()) {
             phone.requestFocus();
             phone.setError(getString(R.string.le_champ_ne_peut_pas_tre_vide));
             return false;
         } else if (val.trim().length() != 8) {
             phone.requestFocus();
             phone.setError(getString(R.string.num_ro_de_t_l_phone_invalide));
             phone.setTextColor(getResources().getColor(R.color.red));
             return false;
         } else if (!val.startsWith("2") && !val.startsWith("9") && !val.startsWith("5") && !val.startsWith("4")) {
             phone.requestFocus();
             phone.setError(getString(R.string.num_ro_de_t_l_phone_invalide));
             phone.setTextColor(getResources().getColor(R.color.red));
             return false;
         } else {
             phone.setError(null);
             phone.setTextColor(getResources().getColor(R.color.grey));
             return true;
         }

     }



     private Boolean validateUserAccessLevel() {
         String userType = getUserAccessLevel();
         if (userType.trim().isEmpty()) {
             dropDownMenu.requestFocus();
             dropDownMenu.setError(getString(R.string.choisir_un_type_s_il_vous_plait));
             return false;
         } else {
             dropDownMenu.setError(null);
             dropDownMenu.setErrorEnabled(false);
             return true;
         }

     }



     private Boolean isTermesChecked() {
         //other verifications
         if (!agree_terms.isChecked()) {
             agree_terms.setError(getString(R.string.veuillez_accepter_les_conditions_d_utilisation_et_la_politique_de_confidentialit));
             agree_terms.setTextColor(getResources().getColor(R.color.baseRed));
             return false;
         } else {
             agree_terms.setError(null);
             agree_terms.setTextColor(getResources().getColor(R.color.baseGreen));
             return true;
         }

     }

//    **end validation functions***



     //get the user access level from the dropdown menu
     private String getUserAccessLevel() {
         return ((AutoCompleteTextView) Objects.requireNonNull(dropDownMenu.getEditText())).getText().toString();
     }



     private void checkFields(String Error) {

         boolean emailError = Error.contains("already in use");

             if (emailError) {

                 email.requestFocus();
                 email.setError(getString(R.string.l_email_est_d_j_utilis));
                 email.setTextColor(getResources().getColor(R.color.red));

             } else {

                 email.setError(null);
                 email.setTextColor(getResources().getColor(R.color.grey));
                 Toast.makeText(this,R.string.un_erreur_ce_produit, Toast.LENGTH_SHORT).show();
             }


     }






 }  //class end