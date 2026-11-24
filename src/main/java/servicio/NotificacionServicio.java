package servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.EstadoPedido;
import modelo.Pedido;
import patron.ObservadorPedido;

public class NotificacionServicio {
    private final List<ObservadorPedido> observadores = new ArrayList<>();
    private final List<String> logGlobalNotificaciones = new ArrayList<>();

    public void suscribir(ObservadorPedido observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorPedido observador) {
        observadores.remove(observador);
    }

    public void notificarCambioEstado(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido nuevoEstado) {
        for (ObservadorPedido obs : observadores) {
            obs.actualizar(pedido, estadoAnterior, nuevoEstado);
            logGlobalNotificaciones.add("[" + obs.getNombreEstacion().toUpperCase() + "] Pedido #"
                    + pedido.getId() + " -> " + nuevoEstado);
        }
    }

    public List<String> getLogGlobalNotificaciones() {
        return Collections.unmodifiableList(logGlobalNotificaciones);
    }
}