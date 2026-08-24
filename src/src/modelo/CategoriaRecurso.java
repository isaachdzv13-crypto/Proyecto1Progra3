package modelo;

public class CategoriaRecurso {

    private String desc;
    private String id;

    CategoriaRecurso(String id, String desc){
        this.id=id;
        this.desc=desc;

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






}
