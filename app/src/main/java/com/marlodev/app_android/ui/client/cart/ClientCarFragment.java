package com.marlodev.app_android.ui.client.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.di.DependencyProvider;
import com.marlodev.app_android.ui.client.cart.components.ItemProductCarAdapter;
import com.marlodev.app_android.utils.Result;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartViewModel cartVM;
    private ItemProductCarAdapter cartAdapter;



//    Es llamado cuando Android necesita crear la vista del fragment.
//    Aquí es donde se infla (crea) el layout XML del fragment.
//    En este caso, estás usando ViewBinding, por eso haces:
//    binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
//    Por qué es importante:
//    Antes de onCreateView, el fragment no tiene vista, así que no puedes usar findViewById ni manipular componentes visuales.
//    Es el primer paso para preparar la UI.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

//    Se llama después de que la vista fue creada por onCreateView.
//    Aquí ya tienes acceso a todos los elementos de la interfaz, como botones, RecyclerViews, EditTexts, etc.
//    Es el lugar ideal para inicializar la lógica que depende de la vista.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(); // 1. Inicializa el Adapter (debe estar ordenado para no generar errores)
        setupViewModel();    // 2. Inicializa el ViewModel y usa el Adapter
        setupListeners();    // 3. Configura todos los listeners (listados)
    }

//    CUANDO SE LLAMA? sé llama cuando el fragmento ya es visible y está en primer plano,
//    listo para que el usuario interactúe con él.

//    QUE HACE EN EL CODIGO?
//    Comprueba que el ViewModel (cartVM) exista: if (cartVM != null)
//    Llama al metodo refreshCart() del ViewModel, que probablemente hace algo como:
//    - Consultar el carrito de compras actualizado
//    - Actualizar la lista de productos en la UI
//    - Refrescar totales o precios

//    PORQUE SE USA AQUI?
//    Para asegurarte de que cada vez que el usuario vuelve al fragmento, la información del carrito esté siempre actualizada.
//    Ejemplo práctico:
//    Usuario añade un producto desde otra pantalla → vuelve al carrito → quieres que la lista refleje el cambio inmediatamente.
//    Si lo pusieras solo en onViewCreated(), solo se ejecutaría la primera vez, y los cambios posteriores no se reflejarían.

    @Override
    public void onResume() {
        super.onResume();
        if (cartVM != null) {
            cartVM.refreshCart();
        }
    }

//    Un listener es un observador de eventos: detecta cuando el usuario hace algo
//    (click, swipe, cambio de cantidad) y ejecuta una acción.
//    En Android es la forma de reaccionar a interacciones de la UI.
    private void setupListeners() {

//        Listener Botón “Realizar orden”
//        Cuando el usuario presiona el botón:
//        Se llama al metodo checkout() del ViewModel (cartVM)
        binding.btnRealizarOrder.setOnClickListener(v -> cartVM.checkout());

//        Listener de cambio de cantidad
//        El Adapter tiene un listener que detecta cuando el usuario cambia la cantidad de un producto.
//        Cada vez que el usuario aumenta/disminuye una cantidad:
//        Se llama a cartVM.updateQuantity(item, newQuantity)
//        Actualiza la información en el ViewModel y usualmente refresca la UI con el nuevo total.
        cartAdapter.setOnQuantityChangeListener(cartVM::updateQuantity);

//        Listener de eliminar producto
//        Cada vez que el usuario pulsa el botón de eliminar un producto:
//        - Se llama a cartVM.deleteItem(item)
//        - Se elimina del carrito, se actualiza la lista y el total.
        cartAdapter.setOnDeleteClickListener(cartVM::deleteItem);
    }

//    Este metodo tiene dos propósitos principales:
//    - Inicializar el ViewModel (cartVM) con la fábrica que provee dependencias.
//    - Registrar observadores (LiveData) para que la UI reaccione automáticamente a los cambios de datos.
//    Este metodo Permite que la UI sea reactiva:
//    se actualiza automáticamente sin tener que consultar manualmente el ViewModel.
    private void setupViewModel() {
//        1️⃣ Inicialización del ViewModel
//        - ViewModelProvider obtiene o crea una instancia del ViewModel.
//        - Se usa ViewModelFactory cuando el ViewModel requiere parámetros
//          (repositorios, casos de uso, etc.).
//        - requireActivity() asegura que el ViewModel sea compartido entre fragments si es necesario.
//        ✅ Esto separa la lógica del carrito de la UI, siguiendo MVVM.
        ClientCartViewModelFactory factory = DependencyProvider.provideClientCartViewModelFactory(requireContext());
        cartVM = new ViewModelProvider(requireActivity(), factory).get(ClientCartViewModel.class);



        // 🔹 Vinculación con DataBinding
        binding.setViewModel(cartVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

//        2️⃣ Observadores de LiveData
//        Android usa LiveData para notificar automáticamente a la UI cuando cambian los datos.

//        a) Estado del carrito vacío
//        Observa si el carrito está vacío.
//        Llama a updateCartView(boolean) para mostrar un mensaje o la lista según corresponda.
        cartVM.getIsCartEmpty().observe(getViewLifecycleOwner(), this::updateCartView);

//        b) Lista de items del carrito
//        Observa la lista de productos del carrito.
//        Actualiza automáticamente el RecyclerView mediante el Adapter
        cartVM.getCartItems().observe(getViewLifecycleOwner(), cartAdapter::submitList);


//        d) Mensajes de error
//        Observa errores que puedan surgir (ej: fallo al actualizar el carrito).
//        Muestra un mensaje al usuario automáticamente.
        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

//        e) Estado de carga
//        Muestra u oculta un ProgressBar según si el carrito está cargando datos o no.

        cartVM.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null && binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

//        f) Resultado del checkout
//        Observa el resultado de la operación de “pagar / crear orden”.
//        Dependiendo del resultado:
//        - SUCCESS → muestra un toast de éxito
//        - ERROR → muestra un mensaje de error
        cartVM.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Result.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Orden creada exitosamente", Toast.LENGTH_SHORT).show();
            } else if (result.status == Result.Status.ERROR) {
                showError(result.message);
            }
        });
    }

