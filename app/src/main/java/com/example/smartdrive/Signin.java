package com.example.smartdrive;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdrive.Utility.NetworkChangeListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Signin extends AppCompatActivity {

    //connection verification class
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    ConstraintLayout login_btn;
    EditText password, email;
    ProgressBar progressBar;
    private static final int DELAY = 1000;

    //checkbox
    CheckBox souviens_moi;

    //for logged in process we need  :
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // Declare the GoogleSignInClient variable as a class-level variable
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    //google signin button
    LinearLayout google_sigin_btn;

    //firebase firestore reference
    FirebaseFirestore db;



    //progress bar initialisation
    com.example.smartdrive.Utility.ProgressBar progressBarDialog;




    @Override
    public void onBackPressed() {
        backRotationAnimation();
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


    public void resetBtn(View view) {
        startActivity(new Intent(getApplicationContext(),ResetPassword.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void returnBtn(View view) {
        backRotationAnimation();
    }




    // on create begin //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);



        //initializing firebase variables :
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //checkbox hook
        souviens_moi = findViewById(R.id.souviens_moi);


        //other hooks
        login_btn = findViewById(R.id.login_btn);
        souviens_moi = findViewById(R.id.souviens_moi);

        //login fields hook
        password = findViewById(R.id.password_field);
        email = findViewById(R.id.email_field);

        //progressBar hook
        progressBar = findViewById(R.id.progressbar);



        //google signin hook
        google_sigin_btn = findViewById(R.id.google_sigin_btn);

        //progress bar initialisation
        progressBarDialog = new com.example.smartdrive.Utility.ProgressBar(Signin.this);




        //outlined tint color change
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {getResources().getColor(R.color.baseGreen),
                getResources().getColor(R.color.baseRed)};
        souviens_moi.setButtonTintList(new ColorStateList(states, colors));


        // Call the method to configure Google sign-in
        Configure_Google_Sign_In();

        //check remember me
        rememberMeCheck();


        //transition to the lineair layouts to take effects
        login_btn.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);



        //login the user
        login_btn.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {


                if (!validatePassword() | !validateEmail()) {

                    progressBar.setVisibility(View.GONE);


                    /* check the login credentials here */
                } else {




                    //login operation here :
                    progressBar.setVisibility(View.VISIBLE);
                    String user_email = email.getText().toString();
                    String user_password = password.getText().toString();

                    //check user data
                    fAuth.signInWithEmailAndPassword(user_email, user_password).addOnSuccessListener(authResult -> {

                        //success login
                        progressBar.setVisibility(View.GONE);
                        checkUserAccessLevel(Objects.requireNonNull(authResult.getUser()).getUid());


                        //save user informations
                        if (souviens_moi.isChecked()) {

                            //save state
                            SharedPreferences.Editor editorData = getSharedPreferences("user_credentials", Context.MODE_PRIVATE).edit();
                            editorData.putString("email", user_email);
                            editorData.apply();


                            SharedPreferences.Editor editorState = getSharedPreferences("remember_me", Context.MODE_PRIVATE).edit();
                            editorState.putBoolean("isChecked",true);
                            editorState.apply();

                        } else {

                            SharedPreferences.Editor editorData = getSharedPreferences("user_credentials", Context.MODE_PRIVATE).edit();
                            editorData.putString("email", "");
                            editorData.apply();

                            SharedPreferences.Editor editorState = getSharedPreferences("remember_me", Context.MODE_PRIVATE).edit();
                            editorState.putBoolean("isChecked",false);
                            editorState.apply();

                        }  //checkbox end


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //failed to login
                            progressBar.setVisibility(View.GONE);
                            checkError(Objects.requireNonNull(e.getMessage()));

                        }
                    });


                }


            }, DELAY);


        });







        //google signin button
        google_sigin_btn.setOnClickListener(view -> {

            progressBarDialog.show();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });






    }  //onCreate end


    private void checkUserAccessLevel(String uid) {

        DocumentReference df = fStore.collection("Users").document(uid);
        //extract the data from the user document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //this snapshot contains the partucular document data for this user (not necessary)
                Log.d("TAG", "onSuccess: " + documentSnapshot.getId());

                //identify the user access level

                if (Boolean.TRUE.equals(documentSnapshot.getBoolean("REGULAR"))) {

                    //welcome message :
                    Toast.makeText(Signin.this, "welcome..", Toast.LENGTH_SHORT).show();

                    //user is admin
                    startActivity(new Intent(getApplicationContext(), UserScreen.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    Signin.super.finish();



                }else if (Boolean.TRUE.equals(documentSnapshot.getBoolean("VIP"))) {

                        //welcome message :
                        Toast.makeText(Signin.this, "welcome..", Toast.LENGTH_SHORT).show();

                        //user is admin
                        startActivity(new Intent(getApplicationContext(), VipScreen.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        Signin.super.finish();


                } else if(Boolean.TRUE.equals(documentSnapshot.getBoolean("VerifState"))){

                    //Toast.makeText(Signin.this, "Nous vous contacterons tres bientot", Toast.LENGTH_SHORT).show();
                    snackBar("nous vous contacterons tres bientot",4000,R.color.baseGreen,R.drawable.ic_info);

                    //signout the user
                    FirebaseAuth.getInstance().signOut();

                }
            }
        });

    }










//    **validation functions***

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
        } else {
            password.setError(null);
            password.setTextColor(getResources().getColor(R.color.grey));
            return true;
        }

    }

