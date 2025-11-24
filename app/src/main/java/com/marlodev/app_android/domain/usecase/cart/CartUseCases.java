package com.marlodev.app_android.domain.usecase.cart;

/**
 * Clase contenedora (caja de herramientas) para todos los casos de uso del carrito.
 * Simplifica la inyección de dependencias en el ViewModel.
 */
public class CartUseCases {

    private final AddOrUpdateCartItemUseCase addOrUpdateItem;
    private final GetCartUseCase getCart;
    private final UpdateCartItemUseCase updateCartItem; // <--- Restaurado
    private final DeleteCartItemUseCase deleteCartItem;

    public CartUseCases(
            AddOrUpdateCartItemUseCase addOrUpdateItem,
            GetCartUseCase getCart,
            UpdateCartItemUseCase updateCartItem, // <--- Restaurado
            DeleteCartItemUseCase deleteCartItem
    ) {
        this.addOrUpdateItem = addOrUpdateItem;
        this.getCart = getCart;
        this.updateCartItem = updateCartItem;
        this.deleteCartItem = deleteCartItem;
    }

    public AddOrUpdateCartItemUseCase getAddOrUpdateItem() {
        return addOrUpdateItem;
    }

    public GetCartUseCase getGetCart() {
        return getCart;
    }

    // Getter restaurado para que el ViewModel pueda usarlo
    public UpdateCartItemUseCase getUpdateCartItem() {
        return updateCartItem;
    }

    public DeleteCartItemUseCase getDeleteCartItem() {
        return deleteCartItem;
    }
}
