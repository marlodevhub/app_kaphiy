package com.marlodev.app_android.ui.barista.mas;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.dtoParcelable.OrderParcelable;
import com.marlodev.app_android.domain.mappers_parceables.OrderMapper;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.ui.barista.mas.components.OrderItemAdapter;

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

        recycler = findViewById(R.id.recycle_items_detail_orden);
        adapter = new OrderItemAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnFinishOrder = findViewById(R.id.btnFinalizarPreparacion);

        viewModel = new ViewModelProvider(this).get(BaristaOrderDetailViewModel.class);

        // Recibir la orden desde Fragment
        OrderParcelable parcelableOrder = getIntent().getParcelableExtra("ORDER");
        if (parcelableOrder != null) {
            Order order = OrderMapper.fromParcelable(parcelableOrder); // Convierte a modelo de dominio
            viewModel.setOrder(order);
        }        // Observa cambios de la orden
        viewModel.getOrder().observe(this, o -> {
            if (o != null) adapter.submitList(o.getItems());
        });

        // Botón finalizar
        btnFinishOrder.setOnClickListener(v -> {
            viewModel.finishOrder();
            btnFinishOrder.setEnabled(false);
        });
    }
}
