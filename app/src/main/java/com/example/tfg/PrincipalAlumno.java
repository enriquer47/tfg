package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrincipalAlumno extends AppCompatActivity {

    FirebaseAuth auth;
    TextView detallesUsuario;
    Button logout, nuevoEvento;
    FirebaseUser user;
    DatabaseReference myRef;
    public ArrayList<Evento> eventos;
    ArrayList<String> eventosMostrados;
    AlertDialog nuevoEventoDialogo;
    LinearLayout eventosLayout;
    String centro;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        detallesUsuario= findViewById(R.id.detallesUsuario);
        logout=findViewById(R.id.logout);
        nuevoEvento=findViewById(R.id.nuevoEvento);
        user=auth.getCurrentUser();
        eventosLayout=findViewById(R.id.eventos);
        buildDialog();

        String centro="1";


        eventos=new ArrayList<>();
        eventosMostrados=new ArrayList<>();

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            detallesUsuario.setText(user.getEmail());
            myRef.child("usuarios").child(user.getUid()).child("eventos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventos=new ArrayList<>();
                    eventosLayout.removeAllViews();
                    eventosMostrados=new ArrayList<>();
                    for (DataSnapshot eventosSnapshot : snapshot.getChildren()) {
                        Evento e = eventosSnapshot.getValue(Evento.class);
                        eventos.add(e);
                        mostrarEvento(e);
                        eventosMostrados.add(eventosSnapshot.getKey());


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
        boolean yaexiste=false;
        for(int i = 0;  i<eventos.size()&&!yaexiste;i++){
            if(eventos.get(i).nombre.equals(nombre))
                yaexiste=true;
        }
        if(!yaexiste) {
            Evento evento = new Evento(nombre, estres);
            myRef.child("usuarios").child(user.getUid()).child("eventos").push().setValue(evento);
        }else{
            Toast.makeText(PrincipalAlumno.this, "Ya hay un evento con ese nombre", Toast.LENGTH_LONG).show();

        }
    }
    private void mostrarEvento(Evento e) {

            View view = getLayoutInflater().inflate(R.layout.tarjetaevento, null);

            TextView nombreMostrar = view.findViewById(R.id.nombreEventoTexto);
            Button borrarEvento = view.findViewById(R.id.borrarEvento);
            TextView estresMostrar = view.findViewById(R.id.nivelEstresTexto);
            nombreMostrar.setText("Evento: " + e.nombre);
            estresMostrar.setText("Estrés: " + e.estres);


            borrarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventosLayout.removeView(view);
                    myRef.child("usuarios").child(user.getUid()).child("eventos").orderByChild("nombre").equalTo(e.nombre).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hijos: snapshot.getChildren()){
                                eventosMostrados.remove(hijos.getKey());
                                myRef.child("usuarios").child(user.getUid()).child("eventos").child(hijos.getKey()).removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            eventosLayout.addView(view);

    }
}