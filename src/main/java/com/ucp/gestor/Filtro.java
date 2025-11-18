package com.ucp.gestor;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Representa un filtro con nombre y un Predicate<Email>.
 * Tiene métodos para combinar filtros (and, or, negate) y producir nuevos filtros.
 */
public class Filtro {
    private final String nombre;
    private final Predicate<Email> predicado;

    public Filtro(String nombre, Predicate<Email> predicado) {
        this.nombre = Objects.requireNonNull(nombre);
        this.predicado = Objects.requireNonNull(predicado);
    }

    public String getNombre() { return nombre; }

    public Predicate<Email> getPredicado() { return predicado; }

    public Filtro and(Filtro otro) {
        return new Filtro(this.nombre + " AND " + otro.nombre, this.predicado.and(otro.predicado));
    }

    public Filtro or(Filtro otro) {
        return new Filtro(this.nombre + " OR " + otro.nombre, this.predicado.or(otro.predicado));
    }

    public Filtro negate() {
        return new Filtro("NOT " + this.nombre, this.predicado.negate());
    }

    @Override
    public String toString() {
        return "Filtro{" + nombre + "}";
    }
}
