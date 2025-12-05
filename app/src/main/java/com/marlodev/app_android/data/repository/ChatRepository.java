package com.marlodev.app_android.data.repository;


import com.marlodev.app_android.domain.model.ChatPreview;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private static ChatRepository instance;
    private final List<ChatPreview> chats = new ArrayList<>();

    public static ChatRepository getInstance() {
        if (instance == null) instance = new ChatRepository();
        return instance;
    }

    public void agregarChat(ChatPreview chat) {
        // evitar duplicados por idChat
        for (ChatPreview c : chats) {
            if (c.getIdChat() != null && c.getIdChat().equals(chat.getIdChat())) return;
        }
        chats.add(chat);
    }

    public List<ChatPreview> getChats() {
        return new ArrayList<>(chats); // devolvemos copia para evitar modificaciones externas
    }
}