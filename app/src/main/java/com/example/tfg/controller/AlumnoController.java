package com.example.tfg.controller;

import androidx.annotation.NonNull;

import com.example.tfg.AlumnoSimple;
import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Evento;
import com.example.tfg.Model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlumnoController {
    final DatabaseReference myRef;
    final AlumnoSimple alumnoSimple;
    final String alumnoID;

    public AlumnoController(AlumnoSimple alumnoSimple, String alumnoID) {
        this.myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.alumnoSimple=alumnoSimple;
        this.alumnoID=alumnoID;
    }

    public void goBack(String userId){
        myRef.child("usuarios").child(userId).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String tipoCuenta = snapshot.getValue(String.class);
                    alumnoSimple.userIntent(tipoCuenta);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getDetallesAlumno(FirebaseUser user){
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

    public void aniadirEvento(String nombre, int estres,FirebaseUser user) {

        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("eventos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Evento evento = new Evento(nombre , estres);
                snapshot.getRef().push().setValue(nombre+": "+estres);
                updateEstres(user,estres);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateEstres(FirebaseUser user,int estres){
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int estresActual=snapshot.getValue(Integer.class);
                Alumno alumno=new Alumno();
                alumno.setEstres(estresActual);
                alumno.addEstres(estres);
                myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("estres").setValue(alumno.getEstres());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
