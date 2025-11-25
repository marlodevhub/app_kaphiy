package com.marlodev.app_android.ui.client.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClientCartViewModel extends ViewModel {

    private final CartUseCases cartUseCases;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> isCartEmpty = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> totalPrice = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Result<Order>> checkoutResult = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> cartOperationResult = new MutableLiveData<>();

    public ClientCartViewModel(CartUseCases cartUseCases) {
        this.cartUseCases = cartUseCases;
        loadCart();
    }

    //<editor-fold desc="Getters">
    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Boolean> getIsCartEmpty() { return isCartEmpty; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<BigDecimal> getTotalPrice() { return totalPrice; }
    public LiveData<Result<Order>> getCheckoutResult() { return checkoutResult; }
    public LiveData<Result<String>> getCartOperationResult() { return cartOperationResult; }
    //</editor-fold>

    public void loadCart() {
        isLoading.setValue(true);
        final LiveData<Result<Order>> getCartLiveData = cartUseCases.getGetCart().execute();
        getCartLiveData.observeForever(new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result.status != Result.Status.LOADING) {
                    getCartLiveData.removeObserver(this);
                    isLoading.setValue(false);
                }
                if (result.status == Result.Status.SUCCESS) {
                    updateStateFromOrder(result.data);
                } else if (result.status == Result.Status.ERROR) {
                    errorMessage.postValue(result.message);
                }
            }
        });
    }

    public void refreshCart() {
        loadCart();
    }

    public void addItemToCart(Product product, int quantity) {
        // Esta operación es pesada, mantenemos el bloqueo de UI.
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            cartOperationResult.postValue(Result.error("Operación en curso, intente de nuevo."));
            return;
        }
        isLoading.setValue(true);

        List<CartItem> currentItemsList = cartItems.getValue();
        final LiveData<Result<Order>> addItemLiveData = cartUseCases.getAddOrUpdateItem().execute(currentItemsList, product, quantity);
        addItemLiveData.observeForever(new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result.status == Result.Status.LOADING) return;

                addItemLiveData.removeObserver(this);

                if (result.status == Result.Status.SUCCESS) {
                    cartOperationResult.postValue(Result.success("Producto agregado al carrito"));
                    loadCart();
                } else {
                    isLoading.setValue(false);
                    cartOperationResult.postValue(Result.error(result.message));
                }
            }
        });
    }

    public void checkout() {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;
        isLoading.setValue(true);
        checkoutResult.setValue(Result.loading());

        final LiveData<Result<Order>> checkoutLiveData = cartUseCases.getCheckoutUseCase().execute();
        checkoutLiveData.observeForever(new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result.status == Result.Status.LOADING) return;

                checkoutLiveData.removeObserver(this);
                checkoutResult.setValue(result);

                if (result.status == Result.Status.SUCCESS) {
                    loadCart();
                } else {
                    isLoading.setValue(false);
                    loadCart();
                }
            }
        });
    }

    public void deleteItem(CartItem item) {
        performModificationOperation(cartUseCases.getDeleteCartItem().execute(item.getId()));
    }

    // --- SOLUCIÓN OPTIMISTA PARA ACTUALIZAR CANTIDAD ---
    public void updateQuantity(CartItem item, int newQty) {
        if (newQty <= 0) {
            deleteItem(item);
            return;
        }

        List<CartItem> originalItems = cartItems.getValue();
        if (originalItems == null) return;

        // 1. Crea una nueva lista con la cantidad actualizada (Actualización Optimista)
        List<CartItem> newItems = new ArrayList<>();
        CartItem itemToUpdateInBackend = null;

        for (CartItem currentItem : originalItems) {
            if (Objects.equals(currentItem.getId(), item.getId())) {
                CartItem updatedItem = CartItem.builder()
                        .id(currentItem.getId())
                        .product(currentItem.getProduct())
                        .variant(currentItem.getVariant())
                        .extras(currentItem.getExtras())
                        .quantity(newQty)
                        .unitPrice(currentItem.getUnitPrice())
                        .totalPrice(currentItem.getUnitPrice() != null ? currentItem.getUnitPrice().multiply(BigDecimal.valueOf(newQty)) : null)
                        .build();
                newItems.add(updatedItem);
                itemToUpdateInBackend = updatedItem;
            } else {
                newItems.add(currentItem);
            }
        }

        if (itemToUpdateInBackend == null) return;

        // 2. Actualiza la UI INMEDIATAMENTE
        cartItems.setValue(newItems);
        updateTotals(newItems);

        // 3. Llama al servidor en segundo plano
        final LiveData<Result<Order>> updateLiveData = cartUseCases.getUpdateCartItem().execute(item.getId(), itemToUpdateInBackend);
        updateLiveData.observeForever(new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result.status == Result.Status.LOADING) return;

                updateLiveData.removeObserver(this);

                if (result.status == Result.Status.ERROR) {
                    // 4. Si falla, revierte la UI al estado original y muestra un error
                    errorMessage.postValue(result.message != null ? result.message : "Error al actualizar");
                    cartItems.postValue(originalItems); // Revertir
                    updateTotals(originalItems);
                } else if (result.status == Result.Status.SUCCESS) {
                    // 5. Si tiene éxito, opcionalmente puedes resincronizar con la respuesta del servidor para máxima consistencia.
                    updateStateFromOrder(result.data);
                }
            }
        });
    }

    private void performModificationOperation(LiveData<Result<Order>> operationLiveData) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;
        isLoading.setValue(true);

        operationLiveData.observeForever(new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result.status == Result.Status.LOADING) return;

                operationLiveData.removeObserver(this);

                if (result.status == Result.Status.SUCCESS) {
                    loadCart();
                } else { 
                    isLoading.setValue(false);
                    errorMessage.postValue(result.message);
                }
            }
        });
    }

    private void updateStateFromOrder(Order order) {
        List<CartItem> items = (order != null && order.getItems() != null) ? order.getItems() : Collections.emptyList();
        cartItems.postValue(items);
        isCartEmpty.postValue(items.isEmpty());
        updateTotals(items);
    }

    private void updateTotals(List<CartItem> items) {
        int count = items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
        BigDecimal total = items.stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalItems.postValue(count);
        totalPrice.postValue(total);
    }
}
