package accesoDatos;

import model.Funcionario;

public class PruebaConexion extends Servicio {

    public static void main(String[] args) {

        ServicioFuncionarios servicio = new ServicioFuncionarios();
        String id = "PRUEBA-JDBC-01";

        try {
            if (servicio.buscarPorId(id) != null) {
                System.out.println("Ese ID ya existe. Usa otro para la prueba.");
                return;
            }

            Funcionario funcionario = new Funcionario(
                    id,
                    "Funcionario de prueba",
                    "8888-9999"
            );

            servicio.insertar(funcionario);
            System.out.println("Funcionario insertado.");

            Funcionario encontrado = servicio.buscarPorId(id);

            if (encontrado == null) {
                throw new IllegalStateException(
                        "No se encontro el funcionario insertado."
                );
            }

            System.out.println("Encontrado: " + encontrado.getNombre());

            encontrado.setNombre("Nombre actualizado");
            servicio.modificar(encontrado);

            encontrado = servicio.buscarPorId(id);

            if (encontrado == null
                    || !"Nombre actualizado".equals(encontrado.getNombre())) {

                throw new IllegalStateException(
                        "No se guardo el cambio de nombre."
                );
            }

            System.out.println("Modificacion comprobada.");

            boolean aparece = false;

            for (Funcionario f : servicio.listar()) {
                System.out.println(f.getId() + " - " + f.getNombre());

                if (id.equals(f.getId())) {
                    aparece = true;
                }
            }

            if (!aparece) {
                throw new IllegalStateException(
                        "El funcionario no aparece en la lista."
                );
            }

            servicio.eliminar(id);

            if (servicio.buscarPorId(id) != null) {
                throw new IllegalStateException(
                        "El funcionario no se elimino."
                );
            }

            System.out.println("Eliminacion comprobada.");
            System.out.println("Todas las operaciones funcionaron.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}