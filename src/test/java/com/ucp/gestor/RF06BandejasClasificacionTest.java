package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RF06BandejasClasificacionTest {

    private GestorEmails gestor;
    private Contacto c1, c2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
    }

    @Test
    void moverEntreBandejas_yRestaurarDesdeEliminados() {
        Email e = gestor.crearEmail(c1, "Mover", "Cuerpo", Arrays.asList(c2));
        gestor.enviar(e);

        boolean movido = gestor.moverEmail(e, BandejaType.ENTRADA, BandejaType.BORRADORES);
        assertTrue(movido);
        assertFalse(gestor.getTodosEnBandeja(BandejaType.ENTRADA).contains(e));
        assertTrue(gestor.getTodosEnBandeja(BandejaType.BORRADORES).contains(e));

        gestor.eliminarDefinitivo(e);
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ELIMINADOS).contains(e));

        boolean restaurado = gestor.restaurarAEntrada(e);
        assertTrue(restaurado);
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENTRADA).contains(e));
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ELIMINADOS).isEmpty());
    }

    @Test
    void moverEmailInvalido_devuelveFalse() {
        Email e = gestor.crearEmail(c1, "SinMover", "C", Arrays.asList(c2));
        // nunca lo pongo en ENTRADA

        boolean movido = gestor.moverEmail(e, BandejaType.ENTRADA, BandejaType.FAVORITOS);
        assertFalse(movido);
    }

    @Test
    void operacionesDirectasDeBandeja_buscarYVaciar() {
        Bandeja bandeja = new Bandeja(BandejaType.ENTRADA);
        Email e1 = new Email();
        e1.setAsunto("Hola");
        Email e2 = new Email();
        e2.setAsunto("Chau");

        bandeja.agregar(e1);
        bandeja.agregar(e2);

        assertEquals(BandejaType.ENTRADA, bandeja.getTipo());
        assertEquals(2, bandeja.todos().size());

        assertEquals(1, bandeja.buscar(m -> "Hola".equals(m.getAsunto())).size());

        bandeja.vaciar();
        assertTrue(bandeja.todos().isEmpty());
    }
}
