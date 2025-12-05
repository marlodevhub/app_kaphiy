package com.marlodev.app_android.ui.delivery.chat;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlodev.app_android.R;
import com.marlodev.app_android.data.repository.ChatRepository;
import com.marlodev.app_android.domain.model.ChatPreview;
import com.marlodev.app_android.ui.delivery.DeliveryMensajeriaFragment;

import java.util.ArrayList;
import java.util.List;

public class DeliveryChatListFragment extends Fragment {

    private RecyclerView recyclerChats;
    private ChatListAdapter adapter;
    private final List<ChatPreview> listaChats = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerChats = view.findViewById(R.id.recyclerChatList);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));

        // cargamos chats desde el repositorio
        cargarChats();

        adapter = new ChatListAdapter(listaChats, this::abrirChat);
        recyclerChats.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refrescar lista cada vez que se reanude (por si se creó un chat nuevo)
        cargarChats();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void cargarChats() {
        listaChats.clear();
        listaChats.addAll(ChatRepository.getInstance().getChats());
    }

    private void abrirChat(ChatPreview chat) {
        DeliveryMensajeriaFragment fragment =
                DeliveryMensajeriaFragment.newInstance(
                        chat.getTipoChat(),
                        chat.getNombre(),
                        chat.getIdChat()
                );

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("CHAT")
                .commit();
    }
}