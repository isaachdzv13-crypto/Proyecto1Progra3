# Pruebas que solo se pueden hacer con Oracle (Isaac)

Preparacion: ejecutar `sql/Proyecto2_oracle.sql` en SQL Developer, definir la variable de entorno `ORACLE_PASSWORD`.

1. Ejecutar `Main` del backend. Debe imprimir "Backend escuchando en el puerto 5000".
2. Ejecutar `Main` del frontend dos veces (dos ventanas) e ingresar a `localhost:5000`.
3. Login de administrador y de funcionario. Revisar que el texto del rol del administrador sea "ADMINISTRADOR".
4. Crear un funcionario, una categoria y un recurso. Intentar borrar una categoria con recursos: debe salir "No se puede borrar: hay registros asociados."
5. Con la ventana A crear una reserva. En la ventana B (otro funcionario) debe aparecer la alerta; en A no.
6. En B probar Ver (detalle) y Descartar (pide confirmacion y solo quita la alerta).
7. Crear dos reservas que choquen en horario con la misma categoria: la segunda debe dar "No hay recursos disponibles".
8. Cancelar una reserva futura propia.
9. Revisar Calendarizacion, Actividades y Estadisticas con datos.
10. `mvn verify`: las pruebas que no necesitan base de datos deben pasar.
