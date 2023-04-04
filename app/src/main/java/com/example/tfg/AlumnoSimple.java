package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tfg.Model.Evento;
import com.example.tfg.controller.AlumnoController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlumnoSimple extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;
    public TextView detallesUsuario;
    Button atras;
    ImageButton eventoFeliz,eventoTriste;
    AlertDialog nuevoEventoDialogo;
    int estilo=1;
    public ArrayList<Evento> eventos;
    String alumnoID;
    final String[] tipoCuentaArray={"Profesor", "Padre"};
    AlumnoController alumnoController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_simple);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            alumnoID=extras.getString("alumnoID");
        }
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        detallesUsuario= findViewById(R.id.detallesAlumnoClase);
        atras=findViewById(R.id.atrasAlumnoClase);
        eventoFeliz=findViewById(R.id.eventoFeliz);
        eventoTriste=findViewById(R.id.eventoTriste);
        alumnoController=new AlumnoController(this);

        eventos=new ArrayList<>();

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            alumnoController.getDetallesAlumno(alumnoID,user);
        }

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goBack();
            }
        });
        eventoFeliz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog(true);
                nuevoEventoDialogo.show();
            }
        });
        eventoTriste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog(false);
                nuevoEventoDialogo.show();
            }
        });
    }

    private void buildDialog(boolean esFeliz) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.nuevoeventosimple, null);

        ImageButton evento1= view.findViewById(R.id.evento1);
        ImageButton evento2= view.findViewById(R.id.evento2);
        //TODO Generalizar esto para usar eventos predeterminados
        if(esFeliz){

            evento1.setBackground(getResources().getDrawable(R.drawable.ic_chocolate));
            evento2.setBackground(getResources().getDrawable(R.drawable.ic_consola));

            evento1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aniadirEvento("Chocolate", -5);
                    nuevoEventoDialogo.dismiss();
                }
            });
            evento2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aniadirEvento("Consola", -10);
                    nuevoEventoDialogo.dismiss();
                }
            });

        }else{
            evento1.setBackground(getResources().getDrawable(R.drawable.ic_hambre));
            evento2.setBackground(getResources().getDrawable(R.drawable.ic_sed));
            evento1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aniadirEvento("Hambre", 10);
                    nuevoEventoDialogo.dismiss();
                }
            });
            evento2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aniadirEvento("Sed", 5);
                    nuevoEventoDialogo.dismiss();
                }
            });
        }

        builder.setView(view);

        builder.setTitle("Nuevo evento").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        nuevoEventoDialogo= builder.create();

    }


    private void aniadirEvento(String nombre, int estres) {
        myRef.child("usuarios").child(user.getUid()).child("hijos").child(alumnoID).child("eventos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int max=0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("nombre").getValue(String.class).startsWith(nombre)){
                        if(isDigit(ds.child("nombre").getValue(String.class).substring(nombre.length())))
                            if(Integer.parseInt(ds.child("nombre").getValue(String.class).substring(nombre.length()))>max)
                                max=Integer.parseInt(ds.child("nombre").getValue(String.class).substring(nombre.length()));
                    }
                }
                Evento evento = new Evento(nombre + (max+1), estres);
                myRef.child("usuarios").child(alumnoID).child("eventos").push().setValue(evento);
                myRef.child("usuarios").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int estresActual=snapshot.getValue(Integer.class);
                        myRef.child("usuarios").child(alumnoID).child("estres").setValue(estresActual+estres);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
    private boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void goBack(){
        String tipoCuenta= alumnoController.getTypeAccount(user.getUid());
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(getApplicationContext(), PrincipalPadre.class);
            startActivity(intent);
            finish();
        }
    }

}