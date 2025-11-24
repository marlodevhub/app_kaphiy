package com.marlodev.app_android.ui.delivery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryMensajeriaFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText edtMensaje;
    private ImageButton btnEnviar;

    private ChatAdapter chatAdapter;
    private final List<ChatMessage> mensajes = new ArrayList<>();

    // Variables que indican el tipo de chat (delivery con tienda o cliente)
    private String remitenteActual = "delivery";
    private String destinoChat = "cliente"; // valor por defecto

    public DeliveryMensajeriaFragment() {
        // Constructor vacío obligatorio
    }

    public static DeliveryMensajeriaFragment newInstance(String tipoChat) {
        DeliveryMensajeriaFragment fragment = new DeliveryMensajeriaFragment();
        Bundle args = new Bundle();
        args.putString("tipoChat", tipoChat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_mensajeria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        edtMensaje = view.findViewById(R.id.edtMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);

        // Verificar si se recibió el tipo de chat desde el fragment anterior
        if (getArguments() != null) {
            destinoChat = getArguments().getString("tipoChat", "cliente");
        }

        chatAdapter = new ChatAdapter(mensajes, remitenteActual);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        // Mensajes de bienvenida simulados según tipo de chat
        if (destinoChat.equals("cliente")) {
            mensajes.add(new ChatMessage("cliente", "Hola, ¿ya vienes con mi pedido?", getHoraActual()));
            mensajes.add(new ChatMessage("delivery", "Sí, estoy a unos minutos de tu ubicación.", getHoraActual()));
        } else if (destinoChat.equals("tienda")) {
            mensajes.add(new ChatMessage("tienda", "Hola, el pedido está listo para recoger.", getHoraActual()));
            mensajes.add(new ChatMessage("delivery", "Perfecto, en 5 minutos llego a la tienda.", getHoraActual()));
        }

        chatAdapter.notifyDataSetChanged();

        // Boton para volver a la vista de detalle de cliente
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack(); // Vuelve al fragmento anterior (detalle de pedido del cliente)
        });
    }

    private void enviarMensaje() {
        String texto = edtMensaje.getText().toString().trim();
        if (TextUtils.isEmpty(texto)) return;

        // Agregar mensaje del delivery
        mensajes.add(new ChatMessage(remitenteActual, texto, getHoraActual()));
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerChat.scrollToPosition(mensajes.size() - 1);
        edtMensaje.setText("");

        // Simular respuesta automática del interlocutor
        recyclerChat.postDelayed(() -> {
            String respuesta;
            if (destinoChat.equals("cliente")) {
                respuesta = "Gracias por avisar, te espero.";
                mensajes.add(new ChatMessage("cliente", respuesta, getHoraActual()));
            } else {
                respuesta = "Perfecto, te esperamos para la entrega.";
                mensajes.add(new ChatMessage("tienda", respuesta, getHoraActual()));
            }
            chatAdapter.notifyItemInserted(mensajes.size() - 1);
            recyclerChat.scrollToPosition(mensajes.size() - 1);
        }, 1500);
    }

    private String getHoraActual() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
}
