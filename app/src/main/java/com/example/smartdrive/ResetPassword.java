package com.example.smartdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {


    ConstraintLayout reinitialisationBtn;
    TextView reinitialisationTxt;
    EditText email;
    ProgressBar progressBar;

    //for logged in process we need  :
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    //re-send email params
    //resend cooldown
    private final int resendTime = 60;
    //true after 60 seconds
    private boolean resendEnabled = false;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);






        //initializing firebase variables :
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




        //other hooks
        reinitialisationBtn = findViewById(R.id.reinitialisationBtn);
        reinitialisationTxt = findViewById(R.id.reinitialisationTxt);
        //login fields hook
        email = findViewById(R.id.email_field);
        //progressBar hook
        progressBar = findViewById(R.id.progressbar);


        //animate layout
        reinitialisationBtn.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);



        //login the user
        reinitialisationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(resendEnabled) {

                    //error message here
                    snackBar("reessayez plus tard",1000,R.color.baseRed,R.drawable.ic_warning);



                }else{

                    //resend Email here...



                    progressBar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (!validateEmail()) {
                            progressBar.setVisibility(View.GONE);
                        }


                        //reinitialisation de mot de passe :
                        else {

                            progressBar.setVisibility(View.GONE);
                            forgotPassword();

                        }

                    }
                }, 600);


            }

            }
        });













    } //on Create end





    private void checkError(String Error) {

        boolean emailError = Error.contains("no user record");


            if (emailError) {

                email.requestFocus();
                email.setError(getString(R.string.email_non_enregistre));
                email.setTextColor(getResources().getColor(R.color.red));

            } else {

                email.setError(null);
                email.setTextColor(getResources().getColor(R.color.grey));
            }


    }












    private void forgotPassword() {

            String user_email = email.getText().toString();

            fAuth = FirebaseAuth.getInstance();
            fAuth.sendPasswordResetEmail(user_email)

                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                progressBar.setVisibility(View.GONE);
                                //peut remplacer with snackbar later
                                //Toast.makeText(ResetPassword.this, "verifier vos emails", Toast.LENGTH_LONG).show();

                                //button countdown here
                                startCountDownTimer();
                                snackBar("verifiez vos e-mails",4000,R.color.baseGreen,R.drawable.ic_info);

//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        finish();
//                                    }
//                                }, 2000);

                            } else {

                                //failed to login
                                progressBar.setVisibility(View.GONE);
                                checkError(Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));

                            }

                        }
                    });

    }


    public void returnBtn(View view) {backRotationAnimation();}























    private void backRotationAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.log_back).setRotation(60);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.log_back).setRotation(120);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ResetPassword.super.onBackPressed();
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


    //    **end validation functions***



    private void snackBar(String message,int time,int backgroundColor,int icon) {

    CookieBar.build(this)
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
            CookieBar.dismiss(ResetPassword.this);
        }
    })
                .show();
}



private void startCountDownTimer() {

    resendEnabled = true;
//    reinitialisationBtn.setBackgroundResource(R.drawable.login_btn_pressed);
//    ResourcesCompat.getDrawable(getResources(),R.drawable.signin_bg_red,null);
    reinitialisationBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.signin_bg_pressedv2));



    new CountDownTimer(resendTime * 1000, 1000){

        @Override
        public void onTick(long l) {
            reinitialisationTxt.setText("Reinitialiser ("+(l/1000)+")");

        }

        @Override
        public void onFinish() {

            resendEnabled = false;
//            reinitialisationBtn.setBackgroundResource(R.drawable.login_btn);
//            ResourcesCompat.getDrawable(getResources(),R.drawable.signin_bg_red,null);
            reinitialisationBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.signin_btn_selector));
            reinitialisationTxt.setText("Reinitialiser");



        }
    }.start();

    }


}  //class end