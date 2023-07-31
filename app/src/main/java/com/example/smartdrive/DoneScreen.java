package com.example.smartdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DoneScreen extends AppCompatActivity {

    ConstraintLayout doneBtn;


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        //do nothing..
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.done_screen);

        //done btn initialisation
        doneBtn = findViewById(R.id.done_btn);





        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SigninSignup.class));
                finish();
            }
        });


    }

    public void exit_btn(View view) {
        finishAffinity();
    }


}