package com.example.smartdrive.home.page.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.SharedPreferences;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartdrive.R;
import com.example.smartdrive.Signin;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

public class Settings extends Fragment {

    SwitchMaterial nightModeSwitch,notificationsSwitch,privateAccSwitch;

    //firebase firestore reference
    FirebaseFirestore db;

    //settings layout
    LinearLayout settingLayout;

    //load data estimated time
    private static final int LOAD_DATA_TIME = 300 ;

    RelativeLayout contactUs,documentsButton,logoutButton,changePassword,faqs,notificationButton,nightModeButton,privateAccButton,DeleteAccount;
    AppCompatButton profileButton;
    //initialise progress bar
    ProgressBar progressBar;

    //profile data
    TextView phoneNumber,userName;
    ShapeableImageView profilePicture;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.settings, container, false);

        profileButton = v.findViewById(R.id.profileButton);
        logoutButton = v.findViewById(R.id.logoutButton);
        contactUs = v.findViewById(R.id.contactUs);
        documentsButton = v.findViewById(R.id.documentsButton);
        changePassword = v.findViewById(R.id.changePassword);
        faqs = v.findViewById(R.id.faqs);
        notificationButton = v.findViewById(R.id.notificationButton);
        nightModeButton = v.findViewById(R.id.nightModeButton);
        privateAccButton = v.findViewById(R.id.privateAccButton);
        DeleteAccount = v.findViewById(R.id.DeleteAccount);


        //settingLayout hook
        settingLayout = v.findViewById(R.id.settingLayout);


        //switch compat views
        nightModeSwitch = v.findViewById(R.id.nightModeSwitch);
        notificationsSwitch = v.findViewById(R.id.notificationsSwitch);
        privateAccSwitch = v.findViewById(R.id.privateAccSwitch);


        //create a new instance of progressbar
        progressBar = new ProgressBar(getContext());


        preferencesCheck();


        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactUs();
            }
        });


        setProfileCoordinates(v);


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openProfile();

            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });




        documentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDocument();
            }
        });



        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPasswordChange();

            }
        });


        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFaqs();

            }
        });



        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDeleteAccount();

            }
        });




        notificationsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(notificationsSwitch.isChecked()){

                    notificationFunction();


                    SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putBoolean("notifications", true);
                    editor1.apply();


                    SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putBoolean("isNotificationsEnabled", true);
                    editor2.apply();


                    snackBar("Notifications activees", 2500, R.color.baseGreen, R.drawable.ic_error);



                }else{
                    snackBar("Notifications desactivees", 2500, R.color.baseGreen, R.drawable.ic_error);

                    SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putBoolean("notifications", false);
                    editor1.apply();

                    SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putBoolean("isNotificationsEnabled", false);
                    editor2.apply();

                }


            }
        });


        nightModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(nightModeSwitch.isChecked()){

                    nightModeFunction();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", true);
                    editor.apply();

                    snackBar("Mode nuit active", 2500, R.color.baseGreen, R.drawable.ic_error);

                }else{

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", false);
                    editor.apply();

                    snackBar("Mode nuit desactive", 2500, R.color.baseGreen, R.drawable.ic_error);

                }


            }
        });



        privateAccSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(privateAccSwitch.isChecked()){


                    privateAccFunction();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("privateAcc", true);
                    editor.apply();

                    snackBar("Compte en mode prive", 2500, R.color.baseGreen, R.drawable.ic_error);

                }else{


                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("privateAcc", false);
                    editor.apply();

                    snackBar("Mode prive desactive", 2500, R.color.baseGreen, R.drawable.ic_error);

                }


            }
        });



        privateAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                privateAccSwitch.setChecked(!privateAccSwitch.isChecked());
            }
        });



        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationsSwitch.setChecked(!notificationsSwitch.isChecked());


            }
        });




        nightModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nightModeSwitch.setChecked(!nightModeSwitch.isChecked());


            }
        });





        return v;
    } //onCreate end














    private void privateAccFunction() {}

    private void nightModeFunction() {}

    private void notificationFunction() {}


    private void logout(){

        progressBar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.dismiss();
                FirebaseAuth.getInstance().signOut();

                // Google sign out
                GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                        .signOut();


                requireActivity().finish();


            }},2500);
    }



    private void openProfile() {

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new UserProfile(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    private void openDocument() {

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new UserDocument(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }



    private void openPasswordChange() {

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new ChangePassword(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }



    private void openContactUs() {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new ContactUs(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }




    private void openFaqs() {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new Faqs(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }



    private void openDeleteAccount() {

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.container,new DeleteAccount(),null)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }



    @SuppressLint("SetTextI18n")
    private void setProfileCoordinates(View v){

        progressBar.show();

        //profile data hook
        userName = v.findViewById(R.id.userName);
        phoneNumber = v.findViewById(R.id.phoneNumber);
        profilePicture = v.findViewById(R.id.profilePicture);

        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(getUserUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String UserName = documentSnapshot.getString("UserName");
                        String UserPhone = documentSnapshot.getString("UserPhone");
                        String UserPicture = documentSnapshot.getString("UserPicture");


                        userName.setText(UserName);
                        setThePhoneNumber(UserPhone,phoneNumber);



                        if (UserPicture == null){


                            //set Layout visivility
                            settingLayout.setVisibility(View.VISIBLE);

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
                                .into(profilePicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();

                                            }
                                        },LOAD_DATA_TIME);

                                        //set Layout visivility
                                        settingLayout.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();

                                            }
                                        },LOAD_DATA_TIME);

                                        //set Layout visivility
                                        settingLayout.setVisibility(View.VISIBLE);
                                        snackBar(e.getMessage(),1000,R.color.baseRed,R.drawable.ic_error);

                                    }

                                });






                    }

                });








    }



    private String getUserUid(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return   currentUser.getUid();
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






    //check the user phone exist opr not
    private void setThePhoneNumber(String phoneNo, TextView phoneNumber) {


        if (phoneNo != null) {

            phoneNumber.setText("+216 " + phoneNo);

        } else {

            phoneNumber.setText("no disponible");
            phoneNumber.setTextColor(getResources().getColor(R.color.baseRed));

        }

    }




    private void preferencesCheck(){

        //initialise sharedPreference state
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications",false));
        nightModeSwitch.setChecked(sharedPreferences.getBoolean("nightMode",false));
        privateAccSwitch.setChecked(sharedPreferences.getBoolean("privateAcc",false));
    }



} //frag class end