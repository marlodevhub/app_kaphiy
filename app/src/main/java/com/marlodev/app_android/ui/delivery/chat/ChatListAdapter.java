package com.marlodev.app_android.ui.delivery.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.ChatPreview;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final List<ChatPreview> chats;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatPreview chat);
    }

    public ChatListAdapter(List<ChatPreview> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    // 🚀 Nuevo método para evitar duplicaciones cuando vuelves al fragment
    public void setData(List<ChatPreview> nuevaLista) {
        chats.clear();
        chats.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatPreview chat = chats.get(position);

        holder.txtNombre.setText(chat.getNombre());
        holder.txtUltimoMensaje.setText(chat.getUltimoMensaje());
        holder.txtHora.setText(chat.getHora());

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtUltimoMensaje, txtHora;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreChat);
            txtUltimoMensaje = itemView.findViewById(R.id.txtUltimoMensaje);
            txtHora = itemView.findViewById(R.id.txtHoraChat);
        }
    }
}