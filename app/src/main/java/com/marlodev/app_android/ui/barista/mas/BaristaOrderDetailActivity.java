package com.marlodev.app_android.ui.barista.mas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.R;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.domain.dtoParcelable.OrderParcelable;
import com.marlodev.app_android.domain.mappers_parceables.OrderMapper;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.ui.barista.mas.components.OrderItemAdapter;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;

public class BaristaOrderDetailActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private OrderItemAdapter adapter;
    private BaristaOrderDetailViewModel viewModel;
    private MaterialButton btnFinishOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barista_order_detail);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat ic = insets;
            v.setPadding(ic.getSystemWindowInsetLeft(), ic.getSystemWindowInsetTop(),
                    ic.getSystemWindowInsetRight(), ic.getSystemWindowInsetBottom());
            return insets;
        });

        // RecyclerView
        recycler = findViewById(R.id.recycle_items_detail_orden);
        adapter = new OrderItemAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Botón finalizar preparación
        btnFinishOrder = findViewById(R.id.btnFinalizarPreparacion);

        // DI: OrderUseCases
        AppContainer di = ((MainApplication) getApplication()).appContainer;
        viewModel = new ViewModelProvider(this,
                new BaristaOrderDetailViewModelFactory(di.orderUseCases))
                .get(BaristaOrderDetailViewModel.class);

        // Recibir la orden desde Fragment/Intent
        OrderParcelable parcelableOrder = getIntent().getParcelableExtra("ORDER");
        if (parcelableOrder != null) {
            Order order = OrderMapper.fromParcelable(parcelableOrder);
            viewModel.setOrder(order);
        }

        // Observar cambios en la orden para actualizar RecyclerView
        viewModel.getOrder().observe(this, order -> {
            if (order != null) {
                adapter.submitList(order.getItems());
            }
        });

        // Observar finalización de orden
        viewModel.orderFinished.observe(this, event -> {
            if (event != null) {
                Long orderId = event.getContentIfNotHandled();
                if (orderId != null) {
                    Toast.makeText(this, "Orden finalizada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // Botón finalizar
        btnFinishOrder.setOnClickListener(v -> {
            btnFinishOrder.setEnabled(false); // evitar doble click
            viewModel.finishOrder(); // método limpio sin manejo de errores
        });
    }
}

