package modelo;

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

}
