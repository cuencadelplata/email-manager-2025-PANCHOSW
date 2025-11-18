package com.ucp.gestor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RF07BorradoresTest {

    private GestorEmails gestor;
    private Contacto c1, c2, c3;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
        c3 = gestor.crearContacto("C", "c@demo.com");
    }

    @Test
    void crearYGuardarBorrador_noSeDuplicaEnBandeja() {
        Email e = gestor.crearEmail(c1, "Borrador", "Texto", Arrays.asList(c2));

        assertTrue(e.isBorrador());
        assertEquals(1, gestor.getTodosEnBandeja(BandejaType.BORRADORES).size());

        gestor.guardarBorrador(e);
        assertEquals(1, gestor.getTodosEnBandeja(BandejaType.BORRADORES).size());
    }

    @Test
    void editarBorrador_yLuegoEnviar() {
        Email e = gestor.crearEmail(c1, "A1", "C1", Arrays.asList(c2));

        gestor.editarEmail(e, "Editado", "Contenido editado", Arrays.asList(c3));
        assertEquals("Editado", e.getAsunto());
        assertEquals("Contenido editado", e.getContenido());
        assertEquals(1, e.getPara().size());
        assertEquals(c3, e.getPara().get(0));

        gestor.enviar(e);
        assertFalse(e.isBorrador());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENVIADOS).contains(e));
    }

    @Test
    void editarBorradorConDestinatariosNull_vaciaLaListaPara() {
        Email e = gestor.crearEmail(c1, "A", "C", Arrays.asList(c2));

        gestor.editarEmail(e, "Nuevo", "NuevoC", null);

        assertEquals("Nuevo", e.getAsunto());
        assertEquals("NuevoC", e.getContenido());
        assertTrue(e.getPara().isEmpty());
    }

 
    
}
