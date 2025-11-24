package com.marlodev.app_android.ui.client.products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.utils.Result;

public class ProductDetailViewModel extends ViewModel {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final MediatorLiveData<Result<Product>> _productResult = new MediatorLiveData<>();
    public LiveData<Result<Product>> productResult = _productResult;

    // 1. El ViewModel ahora es el dueño del estado de la cantidad.
    private final MutableLiveData<Integer> _quantity = new MutableLiveData<>(1);
    public LiveData<Integer> quantity = _quantity;

    public ProductDetailViewModel(GetProductByIdUseCase getProductByIdUseCase) {
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    /** Carga un producto por ID */
    public void loadProductById(long productId) {
        _productResult.setValue(Result.loading());

        LiveData<Result<Product>> source = getProductByIdUseCase.invoke(productId);
        _productResult.addSource(source, result -> {
            if (result != null) {
                _productResult.setValue(result);
            } else {
                _productResult.setValue(Result.error("No se pudo cargar el producto"));
            }
            _productResult.removeSource(source);
        });
    }

    // 2. Métodos públicos para que la UI manipule el estado de la cantidad.

    /** Incrementa la cantidad en 1. */
    public void increaseQuantity() {
        Integer current = _quantity.getValue();
        if (current != null) {
            _quantity.setValue(current + 1);
        }
    }

    /**
     * Decrementa la cantidad en 1, con una regla de negocio para no bajar de 1.
     */
    public void decreaseQuantity() {
        Integer current = _quantity.getValue();
        if (current != null && current > 1) {
            _quantity.setValue(current - 1);
        }
    }

}
