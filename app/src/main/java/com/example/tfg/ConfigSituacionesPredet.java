package com.example.tfg;

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
import android.widget.NumberPicker;
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
        nuevoPredet=findViewById(R.id.nuevaSituacionPredeterminada);

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
            padreController.obtenerPredetsHijo(alumnoID);

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
        ImageButton borrarPredet = view.findViewById(R.id.borrarPredet);
        TextView estresMostrar = view.findViewById(R.id.nivelEstresPredetTexto);
        TextView categoriaMostrar = view.findViewById(R.id.categoriaPredetTexto);
        ImageButton miniaturaPredet = view.findViewById(R.id.miniaturaPredet);
        nombreMostrar.setText( p.getNombre());
        estresMostrar.setText("Estrés: " + p.getEstres());
        categoriaMostrar.setText(p.getCategoria());

        borrarPredet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predetsLayout.removeView(view);
                padreController.borrarPredet(alumnoID,p.getId());
            }
        });
        miniaturaPredet.setBackground(getDrawable(R.drawable.ic_imagen_basica)); //cambiar para que muestre la imagen seleccionada
        predetsLayout.addView(view);

    }
    private void buildDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.nuevopredet, null);

        EditText nombre= view.findViewById(R.id.nombrePredet);
        EditText categoria= view.findViewById(R.id.categoriaPredet);
        NumberPicker nivelEstres= view.findViewById(R.id.nivelEstresPredet);
        nivelEstres.setValue(0);

        builder.setView(view);
        iniciarNumberPicker(nivelEstres);
        builder.setTitle("Nueva Situación Predeterminada").setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Predet predet = new Predet(nombre.getText().toString(), categoria.getText().toString().toUpperCase(), nivelEstres.getValue()-10);
                padreController.aniadirPredetHijo(alumnoID,predet);
                nombre.setText("");
                categoria.setText("");
                nivelEstres.setValue(0);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nombre.setText("");
                nivelEstres.setValue(0);
            }
        });

        nuevoPredetDialogo= builder.create();

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        padreController.goBack(user.getUid());
    }
    public void userIntent(String tipoCuenta){
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(ConfigSituacionesPredet.this, PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(ConfigSituacionesPredet.this, PrincipalPadre.class);
            startActivity(intent);
            finish();
        }

    }
}