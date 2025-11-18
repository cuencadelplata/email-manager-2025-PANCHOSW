package com.ucp.gestor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CoberturaExtraTest {

    private GestorEmails gestor;
    private GestorFiltros gestorFiltros;
    private Contacto c1, c2;

    @BeforeEach
    void setUp() {
        gestor = new GestorEmails();
        gestorFiltros = new GestorFiltros();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
    }

    // RF-01 / RF-07: crear email "raro" y usar guardarBorrador sobre uno que NO estaba en BORRADORES

    @Test
    void rf01_rf07_guardarBorradorSobreEmailExterno_loAgregaYLoIndexa() {
        // Email creado directamente sin pasar por crearEmail (no está en ninguna bandeja)
        Email e = new Email();
        e.setRemitente(c1);
        e.setAsunto("Manual");
        e.setContenido("Creado fuera del gestor");
        e.addPara(c2);

        assertTrue(gestor.getTodosEnBandeja(BandejaType.BORRADORES).isEmpty());

        gestor.guardarBorrador(e);

        List<Email> borradores = gestor.getTodosEnBandeja(BandejaType.BORRADORES);
        assertEquals(1, borradores.size());
        assertTrue(borradores.contains(e));
        assertTrue(e.isBorrador());

        // indexEmail también quedó cubierto
        assertTrue(gestor.buscarPorId(e.getId()).isPresent());
    }

    // RF-02 / RF-08: enviar, cambiar fecha y usar marcarLeido (incluye setFecha explícito)

    @Test
    void rf02_rf08_enviarYForzarSetFechaYMarcarLeido() {
        Email e = gestor.crearEmail(c1, "Envio", "Contenido", Arrays.asList(c2));
        gestor.enviar(e);

        // forzamos un cambio de fecha manual para cubrir setFecha desde el test
        Instant nuevaFecha = Instant.now();
        e.setFecha(nuevaFecha);
        assertEquals(nuevaFecha, e.getFecha());

        // marcar leído / no leído ya cubre Optional.ifPresent internamente
        gestor.marcarLeido(e.getId(), true);
        assertTrue(e.isLeido());

        gestor.marcarLeido(e.getId(), false);
        assertFalse(e.isLeido());

        // id inexistente → rama Optional vacía (no debe romper)
        gestor.marcarLeido("id-inexistente", true);
    }

    // RF-05: GestorFiltros - estado inicial, listar inmodificable, agregar null, eliminar inexistente

    @Test
    void rf05_gestorFiltros_estadoInicial_listarInmodificable_yCasosBorde() {
        // estado inicial
        assertEquals(0, gestorFiltros.cantidadFiltros());
        assertEquals(5, gestorFiltros.maximoFiltros());
        assertTrue(gestorFiltros.listarFiltros().isEmpty());

        // listarFiltros es inmodificable (List.copyOf)
        List<Filtro> vista = gestorFiltros.listarFiltros();
        assertThrows(UnsupportedOperationException.class,
                () -> vista.add(new Filtro("X", m -> true)));

        // agregar null → Objects.requireNonNull dispara NullPointerException
        assertThrows(NullPointerException.class, () -> gestorFiltros.agregarFiltro(null));

        // eliminar filtro que nunca existió → false
        assertFalse(gestorFiltros.eliminarFiltro("no-existe"));
    }

    // RF-05: Filtro - getters y composición encadenada fuerte

    @Test
    void rf05_filtro_gettersYComposicionEncadenada() {
        Filtro siempreTrue = new Filtro("SiempreTrue", m -> true);
        Filtro siempreFalse = new Filtro("SiempreFalse", m -> false);

        Email e = new Email();

        // getters
        assertEquals("SiempreTrue", siempreTrue.getNombre());
        assertTrue(siempreTrue.getPredicado().test(e));
        assertFalse(siempreFalse.getPredicado().test(e));

        // ((true AND false) OR true) NEGATE  → false
        Filtro compuesto = siempreTrue.and(siempreFalse).or(siempreTrue).negate();
        boolean resultado = compuesto.getPredicado().test(e);
        assertFalse(resultado);

        String texto = compuesto.toString();
        assertTrue(texto.contains("AND") || texto.contains("OR") || texto.contains("NOT"));
    }

    // RF-06: restaurarDesdeEliminados hacia una bandeja que no sea ENTRADA

    @Test
    void rf06_restaurarDesdeEliminadosAHaciaEnviados() {
        Email e = gestor.crearEmail(c1, "Multi", "Cuerpo", Arrays.asList(c2));
        gestor.enviar(e);

        // lo marcamos como favorito para asegurarnos que está en varias bandejas
        gestor.marcarFavorito(e.getId(), true);
        assertTrue(gestor.getTodosEnBandeja(BandejaType.FAVORITOS).contains(e));

        gestor.eliminarDefinitivo(e);
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ELIMINADOS).contains(e));

        // restaurar explícitamente a ENVIADOS
        boolean restaurado = gestor.restaurarDesdeEliminados(e, BandejaType.ENVIADOS);
        assertTrue(restaurado);
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENVIADOS).contains(e));
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ELIMINADOS).isEmpty());
    }

    // RF-09: marcarFavorito – probamos directamente la rama "id no encontrado"

    @Test
    void rf09_marcarFavoritoConIdInexistente_noProvocaCambios() {
        // id inexistente → Optional.ofNullable(...) vacío → no ejecuta marcarFavoritoInterno
        gestor.marcarFavorito("id-que-no-existe", true);

        // no hay correos ni en FAVORITOS ni en otras bandejas
        assertTrue(gestor.getTodosEnBandeja(BandejaType.FAVORITOS).isEmpty());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENTRADA).isEmpty());
        assertTrue(gestor.getTodosEnBandeja(BandejaType.ENVIADOS).isEmpty());
    }
}
