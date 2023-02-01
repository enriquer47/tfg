package com.example.tfg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalAlumno extends AppCompatActivity {

    FirebaseAuth auth;
    TextView detallesUsuario;
    Button logout, nuevoEvento;
    FirebaseUser user;


    AlertDialog nuevoEventoDialogo;
    LinearLayout eventosLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        detallesUsuario= findViewById(R.id.detallesUsuario);
        logout=findViewById(R.id.logout);
        nuevoEvento=findViewById(R.id.nuevoEvento);
        user=auth.getCurrentUser();
        eventosLayout=findViewById(R.id.eventos);
        buildDialog();


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            detallesUsuario.setText(user.getEmail());
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
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
                aniadirEvento(nombre.getText().toString(), nivelEstres.getValue());

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        nuevoEventoDialogo= builder.create();

    }

    private void iniciarNumberPicker(NumberPicker np) {
        String[] nums = new String[11];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        np.setMinValue(0);
        np.setMaxValue(10);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(1);

    }

    private void aniadirEvento(String nombre, int estres) {
        View view = getLayoutInflater().inflate(R.layout.tarjetaevento, null);

        TextView nombreMostrar= view.findViewById(R.id.nombreEventoTexto);
        Button borrarEvento= view.findViewById(R.id.borrarEvento);
        TextView estresMostrar= view.findViewById(R.id.nivelEstresTexto);

        nombreMostrar.setText(nombre);
        estresMostrar.setText(""+estres);
        borrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventosLayout.removeView(view);
            }
        });
        eventosLayout.addView(view);

    }
}