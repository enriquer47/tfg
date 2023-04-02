package com.example.tfg;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.Model.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    final String[] tipoCuentaArray={"Alumno", "Profesor", "Padre"};


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        getSupportActionBar().setTitle("Inicio de sesión");

        //Si el usuario ya está logueado, se le redirige a su pantalla
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
        mAuth= FirebaseAuth.getInstance();
        //Change lenguage to spanish
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.LoginTheme)
                .build();
        signInLauncher.launch(signInIntent);

    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            if(response.isNewUser()){
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), PrincipalAlumno.class);
                startActivity(intent);
                finish();
            }
            //FirebaseUser currentUser = mAuth.getCurrentUser();
            //myRef= FirebaseDatabase.getInstance("https://prueba-c426b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
            //Usuario usuario = new Usuario(currentUser.getEmail(), tipoCuentaArray[0]);
           // myRef.child("usuarios").child(currentUser.getUid()).setValue(usuario);
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // handle the error.
            if(response != null)
                Log.i("Error", response.getError().getMessage());
            else
                Log.i("Error", "Error al iniciar sesion");

        }
    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
}