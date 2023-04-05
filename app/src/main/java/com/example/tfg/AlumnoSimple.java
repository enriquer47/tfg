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
        alumnoController=new AlumnoController(this, alumnoID);

        eventos=new ArrayList<>();

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            alumnoController.getDetallesAlumno(user);
        }

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alumnoController.goBack(user.getUid());
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
                    alumnoController.aniadirEvento("Chocolate", -5,user);
                    nuevoEventoDialogo.dismiss();
                }
            });
            evento2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alumnoController.aniadirEvento("Consola", -10,user);
                    nuevoEventoDialogo.dismiss();
                }
            });
        }else{
            evento1.setBackground(getResources().getDrawable(R.drawable.ic_hambre));
            evento2.setBackground(getResources().getDrawable(R.drawable.ic_sed));
            evento1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alumnoController.aniadirEvento("Hambre", 10,user);
                    nuevoEventoDialogo.dismiss();
                }
            });
            evento2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alumnoController.aniadirEvento("Sed", 5,user);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        alumnoController.goBack(user.getUid());
    }
    private boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void userIntent(String tipoCuenta){
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(AlumnoSimple.this, PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(AlumnoSimple.this, PrincipalPadre.class);
            startActivity(intent);
            finish();
        }

    }
}