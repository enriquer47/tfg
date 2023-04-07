package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.controller.ProfesorController;
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
    ProfesorController profesorController;
    Button logout;
    public LinearLayout alumnosLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_profesor);
        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        user=auth.getCurrentUser();
        profesorController=new ProfesorController(this,user);
        alumnosLayout =findViewById(R.id.alumnosProfesorLayout);
        getSupportActionBar().hide();

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            profesorController.obtenerAlumnos();
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

    }


    public void mostrarAlumno(String alumnoID, String padreID) {
        View view = getLayoutInflater().inflate(R.layout.tarjetamostraralumno, null);

        ImageButton borrarAlumnoDeMiLista=view.findViewById(R.id.borrarMostrarAlumno);
        ImageButton modoAlumno=view.findViewById(R.id.verMostrarAlumno);
        ImageButton editarAlumno=view.findViewById(R.id.editarMostrarAlumno);

        profesorController.visualizarAlumno(view,alumnoID,padreID,modoAlumno);
        alumnosLayout.addView(view);

        modoAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), AlumnoSimple.class);
                intent.putExtra("alumnoID",alumnoID);
                startActivity(intent);
                finish();

            }
        });

        editarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), VisualizarAlumno.class);
                intent.putExtra("alumnoID",alumnoID);
                startActivity(intent);
                finish();

            }
        });


        borrarAlumnoDeMiLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profesorController.eliminarAlumno(alumnoID,padreID);
            }
        });


    }
}