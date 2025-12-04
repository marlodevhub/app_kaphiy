package com.marlodev.app_android.ui.barista.ordenes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.R;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.domain.dtoParcelable.OrderParcelable;
import com.marlodev.app_android.domain.mappers_parceables.OrderMapper;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.ui.barista.mas.BaristaOrderDetailActivity;
import com.marlodev.app_android.ui.barista.ordenes.components.OrderCardBaristaAdapter;
import com.marlodev.app_android.utils.Event;

public class BaristaOrderFragment extends Fragment {


    private RecyclerView recycler;

    private View rootView;
    private OrderCardBaristaAdapter adapter;
    private BaristaOrderViewModel viewModel;

    private Button btnNuevo, btnPreparando, btnListo;
    private Button btnPrevious, btnNext;
    private TextView tvPageInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_barista_ordenes, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        RecyclerView recycler = view.findViewById(R.id.recycle_ordenes_espera);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter();
        recycler.setAdapter(adapter);

        // Botones
        btnNuevo = view.findViewById(R.id.btnNuevos);
        btnPreparando = view.findViewById(R.id.btnPreparando);
        btnListo = view.findViewById(R.id.btnListos);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        tvPageInfo = view.findViewById(R.id.tv_page_info);

        // DI & ViewModel
        AppContainer di = ((MainApplication) requireActivity().getApplication()).appContainer;
        viewModel = new ViewModelProvider(requireActivity(),
                new BaristaOrdenesViewModelFactory(di.orderUseCases))
                .get(BaristaOrderViewModel.class);

        // Observers
        viewModel.getVisibleOrders().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.getPageInfo().observe(getViewLifecycleOwner(), text -> {
            tvPageInfo.setText(text);
            btnPrevious.setEnabled(viewModel.hasPreviousPage());
            btnNext.setEnabled(viewModel.hasNextPage());
        });



        // Click listeners estado
        btnNuevo.setOnClickListener(v -> {
            viewModel.setFilter(OrderStatus.EN_ESPERA);
            setSelectedButton(btnNuevo);
        });

        btnPreparando.setOnClickListener(v -> {
            viewModel.setFilter(OrderStatus.EN_PREPARACION);
            setSelectedButton(btnPreparando);
        });

        btnListo.setOnClickListener(v -> {
            viewModel.setFilter(OrderStatus.LISTO_PARA_ENTREGA);
            setSelectedButton(btnListo);
        });

        // Paginación
        btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        btnNext.setOnClickListener(v -> viewModel.nextPage());

        // Adapter callbacks
        adapter.setOnItemClickListener(new OrderCardBaristaAdapter.OnItemClickListener() {
            @Override
            public void onStartPreparation(Order order) {
                viewModel.startPreparation(order.getId());
            }

            @Override
            public void onOpenOrderDetail(Order order) {
                // Convertir a Parcelable
                OrderParcelable parcelableOrder = OrderMapper.toParcelable(order);

                Intent intent = new Intent(requireContext(), BaristaOrderDetailActivity.class);
                intent.putExtra("ORDER", parcelableOrder); // ✅ ahora sí funciona
                startActivity(intent);
            }
        });

        // Selección por defecto
        setSelectedButton(btnNuevo);

        // Primera carga
        viewModel.loadPage(0, OrderStatus.EN_ESPERA);
    }

    private void setSelectedButton(Button selected) {
        btnNuevo.setSelected(false);
        btnPreparando.setSelected(false);
        btnListo.setSelected(false);
        selected.setSelected(true);
    }
}
