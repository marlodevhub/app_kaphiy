package com.marlodev.app_android.domain.model;

import java.io.Serializable;

/**
 * Representa un mensaje dentro del chat entre el delivery,
 * el cliente o la tienda.
 */
public class ChatMessage implements Serializable {

    private String remitente;   // Ejemplo: "delivery", "cliente", "tienda"
    private String contenido;   // Texto del mensaje
    private String hora;        // Hora en formato hh:mm a

    public ChatMessage() {
        // Constructor vacío requerido para Firebase
    }

    public ChatMessage(String remitente, String contenido, String hora) {
        this.remitente = remitente;
        this.contenido = contenido;
        this.hora = hora;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "remitente='" + remitente + '\'' +
                ", contenido='" + contenido + '\'' +
                ", hora='" + hora + '\'' +
                '}';
    }
}