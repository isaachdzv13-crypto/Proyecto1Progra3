package model;

public class CategoriaRecurso {
    private static int contador = 1;
    private String desc;
    private String id;

    public CategoriaRecurso(String desc){
        this.id=generarId();
        this.desc=desc;

    }
    public CategoriaRecurso(String id, String desc) {
        this.id = id;
        this.desc = desc;
        actualizarContador(id);
    }

    private static void actualizarContador(String id) {
        int numero = Integer.parseInt(id.replace("CAT-", ""));

        if (numero >= contador) {
            contador = numero + 1;
        }
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


    private String generarId() {
        return String.format("CAT-%06d", contador++);
    }

    @Override
    public String toString() {
        return desc;
    }

}
