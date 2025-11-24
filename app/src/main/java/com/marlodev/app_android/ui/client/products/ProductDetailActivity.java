package com.marlodev.app_android.ui.client.products;

import static com.marlodev.app_android.utils.Result.Status.LOADING;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.marlodev.app_android.databinding.ActivityProductDetailBinding;
import com.marlodev.app_android.di.DependencyProvider;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.ui.auth.login.LoginActivity;
import com.marlodev.app_android.ui.client.cart.ClientCartViewModel;
import com.marlodev.app_android.ui.client.cart.ClientCartViewModelFactory;
import com.marlodev.app_android.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;
    private ClientCartViewModel cartViewModel;
    private SessionManager sessionManager;
    private long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupEdgeToEdgeDisplay();
        initViewModels();
        setupUI();
        observeViewModel();

        productId = getIntent().getLongExtra("productId", -1);
        if (productId != -1) {
            viewModel.loadProductById(productId);
        }
    }

    private void initViewModels() {
        ProductDetailViewModelFactory factory = DependencyProvider.provideProductDetailViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, factory).get(ProductDetailViewModel.class);

        ClientCartViewModelFactory cartFactory = DependencyProvider.provideClientCartViewModelFactory(getApplicationContext());
        cartViewModel = new ViewModelProvider(this, cartFactory).get(ClientCartViewModel.class);
    }

    private void setupEdgeToEdgeDisplay() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    addProductToCart();
                }
            }
    );

    private void setupUI() {
        binding.btnArrowLeft.setOnClickListener(v -> finish());
        binding.btnPlus.setOnClickListener(v -> updateQuantity(1));
        binding.btnMinus.setOnClickListener(v -> updateQuantity(-1));
        binding.btnAddCart.setOnClickListener(v -> addProductToCart());
    }

    private void observeViewModel() {
        viewModel.productResult.observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    showLoading(true);
                    showContent(false);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (result.data != null) {
                        bindProduct(result.data);
                        showContent(true);
                    } else {
                        showError("No se encontraron datos del producto.");
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    showError(result.message);
                    break;
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBarDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showContent(boolean visible) {
        binding.scrollContent.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void bindProduct(Product product) {
        binding.txtTitleProduct.setText(product.getName());
        binding.tvCurrentPrice.setText(String.format("S/%.2f", product.getPrice()));
        if (product.getOldPrice() != null) {
            binding.tvOldPrice.setText(String.format("S/%.2f", product.getOldPrice()));
            binding.tvOldPrice.setVisibility(View.VISIBLE);
        } else {
            binding.tvOldPrice.setVisibility(View.GONE);
        }
        binding.txtDescripcion.setText(product.getDescription());

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrls().get(0))
                    .into(binding.imageView);
        }
    }


    private void addProductToCart() {
        if (!sessionManager.isLoggedIn()) {
            loginLauncher.launch(new Intent(this, LoginActivity.class));
            return;
        }

        int quantity = getQuantity();

        Product product = viewModel.productResult.getValue() != null
                && viewModel.productResult.getValue().data != null
                ? viewModel.productResult.getValue().data
                : null;

        if (product == null) {
            showError("Producto no cargado aún");
            return;
        }

        cartViewModel.addItemToCart(product, quantity).observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    cartViewModel.refreshCart();
                    break;
                case ERROR:
                    showLoading(false);
                    showError(result.message);
                    break;
            }
        });
    }

    private void updateQuantity(int change) {
        int currentQuantity = getQuantity();
        int newQuantity = currentQuantity + change;
        if (newQuantity < 1) {
            newQuantity = 1;
        }
        binding.txtQuantity.setText(String.valueOf(newQuantity));
    }

    private int getQuantity() {
        try {
            return Integer.parseInt(binding.txtQuantity.getText().toString());
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
