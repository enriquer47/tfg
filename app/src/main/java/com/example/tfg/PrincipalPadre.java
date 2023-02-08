package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    FirebaseAuth auth;
    TextView detallesUsuario;
    Button  aniadirHijo;
    FirebaseUser user;
    DatabaseReference myRef;
    private ArrayList<String> hijos;
    ArrayList<String> hijosMostrados;
    LinearLayout hijosLayout;
    String centro;
    String codigoIntroducido;
    EditText codigoEdit;


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
        logout=findViewById(R.id.logout);
        codigoEdit=findViewById(R.id.introducirCodigo);


        hijos=new ArrayList<>();
        hijosMostrados=new ArrayList<>();
        centro="-1";

        if(user==null){
            Intent intent= new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            myRef.child("usuarios").child(user.getUid()).child("centro").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    centro=snapshot.getValue(String.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            centro="1"; //ERROR AL CONSEGUIR EL VALOR DE centro, CUANDO EJECUTA LA SIGUIENTE LINEA, SIGUE SIENDO -1 (creo que basta con meter el event listener siguiente dentro del anterior)
            myRef.child("usuarios").child(user.getUid()).child("hijos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    hijos=new ArrayList<>();
                    hijosLayout.removeAllViews();
                    hijosMostrados=new ArrayList<>();
                    for (DataSnapshot hijosSnap : snapshot.getChildren()) {
                        String h=hijosSnap.getValue(String.class);
                        hijos.add(h);
                        myRef.child("usuarios").child(h).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Usuario u =new Usuario(snapshot.child("email").getValue(String.class),"Alumno");
                                mostrarHijo(u,h);
                                hijosMostrados.add(h);
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
        nombreAlumno.setText("Email: " + u.email);
        hijosLayout.addView(view);

    }

}