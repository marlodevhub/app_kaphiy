package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.utils.Result;

public class GetProductByIdUseCase {
    private final ProductRepositoryImpl productRepository;

    public GetProductByIdUseCase(ProductRepositoryImpl productRepository) {
        this.productRepository = productRepository;
    }

    public LiveData<Result<Product>> invoke(long productId) {
        return Transformations.map(productRepository.getProductById(productId), product -> {
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("Producto no encontrado");
            }
        });
    }
}
