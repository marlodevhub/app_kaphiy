package com.marlodev.app_android.ui.client.products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;

public class ProductDetailViewModel extends ViewModel {

    private final GetProductByIdUseCase getProductByIdUseCase;
    public LiveData<Result<Product>> productResult;

    private final MutableLiveData<Integer> _quantity = new MutableLiveData<>(1);
    public LiveData<Integer> quantity = _quantity;

    private final MediatorLiveData<BigDecimal> _totalPrice = new MediatorLiveData<>();
    public LiveData<BigDecimal> totalPrice = _totalPrice;

    // NUEVO: LiveData para el precio antiguo total calculado.
    private final MediatorLiveData<BigDecimal> _totalOldPrice = new MediatorLiveData<>();
    public LiveData<BigDecimal> totalOldPrice = _totalOldPrice;

    public ProductDetailViewModel(GetProductByIdUseCase getProductByIdUseCase) {
        this.getProductByIdUseCase = getProductByIdUseCase;
        MediatorLiveData<Result<Product>> productMediator = new MediatorLiveData<>();
        this.productResult = productMediator;

        // Configuración de los MediatorLiveData para recalcular precios.
        _totalPrice.addSource(productResult, result -> recalculatePrices());
        _totalPrice.addSource(quantity, qty -> recalculatePrices());
        _totalOldPrice.addSource(productResult, result -> recalculatePrices());
        _totalOldPrice.addSource(quantity, qty -> recalculatePrices());
    }

    /** Recalcula TODOS los precios (actual y antiguo) basado en el estado actual. */
    private void recalculatePrices() {
        Result<Product> productResult = this.productResult.getValue();
        Integer quantity = _quantity.getValue();

        if (productResult != null && productResult.data != null && quantity != null) {
            Product product = productResult.data;

            // Calcular y emitir el precio actual total
            if (product.getPrice() != null) {
                BigDecimal total = product.getPrice().multiply(new BigDecimal(quantity));
                _totalPrice.setValue(total);
            }

            // Calcular y emitir el precio antiguo total (solo si existe)
            if (product.getOldPrice() != null) {
                BigDecimal totalOld = product.getOldPrice().multiply(new BigDecimal(quantity));
                _totalOldPrice.setValue(totalOld);
            } else {
                _totalOldPrice.setValue(null); // Importante: emitir null si no hay precio antiguo
            }
        }
    }

    /** Carga un producto por ID */
    public void loadProductById(long productId) {
        ((MediatorLiveData<Result<Product>>)this.productResult).setValue(Result.loading());

        LiveData<Result<Product>> source = getProductByIdUseCase.invoke(productId);
        ((MediatorLiveData<Result<Product>>)this.productResult).addSource(source, result -> {
            if (result != null) {
                ((MediatorLiveData<Result<Product>>)this.productResult).setValue(result);
            } else {
                ((MediatorLiveData<Result<Product>>)this.productResult).setValue(Result.error("No se pudo cargar el producto"));
            }
            ((MediatorLiveData<Result<Product>>)this.productResult).removeSource(source);
        });
    }

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
