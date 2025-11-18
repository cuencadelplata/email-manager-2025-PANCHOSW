package com.ucp.gestor;

import java.util.Objects;

public class Contacto {
    private String nombre;
    private String email;

    public Contacto(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        // 1) si es el mismo objeto, siempre true
        // 2) si no, comparamos por email ignorando mayúsculas, siempre que ambos emails no sean null
        return this == o ||
                (o instanceof Contacto
                        && email != null
                        && ((Contacto) o).email != null
                        && email.equalsIgnoreCase(((Contacto) o).email));
    }

    @Override
    public int hashCode() {
        String correo = email == null ? "" : email.toLowerCase();
        return Objects.hash(correo);
    }

    @Override
    public String toString() {
        return nombre + " <" + email + ">";
    }
}
