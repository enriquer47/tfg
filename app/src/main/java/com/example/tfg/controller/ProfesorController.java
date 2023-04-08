package com.example.tfg.controller;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfg.Model.Alumno;
import com.example.tfg.PrincipalProfesor;
import com.example.tfg.R;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfesorController {
    final FirebaseUser profesor;
    final DatabaseReference myRef;
    final PrincipalProfesor principalProfesor;

    public ProfesorController(PrincipalProfesor principalProfesor,FirebaseUser profesor) {
        this.myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.profesor = profesor;
        this.principalProfesor = principalProfesor;
    }

    public void obtenerAlumnos(){
        myRef.child("usuarios").child(profesor.getUid()).child("padres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                principalProfesor.alumnosLayout.removeAllViews();

                for (DataSnapshot padreSnap : snapshot.getChildren()) {
                    String padreID = padreSnap.getKey();
                    for (DataSnapshot hijoSnap : padreSnap.child("hijos").getChildren()){
                        String alumnoID = hijoSnap.child("referencia").getValue(String.class);
                        principalProfesor.mostrarAlumno(alumnoID,padreID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void visualizarAlumno(View view, String alumnoID, String padreID, ImageButton modoAlumno){
        myRef.child("usuarios").child(padreID).child("hijos").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Alumno alumno = snapshot.getValue(Alumno.class);
                int estres = alumno.getEstres();
                TextView nombreAlumno= view.findViewById(R.id.nombreMostarAlumnoTexto);
                nombreAlumno.setText(alumno.getNombre());
                if(estres<=66.6){
                    if(estres<=33.3){
                        modoAlumno.setBackgroundResource(R.drawable.ic_mostraralumnofeliz);
                    }else{
                        modoAlumno.setBackgroundResource(R.drawable.ic_mostraralumnomedio);
                    }
                } else {
                    modoAlumno.setBackgroundResource(R.drawable.ic_mostraralumnotriste);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void eliminarAlumno(String alumnoID, String padreID){
        myRef.child("usuarios").child(profesor.getUid()).child("padres").child(padreID).child("hijos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot hijoSnap : snapshot.getChildren()){
                    if(hijoSnap.child("referencia").getValue(String.class).equals(alumnoID)){
                        hijoSnap.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("usuarios").child(padreID).child("hijos").child(alumnoID).child("profesores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot profesorSnap : snapshot.getChildren()){
                    if(profesorSnap.child("referencia").getValue(String.class).equals(profesor.getUid())){
                        profesorSnap.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
