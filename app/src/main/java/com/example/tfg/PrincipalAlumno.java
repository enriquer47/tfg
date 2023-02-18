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
import com.example.tfg.Model.*;
import java.util.ArrayList;

public class PrincipalAlumno extends AppCompatActivity {

    FirebaseAuth auth;
    TextView detallesUsuario,codigoAlumnoTextView;
    Button logout, nuevoEvento, mostrarCodigo;
    FirebaseUser user;
    DatabaseReference myRef;
    public ArrayList<Evento> eventos;
    AlertDialog nuevoEventoDialogo;
    LinearLayout eventosLayout;
    //TODO: Cambiar el código del alumno a la clase Alumno
    String codigoAlumno;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        detallesUsuario= findViewById(R.id.detallesUsuario);
        logout=findViewById(R.id.logout);
        nuevoEvento=findViewById(R.id.nuevoEvento);
        eventosLayout=findViewById(R.id.eventos);
        mostrarCodigo=findViewById(R.id.mostrarCodigo);
        codigoAlumnoTextView=findViewById(R.id.codigoAlumno);
        buildDialog();

        codigoAlumno="";
        eventos=new ArrayList<>();


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            codigoAlumno=user.getEmail().toString() + user.getUid().toString().substring(0,5);
            detallesUsuario.setText(user.getEmail());

            //Profesor añade evento y lo añade a los del alumno
            myRef.child("usuarios").child(user.getUid()).child("eventos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventos=new ArrayList<>();
                    eventosLayout.removeAllViews();
                    for (DataSnapshot eventosSnapshot : snapshot.getChildren()) {
                        Evento e = eventosSnapshot.getValue(Evento.class);
                        eventos.add(e);
                        mostrarEvento(e);


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

        mostrarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codigoAlumnoTextView.getText().equals("")){
                    codigoAlumnoTextView.setText(codigoAlumno);
                    mostrarCodigo.setText("Ocultar código");
                }else{
                    codigoAlumnoTextView.setText("");
                    mostrarCodigo.setText("Mostrar código");
                }

            }
        });

        nuevoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nuevoEventoDialogo.show();
            }
        });
    }

    //TODO: Cambiar añadir evento del Alumnmo que añada o baje estres
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

    //TODO: Probablemente se quite es para el PopUp
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
    //TODO: Identificar eventos con un ID
    private void aniadirEvento(String nombre, int estres) {
        boolean yaexiste=false;
        for(int i = 0;  i<eventos.size()&&!yaexiste;i++){
            if(eventos.get(i).getNombre().equals(nombre))
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
            nombreMostrar.setText("Evento: " + e.getNombre());
            estresMostrar.setText("Estrés: " + e.getEstres());


            borrarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventosLayout.removeView(view);
                    myRef.child("usuarios").child(user.getUid()).child("eventos").orderByChild("nombre").equalTo(e.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hijos: snapshot.getChildren()){
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