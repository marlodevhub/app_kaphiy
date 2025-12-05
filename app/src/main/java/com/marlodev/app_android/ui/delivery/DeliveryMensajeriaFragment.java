package com.marlodev.app_android.ui.delivery;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.ChatMessage;
import com.marlodev.app_android.ui.delivery.chat.MensajeriaAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryMensajeriaFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText edtMensaje;
    private ImageButton btnEnviar;
    private TextView txtNombreChat;

    private MensajeriaAdapter chatAdapter;
    private final List<ChatMessage> mensajes = new ArrayList<>();

    private String remitenteActual = "delivery";
    private String destinoChat = "cliente";
    private String nombreChat = null;
    private String idChat = null;

    public DeliveryMensajeriaFragment() { }

    public static DeliveryMensajeriaFragment newInstance(String tipoChat, String nombreChat, String idChat) {
        DeliveryMensajeriaFragment fragment = new DeliveryMensajeriaFragment();
        Bundle args = new Bundle();
        args.putString("tipoChat", tipoChat);
        args.putString("nombreChat", nombreChat);
        args.putString("idChat", idChat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recuperar argumentos aquí para que estén disponibles antes de onViewCreated
        if (getArguments() != null) {
            destinoChat = getArguments().getString("tipoChat", "cliente");
            nombreChat = getArguments().getString("nombreChat", null);
            idChat = getArguments().getString("idChat", null);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_mensajeria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerChat = view.findViewById(R.id.recyclerChat);
        edtMensaje = view.findViewById(R.id.edtMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);
        txtNombreChat = view.findViewById(R.id.txtNombre); // Debe existir en tu layout header

        // Mostrar nombre del chat en el header si viene
        if (nombreChat != null && txtNombreChat != null) {
            txtNombreChat.setText(nombreChat);
        }

        chatAdapter = new MensajeriaAdapter(mensajes, remitenteActual);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        // Solo cargar mensajes iniciales si la lista está vacía (evitar duplicados)
        if (mensajes.isEmpty()) {
            cargarMensajesIniciales();
        }

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void cargarMensajesIniciales() {
        // Si en el futuro guardas mensajes por idChat, aquí debes cargarlos desde el repositorio
        // mientras tanto, mantengo mensajes de ejemplo sólo si la lista está vacía.
        if (destinoChat.equals("cliente")) {
            mensajes.add(new ChatMessage("cliente", "Hola, ¿ya vienes con mi pedido?", getHoraActual()));
            mensajes.add(new ChatMessage("delivery", "Sí, estoy a unos minutos de tu ubicación.", getHoraActual()));
        } else {
            mensajes.add(new ChatMessage("tienda", "Hola, el pedido está listo para recoger.", getHoraActual()));
            mensajes.add(new ChatMessage("delivery", "Perfecto, en 5 minutos llego a la tienda.", getHoraActual()));
        }

        chatAdapter.notifyDataSetChanged();
        recyclerChat.scrollToPosition(mensajes.size() - 1);
    }

    private void enviarMensaje() {
        String texto = edtMensaje.getText().toString().trim();
        if (TextUtils.isEmpty(texto)) return;

        mensajes.add(new ChatMessage(remitenteActual, texto, getHoraActual()));
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerChat.scrollToPosition(mensajes.size() - 1);
        edtMensaje.setText("");

        // Simular respuesta automática del interlocutor (puedes quitar esto cuando integres WS/DB)
        recyclerChat.postDelayed(() -> {
            String respuesta = destinoChat.equals("cliente") ?
                    "Gracias por avisar, te espero." :
                    "Perfecto, te esperamos para la entrega.";

            mensajes.add(new ChatMessage(destinoChat, respuesta, getHoraActual()));
            chatAdapter.notifyItemInserted(mensajes.size() - 1);
            recyclerChat.scrollToPosition(mensajes.size() - 1);
        }, 1500);
    }

    private String getHoraActual() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
}