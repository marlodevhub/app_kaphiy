package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.utils.Result;
import java.math.BigDecimal;
import java.util.List;

/**
 * Caso de uso "inteligente" que orquesta la adición o actualización de un item en el carrito.
 * Centraliza la lógica de negocio para decidir si un producto debe ser añadido como un nuevo item
 * o si debe simplemente incrementar la cantidad de un item ya existente.
 * Esta clase es la única puerta de entrada que el ViewModel debe usar para añadir o actualizar productos,
 * promoviendo un código más limpio, reutilizable y desacoplado de la capa de UI.
 */
public class AddOrUpdateCartItemUseCase {

    // Dependencias de los casos de uso atómicos que realizan una sola acción.
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;

    /**
     * Constructor que inyecta los casos de uso atómicos necesarios para la orquestación.
     *
     * @param addItemToCartUseCase Caso de uso para añadir un nuevo item.
     * @param updateCartItemUseCase Caso de uso para actualizar un item existente.
     */
    public AddOrUpdateCartItemUseCase(AddItemToCartUseCase addItemToCartUseCase, UpdateCartItemUseCase updateCartItemUseCase) {
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
    }

    /**
     * Ejecuta la lógica principal del caso de uso.
     *
     * @param currentItems La lista actual de items en el carrito para verificar si el producto ya existe.
     * @param product El producto que el usuario desea añadir.
     * @param quantity La cantidad del producto a añadir.
     * @return Un LiveData que emite el resultado de la operación, conteniendo la orden actualizada.
     */
    public LiveData<Result<Order>> execute(List<CartItem> currentItems, Product product, int quantity) {
        // Primero, busca si ya existe un item con el mismo ID de producto en el carrito.
        CartItem existingItem = findItemByProductId(currentItems, product.getId());

        if (existingItem != null) {
            // --- LÓGICA DE ACTUALIZACIÓN ---
            // Si el item ya existe, se procede a actualizarlo.
            int newQuantity = existingItem.getQuantity() + quantity;
            existingItem.setQuantity(newQuantity);
            if (existingItem.getUnitPrice() != null) {
                existingItem.setTotalPrice(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            }
            // Se delega la acción de actualización al caso de uso correspondiente.
            return updateCartItemUseCase.execute(existingItem.getId(), existingItem);
        } else {
            // --- LÓGICA DE CREACIÓN ---
            // Si el item es nuevo, se crea una nueva instancia de CartItem.
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            // Se delega la acción de creación al caso de uso correspondiente.
            return addItemToCartUseCase.execute(newItem);
        }
    }

    /**
     * Método de utilidad privado para buscar un CartItem en una lista por el ID del producto.
     *
     * @param items La lista de CartItem en la que buscar.
     * @param productId El ID del producto a encontrar.
     * @return El CartItem encontrado, o null si no existe en la lista.
     */
    private CartItem findItemByProductId(List<CartItem> items, Long productId) {
        if (items == null || productId == null) return null;

        return items.stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId() != null
                        && item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
