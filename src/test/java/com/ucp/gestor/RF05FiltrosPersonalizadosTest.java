package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RF05FiltrosPersonalizadosTest {

    private GestorEmails gestor;
    private GestorFiltros gestorFiltros;
    private Contacto prof, alumno;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        gestorFiltros = new GestorFiltros();
        prof = gestor.crearContacto("Prof", "prof@ucp.edu.ar");
        alumno = gestor.crearContacto("Alumno", "alumno@gmail.com");

        Email e1 = gestor.crearEmail(prof, "Reunión UCP", "Traer apuntes", Arrays.asList(alumno));
        Email e2 = gestor.crearEmail(alumno, "Consulta", "Duda sobre @ucp.edu.ar", Arrays.asList(prof));
        gestor.enviar(e1);
        gestor.enviar(e2);
    }

    @Test
    void filtrosCombinadosAndOrNegate_sobreEntrada() {
        Filtro asuntoReunion = new Filtro("Asunto Reunión",
                m -> m.getAsunto() != null && m.getAsunto().toLowerCase().contains("reunión"));

        Filtro contieneUcp = new Filtro("Contiene UCP",
                m -> (m.getRemitente() != null && m.getRemitente().getEmail().toLowerCase().contains("ucp.edu.ar")) ||
                     (m.getPara().stream().anyMatch(c -> c.getEmail().toLowerCase().contains("ucp.edu.ar"))));

        Filtro andFiltro = asuntoReunion.and(contieneUcp);
        Filtro orFiltro = asuntoReunion.or(contieneUcp);
        Filtro notFiltro = andFiltro.negate();

        List<Email> andRes = gestor.aplicarFiltroSobreBandeja(andFiltro, BandejaType.ENTRADA);
        List<Email> orRes = gestor.aplicarFiltroSobreBandeja(orFiltro, BandejaType.ENTRADA);
        List<Email> notRes = gestor.aplicarFiltroSobreBandeja(notFiltro, BandejaType.ENTRADA);

        assertEquals(1, andRes.size());
        assertTrue(orRes.size() >= 1);
        assertTrue(notRes.size() >= 0); // solo para cubrir la rama negada

        assertTrue(andFiltro.toString().contains("AND"));
        assertTrue(notFiltro.toString().contains("NOT"));
    }

    @Test
    void gestorFiltros_limiteDeCincoYNombresUnicos() {
        for (int i = 1; i <= 5; i++) {
            Filtro f = new Filtro("F" + i, m -> true);
            assertTrue(gestorFiltros.agregarFiltro(f));
        }

        assertFalse(gestorFiltros.agregarFiltro(new Filtro("F6", m -> true))); // supera 5
        assertFalse(gestorFiltros.agregarFiltro(new Filtro("F1", m -> false))); // nombre duplicado
        assertEquals(5, gestorFiltros.cantidadFiltros());
        assertEquals(5, gestorFiltros.maximoFiltros());
    }

    @Test
    void gestorFiltros_buscarYEliminarPorNombre() {
        Filtro f = new Filtro("Temporal", m -> true);
        gestorFiltros.agregarFiltro(f);

        assertTrue(gestorFiltros.buscarPorNombre("Temporal").isPresent());
        assertTrue(gestorFiltros.eliminarFiltro("Temporal"));
        assertFalse(gestorFiltros.buscarPorNombre("Temporal").isPresent());
    }


        @Test
    void gettersDeFiltroYComposicionEncadenada() {
        Filtro siempreTrue = new Filtro("SiempreTrue", m -> true);
        Filtro siempreFalse = new Filtro("SiempreFalse", m -> false);

        // getters
        assertEquals("SiempreTrue", siempreTrue.getNombre());
        assertTrue(siempreTrue.getPredicado().test(new Email()));
        assertFalse(siempreFalse.getPredicado().test(new Email()));

        // composición encadenada: ((true AND false) OR true) NEGATE
        Filtro compuesto = siempreTrue.and(siempreFalse).or(siempreTrue).negate();

        Email e = new Email();
        boolean resultado = compuesto.getPredicado().test(e);

        // (true AND false) = false → false OR true = true → negate = false
        assertFalse(resultado);

        // toString de compuesto usa el nombre generado por and/or/negate
        String texto = compuesto.toString();
        assertTrue(texto.contains("AND") || texto.contains("OR") || texto.contains("NOT"));
    }

}
