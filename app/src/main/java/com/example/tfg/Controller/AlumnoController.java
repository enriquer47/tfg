package com.example.tfg.Controller;

import androidx.annotation.NonNull;
import com.example.tfg.AlumnoSimple;
import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Evento;
import com.example.tfg.Model.Predet;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlumnoController {
    final String linkDatabase= "https://afaniastfg-67ecd-default-rtdb.europe-west1.firebasedatabase.app/";
    final DatabaseReference myRef;
    final AlumnoSimple alumnoSimple;
    final String alumnoID;

    public AlumnoController(AlumnoSimple alumnoSimple, String alumnoID) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
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
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = dateFormat.format(calendar.getTime());
                evento.setFecha(currentDate);
                String eventoID = snapshot.getRef().push().getKey();
                evento.setId(eventoID);
                snapshot.getRef().child(eventoID).setValue(evento);
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
                Integer estresActual=snapshot.getValue(Integer.class);
                Alumno alumno=new Alumno();

                alumno.setEstres(estresActual);
                alumno.addEstres(estres);
                myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("estres").setValue(alumno.getEstres());
                if (alumnoSimple.tipoVista == 1) {
                    alumnoSimple.actualizarVaso(alumno.getEstres());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void getEstres(FirebaseUser user){
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int estres=snapshot.getValue(Integer.class);
                alumnoSimple.actualizarVaso(estres);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    public void obtenerPredets(FirebaseUser user, OnPredetsLoadedListener listener){
        ArrayList<Predet> predetsFelices=new ArrayList<>();
        ArrayList<Predet> predetsTristes=new ArrayList<>();
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("predet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Predet predet = ds.getValue(Predet.class);
                        if(predet.getEstres()<=0){
                            predetsFelices.add(predet);
                        }else{
                            predetsTristes.add(predet);
                        }
                    }

                }else{
                    System.out.println("No hay predets");
                }
                listener.onPredetsLoaded(predetsFelices, predetsTristes);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public interface OnPredetsLoadedListener {
        void onPredetsLoaded(ArrayList<Predet> predetsFelices, ArrayList<Predet> predetsTristes);
    }
}
