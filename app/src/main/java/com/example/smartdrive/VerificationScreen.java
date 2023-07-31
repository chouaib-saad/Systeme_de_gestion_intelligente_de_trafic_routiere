package com.example.smartdrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smartdrive.Utility.NetworkChangeListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerificationScreen extends AppCompatActivity {

    //connection verification class
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();



    //get the firestore reference and other stuff..
    FirebaseFirestore db ;

    //reference to the root of the Firebase Storage
    FirebaseStorage storage ;
    StorageReference storageRef ;


    //get the document ref
    DocumentReference userRef ;

    //user name
    public static String userName;
    private String description_verif;
    private String profession_verif;


    //camera needed variables
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public  boolean photoChanged = false;


    //for image
    private static final int PICK_IMAGE_REQUEST=1;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    public String photoUrl;


    //verification button
    ConstraintLayout verification_btn;

    //pick photo buttons
    ImageView take_btn,upload_btn;

    //progressBar
    ProgressBar progressBar;


    //verification fields
    EditText description_field;


    //dropDownMenu for proffesion
    AutoCompleteTextView profession_dropmenu;

    //dropDownMenu
    TextInputLayout profession;

    //done marks
    ImageView done_upload,done_take;
    public static boolean from_camera= false;
    public static boolean from_galerry = false;




    @Override
    public void onBackPressed() {

        //the onBack is not permitted here..
    }

    @Override
    protected void onResume() {
        super.onResume();
        //accessLevelType hook
        profession_dropmenu = findViewById(R.id.profession_dropmenu);
        //dropDownMenu adapter for userAccessLevel
        String[] profession_list = getResources().getStringArray(R.array.profession_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, profession_list);
        profession_dropmenu.setAdapter(arrayAdapter);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_screen);


        //maybe firebase initialisation later..
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        //get the document ref
        userRef = db.collection("Users").document(getUserUid());


        //signup_btn hook
        verification_btn = findViewById(R.id.verification_btn);

        //hook fields
        description_field = findViewById(R.id.description_field);

        //progressBar hook
        progressBar = findViewById(R.id.progressbar);

        //dropDownMenu hook
        profession = findViewById(R.id.profession);

        //done marks hooks
        done_take = findViewById(R.id.done_take);
        done_upload = findViewById(R.id.done_upload);

        //photo pick hooks
        take_btn = findViewById(R.id.take_btn);
        upload_btn = findViewById(R.id.upload_btn);



        //animate layout
        verification_btn.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);





        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickImageFromGallery();
                    from_galerry = true;
                    from_camera = false;
                }

            }
        });



        take_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickImageFromCamera();
                    from_camera = true;
                    from_galerry = false;

                }
            }

        });









        //i need this String function later :
        //getProfession()


        verification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);


                if (!validatePhotoSelected() | !validateDescription() | !validateProfession()) {

                    progressBar.setVisibility(View.GONE);


                }else {


                    //send verification data to firebase..



                    //upload the photo and url here

                    //the URI of the image file
                    Uri fileUri = mImageUri;
                        FirebaseFirestore fStore;
                        DocumentReference userRef = db.collection("Users").document(getUserUid());
                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){


                                                                //data
                                                                userName = documentSnapshot.getString("UserName");
                                                                profession_verif = getProfession();
                                                                description_verif = getDescription();




                                                                //StorageReference imageRef = storageRef.child("userVerificationDocs/" + userId + ".jpg");
                                                                String userUidPath = getUserUid() +"/";
                                                                String userFolder = userName.replace(" ","_")+"/";

                                                                //el mochkla mel data base name  mch mel champ (fetch name from db -solved)
                                                                StorageReference imageRef = storageRef.child("usersDocs/" + userUidPath + userFolder + "verificationPhoto.jpg");

                                                                imageRef.putFile(fileUri)
                                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //                                    Log.d("TAG", "Image uploaded successfully");
                                //                                    Toast.makeText(VerificationScreen.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();





                                                                                //add a new document to the "VerificationData" collection with the verification documents


                                                                                // Get the download URL of the image and add it to the user document
                                                                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri) {

                                                                                                        //Log.d("TAG", "Image URL added to user document");




                                                                                                        Map<String,Object> verifyMap = new HashMap<>();
                                                                                                        verifyMap.put("profession_verif",profession_verif);
                                                                                                        verifyMap.put("description_verif",description_verif);
                                                                                                        verifyMap.put("image_verif",uri.toString());

                                                                                                        //make the progressbar invisible
                                                                                                        progressBar.setVisibility(View.GONE);


                                                                                                        //create a new collection within the user document called "VerificationData"
                                                                                                        //save verification data to the user doc

//                                                                                        CollectionReference verifRef = userRef.collection("VerificationData");
                                                                                        DocumentReference verifRef = userRef.collection("VerificationData").document(getUserUid());
                                                                                        verifRef.set(verifyMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                                Log.d("verif_data", "les donnes de verification sont ajoute avec succes");

                                                                                                //almost done
                                                                                                //signout the user
                                                                                                FirebaseAuth.getInstance().signOut();
                                                                                                //open the done screen
                                                                                                startActivity(new Intent(getApplicationContext(),DoneScreen.class));
                                                                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                                                VerificationScreen.super.finish();


                                                                                            }
                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {

                                                                                                                Log.d("verif_data", "erreur d'ajout de donnes de verification : "+e.getMessage());


                                                                                                            }
                                                                                                        });

















                                                                                                        //save verification data to the user doc
