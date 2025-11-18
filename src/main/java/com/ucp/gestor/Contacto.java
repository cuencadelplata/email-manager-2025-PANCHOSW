package com.ucp.gestor;

import java.util.Objects;

public class Contacto {
    private String nombre;
    private String email;

    public Contacto(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;
        return Objects.equals(email.toLowerCase(), contacto.email.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }

    @Override
    public String toString() {
        return nombre + " <" + email + ">";
    }
}
