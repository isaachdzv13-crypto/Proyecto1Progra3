package accesoDatos;

import model.CategoriaRecurso;
import model.Funcionario;

import java.util.ArrayList;

public class PruebaConexion extends Servicio {

    public static void main(String[] args) {

        ServicioCategoria servicio = new ServicioCategoria();
        String descripcion = "Categoria prueba JDBC";

        try {
            if (!servicio.buscarPorDescripcion(descripcion).isEmpty()) {
                System.out.println("Ya existe una categoria de prueba.");
                return;
            }

            servicio.insertar(descripcion);
            System.out.println("Categoria insertada.");

            ArrayList<CategoriaRecurso> encontradas =
                    servicio.buscarPorDescripcion(descripcion);

            if (encontradas.size() != 1) {
                throw new IllegalStateException(
                        "No se encontro una unica categoria de prueba."
                );
            }

            CategoriaRecurso categoria = encontradas.get(0);
            String id = categoria.getId();

            System.out.println("ID generado por Oracle: " + id);

            categoria.setDesc("Categoria JDBC modificada");
            servicio.modificar(categoria);

            boolean modificada = false;

            for (CategoriaRecurso c : servicio.listar()) {
                System.out.println(c.getId() + " - " + c.getDesc());

                if (id.equals(c.getId())
                        && "Categoria JDBC modificada".equals(c.getDesc())) {
                    modificada = true;
                }
            }

            if (!modificada) {
                throw new IllegalStateException(
                        "No se guardo la modificacion."
                );
            }

            System.out.println("Modificacion comprobada.");

            servicio.eliminar(id);

            for (CategoriaRecurso c : servicio.listar()) {
                if (id.equals(c.getId())) {
                    throw new IllegalStateException(
                            "La categoria no se elimino."
                    );
                }
            }

            System.out.println("Eliminacion comprobada.");
            System.out.println("Todas las operaciones funcionaron.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}