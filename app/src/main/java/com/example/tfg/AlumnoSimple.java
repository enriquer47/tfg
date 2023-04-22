package com.example.tfg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Controller.AlumnoController;
import com.example.tfg.Model.Predet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AlumnoSimple extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    public TextView detallesUsuario;
    Button atras;
    ImageButton eventoFeliz,eventoTriste;
    AlertDialog nuevoEventoDialogo;
    String alumnoID;
    final String[] tipoCuentaArray={"Profesor", "Padre"};

    AlumnoController alumnoController;

    public int tipoVista = 1; //0 = simple, 1 = vaso




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (tipoVista == 0) {
            setContentView(R.layout.activity_alumno_simple);
        } else {
            setContentView(R.layout.activity_alumno_vaso);
        }
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            alumnoID=extras.getString("alumnoID");
        }
        detallesUsuario= findViewById(R.id.detallesAlumnoClase);
        atras=findViewById(R.id.atrasAlumnoClase);
        eventoFeliz=findViewById(R.id.eventoFeliz);
        eventoTriste=findViewById(R.id.eventoTriste);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        alumnoController=new AlumnoController(this, alumnoID);

        getSupportActionBar().setTitle("Visualizar alumno");


        if(user==null){
            Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            alumnoController.getDetallesAlumno(user);
            alumnoController.getEstres(user);
        }

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alumnoController.goBack(user.getUid());
            }
        });
        eventoFeliz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog(true);
                nuevoEventoDialogo.show();
            }
        });
        eventoTriste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog(false);
                nuevoEventoDialogo.show();
            }
        });


    }


    private void buildDialog(boolean esFeliz) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Nuevo evento").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        View view= getLayoutInflater().inflate(R.layout.nuevoeventosimple, null);
        LinearLayout predetsLayout = view.findViewById(R.id.predetSimpleLayout);

        int widthInDp = 60;
        int heightInDp = 60;
        int widthInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics());
        int heightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

        alumnoController.obtenerPredets(user, new AlumnoController.OnPredetsLoadedListener() {
            @Override
            public void onPredetsLoaded(ArrayList<Predet> predetsFelices, ArrayList<Predet> predetsTristes) {

                if(esFeliz) {
                    if (predetsFelices.size() == 0) {
                        builder.setMessage("No hay situaciones predeterminadas relajantes");
                    }
                    for (Predet predetfeliz : predetsFelices) {
                        System.out.println("PREDET FELIZ: " + predetfeliz.getNombre());
                        System.out.println("PREDET FELIZ: " + predetfeliz.getEstres());
                        ImageButton predet = new ImageButton(getApplicationContext());
                        int resId;

                        if (predetfeliz.getImagen() == null) {
                            resId=getResources().getIdentifier("ic_imagen_basica", "drawable", getPackageName());
                        } else {
                            resId = getResources().getIdentifier(predetfeliz.getImagen(), "drawable", getPackageName());
                        }
                        predet.setBackground(getResources().getDrawable(resId));

                        predet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                alumnoController.aniadirEvento(predetfeliz.getNombre(), predetfeliz.getEstres(), user);
                                nuevoEventoDialogo.dismiss();
                            }
                        });
                        predet.setLayoutParams(new ViewGroup.LayoutParams(widthInPixels, heightInPixels));

                        predetsLayout.addView(predet);

                    }
                }else {
                    if (predetsTristes.size() == 0) {
                        builder.setMessage("No hay situaciones predeterminadas estresantes");
                    }
                    for (Predet predettriste : predetsTristes) {
                        ImageButton predet = new ImageButton(getApplicationContext());
                        System.out.println("PREDET TRISTE: " + predettriste.getNombre());
                        System.out.println("PREDET TRISTE: " + predettriste.getEstres());
                        int resId;

                        if (predettriste.getImagen() == null) {
                            resId=getResources().getIdentifier("ic_imagen_basica", "drawable", getPackageName());
                        } else {
                            resId = getResources().getIdentifier(predettriste.getImagen(), "drawable", getPackageName());
                        }
                        predet.setBackground(getResources().getDrawable(resId));
                        predet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alumnoController.aniadirEvento(predettriste.getNombre(), predettriste.getEstres(), user);
                                nuevoEventoDialogo.dismiss();
                            }
                        });
                        predet.setLayoutParams(new ViewGroup.LayoutParams(widthInPixels, heightInPixels));

                        predetsLayout.addView(predet);
                    }
                }
            }
        });
        builder.setView(view);
        nuevoEventoDialogo= builder.create();
        nuevoEventoDialogo.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        alumnoController.goBack(user.getUid());
    }

    public void userIntent(String tipoCuenta){
        if(tipoCuenta.equals(tipoCuentaArray[0])) {
            Intent intent= new Intent(AlumnoSimple.this, PrincipalProfesor.class);
            startActivity(intent);
            finish();
        }else if (tipoCuenta.equals(tipoCuentaArray[1])){
            Intent intent= new Intent(AlumnoSimple.this, PrincipalPadre.class);
            startActivity(intent);
            finish();
        }

    }
    public void actualizarVaso(int estres){
        ImageView vaso = findViewById(R.id.vaso);
        int imagenes[] = {R.drawable.ic_vaso0, R.drawable.ic_vaso1, R.drawable.ic_vaso2, R.drawable.ic_vaso3, R.drawable.ic_vaso4, R.drawable.ic_vaso5, R.drawable.ic_vaso6, R.drawable.ic_vaso7, R.drawable.ic_vaso8, R.drawable.ic_vaso9, R.drawable.ic_vaso10, R.drawable.ic_vaso11, R.drawable.ic_vaso12, R.drawable.ic_vaso13};
        int numeroImagen = (int) Math.floor(estres/7.69);
        vaso.setImageResource(imagenes[numeroImagen]);

    }
}