package com.example.smartdrive.home.page.frags;

import android.animation.LayoutTransition;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdrive.R;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;


public class UserDocument extends Fragment {

    //verification fields
    TextView profession,profDesc;
    ImageView imageDoc;

    //visibility of layouts
    ConstraintLayout dataLayout,errorMessage;

    //progress bar initiamisation
    ProgressBar progressBar;

    //load data estimated time
    private static final int LOAD_DATA_TIME = 300;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_documents, container, false);

        //create a new instance of progressbar
        progressBar = new ProgressBar(getContext());




        //views hook :
        //data
        profession = v.findViewById(R.id.profession);
        profDesc = v.findViewById(R.id.profDesc);
        imageDoc = v.findViewById(R.id.imageDoc);
        //specefic layouts showing
        dataLayout = v.findViewById(R.id.dataLayout);
        errorMessage = v.findViewById(R.id.errorMessage);



        //transition to the lineair layouts to take effects
        dataLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        errorMessage.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);



        setProfileCoordinates();






        return v;





    } //on Create end



















    private void setProfileCoordinates() {

        progressBar.show();

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    DocumentReference userRef = fStore.collection("Users").document(getUserUid());
    CollectionReference verifRef = userRef.collection("VerificationData");

        verifRef.document(getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()) {

                //set layout visibility
                errorMessage.setVisibility(View.INVISIBLE);
                dataLayout.setVisibility(View.VISIBLE);


                String profession_verif = documentSnapshot.getString("profession_verif");
                String description_verif = documentSnapshot.getString("description_verif");
                String image_verif = documentSnapshot.getString("image_verif");


                //set the data to fields
                profession.setText(profession_verif);
                profDesc.setText(description_verif);



                if (image_verif == null) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();

                        }
                    }, LOAD_DATA_TIME);

                }

                Picasso.get()
                        .load(image_verif)
                        //.fit()
                        .centerCrop()
//                        .placeholder(R.drawable.custom_user_ic)
                        .error(R.drawable.ic_error)
                        .resize(300, 200)
                        .into(imageDoc, new Callback() {
                            @Override
                            public void onSuccess() {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.dismiss();

                                    }
                                }, LOAD_DATA_TIME);
                            }

                            @Override
                            public void onError(Exception e) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressBar.dismiss();

                                    }
                                }, LOAD_DATA_TIME);

                                snackBar(e.getMessage(), 1000, R.color.baseRed, R.drawable.ic_error);

                            }
                        });



            } else {

                //set layout visibility
                errorMessage.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.INVISIBLE);

                progressBar.dismiss();
                Log.d("verification data", "pas de donnes de verification trouve");

            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

            //set layout visibility
            errorMessage.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.INVISIBLE);

            progressBar.dismiss();
            snackBar(e.getMessage(), 1000, R.color.baseRed, R.drawable.ic_error);


        }
    });






} //setProfile data methode




    private String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return currentUser.getUid();
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






}   //class end