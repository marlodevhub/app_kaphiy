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
        // 1. Inmediatamente emitir la lista de esqueletos para mostrar la UI de carga.
        _pedidos.setValue(createSkeletonList(5));

        // 2. Simular una carga de red con un retraso.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 3. Cuando la carga finaliza, emitir la lista de datos reales.
            List<Pedido> listaPedidos = new ArrayList<>();
            listaPedidos.add(new Pedido(1, "Benjamin Rumay", "Jirón Del Comercio 456, Cajamarca, Perú", "1.2 km", false));
            listaPedidos.add(new Pedido(2, "Fany Palomino", "Avenida Los Héroes 1020, Cajamarca, Perú", "3.5 km", false));
            listaPedidos.add(new Pedido(3, "Hugo Silva", "Jirón Dos de Mayo 315, Cajamarca, Perú", "2.1 km", false));
            _pedidos.setValue(listaPedidos);
        }, 2000); // Retraso de 2 segundos
    }

    /**
     * Crea una lista de objetos Pedido marcados como esqueletos.
     * @param count El número de esqueletos a crear.
     * @return Una lista de pedidos "fantasma".
     */
    private List<Pedido> createSkeletonList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Pedido.builder().isSkeleton(true).id(-i).build())
                .collect(Collectors.toList());
    }
}
