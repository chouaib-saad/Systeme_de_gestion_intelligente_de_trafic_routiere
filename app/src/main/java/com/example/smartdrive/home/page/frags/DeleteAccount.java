package com.example.smartdrive.home.page.frags;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdrive.R;
import com.example.smartdrive.Signin;
import com.example.smartdrive.Utility.ProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeleteAccount extends Fragment {


    ProgressBar customProgressbar;

    //Button
    ConstraintLayout deleteButton;

    //progressbar
    View supression_progressbar;


    //firebase fire auth
    FirebaseAuth firebaseAuth ;
    FirebaseUser currentUser ;

    //firebase firestore reference
    FirebaseFirestore db;




    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.delete_account, container, false);



        //delete button hook
        deleteButton = v.findViewById(R.id.deleteButton);
        //supression progressbar hook
        supression_progressbar = v.findViewById(R.id.supression_progressbar);

        customProgressbar = new ProgressBar(getContext());



        firebaseAuth = FirebaseAuth.getInstance();

        //get the current user from firebase fire auth
        currentUser = firebaseAuth.getCurrentUser();



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                supression_progressbar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        supression_progressbar.setVisibility(View.INVISIBLE);
                        confirmSupressionDialog();

                    }
                },1000);


            }
        });







        return v;


    }   //onCreateView end











    //setting dialog
    private void confirmSupressionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout_dialog = LayoutInflater.from(getContext()).inflate(R.layout.delete_account_alert, null);
        builder.setView(layout_dialog);

        ConstraintLayout annuler_btn = layout_dialog.findViewById(R.id.annuler_button);
        ConstraintLayout confirmer_btn = layout_dialog.findViewById(R.id.confirmer_button);


        //progressbar confirmer / annuler
        View confirmer_progress = layout_dialog.findViewById(R.id.progressbar1);
        View annuler_progress = layout_dialog.findViewById(R.id.progressbar2);

        //password edit text
        EditText password_field = layout_dialog.findViewById(R.id.password_field);



        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        //btn parametres
        confirmer_btn.setOnClickListener(view -> {

            confirmer_progress.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    confirmer_progress.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(!validatePassword(password_field)){
                                confirmer_progress.setVisibility(View.INVISIBLE);
                                return;
                            }else{
                                checkUserPassword(password_field,dialog);
                                confirmer_progress.setVisibility(View.INVISIBLE);
                            }



                        }},500);



                }},1500);


        });
        //btn annuler
        annuler_btn.setOnClickListener(view -> {
            annuler_progress.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    annuler_progress.setVisibility(View.INVISIBLE);
                    dialog.dismiss();



                }},500);

        });

        //show dialog
        dialog.show();
    }






    private void deleteUserDocument(AlertDialog Alertdialog){

        if (currentUser != null) {



            Alertdialog.dismiss();
            customProgressbar.show();



                                //***   delete user document    ***


                            // Account deletion successful
                            // Perform any additional actions or show a success message
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            WriteBatch batch = db.batch();

                            // Delete a document
                            batch.delete(db.collection("Users").document(getUserUid()));

                            batch.commit()
                                    .addOnSuccessListener(aVoid -> {

                                        deleteUserAccount(Alertdialog);

                                    })
                                    .addOnFailureListener(e -> {
                                        // Deletion failed
                                        // Display an error message or handle the failure
                                        snackBar("La suppression du compte a échoué : "+e.getMessage(),2000,R.color.baseRed,R.drawable.ic_error);
                                        customProgressbar.dismiss();

                                    });

        }       //check user not null


    }       //end user document



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





    //get the user Uid
    private String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return currentUser.getUid();
    }






    private void checkUserPassword(EditText password, AlertDialog dialog){


        //initialise the mAuth instance of the FirebaseAuth class
        firebaseAuth = FirebaseAuth.getInstance();


        String enteredPassword = password.getText().toString().trim();

        // Check if old password is correct
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()),enteredPassword);
        firebaseAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            dialog.dismiss();
                            customProgressbar.show();

                            //delete the user account
                            deleteUserDocument(dialog);

                        }else{

                            password.requestFocus();
                            password.setError("mot de passe incorrect");
                            password.setTextColor(getResources().getColor(R.color.red));
                        }


                    }

                });

    }       //check the user password end




    private Boolean validatePassword(EditText password) {
        String val = password.getText().toString();


        if (val.trim().isEmpty()) {
            password.requestFocus();
            password.setError("field can not be empty");
            return false;
        }

        else {
            password.setError(null);
            password.setTextColor(getResources().getColor(R.color.grey));
            return true;
        }

    }










    private void deleteUserFiles(AlertDialog alertDialog){


        //***   delete user pictures from storage    ***

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

                  String userName = "";

                // Use the retrieved username here
                // For example, you can store it in a variable or pass it to another method
                // Define the path to the folder you want to delete
                String userUidPath = getUserUid() + "/";
                String userFolder = userName.replace(" ", "_") + "/";
                String folderPath = "usersDocs/" + userUidPath + userFolder;


                // Create a StorageReference to the folder
                StorageReference folderRef = storageRef.child(folderPath);

                // Delete the folder and its contents
                folderRef.listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                // Delete each file in the folder
                                List<StorageReference> items = listResult.getItems();
                                for (StorageReference item : items) {
                                    item.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {



                                                            // File deleted successfully
                                                            deleteUserAccount(alertDialog);


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    // Error occurred while deleting the file
                                                    customProgressbar.dismiss();
                                                    snackBar("La suppression du compte a échoué : "+e.getMessage(),2000,R.color.baseRed,R.drawable.ic_warning);

                                                }
                                            });
                                }

                                // Delete the folder itself
                                folderRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Folder deleted successfully
                                                customProgressbar.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error occurred while deleting the folder
                                                customProgressbar.dismiss();
                                                snackBar("La suppression du compte a échoué : "+e.getMessage(),2000,R.color.baseRed,R.drawable.ic_error);



                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while listing the folder contents
                                customProgressbar.dismiss();
                                snackBar("La suppression du compte a échoué : "+e.getMessage(),2000,R.color.baseRed,R.drawable.ic_error);

                            }
                        });



    }





    private void deleteUserAccount(AlertDialog dialog){

        //***   delete user account from firebase authentication    ***

        currentUser.delete()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {



                        // File deletion successful
                        // Proceed with deleting the user account and Firestore data
                        snackBar("compte supprimé avec succès",2500,R.color.baseGreen,R.drawable.ic_warning);
                        customProgressbar.dismiss();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //logout the user before deletion
                                logout();
                                requireActivity().finish();
                                dialog.dismiss();
                            }
                        },3000);



                    } else {

                        // Account deletion failed
                        snackBar("La suppression du compte a échoué",2000,R.color.baseRed,R.drawable.ic_error);
                        customProgressbar.dismiss();

                    }
                });


    }       //end delete user account



    private void logout(){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Signin.class));

                // Google sign out
                GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                        .signOut();
    }






} // DeleteAccount class end