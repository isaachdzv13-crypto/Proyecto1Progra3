# Proyecto 2 - Sistema de Reserva de Recursos (Frontend - Backend - Base de datos)

Modulos Maven:

- `comun`: modelos, interfaz `Service` y mensajes de red (Peticion, Respuesta, Notificacion).
- `backend`: servidor de sockets con hilos. Atiende las peticiones y notifica reservas nuevas. La capa de datos son las clases `accesoDatos/Servicio*` (Oracle y procedimientos almacenados), usadas desde `logica/ServiceReal`.
- `frontend`: interfaz Swing (MVC). Usa `ServiceProxy` para hablar con el backend por sockets.

## Base de datos (Oracle)

El script `sql/Proyecto2_oracle.sql` se ejecuta en Oracle SQL Developer: crea tablas, secuencias, el paquete de tipos y los procedimientos que usan las clases `accesoDatos`.
La clave del usuario de Oracle se lee de la variable de entorno `ORACLE_PASSWORD`.

## Como ejecutar

1. Ejecutar `sql/Proyecto2_oracle.sql` en Oracle SQL Developer.
2. Backend: ejecutar `Main` del modulo `backend` desde la carpeta `backend` (puerto 5000 por defecto).
3. Frontend: ejecutar `Main` del modulo `frontend` desde la carpeta `frontend`; pide `host:puerto` del backend.
4. Pruebas: `mvn verify`. Lista de verificacion con Oracle: `PRUEBAS_ISAAC.md`.
