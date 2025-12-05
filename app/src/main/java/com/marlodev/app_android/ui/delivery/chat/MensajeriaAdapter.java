package com.marlodev.app_android.ui.delivery.chat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.ChatMessage;

import java.util.List;

public class MensajeriaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<ChatMessage> mensajes;
    private String remitenteActual; // "delivery", "cliente", "tienda"

    public MensajeriaAdapter(List<ChatMessage> mensajes, String remitenteActual) {
        this.mensajes = mensajes;
        this.remitenteActual = remitenteActual;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = mensajes.get(position);
        if (msg.getRemitente().equals(remitenteActual)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_enviado, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_recibido, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = mensajes.get(position);

        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).txtMensaje.setText(msg.getContenido());
            ((SentViewHolder) holder).txtHora.setText(msg.getHora());
        } else if (holder instanceof ReceivedViewHolder) {
            ((ReceivedViewHolder) holder).txtMensaje.setText(msg.getContenido());
            ((ReceivedViewHolder) holder).txtHora.setText(msg.getHora());
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtHora;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            txtHora = itemView.findViewById(R.id.txtHora);
        }
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtHora;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            txtHora = itemView.findViewById(R.id.txtHora);
        }
    }
}