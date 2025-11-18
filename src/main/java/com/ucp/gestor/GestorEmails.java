package com.ucp.gestor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class GestorEmails {
    private final Map<BandejaType, Bandeja> bandejas;
    private final Set<Contacto> contactos;
    private final Map<String, Email> emailIndex;

    public GestorEmails() {
        bandejas = new EnumMap<>(BandejaType.class);
        for (BandejaType t : BandejaType.values()) {
            bandejas.put(t, new Bandeja(t));
        }
        contactos = new HashSet<>();
        emailIndex = new HashMap<>();
    }

    public Contacto crearContacto(String nombre, String email) {
        Contacto c = new Contacto(nombre, email);
        contactos.remove(c);
        contactos.add(c);
        return c;
    }

    public boolean eliminarContacto(String email) {
        return contactos.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public Optional<Contacto> buscarContactoPorEmail(String email) {
        return contactos.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Set<Contacto> listarContactos() {
        return Collections.unmodifiableSet(contactos);
    }

    public boolean editarContacto(String emailOriginal, String nuevoNombre, String nuevoEmail) {
        return buscarContactoPorEmail(emailOriginal)
                .map(c -> editarContactoInterno(c, nuevoNombre, nuevoEmail))
                .orElse(false);
    }

    private boolean editarContactoInterno(Contacto contacto, String nuevoNombre, String nuevoEmail) {
        contactos.remove(contacto);
        contacto.setNombre(nuevoNombre);
        contacto.setEmail(nuevoEmail);
        contactos.add(contacto);
        return true;
    }

    public Email crearEmail(Contacto remitente, String asunto, String contenido, List<Contacto> para) {
        Email e = new Email();
        e.setRemitente(remitente);
        e.setAsunto(asunto);
        e.setContenido(contenido);
        for (Contacto c : para) {
            e.addPara(c);
        }
        e.setBorrador(true);
        indexEmail(e);
        bandejas.get(BandejaType.BORRADORES).agregar(e);
        return e;
    }

    public void guardarBorrador(Email e) {
        e.setBorrador(true);

        Bandeja borradores = bandejas.get(BandejaType.BORRADORES);
        List<Email> lista = borradores.todos();
        boolean noEsta = !lista.contains(e);

        // solo agrega si no estaba: usamos cortocircuito de &&
        boolean ignorar = noEsta && agregarEnBandeja(borradores, e);

        indexEmail(e);
    }

    private boolean agregarEnBandeja(Bandeja b, Email e) {
        b.agregar(e);
        return true;
    }

    public void editarEmail(Email e, String asunto, String contenido, List<Contacto> para) {
        e.setAsunto(asunto);
        e.setContenido(contenido);
        e.getPara().clear();

        List<Contacto> listaPara = Optional.ofNullable(para)
                .orElseGet(Collections::emptyList);
        listaPara.forEach(e::addPara);

        indexEmail(e);
    }

    /**
     * Enviar email: marca como no borrador, lo mueve a Enviados, y simula entrega agregándolo a la Bandeja ENTRADA
     * de cada destinatario (en este modelo simple; no gestionamos cuentas separadas).
     */
    public void enviar(Email e) {
        bandejas.get(BandejaType.BORRADORES).eliminar(e);

        e.setBorrador(false);
        e.setFecha(java.time.Instant.now());
        e.setLeido(false);

        bandejas.get(BandejaType.ENVIADOS).agregar(e);
        indexEmail(e);

        bandejas.get(BandejaType.ENTRADA).agregar(e);
    }

    public boolean moverEmail(Email e, BandejaType origen, BandejaType destino) {
        Bandeja bOrigen = bandejas.get(origen);
        Bandeja bDestino = bandejas.get(destino);

        boolean bandejasValidas = bOrigen != null && bDestino != null;
        boolean contiene = bandejasValidas && bOrigen.todos().contains(e);

        return contiene && moverInterno(bOrigen, bDestino, e);
    }

    private boolean moverInterno(Bandeja origen, Bandeja destino, Email e) {
        origen.eliminar(e);
        destino.agregar(e);
        return true;
    }

    public void marcarLeido(String emailId, boolean leido) {
        Optional.ofNullable(emailIndex.get(emailId))
                .ifPresent(e -> e.setLeido(leido));
    }

    public void marcarFavorito(String emailId, boolean fav) {
        Optional.ofNullable(emailIndex.get(emailId))
                .ifPresent(e -> marcarFavoritoInterno(e, fav));
    }

    private void marcarFavoritoInterno(Email e, boolean fav) {
        e.setFavorito(fav);

        Bandeja favoritos = bandejas.get(BandejaType.FAVORITOS);
        List<Email> lista = favoritos.todos();

        boolean esta = lista.contains(e);
        boolean debeAgregar = fav && !esta;
        boolean debeEliminar = !fav && esta;

        boolean ignorar1 = debeAgregar && agregarEnBandeja(favoritos, e);
        boolean ignorar2 = debeEliminar && favoritos.eliminar(e);
    }

    public void eliminarDefinitivo(Email e) {
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

    // ----------------- Restaurar desde Eliminados -----------------

    public boolean restaurarDesdeEliminados(Email email, BandejaType destino) {
        return moverEmail(email, BandejaType.ELIMINADOS, destino);
    }

    public boolean restaurarAEntrada(Email email) {
        return restaurarDesdeEliminados(email, BandejaType.ENTRADA);
    }

    // ----------------- Indexing -----------------
    private void indexEmail(Email e) {
        emailIndex.put(e.getId(), e);
    }

    public Optional<Email> buscarPorId(String id) {
        return Optional.ofNullable(emailIndex.get(id));
    }
}
