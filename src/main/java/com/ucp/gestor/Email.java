package com.ucp.gestor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Email {
    private final String id;
    private String asunto;
    private String contenido;
    private Contacto remitente;
    private final List<Contacto> para;
    private Instant fecha;
    private boolean leido;
    private boolean favorito;
    private boolean borrador;

    public Email() {
        this.id = UUID.randomUUID().toString();
        this.para = new ArrayList<>();
        this.fecha = Instant.now();
        this.borrador = true;
    }

    public String getId() { return id; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Contacto getRemitente() { return remitente; }
    public void setRemitente(Contacto remitente) { this.remitente = remitente; }

    public List<Contacto> getPara() { return para; }
    public void addPara(Contacto c) { this.para.add(c); }

    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }

    public boolean isBorrador() { return borrador; }
    public void setBorrador(boolean borrador) { this.borrador = borrador; }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", asunto='" + asunto + '\'' +
                ", remitente=" + remitente +
                ", para=" + para +
                ", fecha=" + fecha +
                ", leido=" + leido +
                ", favorito=" + favorito +
                ", borrador=" + borrador +
                '}';
    }
}
