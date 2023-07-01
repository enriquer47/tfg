package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Controller.ProfesorController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class PrincipalProfesor extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseUser user;
    ProfesorController profesorController;
    Button logout;
    public LinearLayout alumnosLayout;
    TextView instrucciones;

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
            instrucciones=findViewById(R.id.instrucciones_profesor);
            instrucciones.setText("Para agregar alumnos, un padre debe introducir tu correo electrónico en su cuenta: " + user.getEmail());
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
        ImageButton statsAlumno=view.findViewById(R.id.statsAlumno);

        profesorController.visualizarAlumno(view,alumnoID,padreID,modoAlumno);
        alumnosLayout.addView(view);

        statsAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Estadisticas.class);
                intent.putExtra("alumnoID",alumnoID);
                intent.putExtra("padreID",padreID);
                startActivity(intent);
                finish();
            }
        });
        editarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), VisualizarAlumno.class);
                intent.putExtra("alumnoID",alumnoID);
                intent.putExtra("padreID",padreID);
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