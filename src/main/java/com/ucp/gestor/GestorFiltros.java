package com.ucp.gestor;

import java.util.*;

/**
 * Gestor de filtros personalizados.
 * - Permite registrar hasta 5 filtros.
 * - Evita nombres duplicados.
 * - Permite listar y buscar filtros por nombre.
 */
public class GestorFiltros {

    private static final int MAX_FILTROS = 5;

    // Usamos un Map para acceder rápido por nombre
    private final Map<String, Filtro> filtros;

    public GestorFiltros() {
        this.filtros = new LinkedHashMap<>(); // mantiene orden de inserción
    }

    /**
     * Intenta registrar un filtro nuevo.
     * @param filtro Filtro a agregar
     * @return true si se agregó, false si ya había 5 filtros o el nombre ya existía
     */
    public boolean agregarFiltro(Filtro filtro) {
        Objects.requireNonNull(filtro, "El filtro no puede ser null");

        if (filtros.containsKey(filtro.getNombre())) {
            // ya existe uno con ese nombre
            return false;
        }
        if (filtros.size() >= MAX_FILTROS) {
            // ya alcanzamos el máximo permitido
            return false;
        }

        filtros.put(filtro.getNombre(), filtro);
        return true;
    }

    /**
     * Elimina un filtro por nombre.
     * @param nombre nombre del filtro a eliminar
     * @return true si existía y se eliminó, false en caso contrario
     */
    public boolean eliminarFiltro(String nombre) {
        return filtros.remove(nombre) != null;
    }

    /**
     * Busca un filtro por nombre.
     */
    public Optional<Filtro> buscarPorNombre(String nombre) {
        return Optional.ofNullable(filtros.get(nombre));
    }

    /**
     * Lista todos los filtros registrados (vista de solo lectura).
     */
    public List<Filtro> listarFiltros() {
        return List.copyOf(filtros.values());
    }

    /**
     * Devuelve cuántos filtros hay actualmente registrados.
     */
    public int cantidadFiltros() {
        return filtros.size();
    }

    /**
     * Devuelve el máximo permitido de filtros.
     */
    public int maximoFiltros() {
        return MAX_FILTROS;
    }
}
