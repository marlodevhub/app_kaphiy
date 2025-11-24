package com.marlodev.app_android.domain.usecase.cart;

public class CartUseCases {

    private final AddItemToCartUseCase addItemToCart;
    private final GetCartUseCase getCart;
    private final UpdateCartItemUseCase updateCartItem;
    private final DeleteCartItemUseCase deleteCartItem;

    public CartUseCases(
            AddItemToCartUseCase addItemToCart,
            GetCartUseCase getCart,
            UpdateCartItemUseCase updateCartItem,
            DeleteCartItemUseCase deleteCartItem
    ) {
        this.addItemToCart = addItemToCart;
        this.getCart = getCart;
        this.updateCartItem = updateCartItem;
        this.deleteCartItem = deleteCartItem;
    }

    public AddItemToCartUseCase getAddItemToCart() {
        return addItemToCart;
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
