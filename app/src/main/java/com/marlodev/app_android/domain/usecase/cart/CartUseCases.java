package com.marlodev.app_android.domain.usecase.cart;

/**
 * Clase contenedora o "caja de herramientas" para todos los casos de uso del carrito.
 * Su propósito principal es simplificar la inyección de dependencias en el ViewModel.
 * En lugar de inyectar múltiples casos de uso en el constructor del ViewModel, se inyecta
 * únicamente esta clase, lo que resulta en un código más limpio y fácil de mantener.
 */
public class CartUseCases {

    // Casos de uso que el ViewModel necesita para operar.
    private final AddOrUpdateCartItemUseCase addOrUpdateItem;
    private final GetCartUseCase getCart;
    private final UpdateCartItemUseCase updateCartItem;
    private final DeleteCartItemUseCase deleteCartItem;

    /**
     * Constructor que agrupa todos los casos de uso del carrito.
     *
     * @param addOrUpdateItem Caso de uso para añadir o actualizar un item.
     * @param getCart         Caso de uso para obtener el carrito completo.
     * @param updateCartItem  Caso de uso para actualizar un item existente (ej. cambiar cantidad).
     * @param deleteCartItem  Caso de uso para eliminar un item.
     */
    public CartUseCases(
            AddOrUpdateCartItemUseCase addOrUpdateItem,
            GetCartUseCase getCart,
            UpdateCartItemUseCase updateCartItem,
            DeleteCartItemUseCase deleteCartItem
    ) {
        this.addOrUpdateItem = addOrUpdateItem;
        this.getCart = getCart;
        this.updateCartItem = updateCartItem;
        this.deleteCartItem = deleteCartItem;
    }

    // --- Getters para que el ViewModel pueda acceder a cada caso de uso específico ---

    public AddOrUpdateCartItemUseCase getAddOrUpdateItem() {
        return addOrUpdateItem;
    }

    public GetCartUseCase getGetCart() {
        return getCart;
    }

    public UpdateCartItemUseCase getUpdateCartItem() {
        return updateCartItem;
    }

    public DeleteCartItemUseCase getDeleteCartItem() {
        return deleteCartItem;
    }
}
