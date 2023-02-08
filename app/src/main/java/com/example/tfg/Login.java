package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogIn;
    FirebaseAuth mAuth;
    ProgressBar barraLogin;
    TextView registerNow;
    DatabaseReference myRef;
    final String[] tipoCuentaArray={"Alumno", "Profesor", "Padre"};


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();


        if(currentUser != null){
            myRef.child("usuarios").child(currentUser.getUid()).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        editTextEmail= findViewById(R.id.email);
        editTextPassword= findViewById(R.id.password);
        buttonLogIn=findViewById(R.id.loginButton);
        barraLogin=findViewById(R.id.progreso_login);
        registerNow=findViewById(R.id.registerNow);

        registerNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent= new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barraLogin.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText().toString());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Debe introducir un email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Debe introducir una contraseña", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barraLogin.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    myRef.child("usuarios").child(task.getResult().getUser().getUid()).child("tipoCuenta").addListenerForSingleValueEvent(new ValueEventListener() {
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

                                } else {

                                    Toast.makeText(Login.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}