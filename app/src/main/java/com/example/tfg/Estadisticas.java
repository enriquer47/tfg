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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.tfg.Controller.PadreController;
import com.example.tfg.Model.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Estadisticas extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button atras;
    public LinearLayout eventosLayout;

    String alumnoID;
    String padreID;
    final String[] tipoCuentaArray={"Profesor", "Padre"};
    PadreController padreController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            alumnoID=extras.getString("alumnoID");
            padreID=extras.getString("padreID");
        }
        setContentView(R.layout.activity_estadisticas);
        atras=findViewById(R.id.atrasStats);
        eventosLayout=findViewById(R.id.rankingEventos);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        padreController=new PadreController(this,padreID);

        this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Estadísticas alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            padreController.getDetallesAlumno(alumnoID);
            padreController.obtenerStats(alumnoID);

        }

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                padreController.goBack(user.getUid());
            }
        });


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        padreController.goBack(user.getUid());
    }

    public void userIntent(String tipoCuenta){
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(Estadisticas.this, PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(Estadisticas.this, PrincipalPadre.class);
            startActivity(intent);
            finish();
        }

    }

    public void mostrarStats(ArrayList<Evento> eventos, ArrayList<Integer> contador) {
        ArrayList<Integer> sortedIndexes= new ArrayList<>();
        ArrayList<Integer> apariciones= new ArrayList<>();
        for (int i =0; i<contador.size(); i++){
            apariciones.add(contador.get(i));
        }
        for (int i = 0; i<contador.size(); i++){
            int max=0;
            int index=0;
            for (int j = 0; j<contador.size(); j++){
                if (contador.get(j)>max){
                    max=contador.get(j);
                    index=j;
                }
            }
            sortedIndexes.add(index);
            contador.set(index,0);
        }

        for (int i = 0; i<eventos.size(); i++){
            System.out.println("Evento: "+eventos.get(sortedIndexes.get(i)).getNombre()+" Apariciones: "+apariciones.get(sortedIndexes.get(i)));
            mostrarEventoRanking(eventos.get(sortedIndexes.get(i)),i+1, apariciones.get(sortedIndexes.get(i)));
        }







    }
    public void mostrarEventoRanking(Evento e, int puesto, int apariciones) {
        View view = getLayoutInflater().inflate(R.layout.tarjetarankingevento, null);
        TextView nombreTexto = view.findViewById(R.id.nombreEventoRanking);
        TextView aparicionesTexto = view.findViewById(R.id.aparicionesRanking);
        TextView puestoTexto = view.findViewById(R.id.puestoRanking);
        nombreTexto.setText("" + e.getNombre());
        aparicionesTexto.setText("Apariciones: " + apariciones);
        puestoTexto.setText("Puesto: " + puesto);
        eventosLayout.addView(view);
    }





}