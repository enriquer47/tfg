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

import com.example.tfg.Model.Evento;
import com.example.tfg.Controller.PadreController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class VisualizarAlumno extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    public TextView detallesUsuario;
    Button atras, nuevoEvento, configSituacionesPredet;
    AlertDialog nuevoEventoDialogo;
    public LinearLayout eventosLayout;
    public ArrayList<Evento> eventos;
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
        setContentView(R.layout.activity_visualizar_alumno);
        detallesUsuario= findViewById(R.id.detallesAlumnoClase);
        atras=findViewById(R.id.atrasAlumnoClase);
        nuevoEvento=findViewById(R.id.nuevoEventoAlumnoClase);
        eventosLayout=findViewById(R.id.eventosAlumnosClase);
        configSituacionesPredet=findViewById(R.id.configSituacionesPredet);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        padreController=new PadreController(this,padreID);

        buildDialog();
        this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        eventos=new ArrayList<>();

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            padreController.getDetallesAlumno(alumnoID);
            padreController.obtenerEventos(alumnoID);

        }
        configSituacionesPredet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ConfigSituacionesPredet.class);
                intent.putExtra("alumnoID", alumnoID);
                intent.putExtra("padreID", padreID);
                startActivity(intent);
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                padreController.goBack(user.getUid());
            }
        });

        nuevoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoEventoDialogo.show();
            }
        });
    }

    private void buildDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.nuevoevento, null);

        EditText nombre= view.findViewById(R.id.nombreEvento);
        NumberPicker nivelEstres= view.findViewById(R.id.nivelEstres);

        builder.setView(view);
        iniciarNumberPicker(nivelEstres);
        builder.setTitle("Nuevo evento").setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                padreController.aniadirEvento(nombre.getText().toString(), nivelEstres.getValue()-10, alumnoID, user.getUid() );
                nombre.setText("");
                nivelEstres.setValue(0);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nombre.setText("");
                nivelEstres.setValue(0);
            }
        });

        nuevoEventoDialogo= builder.create();

    }

    private void iniciarNumberPicker(NumberPicker np) {
        String[] nums = new String[21];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i-10);
        np.setMinValue(0);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(0);

    }

    public void mostrarEvento(Evento e) {
        View view = getLayoutInflater().inflate(R.layout.tarjetaevento, null);
        TextView nombreMostrar = view.findViewById(R.id.nombreEventoTexto);
        Button borrarEvento = view.findViewById(R.id.borrarEvento);
        TextView estresMostrar = view.findViewById(R.id.nivelEstresTexto);
        TextView creadorMostrar = view.findViewById(R.id.creadorTexto);
        nombreMostrar.setText("" + e.getNombre());
        estresMostrar.setText("Estrés: " + e.getEstres());
        creadorMostrar.setText("Por: " + e.getCreador());

        borrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventosLayout.removeView(view);
                padreController.borrarEvento(alumnoID,e.getEstres(),e.getId());
            }
        });

        eventosLayout.addView(view);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        padreController.goBack(user.getUid());
    }

    public void userIntent(String tipoCuenta){
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(VisualizarAlumno.this, PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(VisualizarAlumno.this, PrincipalPadre.class);
            startActivity(intent);
            finish();
        }

    }

}