package com.example.tfg.controller;



import android.content.Intent;

import androidx.annotation.NonNull;
import com.example.tfg.LoginActivity;
import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Usuario;
import com.example.tfg.PrincipalAlumno;
import com.example.tfg.PrincipalPadre;
import com.example.tfg.PrincipalProfesor;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginController {
    private Usuario usuario;
    private DatabaseReference myRef;

    public LoginController() {
        this.myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.usuario=new Usuario();
    }


    public String getTypeAccount(String userId){
       if (this.usuario.getTipoCuenta().equals("")){
           myRef.child("usuarios").child(userId).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       usuario.setTipoCuenta(snapshot.getValue(String.class));
                   }
               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
       }
       return this.usuario.getTipoCuenta();
    }

    public void saveNewUser(FirebaseUser user, String email){
        this.usuario.setNombre(user.getDisplayName());
        this.usuario.setApellidos("");
        this.usuario.setEmail(email);
        myRef.child("usuarios").child(user.getUid()).setValue(this.usuario);
    }
    public void setTipoCuenta(FirebaseUser user,String tipoCuenta){
        this.usuario.setTipoCuenta(tipoCuenta);
        myRef.child("usuarios").child(user.getUid()).child("tipoCuenta").setValue(tipoCuenta);
    }



}
