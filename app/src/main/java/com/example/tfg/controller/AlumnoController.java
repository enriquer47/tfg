package com.example.tfg.controller;

import androidx.annotation.NonNull;

import com.example.tfg.AlumnoSimple;
import com.example.tfg.Model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlumnoController {
    final Usuario padre;
    final DatabaseReference myRef;
    final AlumnoSimple alumnoSimple;

    public AlumnoController(AlumnoSimple alumnoSimple) {
        this.myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.padre=new Usuario();
        this.alumnoSimple=alumnoSimple;
    }
    public String getTypeAccount(String userId){
            myRef.child("usuarios").child(userId).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        padre.setTipoCuenta(snapshot.getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return this.padre.getTipoCuenta();
    }
    public void getDetallesAlumno(String alumnoID, FirebaseUser user){
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alumnoSimple.detallesUsuario.setText(snapshot.child("nombre").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
