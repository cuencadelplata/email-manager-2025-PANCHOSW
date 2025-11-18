package com.ucp.gestor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Bandeja {
    private final BandejaType tipo;
    private final List<Email> correos;

    public Bandeja(BandejaType tipo) {
        this.tipo = tipo;
        this.correos = new ArrayList<>();
    }

    public BandejaType getTipo() { return tipo; }

    public void agregar(Email e) {
        correos.add(e);
    }

    public boolean eliminar(Email e) {
        return correos.remove(e);
    }

    public List<Email> todos() {
        return new ArrayList<>(correos); // copia defensiva
    }

    public List<Email> buscar(Predicate<Email> pred) {
        return correos.stream().filter(pred).collect(Collectors.toList());
    }

    public void vaciar() {
        correos.clear();
    }
}