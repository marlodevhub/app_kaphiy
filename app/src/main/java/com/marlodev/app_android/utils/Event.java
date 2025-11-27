package com.marlodev.app_android.utils;

/**
 * Un wrapper para datos que se exponen a través de LiveData y que deben tratarse como eventos.
 * Estos eventos solo deben consumirse una vez, por ejemplo, para mostrar un Snackbar o un Toast,
 * o para navegar a otra pantalla.
 *
 * @param <T> El tipo de contenido.
 */
public class Event<T> {

    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Devuelve el contenido y evita que se vuelva a utilizar.
     * Si el contenido ya ha sido manejado, devuelve null.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Devuelve el contenido, incluso si ya ha sido manejado.
     * Útil para inspeccionar el valor sin consumirlo.
     */
    public T peekContent() {
        return content;
    }

    /**
     * Devuelve si el evento ya ha sido manejado.
     */
    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}
