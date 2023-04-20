package com.example.tfg.Controller;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.tfg.ConfigSituacionesPredet;
import com.example.tfg.Model.Alumno;
import com.example.tfg.Model.Evento;
import com.example.tfg.Model.Predet;
import com.example.tfg.PrincipalPadre;
import com.example.tfg.R;
import com.example.tfg.VisualizarAlumno;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PadreController {
    final String linkDatabase= "https://afaniastfg-67ecd-default-rtdb.europe-west1.firebasedatabase.app/";
    final String padre;
    final DatabaseReference myRef;
    final PrincipalPadre principalPadre;
    final VisualizarAlumno visualizarAlumno;
    final ConfigSituacionesPredet visualizarPredet;


    public PadreController(PrincipalPadre principalPadre,String currentUser) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
        this.principalPadre=principalPadre;
        this.padre=currentUser;
        this.visualizarAlumno=null;
        this.visualizarPredet=null;
    }
    public PadreController(VisualizarAlumno visualizarAlumno,String currentUser) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
        this.visualizarAlumno=visualizarAlumno;
        this.padre=currentUser;
        this.principalPadre=null;
        this.visualizarPredet=null;
    }
    public PadreController(ConfigSituacionesPredet visualizarPredet, String currentUser) {
        this.myRef= FirebaseDatabase.getInstance(linkDatabase).getReference();
        this.visualizarPredet=visualizarPredet;
        this.padre=currentUser;
        this.principalPadre=null;
        this.visualizarAlumno=null;
    }

    public void obtenerHijos(){
        myRef.child("usuarios").child(padre).child("hijos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                principalPadre.hijosLayout.removeAllViews();

                for (DataSnapshot hijosSnap : snapshot.getChildren()) {
                    String nombre = hijosSnap.child("nombre").getValue().toString();
                    int estres = hijosSnap.child("estres").getValue(Integer.class);
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
    public void obtenerEventos(String alumnoID){

        myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("eventos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                visualizarAlumno.eventosLayout.removeAllViews();

                for (DataSnapshot eventosSnap : snapshot.getChildren()) {
                    Evento evento = eventosSnap.getValue(Evento.class);
                    visualizarAlumno.mostrarEvento(evento);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void obtenerPredetsPadre(){

        myRef.child("usuarios").child(padre).child("predef").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                visualizarPredet.predetsLayout.removeAllViews();

                for (DataSnapshot predetsSnap : snapshot.getChildren()) {
                    Predet predet = predetsSnap.getValue(Predet.class);
                    visualizarPredet.mostrarPredet(predet);
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

    public void borrarHijo(String idHijo){

        //Borrar hijo de la lista de profesores
        myRef.child("usuarios").child(padre).child("hijos").child(idHijo).child("profesores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot profesoresSnap : snapshot.getChildren()) {
                        String profesorID = profesoresSnap.child("referencia").getValue().toString();
                        myRef.child("usuarios").child(profesorID).child("padres").child(padre).child("hijos").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot hijoSnap : snapshot.getChildren()) {
                                    if(hijoSnap.child("referencia").getValue().toString().equals(idHijo)){
                                        hijoSnap.getRef().removeValue();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //borra hijo de la lista de hijos del padre
        myRef.child("usuarios").child(padre).child("hijos").child(idHijo).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void borrarEvento(String alumnoID,int estres,String idEvento){
        updateEstres(estres*-1,alumnoID);
        myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("eventos").child(idEvento).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i("borrarEvento", "borrado con exito");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Write failed
                Log.i("borrarEvento", "error al borrar");
            }
        });
    }


    public void aniadirHijo(String nombre){
        Alumno newHijo =new Alumno(nombre);
        String hijoID=myRef.child("usuarios").child(padre).child("hijos").push().getKey();
        myRef.child("usuarios").child(padre).child("hijos").child(hijoID).setValue(newHijo);
    }

    public void aniadirEvento(String nombre, int estres,String alumnoID) {
        if(!nombre.equals("")) {
            myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("eventos").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    updateEstres(estres,alumnoID);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{
            Toast.makeText(visualizarAlumno, "El nombre está vacío", Toast.LENGTH_LONG).show();
        }
    }

    public void getDetallesAlumno(String alumnoID){
        myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                visualizarAlumno.detallesUsuario.setText(snapshot.child("nombre").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void updateEstres(int estres,String alumnoID){
        myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer estresActual=snapshot.getValue(Integer.class);
                Alumno alumno=new Alumno();
                alumno.setEstres(estresActual);
                alumno.addEstres(estres);
                snapshot.getRef().setValue(alumno.getEstres());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void asignarProfesor(String profesorCorreo,ArrayList<String> alumnosID){
        //Comprobamos que hay hijos para asignar
        if (alumnosID.size()==0){
            Toast.makeText(principalPadre, "No hay hijos para asignar", Toast.LENGTH_LONG).show();
        }else{
            myRef.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean existe=false;
                    //comprobamos si existe un profesor con ese correo
                    for (DataSnapshot usuario : snapshot.getChildren()) {
                        if (profesorCorreo.equals(usuario.child("email").getValue(String.class))){
                            existe=true;
                            //eliminamos los alumnos que ya estan asignados por si el padre asigna
                            //varias veces al mismo hijo
                            ArrayList<String> aux= elminarRepetidos(alumnosID,usuario);
                            if (aux.isEmpty()){
                                Toast.makeText(principalPadre, "Todos tus hijos ya estan asignados", Toast.LENGTH_LONG).show();
                                return;
                            }else{
                                for (String alumnoID:aux){
                                    usuario.getRef().child("padres").child(padre).child("hijos").push().child("referencia").setValue(alumnoID);
                                    myRef.child("usuarios").child(padre).child("hijos").child(alumnoID).child("profesores").push().child("referencia").setValue(usuario.getKey());
                                }
                            }
                        }
                    }
                    if (!existe){
                        Toast.makeText(principalPadre, "No existe el profesor", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(principalPadre, "Profesor asignado", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

    public void goBack(String userId){
        myRef.child("usuarios").child(userId).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String tipoCuenta = snapshot.getValue(String.class);
                    if(visualizarAlumno!=null)
                        visualizarAlumno.userIntent(tipoCuenta);
                    else if(visualizarPredet!=null)
                        visualizarPredet.userIntent(tipoCuenta);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private ArrayList<String>  elminarRepetidos(ArrayList<String> lista, DataSnapshot snapshot){
        ArrayList<String> aux=new ArrayList<>();
        for (String alumnoID:lista){
            aux.add(alumnoID);
        }
        for(DataSnapshot hijo: snapshot.child("padres").child(padre).child("hijos").getChildren()){
            aux.remove(hijo.child("referencia").getValue(String.class));
        }
        return aux;
    }

}

