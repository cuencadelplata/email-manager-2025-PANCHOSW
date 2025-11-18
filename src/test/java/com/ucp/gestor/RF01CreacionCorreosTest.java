package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RF01CreacionCorreosTest {

    private GestorEmails gestor;
    private Contacto remitente, d1, d2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        remitente = gestor.crearContacto("Remitente", "remitente@demo.com");
        d1 = gestor.crearContacto("Dest1", "d1@demo.com");
        d2 = gestor.crearContacto("Dest2", "d2@demo.com");
    }

    @Test
    void crearCorreoConVariosDestinatarios_guardaDatosBasicos() {
        Email e = gestor.crearEmail(remitente, "Asunto prueba", "Cuerpo prueba",
                Arrays.asList(d1, d2));

        assertEquals("Asunto prueba", e.getAsunto());
        assertEquals("Cuerpo prueba", e.getContenido());
        assertEquals(remitente, e.getRemitente());
        assertEquals(2, e.getPara().size());
        assertTrue(e.isBorrador());
    }

    @Test
    void crearCorreo_loDejaEnBandejaBorradoresYSeIndexaPorId() {
        Email e = gestor.crearEmail(remitente, "Asunto", "Cuerpo",
                Arrays.asList(d1));

        assertTrue(gestor.getTodosEnBandeja(BandejaType.BORRADORES).contains(e));
        assertNotNull(e.getId());
        assertTrue(gestor.buscarPorId(e.getId()).isPresent());
    }

    @Test
    void emailNuevo_tieneFechaYEstadosInicialesCorrectos() {
        Email e = gestor.crearEmail(remitente, "A", "C", Arrays.asList(d1));

        assertNotNull(e.getFecha());
        assertTrue(e.isBorrador());
        assertFalse(e.isLeido());
        assertFalse(e.isFavorito());
        assertNotNull(e.toString());
    }

}


