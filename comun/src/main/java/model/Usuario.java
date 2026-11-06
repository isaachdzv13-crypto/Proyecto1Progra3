package model;

import java.io.Serializable;

public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Rol {
        ADMINISTRADOR,
        FUNCIONARIO
    }

    private String id;
    private String clave;
    private Rol rol;

    public Usuario(String id, String clave, Rol rol) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }
        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("La clave del usuario es obligatoria");
        }
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{ id='" + id + "', rol=" + rol + "}";
    }
}
