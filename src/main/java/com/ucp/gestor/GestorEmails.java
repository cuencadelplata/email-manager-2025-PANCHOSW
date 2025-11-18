package com.ucp.gestor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Gestor principal: maneja contactos, bandejas, envíos, borradores y filtros.
 * No implementa transporte real de correo — es un simulador para el TP.
 */
public class GestorEmails {
    private final Map<BandejaType, Bandeja> bandejas;
    private final Set<Contacto> contactos;
    private final Map<String, Email> emailIndex; // por id, para operaciones rápidas

    public GestorEmails() {
        bandejas = new EnumMap<>(BandejaType.class);
        for (BandejaType t : BandejaType.values()) {
            bandejas.put(t, new Bandeja(t));
        }
        contactos = new HashSet<>();
        emailIndex = new HashMap<>();
    }

    // ----------------- Contactos -----------------
    public Contacto crearContacto(String nombre, String email) {
        Contacto c = new Contacto(nombre, email);
        contactos.remove(c); // evitar duplicados por email
        contactos.add(c);
        return c;
    }

    public boolean eliminarContacto(String email) {
        return contactos.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public Optional<Contacto> buscarContactoPorEmail(String email) {
        return contactos.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public Set<Contacto> listarContactos() {
        return Collections.unmodifiableSet(contactos);
    }

    // ----------------- Email lifecycle -----------------
    public Email crearEmail(Contacto remitente, String asunto, String contenido, List<Contacto> para) {
        Email e = new Email();
        e.setRemitente(remitente);
        e.setAsunto(asunto);
        e.setContenido(contenido);
        for (Contacto c : para) e.addPara(c);
        e.setBorrador(true);
        indexEmail(e);
        bandejas.get(BandejaType.BORRADORES).agregar(e);
        return e;
    }

    public void guardarBorrador(Email e) {
        e.setBorrador(true);
        if (!bandejas.get(BandejaType.BORRADORES).todos().contains(e)) {
            bandejas.get(BandejaType.BORRADORES).agregar(e);
        }
        indexEmail(e);
    }

    public void editarEmail(Email e, String asunto, String contenido, List<Contacto> para) {
        e.setAsunto(asunto);
        e.setContenido(contenido);
        e.getPara().clear();
        if (para != null) para.forEach(e::addPara);
        indexEmail(e);
    }

    /**
     * Enviar email: marca como no borrador, lo mueve a Enviados, y simula entrega agregándolo a la Bandeja ENTRADA
     * de cada destinatario (en este modelo simple; no gestionamos cuentas separadas).
     */
    public void enviar(Email e) {
        // quitar de borradores si estaba ahi
        bandejas.get(BandejaType.BORRADORES).eliminar(e);

        e.setBorrador(false);
        e.setFecha(java.time.Instant.now());
        e.setLeido(false);

        bandejas.get(BandejaType.ENVIADOS).agregar(e);
        indexEmail(e);

        // simulamos entrega: agregamos copia en la bandeja ENTRADA (mismo objeto en este modelo simple)
        bandejas.get(BandejaType.ENTRADA).agregar(e);
    }

    public boolean moverEmail(Email e, BandejaType origen, BandejaType destino) {
        Bandeja bOrigen = bandejas.get(origen);
        Bandeja bDestino = bandejas.get(destino);
        if (bOrigen == null || bDestino == null) return false;
        if (!bOrigen.todos().contains(e)) return false;
        bOrigen.eliminar(e);
        bDestino.agregar(e);
        return true;
    }

    public void marcarLeido(String emailId, boolean leido) {
        Email e = emailIndex.get(emailId);
        if (e != null) e.setLeido(leido);
    }

    public void marcarFavorito(String emailId, boolean fav) {
        Email e = emailIndex.get(emailId);
        if (e != null) {
            e.setFavorito(fav);
            // si es favorito, asegurar en bandeja FAVORITOS (evitar duplicados)
            List<Email> favs = bandejas.get(BandejaType.FAVORITOS).todos();
            if (fav && !favs.contains(e)) bandejas.get(BandejaType.FAVORITOS).agregar(e);
            if (!fav) bandejas.get(BandejaType.FAVORITOS).eliminar(e);
        }
    }

    public void eliminarDefinitivo(Email e) {
        // quitar de todas las bandejas y ubicar en ELIMINADOS
        for (Bandeja b : bandejas.values()) {
            b.eliminar(e);
        }
        bandejas.get(BandejaType.ELIMINADOS).agregar(e);
    }

    public List<Email> buscarEnBandeja(BandejaType tipo, Predicate<Email> pred) {
        return bandejas.get(tipo).buscar(pred);
    }

    public List<Email> buscarTextoEnBandeja(BandejaType tipo, String textoLibre) {
        String q = textoLibre == null ? "" : textoLibre.toLowerCase();
        return buscarEnBandeja(tipo, e ->
                (e.getAsunto() != null && e.getAsunto().toLowerCase().contains(q)) ||
                (e.getContenido() != null && e.getContenido().toLowerCase().contains(q)) ||
                (e.getRemitente() != null && e.getRemitente().getEmail().toLowerCase().contains(q)) ||
                (e.getPara().stream().anyMatch(c -> c.getEmail().toLowerCase().contains(q)))
        );
    }

    public List<Email> aplicarFiltroSobreBandeja(Filtro filtro, BandejaType tipo) {
        return buscarEnBandeja(tipo, filtro.getPredicado());
    }

    public List<Email> getTodosEnBandeja(BandejaType tipo) {
        return bandejas.get(tipo).todos();
    }

    // ----------------- Indexing -----------------
    private void indexEmail(Email e) {
        emailIndex.put(e.getId(), e);
    }

    public Optional<Email> buscarPorId(String id) {
        return Optional.ofNullable(emailIndex.get(id));
    }
}
