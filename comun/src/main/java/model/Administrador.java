package model;

public class Administrador extends Usuario {

    private static final long serialVersionUID = 1L;

    public Administrador(String id, String clave) {
        super(id, clave, Rol.ADMINISTRADOR);
    }
}
