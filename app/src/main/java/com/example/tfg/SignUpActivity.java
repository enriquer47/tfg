package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tfg.controller.LoginController;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSoyPadre, buttonSoyprofesor;
    FirebaseAuth mAuth;
    LoginController loginController;
    final String[] tipoCuentaArray={"Profesor", "Padre"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth= FirebaseAuth.getInstance();
        buttonSoyPadre=findViewById(R.id.padreButton);
        buttonSoyprofesor=findViewById(R.id.profesorButton);
        getSupportActionBar().setTitle("Registro");
        loginController=new LoginController();

        buttonSoyPadre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginController.setTipoCuenta(mAuth.getCurrentUser(),tipoCuentaArray[1]);
                Intent intent = new Intent(getApplicationContext(), PrincipalPadre.class);
                startActivity(intent);
                finish();

            }
        });

        buttonSoyprofesor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginController.setTipoCuenta(mAuth.getCurrentUser(),tipoCuentaArray[0]);
                Intent intent = new Intent(getApplicationContext(), PrincipalProfesor.class);
                startActivity(intent);
                finish();

            }
        });
    }
}