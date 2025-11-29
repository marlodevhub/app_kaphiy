package com.marlodev.app_android.data.network.websocket;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

/**
 * Generic WebSocket Manager profesional.
 *
 * @param <T> Tipo de evento (ProductWebSocketEvent, UserWebSocketEvent, etc.)
 */
public class GenericWebSocketManager<T> {

    private static final String TAG = "GenericWebSocket";

    private final StompClient stompClient;
    private final Gson gson = new Gson();
    private final Class<T> eventClass;
    private final String wsUrl;
    private final String token;
    private final String topic;

    private Disposable subscription;
    private final MutableLiveData<T> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ConnectionState> connectionState = new MutableLiveData<>(ConnectionState.DISCONNECTED);

    // Scheduler seguro para reconexiones controladas
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int retryDelay = 3; // segundos
    private boolean reconnecting = false;

    public enum ConnectionState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        ERROR
    }

    public GenericWebSocketManager(String wsUrl, String token, String topic, Class<T> eventClass) {
        this.wsUrl = wsUrl;
        this.token = token;
        this.topic = topic;
        this.eventClass = eventClass;

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        // Observa cambios en la conexión
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            Log.d(TAG, "📡 Estado WS: " + lifecycleEvent.getType());
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.i(TAG, "✅ WebSocket conectado");
                    retryDelay = 3;
                    reconnecting = false;
                    connectionState.postValue(ConnectionState.CONNECTED);
                    subscribeToTopic();
                    break;

                case ERROR:
                    Log.e(TAG, "❌ Error WS", lifecycleEvent.getException());
                    connectionState.postValue(ConnectionState.ERROR);
                    scheduleReconnect();
                    break;

                case CLOSED:
                    Log.w(TAG, "⚠️ WebSocket cerrado");
                    connectionState.postValue(ConnectionState.DISCONNECTED);
                    scheduleReconnect();
                    break;

                default:
                    connectionState.postValue(ConnectionState.CONNECTING);
            }
        });
    }

    /** Suscripción segura al topic */
    private void subscribeToTopic() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();

        subscription = stompClient.topic(topic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        T event = gson.fromJson(message.getPayload(), eventClass);
                        eventLiveData.postValue(event);
                        Log.d(TAG, "🔔 Evento recibido: " + message.getPayload());
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando evento WS", e);
                    }
                }, throwable -> Log.e(TAG, "❌ Error recibiendo evento WS", throwable));
    }

    /** Reconexión automática con backoff exponencial */
    private void scheduleReconnect() {
        if (reconnecting) return; // evita múltiples hilos
        reconnecting = true;

        scheduler.schedule(() -> {
            Log.i(TAG, "🔁 Intentando reconectar al WS...");
            connect();
            retryDelay = Math.min(retryDelay * 2, 30); // backoff hasta 30s
            reconnecting = false;
        }, retryDelay, TimeUnit.SECONDS);
    }

    /** Conecta al WebSocket con headers */
    public void connect() {
        try {
            List<StompHeader> headers = new ArrayList<>();
            if (token != null && !token.isEmpty()) {
                headers.add(new StompHeader("Authorization", "Bearer " + token));
            }

            connectionState.postValue(ConnectionState.CONNECTING);
            stompClient.connect(headers);
            Log.i(TAG, "🚀 Conectando a WS: " + wsUrl);
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception en connect()", e);
            scheduleReconnect();
        }
    }

    /** Desconecta y libera recursos de manera segura */
    public void disconnect() {
        try {
            if (subscription != null && !subscription.isDisposed()) subscription.dispose();
            stompClient.disconnect();
            connectionState.postValue(ConnectionState.DISCONNECTED);
            reconnecting = false;
            scheduler.shutdownNow(); // evita fugas de hilos
            Log.i(TAG, "❎ WebSocket desconectado");
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception en disconnect()", e);
        }
    }



    /** Eventos emitidos por el servidor */
    public LiveData<T> getEventLiveData() {
        return eventLiveData;
    }

}
