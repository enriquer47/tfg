package com.example.tfg.Controller;

import androidx.annotation.NonNull;
import com.example.tfg.AlumnoSimple;
import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Evento;
import com.example.tfg.Model.Predet;
import com.example.tfg.SendNotification;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

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
                evento.setCreador("Alumno");
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
    public void aniadirEventoConCategoria(String nombre, int estres,FirebaseUser user, String categoria) {
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("eventos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Evento evento = new Evento(nombre , estres);
                evento.setCreador("Alumno");
                evento.setCategoria(categoria);
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
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer estresActual=snapshot.child("estres").getValue(Integer.class);
                Alumno alumno=new Alumno(snapshot.child("nombre").getValue(String.class));
                alumno.setEstres(estresActual);
                alumno.addEstres(estres);
                if (estresActual<50 && alumno.getEstres()>=50 && snapshot.child("profesores").exists()) {
                    notifyPadre(user.getUid(), alumno);
                    for (DataSnapshot dataSnapshot : snapshot.child("profesores").getChildren()) {
                        String profesorID = dataSnapshot.child("referencia").getValue(String.class);
                        notifyProfesor(profesorID, alumno);
                    }
                } else if(estresActual<75 && alumno.getEstres()>=75 && snapshot.child("profesores").exists()){
                    notifyPadre(user.getUid(), alumno);
                    for (DataSnapshot dataSnapshot : snapshot.child("profesores").getChildren()) {
                        String profesorID = dataSnapshot.child("referencia").getValue(String.class);
                        notifyProfesor(profesorID, alumno);
                    }
                }
                snapshot.getRef().child("estres").setValue(alumno.getEstres());
                if (alumnoSimple.tipoVista == 1) {
                    alumnoSimple.actualizarVaso(alumno.getEstres());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void notifyProfesor(String profesorID,Alumno alumno){
        myRef.child("usuarios").child(profesorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("tokenNotification").getValue(String.class);
                String umbral=alumno.getEstres()>75?"del 75 porciento":"del 50 porciento";
                String mensaje="El alumno "+alumno.getNombre()+" ha superado el umbral de estres "+umbral+"";
                sendNotification(token,mensaje);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void notifyPadre(String padreID,Alumno alumno){
        myRef.child("usuarios").child(padreID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("tokenNotification").getValue(String.class);
                String umbral=alumno.getEstres()>75?"del 75 porciento":"del 50 porciento";
                String mensaje="Tu hijo "+alumno.getNombre()+" ha superado el umbral de estres "+umbral+"";
                sendNotification(token,mensaje);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendNotification(String token,String mensaje){
        SendNotification.pushNotification(alumnoSimple,token,"Estres",mensaje);
    }

    public void getEstres(FirebaseUser user){
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("estres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO
                //Daba null pointer exception al crear un hijo con eventos y borrarlo
                if(snapshot.exists()){
                    Integer estres=snapshot.getValue(Integer.class);
                    if(alumnoSimple.tipoVista==1) {
                        alumnoSimple.actualizarVaso(estres);
                    }
                }
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
