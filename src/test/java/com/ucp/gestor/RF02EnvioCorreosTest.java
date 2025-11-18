package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RF02EnvioCorreosTest {

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
    void enviarCorreo_pasaDeBorradorAEnviadosYEntrada() {
        Email e = gestor.crearEmail(c1, "Asunto", "Contenido", Arrays.asList(c2, c3));

        gestor.enviar(e);

        assertFalse(e.isBorrador());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENVIADOS).contains(e));
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENTRADA).contains(e));
    }

    @Test
    void enviarCorreo_actualizaFechaYLoMarcaComoNoLeido() {
        Email e = gestor.crearEmail(c1, "Asunto", "Contenido", Arrays.asList(c2));

        gestor.enviar(e);

        assertNotNull(e.getFecha());
        assertFalse(e.isLeido());
    }

@Test
void correoEnviado_apareceEnEnviadosYEnEntrada() {
    GestorEmails gestor = new GestorEmails();
    Contacto remitente = gestor.crearContacto("Profe", "profe@demo.com");
    Contacto alumno1 = gestor.crearContacto("Alumno 1", "alumno1@demo.com");
    Contacto alumno2 = gestor.crearContacto("Alumno 2", "alumno2@demo.com");

    Email correo = gestor.crearEmail(
            remitente,
            "Entrega TP",
            "Recordatorio para entregar el TP",
            Arrays.asList(alumno1, alumno2)
    );

    assertTrue(correo.isBorrador());
    assertTrue(gestor.getTodosEnBandeja(BandejaType.BORRADORES).contains(correo));

    gestor.enviar(correo);

    assertFalse(correo.isBorrador());


    List<Email> enviados = gestor.getTodosEnBandeja(BandejaType.ENVIADOS);
    assertTrue(enviados.contains(correo));

    List<Email> entrada = gestor.getTodosEnBandeja(BandejaType.ENTRADA);
    assertTrue(entrada.contains(correo));

    assertEquals(remitente, correo.getRemitente());
    assertEquals(2, correo.getPara().size());
    assertTrue(correo.getPara().contains(alumno1));
    assertTrue(correo.getPara().contains(alumno2));
    assertFalse(correo.isLeido());
}

}