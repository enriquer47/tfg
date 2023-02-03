package com.example.tfg;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Evento {

    public int estres;
    public String nombre;
    //DatabaseReference myRef;

    public Evento() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        //myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

    }
    public Evento(String nombre, int estres) {
            this.estres = estres;
            this.nombre=nombre;
        //myRef= FirebaseDatabase.getInstance("https://registro-tfg-92125-default-rtdb.europe-west1.firebasedatabase.app").getReference();

    }


}


