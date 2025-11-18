package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RF03GestionContactosTest {

    private GestorEmails gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        gestor.crearContacto("A", "a@demo.com");
    }

    @Test
    void crearEditarEliminarContacto_flujoCompleto() {
        Contacto nuevo = gestor.crearContacto("Nuevo", "nuevo@demo.com");
        assertTrue(gestor.listarContactos().contains(nuevo));

        boolean editado = gestor.editarContacto("nuevo@demo.com", "Editado", "editado@demo.com");
        assertTrue(editado);

        Optional<Contacto> buscado = gestor.buscarContactoPorEmail("editado@demo.com");
        assertTrue(buscado.isPresent());
        assertEquals("Editado", buscado.get().getNombre());

        boolean eliminado = gestor.eliminarContacto("editado@demo.com");
        assertTrue(eliminado);
    }

    @Test
    void editarOEliminarContactoInexistente_devuelveFalse() {
        boolean editado = gestor.editarContacto("no@existe.com", "X", "Y");
        boolean eliminado = gestor.eliminarContacto("no@existe.com");

        assertFalse(editado);
        assertFalse(eliminado);
    }

    @Test
    void equalsHashCodeYToStringDeContacto_funcionanPorEmail() {
        Contacto c1 = new Contacto("Nombre1", "demo@correo.com");
        Contacto c2 = new Contacto("Nombre2", "Demo@Correo.com");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.toString().contains("demo@correo.com") || c1.toString().contains("Demo@Correo.com"));
    }

    @Test
    void equalsDeContacto_conEmailsNullNoSonIguales() {
        Contacto cNull1 = new Contacto("X", null);
        Contacto cNull2 = new Contacto("Y", null);

        // mismo objeto
        assertTrue(cNull1.equals(cNull1));

        // distintos objetos, ambos con email null → equals debe dar false con nuestra implementación
        assertFalse(cNull1.equals(cNull2));

        // comparar con otro tipo y con null
        assertFalse(cNull1.equals("cadena"));
        assertFalse(cNull1.equals(null));

        // hashCode con null ya lo ejercita
        assertDoesNotThrow(cNull1::hashCode);
    }

    @Test
    void equalsDeContacto_conUnoNullYOtroNo_devuelveFalse() {
        Contacto conMail = new Contacto("Con", "mail@demo.com");
        Contacto sinMail = new Contacto("Sin", null);

        assertFalse(conMail.equals(sinMail));
        assertFalse(sinMail.equals(conMail));

        // self-equals con mail no nulo
        assertTrue(conMail.equals(conMail));
    }


}
