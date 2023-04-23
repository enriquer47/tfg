package com.example.tfg;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


//CLASE NECESARIA PARA EL FUNCIONAMIENTO DE LA LECTURA DE IMÁGENES EN FIREBASE
@GlideModule
public class MyGlideModule extends AppGlideModule {

    // Optional: override applyOptions() to set custom configuration options for Glide
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Add custom configuration options here
    }

    // Optional: override registerComponents() to provide a custom OkHttpClient or HttpUrlConnection
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Add custom components here
    }
}