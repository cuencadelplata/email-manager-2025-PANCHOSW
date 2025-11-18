package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RF08LeidoNoLeidoTest {

    private GestorEmails gestor;
    private Contacto c1, c2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
    }

    @Test
    void marcarLeidoYNoLeido_cambiaEstadoEnEmail() {
        Email e = gestor.crearEmail(c1, "Leido", "C", Arrays.asList(c2));
        gestor.enviar(e);

        gestor.marcarLeido(e.getId(), true);
        assertTrue(e.isLeido());

        gestor.marcarLeido(e.getId(), false);
        assertFalse(e.isLeido());
    }

    @Test
    void marcarLeidoConIdInexistente_noRompe() {
        gestor.marcarLeido("id-que-no-existe", true);
        // Si no tira excepción, el test pasa
    }
}
