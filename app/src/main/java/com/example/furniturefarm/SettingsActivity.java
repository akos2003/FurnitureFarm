package com.example.furniturefarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SettingsActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    CollectionReference mUsers;
    FirebaseUser user;
    EditText jelszoDel;
    EditText newUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        firestore = FirebaseFirestore.getInstance();
        mUsers = firestore.collection("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        jelszoDel = findViewById(R.id.jelszoDel);
        newUserName = findViewById(R.id.newUserName);

    }

    public void deleteUser(View view) {
        if (!user.isAnonymous()) {
            mUsers.whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(user.getEmail().toString(), jelszoDel.getText().toString());

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                doc.getReference().delete();
                                                user.delete();
                                                finish();
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        }else {
            Toast.makeText(SettingsActivity.this, "Regisztrálj az oldal használatához!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void updateUserName(View view) {
        if (!user.isAnonymous()) {
            mUsers.whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                doc.getReference()
                                        .update("username", newUserName.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SettingsActivity.this, "Sikeresen frissítve!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        } else {
            Toast.makeText(SettingsActivity.this, "Regisztrálj az oldal használatához!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void back(View view) {
        finish();
    }
}