package com.marlodev.app_android.ui.delivery;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.marlodev.app_android.domain.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeliveryViewModel extends ViewModel {

    private final MutableLiveData<List<Pedido>> _pedidos = new MutableLiveData<>();
    public final LiveData<List<Pedido>> pedidos = _pedidos;

    public DeliveryViewModel() {
        loadPedidos();
    }

    private void loadPedidos() {

        // 1. Emitir esqueletos para mostrar la UI de carga
        _pedidos.setValue(createSkeletonList(5));

        // 2. Simular carga desde API
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            List<Pedido> listaPedidos = new ArrayList<>();

            listaPedidos.add(
                    Pedido.builder()
                            .idPedido(1)
                            .cliente("Benjamin Rumay")
                            .direccion("Jirón Del Comercio 456, Cajamarca, Perú")
                            .distancia("1.2 km")
                            .fechaEntrega("2025-01-20 13:45")
                            .total(28.50)
                            .propina(2.50)
                            .aceptado(true)
                            .entregado(true)
                            .isSkeleton(false)
                            .build()
            );

            listaPedidos.add(
                    Pedido.builder()
                            .idPedido(2)
                            .cliente("Fany Palomino")
                            .direccion("Av. Los Héroes 1020, Cajamarca, Perú")
                            .distancia("3.5 km")
                            .fechaEntrega("2025-01-18 18:20")
                            .total(18.00)
                            .propina(1.00)
                            .aceptado(true)
                            .entregado(true)
                            .isSkeleton(false)
                            .build()
            );

            listaPedidos.add(
                    Pedido.builder()
                            .idPedido(3)
                            .cliente("Hugo Silva")
                            .direccion("Jr. Dos de Mayo 315, Cajamarca, Perú")
                            .distancia("2.1 km")
                            .fechaEntrega("2025-01-10 09:30")
                            .total(12.50)
                            .propina(0.50)
                            .aceptado(true)
                            .entregado(false)
                            .isSkeleton(false)
                            .build()
            );

            listaPedidos.add(
                    Pedido.builder()
                            .idPedido(4)
                            .cliente("Ross Mera")
                            .direccion("Psje. Santa Rosa 221, Cajamarca")
                            .distancia("4.8 km")
                            .fechaEntrega("2025-01-05 16:00")
                            .total(35.00)
                            .propina(3.00)
                            .aceptado(true)
                            .entregado(true)
                            .isSkeleton(false)
                            .build()
            );

            _pedidos.setValue(listaPedidos);

        }, 2000); // demora simulada de 2 segundos
    }

    /**
     * Genera una lista de placeholders para efecto “loading”.
     */
    private List<Pedido> createSkeletonList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Pedido.builder()
                        .idPedido(-i)
                        .isSkeleton(true)
                        .build()
                ).collect(Collectors.toList());
    }
}
