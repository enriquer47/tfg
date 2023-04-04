package com.example.tfg;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;

import com.example.tfg.controller.LoginController;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    LoginController loginController;
    final String[] tipoCuentaArray={"Profesor", "Padre"};

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getSupportActionBar().setTitle("Inicio de sesión");
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null){
            userIntent(currentUser);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth= FirebaseAuth.getInstance();
        loginController=new LoginController();

        //Change lenguage to spanish
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //UI Configuration
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
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Successfully signed in
        if (result.getResultCode() == RESULT_OK) {
            if(response.isNewUser()){
                loginController.saveNewUser(currentUser,response.getEmail());
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }else{
                userIntent(currentUser);
            }
        }else{
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

    private void userIntent(FirebaseUser currentUser){
        String tipoCuenta=loginController.getTypeAccount(currentUser.getUid());
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(getApplicationContext(), PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(getApplicationContext(), PrincipalPadre.class);
            startActivity(intent);
            finish();
        }
    }
}