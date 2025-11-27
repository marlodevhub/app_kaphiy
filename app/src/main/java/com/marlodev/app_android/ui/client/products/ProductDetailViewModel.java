package com.marlodev.app_android.ui.client.products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.product.ProductUseCases;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;

public class ProductDetailViewModel extends ViewModel {

    private final ProductUseCases productUseCases;  // <-- Ahora usamos ProductUseCases
    public LiveData<Result<Product>> productResult;

    private final MutableLiveData<Integer> _quantity = new MutableLiveData<>(1);
    private final MediatorLiveData<BigDecimal> _totalPrice = new MediatorLiveData<>();
    private final MediatorLiveData<BigDecimal> _totalOldPrice = new MediatorLiveData<>();
    public LiveData<Integer> quantity = _quantity;

    public LiveData<BigDecimal> totalPrice = _totalPrice;
    public LiveData<BigDecimal> totalOldPrice = _totalOldPrice;

    public ProductDetailViewModel(ProductUseCases productUseCases) {
        this.productUseCases = productUseCases;
        MediatorLiveData<Result<Product>> productMediator = new MediatorLiveData<>();
        this.productResult = productMediator;

        // Recalcular precios cuando cambia el producto o la cantidad
        _totalPrice.addSource(productResult, result -> recalculatePrices());
        _totalPrice.addSource(quantity, qty -> recalculatePrices());
        _totalOldPrice.addSource(productResult, result -> recalculatePrices());
        _totalOldPrice.addSource(quantity, qty -> recalculatePrices());
    }

    /** Recalcula precios totales basado en cantidad y producto */
    private void recalculatePrices() {
        Result<Product> productResult = this.productResult.getValue();
        Integer quantity = _quantity.getValue();

        if (productResult != null && productResult.data != null && quantity != null) {
            Product product = productResult.data;

            if (product.getPrice() != null) {
                _totalPrice.setValue(product.getPrice().multiply(new BigDecimal(quantity)));
            }

            if (product.getOldPrice() != null) {
                _totalOldPrice.setValue(product.getOldPrice().multiply(new BigDecimal(quantity)));
            } else {
                _totalOldPrice.setValue(null);
            }
        }
    }

    /** Carga un producto por ID usando ProductUseCases */
    public void loadProductById(long productId) {
        ((MediatorLiveData<Result<Product>>) this.productResult).setValue(Result.loading());

        LiveData<Result<Product>> source = productUseCases.getProductByIdUseCase().invoke(productId);
        ((MediatorLiveData<Result<Product>>) this.productResult).addSource(source, result -> {
            if (result != null) {
                ((MediatorLiveData<Result<Product>>) this.productResult).setValue(result);
            } else {
                ((MediatorLiveData<Result<Product>>) this.productResult).setValue(Result.error("No se pudo cargar el producto"));
            }
            ((MediatorLiveData<Result<Product>>) this.productResult).removeSource(source);
        });
    }

    public void increaseQuantity() {
        Integer current = _quantity.getValue();
        if (current != null) _quantity.setValue(current + 1);
    }

    public void decreaseQuantity() {
        Integer current = _quantity.getValue();
        if (current != null && current > 1) _quantity.setValue(current - 1);
    }
}