//    RecyclerView es un componente de lista avanzada en Android.
//    Permite mostrar listas de elementos de manera eficiente, reutilizando las vistas que se desplazan.
//    Requiere dos cosas básicas:
//    - LayoutManager → define cómo se muestran los items (vertical, horizontal, grilla, etc.)
//    - Adapter → conecta los datos con las vistas de cada item.
    private void setupRecyclerView() {
//        a) Crear el Adapter
//        Adapter es el puente entre los datos (cartItems) y el RecyclerView.
//        Se encarga de inflar los layouts de cada item y mostrarlos.
        cartAdapter = new ItemProductCarAdapter();

//        b) Asignar LayoutManager
//        Define que los elementos se muestran en lista vertical.
//        Otras opciones podrían ser GridLayoutManager (rejilla)
//        o StaggeredGridLayoutManager (rejilla irregular).
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        c) Asignar Adapter
//        Conecta el RecyclerView con el Adapter, de modo que los datos puedan mostrarse.
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);
    }

//    Es un metodo que actualiza la interfaz del carrito según si está vacío o no.
//    - Se llama desde el LiveData del ViewModel:
//    - Esto significa que cada vez que cambia el estado “vacío/no vacío”,
//      la UI se actualiza automáticamente.
//    - UI reactiva: se actualiza automáticamente cada vez que cambia el estado del carrito.

    private void updateCartView(boolean isEmpty) {
//        a) Comprobar binding
//        Evita errores si la vista aún no existe o fue destruida (prevención de NullPointerException).
        if (binding == null) return;

//        b) Si el carrito está vacío
//        - Muestra un mensaje o vista indicando que el carrito está vacío (emptyCartView).
//        - Oculta la vista con la lista de productos (cartContentContainer).
//        c) Si el carrito NO está vacío
//        - Oculta la vista de carrito vacío.
//        - Muestra el contenido del carrito (lista de productos, total, botones, etc.).

        if (isEmpty) {
            binding.emptyCartView.setVisibility(View.VISIBLE);
            binding.cartContentContainer.setVisibility(View.GONE);
        } else {
            binding.emptyCartView.setVisibility(View.GONE);
            binding.cartContentContainer.setVisibility(View.VISIBLE);
        }
    }

//    Un Toast es un mensaje breve que aparece flotando sobre la UI.
//    Sirve para informar al usuario sin interrumpir su interacción.
//    Se oculta automáticamente después de unos segundos.
    private void showError(String message) {
//        Verifica que el mensaje no sea nulo o vacío
//        Evita mostrar un Toast vacío o lanzar errores.
        if (message != null && !message.isBlank()) {
//            Muestra el Toast en pantalla
//            requireContext() obtiene el contexto del fragmento.
//            Toast.LENGTH_LONG indica que el mensaje se muestra más tiempo (unos 3-4 segundos).
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

//    onDestroyView() se llama cuando la vista del fragmento se destruye.
//    Esto ocurre antes de que el fragmento sea destruido completamente, por ejemplo:
//    Cuando navegas a otro fragmento
//    Cuando cierras la app o la pantalla se recrea por rotación
    @Override
    public void onDestroyView() {
//        Limpiar las vistas internas del fragmento
//        Desasociar la jerarquía de vistas del fragmento de la Activity
//        Liberar recursos internos usados por el fragmento
        super.onDestroyView();
//        binding es la instancia generada por ViewBinding, que referencia todas las vistas de tu layout.
//        La vista ya no existe después de onDestroyView(), pero si mantienes binding apuntando a esas vistas,
//        Pueden pasar dos problemas:

//        a) NullPointerException
//          Si algún código intenta usar binding después de que la vista fue destruida, crash seguro.
//        b) Memory Leak (fuga de memoria)
//          Las referencias a las vistas mantienen objetos en memoria innecesariamente, evitando que el recolector de basura los libere.
//          Por eso, asignar binding = null limpia la referencia, evitando estos problemas.
        binding = null;
    }

}
