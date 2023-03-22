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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.Model.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VisualizarAlumno extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;

    TextView detallesUsuario;
    Button atras, nuevoEvento;
    AlertDialog nuevoEventoDialogo;
    LinearLayout eventosLayout;

    public ArrayList<Evento> eventos;
    String alumnoID;
    final String[] tipoCuentaArray={"Alumno", "Profesor", "Padre"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_alumno);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            alumnoID=extras.getString("alumnoID");
        }
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        detallesUsuario= findViewById(R.id.detallesAlumnoClase);
        atras=findViewById(R.id.atrasAlumnoClase);
        nuevoEvento=findViewById(R.id.nuevoEventoAlumnoClase);
        eventosLayout=findViewById(R.id.eventosAlumnosClase);
        buildDialog();

        eventos=new ArrayList<>();

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            myRef.child("usuarios").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    detallesUsuario.setText(snapshot.child("nombre").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            myRef.child("usuarios").child(alumnoID).child("eventos").addValueEventListener(new ValueEventListener() {
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
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("usuarios").child(user.getUid()).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Intent intent;
                            if(snapshot.getValue(String.class).equals(tipoCuentaArray[1]))
                                intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                            else
                            if(snapshot.getValue(String.class).equals(tipoCuentaArray[0]))
                                intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
                            else
                                intent =new Intent(getApplicationContext(), PrincipalPadre.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
            if(eventos.get(i).getNombre().equals(nombre))
                yaexiste=true;
        }
        if(!yaexiste) {
            Evento evento = new Evento(nombre, estres);
            myRef.child("usuarios").child(alumnoID).child("eventos").push().setValue(evento);
            myRef.child("usuarios").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int estresActual=snapshot.getValue(Integer.class);
                    myRef.child("usuarios").child(alumnoID).child("estres").setValue(estresActual+estres);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(VisualizarAlumno.this, "Ya hay un evento con ese nombre", Toast.LENGTH_LONG).show();

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
                System.out.println(alumnoID);
                myRef.child("usuarios").child(alumnoID).child("eventos").orderByChild("nombre").equalTo(e.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot hijos: snapshot.getChildren()){
                            myRef.child("usuarios").child(alumnoID).child("eventos").child(hijos.getKey()).removeValue();
                            myRef.child("usuarios").child(alumnoID).child("estres").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int estresActual=snapshot.getValue(Integer.class);
                                    myRef.child("usuarios").child(alumnoID).child("estres").setValue(estresActual-e.getEstres());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRef.child("usuarios").child(user.getUid()).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Intent intent;
                    if(snapshot.getValue(String.class).equals(tipoCuentaArray[1]))
                        intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                    else
                    if(snapshot.getValue(String.class).equals(tipoCuentaArray[0]))
                        intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
                    else
                        intent =new Intent(getApplicationContext(), PrincipalPadre.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}