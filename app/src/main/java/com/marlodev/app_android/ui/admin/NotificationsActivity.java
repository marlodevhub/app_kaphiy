package com.marlodev.app_android.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.NotificationItem;
import com.marlodev.app_android.ui.admin.adpater.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerNotificaciones;
    private NotificationAdapter adapter;
    private List<NotificationItem> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notifications);

        recyclerNotificaciones = findViewById(R.id.recyclerNotificaciones);

        //  LayoutManager robusto
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // últimas notificaciones arriba
        layoutManager.setStackFromEnd(true);
        recyclerNotificaciones.setLayoutManager(layoutManager);

        //  Lista de prueba
        lista = new ArrayList<>();
        lista.add(new NotificationItem("Nuevo pedido recibido", "pedido", "22 nov 2025"));
        lista.add(new NotificationItem("Stock bajo en Capuccino Grande", "stock", "22 nov 2025"));
        lista.add(new NotificationItem("Producto editado exitosamente", "info", "21 nov 2025"));

        //  Adapter
        adapter = new NotificationAdapter(lista);
        recyclerNotificaciones.setAdapter(adapter);
    }
}