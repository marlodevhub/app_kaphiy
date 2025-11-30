package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

//obtener el historial de órdenes del barista
/**
 * Caso de uso para obtener el historial de órdenes de un barista.
 */
//public class GetBaristaOrderHistoryUseCase {
//    private final OrderRepository orderRepository;
//
//    public GetBaristaOrderHistoryUseCase(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    public LiveData<Result<List<Order>>> invoke(long baristaId) {
//        if (baristaId <= 0) {
//            // Aquí podrías retornar un LiveData con error si quieres
//            throw new IllegalArgumentException("baristaId debe ser mayor a 0");
//        }
////        return orderRepository.getOrderHistory(baristaId);
//    }
//}
