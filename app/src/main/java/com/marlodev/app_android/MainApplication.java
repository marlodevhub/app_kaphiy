package com.marlodev.app_android;

import android.app.Application;

import com.marlodev.app_android.di.AppContainer;

public class MainApplication extends Application {

    // Instancia del contenedor de dependencias para toda la app
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        // El contenedor se crea una sola vez, cuando la app inicia
        appContainer = new AppContainer(this);
    }
}
