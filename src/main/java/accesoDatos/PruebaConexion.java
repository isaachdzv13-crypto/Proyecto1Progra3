package accesoDatos;

public class PruebaConexion extends Servicio {

    public static void main(String[] args) {

        ServicioLogin servicio = new ServicioLogin();

        try {
            String rol = servicio.login("admin", "admin123");

            if (rol == null) {
                System.out.println("ID o contraseña incorrectos.");
            } else {
                System.out.println("Login exitoso. Rol: " + rol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}