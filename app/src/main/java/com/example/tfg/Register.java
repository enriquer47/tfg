package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar barraRegistro;
    TextView loginNow;
    Switch profesorSwitch;
    DatabaseReference myRef;

    @Override
    public void onStart() {
        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            myRef.child("usuarios").child(currentUser.getUid()).child("esProfesor").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Intent intent;
                        if((Boolean) snapshot.getValue())
                            intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                        else
                            intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth= FirebaseAuth.getInstance();
        editTextEmail= findViewById(R.id.email);
        editTextPassword= findViewById(R.id.password);
        buttonReg=findViewById(R.id.registerButton);
        barraRegistro=findViewById(R.id.progreso_registro);
        loginNow=findViewById(R.id.loginNow);
        profesorSwitch=findViewById(R.id.profesor);


        loginNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent= new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                 finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                barraRegistro.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText().toString());


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Debe introducir un email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Debe introducir una contraseña", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barraRegistro.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    escribirNuevoUsuario(mAuth.getCurrentUser().getUid(),email,profesorSwitch.isChecked());
                                    Toast.makeText(Register.this, "Registro Completado",
                                            Toast.LENGTH_SHORT).show();

                                    myRef.child("usuarios").child(mAuth.getCurrentUser().getUid()).child("esProfesor").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                Intent intent;
                                                if((Boolean) snapshot.getValue())
                                                    intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                                                else
                                                    intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Error en el registro",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }
        });
    }
    public void escribirNuevoUsuario(String userid, String email, boolean esProfesor) {
        Usuario usuario = new Usuario(email,esProfesor);
        myRef.child("usuarios").child(userid).setValue(usuario);
    }

}