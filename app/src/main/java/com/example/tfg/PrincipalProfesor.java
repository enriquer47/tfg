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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.Model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalProfesor extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;

    Button logout;
    Button aniadirAlumnos;
    LinearLayout alumnosLayout;
    String centro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_profesor);




        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        aniadirAlumnos =findViewById(R.id.aniadirAlumnoProfesor);
        user=auth.getCurrentUser();
        alumnosLayout =findViewById(R.id.alumnosProfesorLayout);

        getSupportActionBar().setTitle("Principal Profesor");



        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            myRef.child("usuarios").child(user.getUid()).child("alumnos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    alumnosLayout.removeAllViews();

                    for (DataSnapshot alumnosSnap : snapshot.getChildren()) {
                        String alumnoId=alumnosSnap.getValue(String.class);
                        myRef.child("usuarios").child(alumnoId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Usuario usuario =new Usuario(snapshot.child("email").getValue(String.class),"Alumno");
                                mostrarHijo(usuario,alumnoId);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            aniadirAlumnos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
        aniadirAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), AgregarAlumnos.class);
                startActivity(intent);
                finish();

            }
        });

    }


    private void mostrarHijo(Usuario u, String alumnoID) {
        View view = getLayoutInflater().inflate(R.layout.tarjetamostraralumno, null);
        TextView nombreAlumno= view.findViewById(R.id.nombreMostarAlumnoTexto);
        int estres=0;
        //TextView estresAlumno= view.findViewById(R.id.nivelEstresMostrarAlumnoTexto);
        ImageButton borrarAlumno=view.findViewById(R.id.borrarMostrarAlumno);
        ImageButton verAlumno=view.findViewById(R.id.verMostrarAlumno);

        myRef.child("usuarios").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int estres = snapshot.child("estres").getValue(Integer.class);
                    if(estres>=50)
                        verAlumno.setBackgroundResource(R.drawable.ic_mostraralumnotriste);
                    else
                        verAlumno.setBackgroundResource(R.drawable.ic_mostraralumnofeliz);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        verAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), VisualizarAlumno.class);
                intent.putExtra("alumnoID",alumnoID);
                startActivity(intent);
                finish();

            }
        });


        borrarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("usuarios").child(user.getUid()).child("alumnos").orderByValue().equalTo(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap: snapshot.getChildren()) {
                            myRef.child("usuarios").child(user.getUid()).child("alumnos").child(snap.getKey()).removeValue();
                        };

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        nombreAlumno.setText(u.getEmail());
        alumnosLayout.addView(view);

    }
}