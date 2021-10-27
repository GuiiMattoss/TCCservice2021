package com.example.tccservice;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebade {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storageReference;

    public static String getidUsuario (){
        FirebaseAuth autenticaca = getFirebaseAutenticcao();
        return autenticaca.getCurrentUser().getUid();
    }

    public static DatabaseReference getFirebaseDatabase(){
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    public static FirebaseAuth getFirebaseAutenticcao(){
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getFirebaseStorage (){
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }
}
