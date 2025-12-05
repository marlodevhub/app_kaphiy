package com.marlodev.app_android.domain.model;
public class ChatPreview {
    private String idChat;     // id único del chat: ejemplo "pedido_24_cliente"
    private String nombre;     // nombre de la otra persona
    private String ultimoMensaje;
    private String hora;
    private String tipoChat;   // cliente / tienda
    private int pedidoId;      // para vincular al pedido

    public ChatPreview(String idChat, String nombre, String ultimoMensaje, String hora, String tipoChat, int pedidoId) {
        this.idChat = idChat;
        this.nombre = nombre;
        this.ultimoMensaje = ultimoMensaje;
        this.hora = hora;
        this.tipoChat = tipoChat;
        this.pedidoId = pedidoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public String getHora() {
        return hora;
    }

    public String getTipoChat() {
        return tipoChat;
    }

    public String getIdChat() {
        return idChat;
    }

    public int getPedidoId() { return pedidoId; }
}