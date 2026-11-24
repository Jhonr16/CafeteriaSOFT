package prueba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import modelo.ComboItem;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ProductoBase;
import modelo.ResumenTurno;
import modelo.TipoItem;
import org.junit.jupiter.api.Test;
import patron.EstacionCaja;
import patron.EstacionCocina;
import patron.EstacionMesero;
import patron.ExtraShotEspressoDecorator;
import patron.HappyHourStrategy;
import patron.LecheAlmendraDecorator;
import patron.PromoFinDeSemanaStrategy;
import patron.SinPromocionStrategy;
import repositorio.CatalogoRepositorio;
import repositorio.PedidoRepositorio;
import servicio.ComboServicio;
import servicio.DisponibilidadException;
import servicio.NotificacionServicio;
import servicio.PedidoServicio;
import servicio.PersonalizacionServicio;

class PatronesTest {

    @Test
    void testPatronDecoratorCalculaPrecioYDescripcion() {
        ItemMenu cafe = new ProductoBase("BEB01", "Café Americano", 5500.0, TipoItem.BEBIDA, true);
        PersonalizacionServicio servicio = new PersonalizacionServicio();

        ItemMenu personalizado = servicio.aplicarAdiciones(cafe, List.of("ADD_ALMENDRA", "ADD_SHOT"));

        assertEquals(11000.0, personalizado.calcularPrecio(), 0.01);
        assertTrue(personalizado.getDescripcionDetallada().contains("Café Americano"));
        assertTrue(personalizado.getDescripcionDetallada().contains("Leche de Almendra"));
        assertTrue(personalizado.getDescripcionDetallada().contains("Extra Shot Espresso"));
    }

    @Test
    void testPatronStrategyPromociones() {
        LocalDateTime momento = LocalDateTime.of(2026, 11, 21, 16, 0);

        assertEquals(0.0, new SinPromocionStrategy().calcularDescuento(20000.0, momento), 0.01);
        assertEquals(4000.0, new HappyHourStrategy(0.20, 15, 18, false).calcularDescuento(20000.0, momento), 0.01);
        assertEquals(3000.0, new PromoFinDeSemanaStrategy(0.15, false).calcularDescuento(20000.0, momento), 0.01);
    }

    @Test
    void testPatronObserverNotificaEstaciones() {
        PedidoRepositorio repo = new PedidoRepositorio();
        PersonalizacionServicio pers = new PersonalizacionServicio();
        NotificacionServicio notif = new NotificacionServicio();

        EstacionCocina cocina = new EstacionCocina();
        EstacionCaja caja = new EstacionCaja();
        EstacionMesero mesero = new EstacionMesero();
        notif.suscribir(cocina);
        notif.suscribir(caja);
        notif.suscribir(mesero);

        PedidoServicio pedidoServicio = new PedidoServicio(repo, pers, notif);
        Pedido pedido = pedidoServicio.crearPedido("Jhon");
        pedidoServicio.cambiarEstadoPedido(pedido.getId(), EstadoPedido.EN_PREPARACION);
        pedidoServicio.cambiarEstadoPedido(pedido.getId(), EstadoPedido.LISTO);

        assertFalse(cocina.getNotificacionesRecibidas().isEmpty());
        assertFalse(caja.getNotificacionesRecibidas().isEmpty());
        assertFalse(mesero.getNotificacionesRecibidas().isEmpty());
    }

    @Test
    void testValidacionDisponibilidadYComboYPago() {
        CatalogoRepositorio catalogo = new CatalogoRepositorio();
        ItemMenu noDisponible = catalogo.buscarPorCodigo("BEB04").orElseThrow();
        PersonalizacionServicio pers = new PersonalizacionServicio();

        assertThrows(DisponibilidadException.class, () -> pers.validarDisponibilidad(noDisponible));

        ComboServicio comboServicio = new ComboServicio(pers);
        ComboItem combo = comboServicio.crearComboPasoAPaso(
                "CMB01",
                "Combo Mañanero",
                catalogo.buscarPorCodigo("BEB01").orElseThrow(),
                catalogo.buscarPorCodigo("PLA01").orElseThrow(),
                catalogo.buscarPorCodigo("ACO01").orElseThrow(),
                null,
                18000.0
        );
        assertEquals(18000.0, combo.calcularPrecio(), 0.01);

        PedidoServicio pedidoServicio = new PedidoServicio(new PedidoRepositorio(), pers, new NotificacionServicio());
        Pedido p = pedidoServicio.crearPedido("Laura");
        pedidoServicio.agregarItemAPedido(p.getId(), combo);
        pedidoServicio.registrarPago(p.getId(), MedioPago.TARJETA);

        ResumenTurno resumen = pedidoServicio.generarCuadreTurno();
        assertEquals(1, resumen.getPedidosPagados().size());
        assertEquals(18000.0, resumen.getTotalRecaudado(), 0.01);
    }
}