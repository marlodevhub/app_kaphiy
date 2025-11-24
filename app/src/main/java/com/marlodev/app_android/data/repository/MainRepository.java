
package com.marlodev.app_android.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlodev.app_android.domain.model.Banner;
import com.marlodev.app_android.domain.model.Tag;

import java.util.ArrayList;

// Clase encargada de interactuar con Firebase Realtime Database
public class MainRepository {

    // Instancia de FirebaseDatabase
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    /**
     * Carga las categorías (tags) desde Firebase.
     * @return LiveData con lista de Tag para ser observado desde ViewModel
     */
    public LiveData<ArrayList<Tag>> loadTags() {
        // MutableLiveData para exponer los datos a la UI
        MutableLiveData<ArrayList<Tag>> listData = new MutableLiveData<>();

        // Referencia a la ruta "tags" en Firebase
        DatabaseReference ref = firebaseDatabase.getReference("tags");

        // Listener que se activa cuando hay cambios en los datos
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Tag> list = new ArrayList<>();
                // Iterar sobre cada hijo en la ruta "tags"
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Convertir snapshot a objeto Tag
                    Tag item = childSnapshot.getValue(Tag.class);
                    if (item != null) list.add(item); // agregar si no es nulo
                }
                // Actualizar LiveData con la lista
                listData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de Firebase (actualmente vacío)
            }
        });

        return listData; // Retornar LiveData observable
    }

    /**
     * Carga los banners desde Firebase.
     * @return LiveData con lista de Banner para ser observado desde ViewModel
     */
    public LiveData<ArrayList<Banner>> loadBanners() {
        MutableLiveData<ArrayList<Banner>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("banners");

        // Listener que se activa cuando hay cambios en los datos
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Banner> list = new ArrayList<>();
                // Iterar sobre cada hijo en la ruta "banners"
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Banner item = childSnapshot.getValue(Banner.class);
                    if (item != null) list.add(item);
                }
                // Actualizar LiveData con la lista
                listData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de Firebase (actualmente vacío)
            }
        });

        return listData; // Retornar LiveData observable
    }
}

