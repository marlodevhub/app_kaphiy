package com.marlodev.app_android.ui.home.customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ActivityDetailBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.dto.product.ProductWebSocketEvent;
import com.marlodev.app_android.dto.order.network.ApiClient;
import com.marlodev.app_android.dto.order.network.GenericWebSocketManager;
import com.marlodev.app_android.dto.order.network.ProductApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.viewmodel.DetailViewModel;
import com.marlodev.app_android.viewmodel.DetailViewModelFactory;

public class ClientDetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private DetailViewModel viewModel;
    private SessionManager sessionManager;
    private long productId;

    // Launcher para el resultado del login
    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (sessionManager.isLoggedIn()) {
                    // Reintentar añadir al carrito si el login fue exitoso
                    addProductToCart(); 
                } else {
                    showError("Debes iniciar sesión para añadir productos al carrito");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupEdgeToEdgeDisplay();
        initViewModel();
        setupUI();
        observeViewModel();

        // Cargar el producto inicial
        productId = getIntent().getLongExtra("productId", -1);
        if (productId != -1) {
            viewModel.loadProductById(productId);
        }
    }

    /**
     * Inicializa el ViewModel usando la Factory para inyectar las dependencias.
     */
    private void initViewModel() {
        Context context = getApplicationContext();
        String token = SessionManager.getInstance(context).getToken();

        ProductApiService apiService = ApiClient.getClient(context).create(ProductApiService.class);
        
        ProductRepository productRepository = new ProductRepository(apiService,
            new GenericWebSocketManager<>(
                BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class
            )
        );

        DetailViewModelFactory factory = new DetailViewModelFactory(productRepository);
        viewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
    }

    /**
     * Configura la UI para el modo Edge-to-Edge, haciendo las barras de sistema transparentes.
     */
    private void setupEdgeToEdgeDisplay() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            
            binding.main.setPadding(0, top, 0, bottom);
            return insets;
        });
    }

    /**
     * Configura los listeners y el estado inicial de la UI.
     */
    private void setupUI() {
        binding.txtQuantity.setText("1");

        binding.btnPlus.setOnClickListener(v -> updateQuantity(1));
        binding.btnMinus.setOnClickListener(v -> updateQuantity(-1));
        binding.btnArrowLeft.setOnClickListener(v -> finish());
        binding.btnAddCart.setOnClickListener(v -> addProductToCart());
    }

    /**
     * Observa los cambios en el ViewModel y actualiza la UI en consecuencia.
     */
    private void observeViewModel() {
        viewModel.product.observe(this, this::displayProduct);
        viewModel.isLoading.observe(this, this::showLoading);
        viewModel.errorMessage.observe(this, this::showError);
    }

    /**
     * Muestra los detalles del producto en la pantalla.
     * @param product El producto a mostrar.
     */
    private void displayProduct(Product product) {
        if (product == null) return;

        binding.txtTitleProduct.setText(product.getName());
        binding.tvCurrentPrice.setText(getString(R.string.price_format, product.getPrice()));
        binding.tvOldPrice.setText(getString(R.string.price_format, product.getOldPrice()));
        binding.txtDescripcion.setText(product.getDescription());

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(binding.imageView);
        } else {
            binding.imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    /**
     * Añade el producto al carrito. Si el usuario no está logueado, inicia el flujo de login.
     */
    private void addProductToCart() {
        if (sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
            // Aquí iría la lógica para añadir al carrito, por ejemplo:
            // viewModel.addToCart(productId, getQuantity());
        } else {
            loginLauncher.launch(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Actualiza la cantidad de productos, asegurando que no baje de 1.
     * @param change La cantidad a sumar o restar.
     */
    private void updateQuantity(int change) {
        int currentQuantity = getQuantity();
        int newQuantity = currentQuantity + change;
        if (newQuantity >= 1) {
            binding.txtQuantity.setText(String.valueOf(newQuantity));
        }
    }

    /**
     * Muestra u oculta el indicador de carga.
     * @param isLoading `true` para mostrar, `false` para ocultar.
     */
    private void showLoading(boolean isLoading) {
        binding.progressBarDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    /**
     * Muestra un mensaje de error en un Snackbar.
     * @param message El mensaje de error a mostrar.
     */
    private void showError(String message) {
        if (message != null && !message.isEmpty()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene la cantidad actual del campo de texto.
     * @return La cantidad como un entero.
     */
    private int getQuantity() {
        try {
            return Integer.parseInt(binding.txtQuantity.getText().toString());
        } catch (NumberFormatException e) {
            return 1; // Retornar un valor por defecto en caso de error
        }
    }
}
