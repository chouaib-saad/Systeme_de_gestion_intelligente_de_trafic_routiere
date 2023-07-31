package com.example.smartdrive.home.page.frags;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdrive.DoneScreen;
import com.example.smartdrive.R;
import com.example.smartdrive.Utility.ProgressBar;
import com.example.smartdrive.VerificationScreen;
import com.example.smartdrive.VipScreen;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UserProfile extends Fragment {


    //get the firestore reference and other stuff..
    FirebaseFirestore db ;

    //reference to the root of the Firebase Storage
    FirebaseStorage storage ;
    StorageReference storageRef ;


    //get the document ref
    DocumentReference userRef ;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA_AND_STORAGE = 463;
    private static final int RESULT_OK = -1 ;

    //progress bar initialisation
    ProgressBar progressBar;

    //userProfil Layout
    ConstraintLayout userProfilLayout;

    //load data estimated time
    private static final int LOAD_DATA_TIME = 300;


    //camera needed variables
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    //for image
    private static final int PICK_IMAGE_REQUEST=1;
    private android.widget.ProgressBar mProgressBar;
    private Uri mImageUri;
    public String photoUrl;





    //profile data
    ShapeableImageView userPicture;
    TextView userName, userName2, profession, email, phoneNumber, userType, profileState;

    //editProfilePicture
    ImageView editProfilePicture, verifMark;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_profile, container, false);

        //maybe firebase initialisation later..
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        //create a new instance of progressbar
        progressBar = new ProgressBar(getContext());

        //edit Profile Picture hook
        editProfilePicture = v.findViewById(R.id.editProfilePicture);


        //userProfil Layout hook
        userProfilLayout = v.findViewById(R.id.userProfilLayout);

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //profile picture edit function here
                editProfilePicture();


            }
        });

        setProfileCoordinates(v);


        return v;


    } //onCreate End


    private void setProfileCoordinates(View v) {

        progressBar.show();


        //profile data fields hook
        userName = v.findViewById(R.id.userName);
        userName2 = v.findViewById(R.id.userName2);
        userPicture = v.findViewById(R.id.userPicture);
        profession = v.findViewById(R.id.profession);
        email = v.findViewById(R.id.email);
        phoneNumber = v.findViewById(R.id.phoneNumber);
        userType = v.findViewById(R.id.userType);


        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fStore.collection("Users").document(getUserUid());
        CollectionReference verifRef = userRef.collection("VerificationData");

        verifRef.document(getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String profession_verif = documentSnapshot.getString("profession_verif");
                    String description_verif = documentSnapshot.getString("description_verif");
                    String image_verif = documentSnapshot.getString("image_verif");




                    profession.setText(profession_verif);
//                    progressBar.dismiss();


                } else {

                    progressBar.dismiss();
                    profession.setText("citoyen");
                    Log.d("verification data", "pas de donnes de verification trouve");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.dismiss();
                Log.d("verification data", "Erreur : " + e.getMessage());
                userProfilLayout.setVisibility(View.VISIBLE);

            }
        });


        db.collection("Users")
                .document(getUserUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        String userNameFromDB = documentSnapshot.getString("UserName");
                        String phoneNumberFromDB = documentSnapshot.getString("UserPhone");
                        String userPictureFromDB = documentSnapshot.getString("UserPicture");
                        String emailFromDB = documentSnapshot.getString("UserEmail");
                        Boolean VIP = documentSnapshot.getBoolean("VIP");
                        Boolean VerifState = documentSnapshot.getBoolean("VerifState");


                        //set the data to fields
                        userName.setText(userNameFromDB);
                        userName2.setText(userNameFromDB);
                        setThePhoneNumber(phoneNumberFromDB,phoneNumber);
                        email.setText(emailFromDB);
                        assert VerifState != null;
                        setProfileState(v, VerifState);
                        assert VIP != null;
                        userType.setText(setUserType(VIP));



                        if (userPictureFromDB == null) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.dismiss();

                                }
                            }, LOAD_DATA_TIME);
                            userProfilLayout.setVisibility(View.VISIBLE);

                        }


                        //picasso function here
                        loadProfilPicture(userPictureFromDB);


                    }
                });


    } //setProfile data methode


    //get the user Uid
    private String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return currentUser.getUid();
    }


    private void editProfilePicture() {

        //sheap bottom bar
        showBottomDialog();

    }





    private String setUserType(Boolean VIP) {

        if (VIP) {

            return "utilisateur vip";

        } else {

            return "utilisateur normale";
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setProfileState(View v, Boolean VerifState) {

        verifMark = v.findViewById(R.id.verifMark);
        profileState = v.findViewById(R.id.profileState);


        if (VerifState) {
            profileState.setText("non verifier");

            verifMark.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
            verifMark.setColorFilter(getResources().getColor(R.color.red));



        } else {
            profileState.setText("verifier");

            verifMark.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
            verifMark.setColorFilter(getResources().getColor(R.color.green_settings));

        }

    }   //profile state end






    //bottom dialog choose picture
    public void showBottomDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //bind with the xml layout
        dialog.setContentView(R.layout.dialog_choose_image_source);



        //layaouts (LinearLayout and ImageView hooks ) hooks
        LinearLayout choosePicture = dialog.findViewById(R.id.choosePicture);
        LinearLayout takePicture = dialog.findViewById(R.id.takePicture);
        ImageView closeIcon = dialog.findViewById(R.id.closeIcon);



        //buttons click listener

        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //choose picture function
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickImageFromGallery();
                    dialog.dismiss();

                }


            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //take picture function
                if (!checkCameraPermission()) {
                    requestCameraPermission();

                } else {
                    pickImageFromCamera();
                    dialog.dismiss();
                }


            }
        });



        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }  //bottom dialog showing


























    //drom gallery function
    private void pickImageFromGallery() {

        ImagePicker.with(UserProfile.this)
                .galleryOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }



    //drom Camera function
    private void pickImageFromCamera() {

        ImagePicker.with(UserProfile.this)
                .cameraOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }


    private void requestCameraPermission() {

        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return res1 && res2;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && data != null && data.getData() != null) {

            //URI of the image
            mImageUri = data.getData();

            if( mImageUri != null){


                progressBar.show();


                //the URI of the image file
                Uri fileUri = mImageUri;
                FirebaseFirestore fStore;
                DocumentReference userRef = db.collection("Users").document(getUserUid());
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){


                            //data
                            String userName = documentSnapshot.getString("UserName");




                            //StorageReference imageRef = storageRef.child("userVerificationDocs/" + userId + ".jpg");
                            String userUidPath = getUserUid() +"/";
                            assert userName != null;
                            String userFolder = userName.replace(" ","_")+"/";


                            StorageReference imageRef = storageRef.child("usersDocs/" + userUidPath + userFolder + "profil_picture.jpg");

                            imageRef.putFile(fileUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Log.d("picture uploading", "Image uploaded successfully");


                                            // Get the download URL of the image and add it to the user document
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    //save the user data on the current user Uid
                                                    DocumentReference df = db.collection("Users").document(getUserUid());


                                                    Map<String,Object> profilePic = new HashMap<>();
                                                    profilePic.put("UserPicture",uri.toString());

                                                    //create a new collection within the user document called "VerificationData"
                                                    //save verification data to the user doc

                                                    df.set(profilePic, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {


                                                                    //on profile picture changed listener here and loading progbar or with picasso
                                                                    loadProfilPicture(uri.toString());
                                                                    snackBar("les donnes de verification sont ajoute avec succes", 1000, R.color.baseGreen, R.drawable.ic_error);



                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {


                                                                    snackBar("erreur lors de modification d'image", 1000, R.color.baseRed, R.drawable.ic_error);
                                                                    progressBar.dismiss();

                                                                }
                                                            });











                                                }  //image uploaded and get the image url successfully
                                            });



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d("image", "erreur d'ajout d'image");
                                            progressBar.dismiss();


                                        }
                                    });


                        }else{
                            Log.d("user", "l'utilisateur n'est pas existe");
                            progressBar.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("fetch_user_data", "Erreur d'obtention des donnes");
                        progressBar.dismiss();
                    }
                });







            }else{

                snackBar("Erreur lors de modification d'image", 3000, R.color.baseRed, R.drawable.ic_error);
                progressBar.dismiss();

            }


        }
    }





    private void loadProfilPicture(String profilPictureUrl){

        Picasso.get()
                .load(profilPictureUrl)
                //.fit()
                .centerCrop()
                .placeholder(R.drawable.default_picture)
                .error(R.drawable.ic_error)
                .resize(1600, 1600)
                .into(userPicture, new Callback() {

                    @Override
                    public void onSuccess() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.dismiss();

                            }
                        }, LOAD_DATA_TIME);

                        userProfilLayout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(Exception e) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.dismiss();

                            }
                        }, LOAD_DATA_TIME);

                        userProfilLayout.setVisibility(View.VISIBLE);

                        snackBar(e.getMessage(), 1000, R.color.baseRed, R.drawable.ic_error);

                    }
                });

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



    //check the user phone exist opr not
    private void setThePhoneNumber(String phoneNo, TextView phoneNumber){
        if(phoneNo != null){

            phoneNumber.setText("+216 " + phoneNo);

        }else{
            phoneNumber.setText("no disponible");
            phoneNumber.setTextColor(getResources().getColor(R.color.baseRed));

        }





    }





}   //class end