package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;

public class AgregarAlumnos extends AppCompatActivity {


    ArrayList<String> alumnos;
    ArrayList<String> alumnosDelProfesor;
    ArrayList<CheckBox> listaCheckBoxes;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;
    String centro;
    Button atras, aniadir;
    LinearLayout listaAlumnos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras=getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alumnos);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        alumnos=new ArrayList<>();
        listaCheckBoxes=new ArrayList<>();
        atras=findViewById(R.id.atrasListaAlumnos);
        aniadir=findViewById(R.id.aniadirAlumnosMarcados);
        listaAlumnos=findViewById(R.id.listaAlumnos);

        getSupportActionBar().setTitle("Agregar Alumnos");


        //Cada vez que haya un cambio en los usuarios, se actualiza la lista de alumnos
        myRef.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaCheckBoxes=new ArrayList<>();
                    alumnos=new ArrayList<>();
                    alumnosDelProfesor =new ArrayList<>();
                    listaAlumnos.removeAllViews();

                    //Se obtienen los alumnos del profesor
                    myRef.child("usuarios").child(user.getUid()).child("alumnos").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            for (DataSnapshot snap: snapshot2.getChildren()){
                                alumnosDelProfesor.add(snap.getValue(String.class));
                            }
                            //Se muestran los alumnos para añadir que no pertenecen ya al profesor
                            for (DataSnapshot usuarioSnap: snapshot.getChildren()) {
                                if(usuarioSnap.child("tipoCuenta").getValue(String.class).equals("Alumno")&&!alumnosDelProfesor.contains(usuarioSnap.getKey())){
                                    Usuario u = usuarioSnap.getValue(Usuario.class);
                                    listaCheckBoxes.add(mostrarAlumno(u));
                                    alumnos.add(usuarioSnap.getKey());
                                }
                            }
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
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalAniadidos=0;
                for(int i =0; i<listaCheckBoxes.size();i++) {
                    if(listaCheckBoxes.get(i).isChecked()){
                        myRef.child("usuarios").child(user.getUid()).child("alumnos").push().setValue(alumnos.get(i));
                        totalAniadidos++;
                    }

                }
                Toast.makeText(AgregarAlumnos.this, totalAniadidos + " alumnos añadidos correctamente",
                        Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
                startActivity(intent);
                finish();
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private CheckBox mostrarAlumno(Usuario u) {
        View view = getLayoutInflater().inflate(R.layout.tarjetaalumno, null);
        TextView nombreAlumno= view.findViewById(R.id.nombreAlumnoTexto);
        CheckBox aniadirAlumnoCheck= view.findViewById(R.id.checkAlumno);
        nombreAlumno.setText("Email: " + u.getEmail());
        listaAlumnos.addView(view);
        return aniadirAlumnoCheck;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
        startActivity(intent);
        finish();
    }

}