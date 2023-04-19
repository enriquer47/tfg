package com.example.tfg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Controller.PadreController;
import com.example.tfg.Model.Evento;
import com.example.tfg.Model.Predet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ConfigSituacionesPredet extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Button atras, nuevoPredet;
    AlertDialog nuevoPredetDialogo;
    public LinearLayout predetsLayout;
    public ArrayList<Evento> eventos;
    String alumnoID;
    String padreID;
    final String[] tipoCuentaArray={"Profesor", "Padre"};
    PadreController padreController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_situaciones_predet);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            alumnoID=extras.getString("alumnoID");
            padreID=extras.getString("padreID");
        }
        setContentView(R.layout.activity_config_situaciones_predet);
        atras=findViewById(R.id.atrasGestionPredet);
        predetsLayout=findViewById(R.id.eventosPredetLayout);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        padreController=new PadreController(this,padreID);

        buildDialog();

        eventos=new ArrayList<>();

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            padreController.obtenerPredetsPadre();

        }
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                padreController.goBack(user.getUid());
            }
        });

        nuevoPredet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoPredetDialogo.show();
            }
        });
    }
    public void mostrarPredet(Predet p) {
        View view = getLayoutInflater().inflate(R.layout.tarjetaeventopredet, null);
        TextView nombreMostrar = view.findViewById(R.id.nombrePredetTexto);
        Button borrarEvento = view.findViewById(R.id.borrarPredet);
        TextView estresMostrar = view.findViewById(R.id.nivelEstresPredetTexto);
        Button editarPredet = view.findViewById(R.id.editarSituacionPredet);
        Button miniaturaPredet = view.findViewById(R.id.miniaturaPredet);
        nombreMostrar.setText("Evento: " + p.getNombre());
        estresMostrar.setText("Estrés: " + p.getEstres());

        borrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predetsLayout.removeView(view);
                padreController.borrarEvento(alumnoID,p.getEstres(),p.getId());
            }
        });

        predetsLayout.addView(view);

    }
}