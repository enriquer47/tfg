package com.example.tfg;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tfg.Model.Alumno;
import com.example.tfg.Controller.PadreController;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;

public class PrincipalPadre extends AppCompatActivity {


    Button logout,aniadirHijo,asignarProfesor;
    public LinearLayout hijosLayout;
    AlertDialog nuevoHijoDialogo;
    FirebaseAuth auth;
    FirebaseUser user;
    PadreController padreController;
    TextInputEditText correoProfesor;
    ArrayList<String> hijosID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_padre);
        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        aniadirHijo=findViewById(R.id.aniadirHijo);
        hijosLayout=findViewById(R.id.alumnosPadreLayout);
        correoProfesor=findViewById(R.id.correoProfesor);
        asignarProfesor=findViewById(R.id.asignarProfesor);
        hijosID=new ArrayList<>();
        user=auth.getCurrentUser();
        buildDialog();
        padreController=new PadreController(this,user.getUid());
        getSupportActionBar().hide();
        this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            padreController.obtenerHijos();
            aniadirHijo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nuevoHijoDialogo.show();
                }
            });
            asignarProfesor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String correo=String.valueOf(correoProfesor.getText());
                    if (correo.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Introduce un correo", Toast.LENGTH_SHORT).show();
                        correoProfesor.setError("");
                    }else if (correo.matches("[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+")){
                        Toast.makeText(getApplicationContext(), "Introduce un correo valido", Toast.LENGTH_SHORT).show();
                        correoProfesor.setError("");
                    }else{
                        padreController.asignarProfesor(correo,hijosID);
                        correoProfesor.setText("");
                    }
                }
            });


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
    private void buildDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.nuevohijo, null);
        EditText nombre= view.findViewById(R.id.nombreHijo);
        builder.setView(view);
        builder.setTitle("Añadir Hijo").setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (nombre.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Introduce un nombre", Toast.LENGTH_SHORT).show();
                    nombre.setError("");
                }else if (nombre.getText().toString().length() < 3) {
                    Toast.makeText(getApplicationContext(), "El nombre no puede tener menos de 3 caracteres", Toast.LENGTH_SHORT).show();
                    nombre.setError("");
                    nombre.setText("");
                }else if (nombre.getText().toString().length() > 12) {
                    Toast.makeText(getApplicationContext(), "El nombre no puede tener más de 12 caracteres", Toast.LENGTH_SHORT).show();
                    nombre.setError("");
                    nombre.setText("");
                }else if (nombre.getText().toString().contains(" ")) {
                    Toast.makeText(getApplicationContext(), "El nombre no puede contener espacios", Toast.LENGTH_SHORT).show();
                    nombre.setError("");
                    nombre.setText("");
                }else if (nombre.getText().toString().matches("^[a-zA-Z]+$")) {
                    padreController.aniadirHijo(nombre.getText().toString());
                    nombre.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), "El nombre no puede contener números", Toast.LENGTH_SHORT).show();
                    nombre.setError("");
                    nombre.setText("");
                }


            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nombre.setText("");
            }
        });
        nuevoHijoDialogo= builder.create();
    }
    public void mostrarHijo(String alumnoID, Alumno hijo) {
        View view = getLayoutInflater().inflate(R.layout.tarjetamostraralumno, null);

        ImageButton statsHijo=view.findViewById(R.id.statsAlumno);
        ImageButton borrarHijo=view.findViewById(R.id.borrarMostrarAlumno);
        ImageButton modoAlumno=view.findViewById(R.id.verMostrarAlumno);
        ImageButton editarAlumno=view.findViewById(R.id.editarMostrarAlumno);
        if(!hijosID.contains(alumnoID)){
            hijosID.add(alumnoID);
        }
        padreController.visualizarHijos(view,modoAlumno,hijo);
        hijosLayout.addView(view);

        statsHijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Estadisticas.class);
                intent.putExtra("alumnoID",alumnoID);
                intent.putExtra("padreID",user.getUid());
                startActivity(intent);
                finish();
            }
        });

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
                intent.putExtra("padreID",user.getUid());
                startActivity(intent);
                finish();

            }
        });


        borrarHijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hijosID.remove(alumnoID);
                padreController.borrarHijo(alumnoID);
            }
        });



    }


}