//                                                                                                        verifRef.add(verifyMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                                                                            @Override
//                                                                                                            public void onSuccess(DocumentReference documentReference) {
//
//                                                                                                                Log.d("verif_data", "les donnes de verification sont ajoute avec succes");
//
//                                                                                                                //almost done
//                                                                                                                //signout the user
//                                                                                                                FirebaseAuth.getInstance().signOut();
//                                                                                                                //open the done screen
//                                                                                                                startActivity(new Intent(getApplicationContext(),DoneScreen.class));
//                                                                                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                                                                                                                VerificationScreen.super.finish();
//
//                                                                                                            }
//                                                                                                        }).addOnFailureListener(new OnFailureListener() {
//                                                                                                            @Override
//                                                                                                            public void onFailure(@NonNull Exception e) {
//
//                                                                                                                Log.d("verif_data", "erreur d'ajout de donnes de verification : "+e.getMessage());
//
//
////                                                                                                            }
//                                                                                                        });


                                                                                    }
                                                                                });



                                                                            }
                                                                        })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("image", "erreur d'ajout d'image");
                                                                            progressBar.setVisibility(View.GONE);


                                                                        }
                                                                    });


                            }else{
                                Log.d("user", "l'utilisateur n'est pas existe");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("fetch_user_data", "Erreur d'obtention des donnes");
                        }
                    });








                }  //verification fields


            }  //button end
        });












    } //end onCreate













    //drom gallery function
    private void pickImageFromGallery() {

        ImagePicker.with(VerificationScreen.this)
                .galleryOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }


    //drom Camera function
    private void pickImageFromCamera() {

        ImagePicker.with(VerificationScreen.this)
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
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return res1 && res2;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        photoChanged = false;
        if(resultCode==RESULT_OK && data != null && data.getData() != null){

            //URI of the image
            mImageUri = data.getData();


            //set the image on the ImageView => remplace with "done mark"
//            image.setImageURI(mImageUri);
            photoChanged = true;


            if(from_galerry){
                done_upload.setVisibility(View.VISIBLE);
                done_take.setVisibility(View.INVISIBLE);
            }
            else if(from_camera){
                done_take.setVisibility(View.VISIBLE);
                done_upload.setVisibility(View.INVISIBLE);
            }

        }else{
            photoChanged = false;
        }
    }




    private boolean isphotoChanged() {
        return photoChanged;
    }










        //    **validation functions***
    private Boolean validateDescription() {
        String val = description_field.getText().toString();
        /*en peut utiliser aussi val.equals("")
        pour verifier que le champ est vide*/
        if (val.trim().isEmpty()) {
            description_field.requestFocus();
            description_field.setError("le champ ne peut pas être vide");
            return false;
        } else if (val.trim().length() <= 10) {
            description_field.requestFocus();
            description_field.setError("la description ne suffit pas");
            description_field.setTextColor(getResources().getColor(R.color.red));
            return false;
        } else {
            description_field.setError(null);
            description_field.setTextColor(getResources().getColor(R.color.grey));
            return true;

        }
    }


    private Boolean validateProfession() {
        String userType = getProfession();
        if (userType.trim().isEmpty()) {
            profession.requestFocus();
            profession.setError(getString(R.string.veuillez_s_lectionner_une_profession));
            return false;
        } else {
            profession.setError(null);
            profession.setErrorEnabled(false);
            return true;
        }

    }




    private Boolean validatePhotoSelected() {
        if(!isphotoChanged()){

            //Toast.makeText(getApplicationContext(), "Veuillez sélectionner une image..", Toast.LENGTH_SHORT).show();
            snackBar(getString(R.string.veuillez_s_lectionner_une_photo));
            return false;
        }else{
            return true;
        }
    }

//    **end validation functions***







    //get the user access level from the dropdown menu
    private String getProfession() {
        return ((AutoCompleteTextView) Objects.requireNonNull(profession.getEditText())).getText().toString();
    }


    private String getDescription() {
        return description_field.getText().toString();
    }








    private void snackBar(String message) {

        CookieBar.build(this)
                .setTitle("Avertissement")
                .setMessage(message)
                .setDuration(1500)
                .setTitleColor(R.color.special_white)
                .setBackgroundColor(R.color.baseRed)
                .setCookiePosition(CookieBar.BOTTOM)
                .setIcon(R.drawable.ic_warning)
                .setAction("D'accord", new OnActionClickListener() {
                    @Override
                    public void onClick() {
                        CookieBar.dismiss(VerificationScreen.this);
                    }
                })
                .show();
    }



    private String getUserUid(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return   currentUser.getUid();
    }



} //end class