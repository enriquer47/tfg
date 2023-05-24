package com.example.tfg.Controller;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Evento;
import com.example.tfg.PrincipalProfesor;
import com.example.tfg.R;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfesorController {
    final String linkDatabase= "https://afaniastfg-67ecd-default-rtdb.europe-west1.firebasedatabase.app/";
    final FirebaseUser profesor;
    final DatabaseReference myRef;
    final PrincipalProfesor principalProfesor;

    public ProfesorController(PrincipalProfesor principalProfesor,FirebaseUser profesor) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
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
                        resetearEstres(padreID, alumnoID);
                        principalProfesor.mostrarAlumno(alumnoID,padreID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void resetearEstres(String padre,String key) {
        myRef.child("usuarios").child(padre).child("hijos").child(key).child("eventos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot eventosSnap : snapshot.getChildren()) {
                        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");

                        String fecha = today.format(Calendar.getInstance().getTime());
                        //fecha+="xd";//HAY QUE BORRAR ESTA LINEA: para que falle y los meta al historico

                        if (!eventosSnap.child("fecha").getValue().toString().contains(fecha)) {

                            Evento evento = eventosSnap.getValue(Evento.class);
                            myRef.child("usuarios").child(padre).child("hijos").child(key).child("historico").child(eventosSnap.getKey()).setValue(evento);
                            myRef.child("usuarios").child(padre).child("hijos").child(key).child("eventos").child(eventosSnap.getKey()).removeValue();
                            ceroEstres( key, padre);
                        }
                    }
                }else {
                    ceroEstres( key, padre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void ceroEstres(String alumnoID, String padre){
        myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("estres").setValue(0);
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
