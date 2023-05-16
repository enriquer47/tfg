package com.example.tfg.Controller;




import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tfg.LoginActivity;
import com.example.tfg.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginController {
    final String linkDatabase= "https://afaniastfg-67ecd-default-rtdb.europe-west1.firebasedatabase.app/";
    final Usuario usuario;
    final DatabaseReference myRef;
    final LoginActivity loginActivity;

    public LoginController() {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
        this.usuario=new Usuario();
        this.loginActivity=new LoginActivity();
    }
    public LoginController(LoginActivity loginActivity) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
        this.usuario=new Usuario();
        this.loginActivity=loginActivity;
    }


    public void login(String userId){
           myRef.child("usuarios").child(userId).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       String tipoCuenta = snapshot.getValue(String.class);
                          loginActivity.userIntent(tipoCuenta);

                   }
               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
    }

    public void saveNewUser(FirebaseUser user, String email){
        this.usuario.setNombre(user.getDisplayName());
        this.usuario.setEmail(email);
        myRef.child("usuarios").child(user.getUid()).setValue(this.usuario);
        setToken(user);
    }
    public void setTipoCuenta(FirebaseUser user,String tipoCuenta){
        this.usuario.setTipoCuenta(tipoCuenta);
        myRef.child("usuarios").child(user.getUid()).child("tipoCuenta").setValue(tipoCuenta);
    }

    private void setToken(FirebaseUser user){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Error", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        myRef.child("usuarios").child(user.getUid()).child("tokenNotification").setValue(token);
                        // Log and toast
                        Log.d("Success", "The token was saved successfully");
                    }
                });
    }




}
