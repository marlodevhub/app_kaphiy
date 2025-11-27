package com.marlodev.app_android.ui.client.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.utils.Event;
import com.marlodev.app_android.utils.Result;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ClientCartViewModel extends ViewModel {

    private final CartUseCases cartUseCases;

    // LiveData para el estado de la UI (persistente)
    private final MutableLiveData<List<CartItem>> _cartItems = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> _isCartEmpty = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> _totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> _totalPrice = new MutableLiveData<>(BigDecimal.ZERO);

    // LiveData para eventos de un solo uso (Toast, Navegación)
    private final MutableLiveData<Event<String>> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Event<Result<Order>>> _checkoutResult = new MutableLiveData<>();
    private final MutableLiveData<Event<Result<String>>> _cartOperationResult = new MutableLiveData<>();

    // LiveData públicos inmutables expuestos a la UI.
    public final LiveData<List<CartItem>> cartItems = _cartItems;
    public final LiveData<Boolean> isCartEmpty = _isCartEmpty;
    public final LiveData<Boolean> isLoading = _isLoading;
    public final LiveData<Integer> totalItems = _totalItems;
    public final LiveData<BigDecimal> totalPrice = _totalPrice;
    public final LiveData<Event<String>> errorMessage = _errorMessage;
    public final LiveData<Event<Result<Order>>> checkoutResult = _checkoutResult;
    public final LiveData<Event<Result<String>>> cartOperationResult = _cartOperationResult;


    public ClientCartViewModel(CartUseCases cartUseCases) {
        this.cartUseCases = cartUseCases;
        loadCart(); // Carga inicial del carrito
    }

    // --- MÉTODOS DE ACCIÓN PRINCIPALES (Expuestos a la UI) ---

    public void refreshCart() {
        loadCart();
    }

    public void addItemToCart(Product product, int quantity) {
        if (_cartItems.getValue() == null) return;
        LiveData<Result<Order>> useCaseLiveData = cartUseCases.getAddOrUpdateItem().execute(_cartItems.getValue(), product, quantity);

        observeUseCaseResult(
                useCaseLiveData,
                order -> {
                    updateStateFromOrder(order);
                    _cartOperationResult.postValue(new Event<>(Result.success("Producto agregado al carrito")));
                },
                message -> _cartOperationResult.postValue(new Event<>(Result.error(message)))
        );
    }

    public void updateQuantity(CartItem item, int newQty) {
        if (newQty <= 0) {
            deleteItem(item); // Si la cantidad es cero o menos, se elimina el item.
            return;
        }
        if (_cartItems.getValue() == null) return;

        LiveData<Result<Order>> useCaseLiveData = cartUseCases.getUpdateCartItem().execute(_cartItems.getValue(), item.getId(), newQty);
        observeUseCaseResult(useCaseLiveData, this::updateStateFromOrder, msg -> _errorMessage.setValue(new Event<>(msg)));
    }

    public void deleteItem(CartItem item) {
        LiveData<Result<Order>> useCaseLiveData = cartUseCases.getDeleteCartItem().execute(item.getId());
        observeUseCaseResult(useCaseLiveData, this::updateStateFromOrder, msg -> _errorMessage.postValue(new Event<>(msg)));
    }

    public void checkout() {
        LiveData<Result<Order>> useCaseLiveData = cartUseCases.getCheckoutUseCase().execute();

        observeUseCaseResult(
                useCaseLiveData,
                order -> {
                    _checkoutResult.setValue(new Event<>(Result.success(order)));
                    loadCart(); // Después de un checkout exitoso, recargamos el carrito (que ahora estará vacío).
                },
                message -> _checkoutResult.setValue(new Event<>(Result.error(message)))
        );
    }

    // --- LÓGICA INTERNA Y ORQUESTACIÓN ---

    private void loadCart() {
        LiveData<Result<Order>> useCaseLiveData = cartUseCases.getGetCart().execute();
        observeUseCaseResult(useCaseLiveData, this::updateStateFromOrder, msg -> _errorMessage.postValue(new Event<>(msg)));
    }

    private <T> void observeUseCaseResult(final LiveData<Result<T>> useCaseLiveData, final Consumer<T> onSuccess, final Consumer<String> onError) {
        if (Boolean.TRUE.equals(_isLoading.getValue())) {
            return;
        }
        _isLoading.setValue(true);

        useCaseLiveData.observeForever(new Observer<Result<T>>() {
            @Override
            public void onChanged(Result<T> result) {
                if (result.status == Result.Status.LOADING) {
                    return;
                }

                useCaseLiveData.removeObserver(this);
                _isLoading.setValue(false);

                if (result.status == Result.Status.SUCCESS) {
                    if (onSuccess != null) {
                        onSuccess.accept(result.data);
                    }
                } else if (result.status == Result.Status.ERROR) {
                    if (onError != null) {
                        onError.accept(result.message);
                    }
                }
            }
        });
    }

    // --- MÉTODOS AUXILIARES DE ACTUALIZACIÓN DE ESTADO ---

    private void updateStateFromOrder(Order order) {
        List<CartItem> items = (order != null && order.getItems() != null) ? order.getItems() : Collections.emptyList();
        _cartItems.postValue(items);
        _isCartEmpty.postValue(items.isEmpty());
        updateTotals(items);
    }

    private void updateTotals(List<CartItem> items) {
        int count = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        _totalItems.postValue(count);
        _totalPrice.postValue(total);
    }
}
