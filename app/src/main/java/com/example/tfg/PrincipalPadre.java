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

import com.example.tfg.Model.Alumno;
import com.example.tfg.controller.PadreController;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrincipalPadre extends AppCompatActivity {


    Button logout;
    Button  aniadirHijo;
    public LinearLayout hijosLayout;
    AlertDialog nuevoHijoDialogo;
    FirebaseAuth auth;
    FirebaseUser user;
    PadreController padreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_padre);
        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        aniadirHijo=findViewById(R.id.aniadirHijo);
        hijosLayout=findViewById(R.id.alumnosPadreLayout);

        user=auth.getCurrentUser();
        buildDialog();
        padreController=new PadreController(this,auth.getCurrentUser());

        getSupportActionBar().setTitle("Principal Padre");

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
                padreController.aniadirHijo(nombre.getText().toString());
                nombre.setText("");

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


        ImageButton borrarHijo=view.findViewById(R.id.borrarMostrarAlumno);
        ImageButton modoAlumno=view.findViewById(R.id.verMostrarAlumno);
        ImageButton editarAlumno=view.findViewById(R.id.editarMostrarAlumno);

        padreController.visualizarHijos(view,modoAlumno,hijo);
        hijosLayout.addView(view);

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


        borrarHijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                padreController.borrarHijo(alumnoID);
            }
        });



    }


}