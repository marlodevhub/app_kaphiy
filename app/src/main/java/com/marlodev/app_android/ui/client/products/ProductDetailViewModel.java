package com.marlodev.app_android.ui.client.products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.GetProductByIdUseCase;
import com.marlodev.app_android.utils.Result;

public class ProductDetailViewModel extends ViewModel {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final MediatorLiveData<Result<Product>> _productResult = new MediatorLiveData<>();
    public LiveData<Result<Product>> productResult = _productResult;

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
}
