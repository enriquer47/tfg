package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VerClase extends AppCompatActivity {

    ArrayList<String> alumnosEnLaClase;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;
    String centro;
    Button atras;
    LinearLayout listaAlumnosClase;
    AlertDialog nuevoEventoDialogo;
    String claseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clase);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            claseID=extras.getString("claseID");
        }

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        atras=findViewById(R.id.atrasClaseAlumnos);
        listaAlumnosClase=findViewById(R.id.listaAlumnosClaseLayout);

        myRef.child("clases").child(claseID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    alumnosEnLaClase=new ArrayList<>();
                    listaAlumnosClase.removeAllViews();
                    for (DataSnapshot snap: snapshot.child("alumnos").getChildren()){
                        Usuario u= new Usuario();
                        myRef.child("usuarios").child(snap.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                u.email=snapshot.child("email").getValue(String.class);
                               //aqui podría calcular el estrés total para mostrarlo
                                mostrarAlumnoClase(u,snap.getValue(String.class));
                                alumnosEnLaClase.add(snap.getValue(String.class));
                                //System.out.println(snap.getValue(String.class));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
                startActivity(intent);
                finish();
            }
        });


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
    private void aniadirEventoAlumnoClase(String nombre, int estres, String alumnoID) {
        myRef.child("usuarios").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean yaexiste=false;
                for (DataSnapshot snap: snapshot.child("eventos").getChildren()) {
                    if(snap.child("nombre").getValue(String.class).equals(nombre))
                        yaexiste=true;
                }
                if(!yaexiste) {
                    Evento evento = new Evento(nombre, estres);
                    myRef.child("usuarios").child(alumnoID).child("eventos").push().setValue(evento);
                }else{
                    Toast.makeText(VerClase.this, "Este alumno ya tiene un evento con ese nombre", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void mostrarAlumnoClase(Usuario u, String alumnoID) {
        //buildDialog(alumnoID);
        View view = getLayoutInflater().inflate(R.layout.tarjetaalumnoclase, null);
        TextView nombreAlumno= view.findViewById(R.id.nombreAlumnoClaseTexto);

        TextView estresAlumno= view.findViewById(R.id.nivelEstresAlumnoClaseTexto);
        Button borrarAlumnoClase=view.findViewById(R.id.borrarAlumnoClase);
        Button verAlumnoClase=view.findViewById(R.id.verAlumnoClase);
        verAlumnoClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), ProfesorVeAlumnoClase.class);
                intent.putExtra("alumnoID",alumnoID);
                intent.putExtra("claseID",claseID);
                startActivity(intent);
                finish();

            }
        });

        //ELIMINAR EL ALUMNO DE LA CLASE
        borrarAlumnoClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        nombreAlumno.setText("Email: " + u.email);
        listaAlumnosClase.addView(view);

    }


}