package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.Model.Usuario;
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
    ProgressBar barraRegistro;
    TextView loginNow;
    Spinner tipoCuenta;

    FirebaseAuth mAuth;
    DatabaseReference myRef;

    final String[] tipoCuentaArray={"Alumno", "Profesor", "Padre"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth= FirebaseAuth.getInstance();
        editTextEmail= findViewById(R.id.email);
        editTextPassword= findViewById(R.id.password);
        buttonReg=findViewById(R.id.registerButton);
        barraRegistro=findViewById(R.id.progreso_registro);
        loginNow=findViewById(R.id.loginNow);
        tipoCuenta=findViewById(R.id.tipoCuenta);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, R.layout.spinner_text, tipoCuentaArray);
        tipoCuenta.setAdapter(adapter);

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

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barraRegistro.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    //TODO Pedir datos del usuario
                                    toMyRef(mAuth.getCurrentUser().getUid(),email);
                                    Toast.makeText(Register.this, "Registro Completado", Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    if(tipoCuenta.getSelectedItem().toString().equals(tipoCuentaArray[1])) {
                                        intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                                    }
                                    else {
                                        if (tipoCuenta.getSelectedItem().toString().equals(tipoCuentaArray[0]))
                                            intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
                                        else
                                            intent = new Intent(getApplicationContext(), PrincipalPadre.class);
                                        startActivity(intent);
                                        finish();
                                    }


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
    //TODO
    private void toMyRef(String userid, String email) {
        String tipo=this.tipoCuenta.getSelectedItem().toString();
        Usuario usuario = new Usuario(email, tipo);

        myRef.child("usuarios").child(userid).setValue(usuario);
    }

}