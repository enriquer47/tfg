package com.example.tfg.controller;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Usuario;
import com.example.tfg.PrincipalPadre;
import com.example.tfg.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PadreController {
    final FirebaseUser padre;
    final DatabaseReference myRef;
    final PrincipalPadre principalPadre;

    public PadreController(PrincipalPadre principalPadre,FirebaseUser currentUser) {
        this.myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.principalPadre=principalPadre;
        this.padre=currentUser;
    }
    public void obtenerHijos(){

        myRef.child("usuarios").child(padre.getUid()).child("hijos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                principalPadre.hijosLayout.removeAllViews();

                for (DataSnapshot hijosSnap : snapshot.getChildren()) {
                    String nombre = hijosSnap.child("nombre").getValue().toString();
                    Double estres = Double.parseDouble(hijosSnap.child("estres").getValue().toString());
                    Alumno hijo = new Alumno(nombre);
                    hijo.setEstres(estres);
                    principalPadre.mostrarHijo(hijosSnap.getKey(),hijo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void visualizarHijos(View view, ImageButton modoAlumno,Alumno pepe){
        double estres = pepe.getEstres();

        TextView nombreAlumno= view.findViewById(R.id.nombreMostarAlumnoTexto);
        String nombre = pepe.getNombre();
        nombreAlumno.setText(nombre);
        if(estres>=50){
            modoAlumno.setBackgroundResource(R.drawable.ic_mostraralumnotriste);
        } else {
            modoAlumno.setBackgroundResource(R.drawable.ic_mostraralumnofeliz);
        }
    }

    public void borrarHijo(String idHijo){
        myRef.child("usuarios").child(padre.getUid()).child("hijos").child(idHijo).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Log.i("borrarHijo", "borrado con exito");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.i("borrarHijo", "error al borrar");
                    }
                });
    }

    public void aniadirHijo(String nombre){
        Alumno newHijo =new Alumno(nombre);
        String hijoID=myRef.child("usuarios").child(padre.getUid()).child("hijos").push().getKey();
        myRef.child("usuarios").child(padre.getUid()).child("hijos").child(hijoID).setValue(newHijo);
    }

    private void setHijo(String hijoID) {

    }


}
