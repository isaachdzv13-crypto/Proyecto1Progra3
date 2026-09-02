package repository;

import model.Funcionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaFuncionarios {
    private List<Funcionario> listaFuncionarios;

    public ListaFuncionarios(){
        listaFuncionarios= new ArrayList<>();
    }
    public void addRecurso(Funcionario func) {
        listaFuncionarios.add(func);
    }

    public boolean borrarRecurso(String id) {

        Funcionario funcionario = buscarPorId(id);

        if (funcionario != null) {
            listaFuncionarios.remove(funcionario);
            return true;
        }

        return false;
    }
    public Funcionario buscarPorId(String id) {

        for (Funcionario r : listaFuncionarios) {

            if (Objects.equals(r.getId(), id)) {
                return r;
            }
        }

        return null;
    }
    public List<Funcionario> buscarPorNombre(String nombre) {
        List<Funcionario> res = new ArrayList<>();
        for (Funcionario f : listaFuncionarios) {
            if (nombre == null || nombre.isBlank()
                    || f.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                res.add(f);
            }
        }
        return res;
    }

    public Funcionario buscarPorContrasenia(String contra){
        for(Funcionario f: listaFuncionarios) {
        if(Objects.equals(f.getClave(), contra))
            return f;

        }
        return null;
    }
    public List<Funcionario> listarTodos() {
        return new ArrayList<>(listaFuncionarios);
    }
}
