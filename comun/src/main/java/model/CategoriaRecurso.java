package model;

import java.io.Serializable;
import java.util.Objects;

public class CategoriaRecurso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String desc;

    public CategoriaRecurso(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaRecurso)) return false;
        CategoriaRecurso otra = (CategoriaRecurso) o;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return desc;
    }
}
