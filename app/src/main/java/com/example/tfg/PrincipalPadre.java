package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrincipalPadre extends AppCompatActivity {


    Button logout;
    TextView detallesUsuario;
    Button  aniadirHijo;
    LinearLayout hijosLayout;
    EditText codigoEdit;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef;

    String codigoIntroducido;
    private ArrayList<String> hijos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_padre);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        auth = FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        aniadirHijo=findViewById(R.id.aniadirHijo);
        user=auth.getCurrentUser();
        hijosLayout=findViewById(R.id.alumnosPadreLayout);
        codigoEdit=findViewById(R.id.introducirCodigo);

        hijos=new ArrayList<>();

        getSupportActionBar().setTitle("Principal Padre");

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {

            myRef.child("usuarios").child(user.getUid()).child("hijos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    hijos=new ArrayList<>();
                    hijosLayout.removeAllViews();

                    for (DataSnapshot hijosSnap : snapshot.getChildren()) {
                        String hijoId=hijosSnap.getValue(String.class);
                        hijos.add(hijoId);
                        myRef.child("usuarios").child(hijoId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //TODO Constructor de usuario
                                Usuario usuario =new Usuario(snapshot.child("email").getValue(String.class),"Alumno");
                                mostrarHijo(usuario,hijoId);
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
            aniadirHijo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codigoIntroducido=codigoEdit.getText().toString();
                    if(codigoIntroducido.length()>10) {
                        String email=codigoIntroducido.substring(0,codigoIntroducido.length()-5);

                        //falta comprobar si el email es valido

                        myRef.child("usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String codigo="codigocompletamentefalso";
                                String hijoID="idcompletamentefalso";
                                for(DataSnapshot snap: snapshot.getChildren()) {
                                    hijoID=snap.getKey();
                                    codigo = hijoID.substring(0, 5);
                                }
                                if (codigoIntroducido.equals(email+codigo)){
                                    myRef.child("usuarios").child(user.getUid()).child("hijos").push().setValue(hijoID);
                                    codigoEdit.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else{
                        //TODO TOAST CODIGO INVALIDO
                    }

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
    }
    private void mostrarHijo(Usuario u, String alumnoID) {
        View view = getLayoutInflater().inflate(R.layout.tarjetamostraralumno, null);
        TextView nombreAlumno= view.findViewById(R.id.nombreMostarAlumnoTexto);

        //TextView estresAlumno= view.findViewById(R.id.nivelEstresMostrarAlumnoTexto);
        ImageButton borrarAlumnoClase=view.findViewById(R.id.borrarMostrarAlumno);
        ImageButton verAlumnoClase=view.findViewById(R.id.verMostrarAlumno);

        myRef.child("usuarios").child(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int estres = snapshot.child("estres").getValue(Integer.class);
                    if(estres>=50)
                        verAlumnoClase.setBackgroundResource(R.drawable.ic_mostraralumnotriste);
                    else
                        verAlumnoClase.setBackgroundResource(R.drawable.ic_mostraralumnofeliz);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        verAlumnoClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(), VisualizarAlumno.class);
                intent.putExtra("alumnoID",alumnoID);
                startActivity(intent);
                finish();

            }
        });


        borrarAlumnoClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("usuarios").child(user.getUid()).child("hijos").orderByValue().equalTo(alumnoID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap: snapshot.getChildren()) {
                            myRef.child("usuarios").child(user.getUid()).child("hijos").child(snap.getKey()).removeValue();
                        };

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        nombreAlumno.setText(u.getEmail());
        hijosLayout.addView(view);

    }

}