package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RF09FavoritosTest {

    private GestorEmails gestor;
    private Contacto c1, c2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
    }

    @Test
    void marcarYDesmarcarFavorito_actualizaBandejaYFlag() {
        Email e = gestor.crearEmail(c1, "Fav", "C", Arrays.asList(c2));
        gestor.enviar(e);

        gestor.marcarFavorito(e.getId(), true);
        assertTrue(e.isFavorito());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.FAVORITOS).contains(e));

        gestor.marcarFavorito(e.getId(), false);
        assertFalse(e.isFavorito());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.FAVORITOS).isEmpty());
    }

    @Test
    void marcarFavoritoDosVeces_noDuplicaEnBandeja() {
        Email e = gestor.crearEmail(c1, "Repetido", "C", Arrays.asList(c2));
        gestor.enviar(e);

        gestor.marcarFavorito(e.getId(), true);
        gestor.marcarFavorito(e.getId(), true);

        assertEquals(1, gestor.getTodosEnBandeja(BandejaType.FAVORITOS).size());
    }

    @Test
    void marcarFavoritoConIdInexistente_noRompe() {
        gestor.marcarFavorito("id-que-no-existe", true);
        // si no tira excepción, está ok
    }
}
