package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RF04BusquedaCorreosTest {

    private GestorEmails gestor;
    private Contacto remitente, d1, d2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        remitente = gestor.crearContacto("Rem", "rem@demo.com");
        d1 = gestor.crearContacto("D1", "d1@demo.com");
        d2 = gestor.crearContacto("D2", "d2@demo.com");

        Email e = gestor.crearEmail(remitente, "Asunto UCP", "Contenido importante UCP",
                Arrays.asList(d1, d2));
        gestor.enviar(e);
    }

    @Test
    void busquedaEnEntrada_porAsuntoContenidoRemitenteODestinatario() {
        List<Email> porAsunto = gestor.buscarTextoEnBandeja(BandejaType.ENTRADA, "ucp");
        assertEquals(1, porAsunto.size());

        List<Email> porRem = gestor.buscarTextoEnBandeja(BandejaType.ENTRADA, "rem@demo.com");
        assertEquals(1, porRem.size());

        List<Email> porDest = gestor.buscarTextoEnBandeja(BandejaType.ENTRADA, "d2@demo.com");
        assertEquals(1, porDest.size());
    }

    @Test
    void busquedaConTextoLibreNull_devuelveCoincidenciasPorCadenaVacia() {
        List<Email> encontrados = gestor.buscarTextoEnBandeja(BandejaType.ENTRADA, null);
        assertEquals(1, encontrados.size());
    }

    @Test
    void buscarEnBandejaConPredicate_directo() {
        List<Email> encontrados = gestor.buscarEnBandeja(
                BandejaType.ENTRADA,
                e -> e.getAsunto() != null && e.getAsunto().contains("UCP")
        );
        assertEquals(1, encontrados.size());
    }
}
