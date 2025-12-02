package com.marlodev.app_android.ui.barista.ordenes;

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

import com.marlodev.app_android.R;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.ui.barista.ordenes.components.OrderCardBaristaAdapter;

public class BaristaOrderFragment extends Fragment {

    private OrderCardBaristaAdapter adapter;
    private BaristaOrderViewModel viewModel;

    private Button btnPrevious, btnNext;
    private TextView tvPageInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barista_ordenes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycle_ordenes_espera);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter();
        recycler.setAdapter(adapter);

        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        tvPageInfo = view.findViewById(R.id.tv_page_info);

        AppContainer di = ((MainApplication) requireActivity().getApplication()).appContainer;

        viewModel = new ViewModelProvider(requireActivity(),
                new BaristaOrdenesViewModelFactory(di.orderUseCases))
                .get(BaristaOrderViewModel.class);

        // ------ Observers ------
        viewModel.getVisibleOrders().observe(getViewLifecycleOwner(), adapter::submitList);

        viewModel.getPageInfo().observe(getViewLifecycleOwner(), text -> {
            tvPageInfo.setText(text);
            btnPrevious.setEnabled(viewModel.hasPreviousPage());
            btnNext.setEnabled(viewModel.hasNextPage());
        });

        // ------ Acciones ------
        btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        btnNext.setOnClickListener(v -> viewModel.nextPage());

        adapter.setOnItemClickListener(order ->
                viewModel.startPreparation(order.getId())
        );

        // Primera carga
        viewModel.loadPage(0);
    }
}