//    **end validation functions***


    private void backRotationAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.log_back).setRotation(60);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Signin.super.onBackPressed();
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




    private void checkError(String Error) {

        boolean passwordError = Error.contains("password");
        boolean emailError = Error.contains("record");
        boolean blocked = Error.contains("blocked");

        if (blocked) {

            //peut remplacer with snackbar later
            //Toast.makeText(this, "account temporarily blocked", Toast.LENGTH_SHORT).show();
            snackBar(getString(R.string.compte_temporairement_bloqu),5000,R.color.baseRed,R.drawable.ic_warning);


        } else {

            if (passwordError) {

                password.requestFocus();
                password.setError(getString(R.string.mot_de_passe_incorrect));
                password.setTextColor(getResources().getColor(R.color.red));

            } else {

                password.setError(null);
                password.setTextColor(getResources().getColor(R.color.grey));
            }

            if (emailError) {

                email.requestFocus();
                email.setError(getString(R.string.email_non_enregistr));
                email.setTextColor(getResources().getColor(R.color.red));

            } else {

                email.setError(null);
                email.setTextColor(getResources().getColor(R.color.grey));
            }

        }
    }


        //check buttons states
    public void GetUserCredentials() {


        //initialise sharedPreference state
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        String  emailTXT =   sharedPreferences.getString("email","");

        if (!emailTXT.isEmpty()) {
            ((EditText) findViewById(R.id.email_field)).setText(emailTXT);
        }

}


    private void rememberMeCheck(){

        //initialise sharedPreference state
        SharedPreferences sharedPreferences = getSharedPreferences("remember_me", Context.MODE_PRIVATE);

        souviens_moi.setChecked(sharedPreferences.getBoolean("isChecked",false));
        if(sharedPreferences.getBoolean("isChecked",false)){

            GetUserCredentials();

        }
    }





    private void snackBar(String message,int time,int backgroundColor,int icon) {

        CookieBar.build(this)
                .setTitle(R.string.avertissement)
                .setMessage(message)
                .setDuration(time)
                .setTitleColor(R.color.special_white)
                .setBackgroundColor(backgroundColor)
                .setCookiePosition(CookieBar.BOTTOM)
                .setIcon(icon)
                .setAction(R.string.d_accord, () -> CookieBar.dismiss(Signin.this))
                .show();
    }







                    /***    sign in with google section ***/


    private void Configure_Google_Sign_In(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();


                            //user sigin with google infos
                            assert user != null;
                            String user_name = user.getDisplayName() ;
                            String user_email = user.getEmail() ;
                            String user_phone = user.getPhoneNumber() ;
                            String user_profilePic = Objects.requireNonNull(user.getPhotoUrl()).toString() ;

                            if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {

                                // ** new  user **



                                        //create  the new user profile
                                        String currentUserUid  = user.getUid();

                                        //save the user data on the current user Uid
                                        DocumentReference df = fStore.collection("Users").document(currentUserUid);
                                        Map<String,Object> userInfo = new HashMap<>();
                                        userInfo.put("UserName",user_name);
                                        userInfo.put("UserEmail",user_email);
                                        userInfo.put("UserPhone",user_phone);
                                        userInfo.put("REGULAR",true);
                                        userInfo.put("VIP",false);
                                        userInfo.put("VerifState",false);
                                        userInfo.put("UserPicture",user_profilePic);


                                        df.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


//                                                snackBar("new user",3000,R.color.baseGreen,R.drawable.ic_error);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        progressBarDialog.dismiss();
                                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                startActivity(new Intent(getApplicationContext(),UserScreen.class));
                                                Signin.super.finish();

                                            }
                                        },3000);


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                snackBar("erreur : "+e.getMessage(),3000,R.color.baseRed,R.drawable.ic_error);
                                                progressBarDialog.dismiss();


                                            }
                                        });










                            } else {

                                // ** already registred user **

//                                snackBar(getString(R.string.already_registred),3000,R.color.baseGreen,R.drawable.ic_error);

                                // User already exists, navigate to appropriate screen
                                new Handler().postDelayed(() -> {





                                    //database initialisation
                                    db = FirebaseFirestore.getInstance();

                                    db.collection("Users")
                                            .document(getUserUid())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Boolean UserVipState = documentSnapshot.getBoolean("VIP");

                                                    progressBarDialog.dismiss();
                                                    redirectToHomePage(UserVipState);

                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    progressBarDialog.dismiss();
                                                    snackBar("erreur : "+e.getMessage(),3000,R.color.baseRed,R.drawable.ic_warning);


                                                }
                                            });










                                },3000);

                            }


                        } else {
                            progressBarDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            snackBar("erreur : "+ Objects.requireNonNull(task.getException()).getMessage(),3000,R.color.baseRed,R.drawable.ic_warning);
                        }
                    }
                });
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {


                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                progressBarDialog.dismiss();
                // Google Sign In failed, update UI appropriately
                snackBar("erreur : error on activity result"+e.getMessage(),3000,R.color.baseRed,R.drawable.ic_warning);
            }
        }
    }






    private  void redirectToHomePage(Boolean isVip){
        if(isVip){
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getApplicationContext(),VipScreen.class));
            Signin.super.finish();
        }else{
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getApplicationContext(),UserScreen.class));
            Signin.super.finish();
        }
    }




    /***    sign in with google section ***/





    //getUser Uid
    private String getUserUid(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return   currentUser.getUid();
    }



}  //class end