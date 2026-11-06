package model;

import java.io.Serializable;
import java.util.Objects;

public class Recurso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private CategoriaRecurso categoria;
    private String descripcion;

    public Recurso(String id, CategoriaRecurso categoria, String descripcion) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recurso)) return false;
        Recurso otro = (Recurso) o;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
