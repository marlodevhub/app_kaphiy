package com.marlodev.app_android.domain.repository;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Product;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface ProductRepository {

    // Obtener todos los productos
    LiveData<List<Product>> getAllProducts();

    // Obtener un producto por su ID
    LiveData<Product> getProductById(long id);

    // Crear un producto
    void createProduct(RequestBody productJson, MultipartBody.Part[] images);

    // Actualizar un producto
    void updateProduct(long productId, RequestBody productJson, MultipartBody.Part[] images);

    // Eliminar un producto
    void deleteProduct(long productId);
}