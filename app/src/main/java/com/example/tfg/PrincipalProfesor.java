package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrincipalProfesor extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;

    Button logout;
    Button crearClase;
    ArrayList<Clase> clases;
    ArrayList<String> clasesMostradas;
    AlertDialog nuevaClaseDialogo;
    LinearLayout clasesLayout;
    String centro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_profesor);




        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        crearClase=findViewById(R.id.crearClase);
        user=auth.getCurrentUser();
        clasesLayout=findViewById(R.id.clasesLayout);
        buildDialog();


        clases=new ArrayList<>();
        clasesMostradas=new ArrayList<>();
        centro="-1";

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            myRef.child("usuarios").child(user.getUid()).child("centro").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    centro=snapshot.getValue(String.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            centro="1"; //ERROR AL CONSEGUIR EL VALOR DE centro, CUANDO EJECUTA LA SIGUIENTE LINEA, SIGUE SIENDO -1 (creo que basta con meter el event listener siguiente dentro del anterior)
            myRef.child("clases").orderByChild("centro").equalTo(centro).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    clases=new ArrayList<>();
                    clasesLayout.removeAllViews();
                    clasesMostradas=new ArrayList<>();
                    for (DataSnapshot clasesSnapshot : snapshot.getChildren()) {
                        Clase c = clasesSnapshot.getValue(Clase.class);
                        clases.add(c);
                        mostrarClase(c);
                        clasesMostradas.add(clasesSnapshot.getKey());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            crearClase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nuevaClaseDialogo.show();
                }
            });

        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void buildDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.nuevaclase, null);

        EditText nombreClase= view.findViewById(R.id.nombreClase);

        builder.setView(view);
        builder.setTitle("Nueva clase").setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aniadirClase(nombreClase.getText().toString());

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        nuevaClaseDialogo= builder.create();

    }
    private void aniadirClase(String nombre) {

        myRef.child("clases").orderByChild("centro").equalTo(centro).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean yaexiste=false;
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        if(snap.child("nombre").getValue().equals(nombre)){

                            yaexiste=true;
                        }
                    }
                    if(!yaexiste) {
                        Clase clase= new Clase(nombre,centro);
                        clase.addProfesor(user.getUid());
                        myRef.child("clases").push().setValue(clase);
                    }else{
                        Toast.makeText(PrincipalProfesor.this, "Ya hay una clase con ese nombre", Toast.LENGTH_LONG).show();

                    }

                }else{
                    Clase clase= new Clase(nombre,centro);
                    clase.addProfesor(user.getUid());
                    myRef.child("clases").push().setValue(clase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    private void mostrarClase(Clase c) {

        View view = getLayoutInflater().inflate(R.layout.tarjetaclase, null);

        TextView nombreMostrar = view.findViewById(R.id.nombreClaseTexto);
        Button borrarClase = view.findViewById(R.id.borrarClase);
        Button aniadirAlumno = view.findViewById(R.id.aniadirAlumno);
        //TODO Ver clase con el estado de los alumnos
        Button verClase = view.findViewById(R.id.verClase);
        TextView centroMostrar = view.findViewById(R.id.centroTexto);
        nombreMostrar.setText("Clase: " + c.getNombre());
        centroMostrar.setText("Centro: " + centro);

        verClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("clases").orderByChild("centro_nombre").equalTo(c.getCentro_nombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent= new Intent(getApplicationContext(), VerClase.class);
                        String claseID="";
                        for(DataSnapshot hijos: snapshot.getChildren()){
                            claseID=hijos.getKey();
                        }
                        intent.putExtra("claseID",claseID);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        borrarClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clasesLayout.removeView(view);
                myRef.child("clases").orderByChild("centro_nombre").equalTo(c.getCentro_nombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot hijos: snapshot.getChildren()){
                            clasesMostradas.remove(hijos.getKey());
                            myRef.child("clases").child(hijos.getKey()).removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        aniadirAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("clases").orderByChild("centro_nombre").equalTo(c.getCentro_nombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent= new Intent(getApplicationContext(), AgregarAlumnos.class);
                        String claseID="";
                        for(DataSnapshot hijos: snapshot.getChildren()){
                            claseID=hijos.getKey();
                        }
                        intent.putExtra("claseID",claseID);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        clasesLayout.addView(view);

    }
}