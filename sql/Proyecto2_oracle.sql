-- PROYECTO 2 - SISTEMA DE RESERVAS


CREATE OR REPLACE PACKAGE P2_TYPES AS
    TYPE REF_CURSOR IS REF CURSOR;
END P2_TYPES;
/

CREATE TABLE USUARIOS (
    ID       VARCHAR2(30)  NOT NULL,
    CLAVE    VARCHAR2(255) NOT NULL,
    ROL      VARCHAR2(20)  NOT NULL,

    CONSTRAINT PK_USUARIOS PRIMARY KEY (ID),
    CONSTRAINT CK_USUARIOS_ROL
        CHECK (ROL IN ('ADMINISTRADOR', 'FUNCIONARIO'))
);

CREATE TABLE FUNCIONARIOS (
    ID        VARCHAR2(30)  NOT NULL,
    NOMBRE    VARCHAR2(100) NOT NULL,
    TELEFONO  VARCHAR2(20)  NOT NULL,

    CONSTRAINT PK_FUNCIONARIOS PRIMARY KEY (ID),
    CONSTRAINT FK_FUNCIONARIOS_USUARIOS
        FOREIGN KEY (ID) REFERENCES USUARIOS(ID)
);

CREATE TABLE CATEGORIAS (
    ID           VARCHAR2(10)  NOT NULL,
    DESCRIPCION  VARCHAR2(100) NOT NULL,

    CONSTRAINT PK_CATEGORIAS PRIMARY KEY (ID)
);

CREATE TABLE RECURSOS (
    ID            VARCHAR2(30)  NOT NULL,
    CATEGORIA_ID  VARCHAR2(10)  NOT NULL,
    DESCRIPCION   VARCHAR2(150) NOT NULL,

    CONSTRAINT PK_RECURSOS PRIMARY KEY (ID),
    CONSTRAINT FK_RECURSOS_CATEGORIAS
        FOREIGN KEY (CATEGORIA_ID) REFERENCES CATEGORIAS(ID)
);

CREATE TABLE RESERVAS (
    ID              VARCHAR2(10)  NOT NULL,
    FUNCIONARIO_ID  VARCHAR2(30)  NOT NULL,
    DESCRIPCION     VARCHAR2(500) NOT NULL,
    ACTIVIDAD       VARCHAR2(150) NOT NULL,
    FECHA           DATE          NOT NULL,
    HORA_INICIO     VARCHAR2(5)   NOT NULL,
    HORA_FIN        VARCHAR2(5)   NOT NULL,
    ESTADO          VARCHAR2(10)  DEFAULT 'ACTIVA' NOT NULL,

    CONSTRAINT PK_RESERVAS PRIMARY KEY (ID),
    CONSTRAINT FK_RESERVAS_FUNCIONARIOS
        FOREIGN KEY (FUNCIONARIO_ID) REFERENCES FUNCIONARIOS(ID),
    CONSTRAINT CK_RESERVAS_ESTADO
        CHECK (ESTADO IN ('ACTIVA', 'CANCELADA'))
);

CREATE TABLE RESERVA_RECURSOS (
    RESERVA_ID  VARCHAR2(10) NOT NULL,
    RECURSO_ID  VARCHAR2(30) NOT NULL,

    CONSTRAINT PK_RESERVA_RECURSOS
        PRIMARY KEY (RESERVA_ID, RECURSO_ID),
    CONSTRAINT FK_RESERVA_RECURSOS_RESERVAS
        FOREIGN KEY (RESERVA_ID) REFERENCES RESERVAS(ID),
    CONSTRAINT FK_RESERVA_RECURSOS_RECURSOS
        FOREIGN KEY (RECURSO_ID) REFERENCES RECURSOS(ID)
);

CREATE SEQUENCE SEQ_CATEGORIAS
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE SEQ_RESERVAS
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE OR REPLACE FUNCTION LOGIN_USUARIO (
    p_id    IN USUARIOS.ID%TYPE,
    p_clave IN USUARIOS.CLAVE%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID, ROL
        FROM USUARIOS
        WHERE ID = p_id
          AND CLAVE = p_clave;

    RETURN v_cursor;
END LOGIN_USUARIO;
/

CREATE OR REPLACE PROCEDURE INSERTAR_USUARIO (
    p_id    IN USUARIOS.ID%TYPE,
    p_clave IN USUARIOS.CLAVE%TYPE,
    p_rol   IN USUARIOS.ROL%TYPE
) AS
BEGIN
    INSERT INTO USUARIOS (ID, CLAVE, ROL)
    VALUES (p_id, p_clave, p_rol);
END INSERTAR_USUARIO;
/
--pruebas
SELECT ID FROM USUARIOS WHERE ID = 'admin';
BEGIN
    INSERTAR_USUARIO('admin', 'admin123', 'ADMINISTRADOR');
END;
/
COMMIT;

VARIABLE resultado REFCURSOR;
EXEC :resultado := LOGIN_USUARIO('admin', 'admin123');
PRINT resultado;


CREATE OR REPLACE PROCEDURE INSERTAR_FUNCIONARIO (
    p_id       IN FUNCIONARIOS.ID%TYPE,
    p_nombre   IN FUNCIONARIOS.NOMBRE%TYPE,
    p_telefono IN FUNCIONARIOS.TELEFONO%TYPE
) AS
BEGIN
    INSERTAR_USUARIO(p_id, p_id, 'FUNCIONARIO');

    INSERT INTO FUNCIONARIOS (ID, NOMBRE, TELEFONO)
    VALUES (p_id, p_nombre, p_telefono);
END INSERTAR_FUNCIONARIO;
/
BEGIN
    INSERTAR_FUNCIONARIO('9999', 'Funcionario Prueba', '8888-9999');
END;
/
COMMIT;

SELECT u.ID, u.ROL, f.NOMBRE, f.TELEFONO
FROM USUARIOS u
JOIN FUNCIONARIOS f ON f.ID = u.ID
WHERE u.ID = '9999';

CREATE OR REPLACE FUNCTION LISTAR_FUNCIONARIOS
RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID, NOMBRE, TELEFONO
        FROM FUNCIONARIOS
        ORDER BY NOMBRE;

    RETURN v_cursor;
END LISTAR_FUNCIONARIOS;
/

VARIABLE resultado REFCURSOR;
EXEC :resultado := LISTAR_FUNCIONARIOS();
PRINT resultado;

CREATE OR REPLACE FUNCTION BUSCAR_FUNCIONARIO (
    p_id IN FUNCIONARIOS.ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID, NOMBRE, TELEFONO
        FROM FUNCIONARIOS
        WHERE ID = p_id;

    RETURN v_cursor;
END BUSCAR_FUNCIONARIO;
/
VARIABLE resultado REFCURSOR;
EXEC :resultado := BUSCAR_FUNCIONARIO('9999');
PRINT resultado;

CREATE OR REPLACE PROCEDURE MODIFICAR_FUNCIONARIO (
    p_id       IN FUNCIONARIOS.ID%TYPE,
    p_nombre   IN FUNCIONARIOS.NOMBRE%TYPE,
    p_telefono IN FUNCIONARIOS.TELEFONO%TYPE
) AS
BEGIN
    UPDATE FUNCIONARIOS
    SET NOMBRE = p_nombre,
        TELEFONO = p_telefono
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'No existe ese funcionario');
    END IF;
END MODIFICAR_FUNCIONARIO;
/

BEGIN
    MODIFICAR_FUNCIONARIO('9999', 'Funcionario Actualizado', '8777-6666');
END;
/
COMMIT;

VARIABLE resultado REFCURSOR;
EXEC :resultado := BUSCAR_FUNCIONARIO('9999');
PRINT resultado;

CREATE OR REPLACE PROCEDURE ELIMINAR_FUNCIONARIO (
    p_id IN FUNCIONARIOS.ID%TYPE
) AS
BEGIN
    DELETE FROM FUNCIONARIOS
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'No existe ese funcionario');
    END IF;

    DELETE FROM USUARIOS
    WHERE ID = p_id;
END ELIMINAR_FUNCIONARIO;
/


--CATEGORIAS CRUD
CREATE OR REPLACE PROCEDURE INSERTAR_CATEGORIA (
    p_descripcion IN CATEGORIAS.DESCRIPCION%TYPE
) AS
    v_id CATEGORIAS.ID%TYPE;
BEGIN
    SELECT 'CAT-' || LPAD(SEQ_CATEGORIAS.NEXTVAL, 6, '0')
    INTO v_id
    FROM DUAL;

    INSERT INTO CATEGORIAS (ID, DESCRIPCION)
    VALUES (v_id, p_descripcion);
END INSERTAR_CATEGORIA;
/

BEGIN
    INSERTAR_CATEGORIA('Sala para 10 personas');
END;
/
COMMIT;

SELECT ID, DESCRIPCION
FROM CATEGORIAS
ORDER BY ID;

CREATE OR REPLACE FUNCTION LISTAR_CATEGORIAS
RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID, DESCRIPCION
        FROM CATEGORIAS
        ORDER BY ID;

    RETURN v_cursor;
END LISTAR_CATEGORIAS;
/

VARIABLE resultado REFCURSOR;
EXEC :resultado := LISTAR_CATEGORIAS();
PRINT resultado;

CREATE OR REPLACE FUNCTION BUSCAR_CATEGORIAS (
    p_descripcion IN CATEGORIAS.DESCRIPCION%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID, DESCRIPCION
        FROM CATEGORIAS
        WHERE UPPER(DESCRIPCION)
              LIKE '%' || UPPER(TRIM(p_descripcion)) || '%'
        ORDER BY DESCRIPCION;

    RETURN v_cursor;
END BUSCAR_CATEGORIAS;
/

VARIABLE resultado REFCURSOR;
EXEC :resultado := BUSCAR_CATEGORIAS('sala');
PRINT resultado;

CREATE OR REPLACE PROCEDURE MODIFICAR_CATEGORIA (
    p_id          IN CATEGORIAS.ID%TYPE,
    p_descripcion IN CATEGORIAS.DESCRIPCION%TYPE
) AS
BEGIN
    UPDATE CATEGORIAS
    SET DESCRIPCION = p_descripcion
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'No existe esa categoria');
    END IF;
END MODIFICAR_CATEGORIA;
/
BEGIN
    MODIFICAR_CATEGORIA('CAT-000001', 'Sala para 15 personas');
END;
/
COMMIT;

VARIABLE resultado REFCURSOR;
EXEC :resultado := LISTAR_CATEGORIAS();
PRINT resultado;


CREATE OR REPLACE PROCEDURE ELIMINAR_CATEGORIA (
    p_id IN CATEGORIAS.ID%TYPE
) AS
BEGIN
    DELETE FROM CATEGORIAS
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'No existe esa categoria');
    END IF;
END ELIMINAR_CATEGORIA;
/

--RECURSOS

CREATE OR REPLACE PROCEDURE INSERTAR_RECURSO (
    p_id           IN RECURSOS.ID%TYPE,
    p_categoria_id IN RECURSOS.CATEGORIA_ID%TYPE,
    p_descripcion  IN RECURSOS.DESCRIPCION%TYPE
) AS
BEGIN
    INSERT INTO RECURSOS (ID, CATEGORIA_ID, DESCRIPCION)
    VALUES (p_id, p_categoria_id, p_descripcion);
END INSERTAR_RECURSO;
/


CREATE OR REPLACE PROCEDURE MODIFICAR_RECURSO (
    p_id           IN RECURSOS.ID%TYPE,
    p_categoria_id IN RECURSOS.CATEGORIA_ID%TYPE,
    p_descripcion  IN RECURSOS.DESCRIPCION%TYPE
) AS
BEGIN
    UPDATE RECURSOS
    SET CATEGORIA_ID = p_categoria_id,
        DESCRIPCION = p_descripcion
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'No existe ese recurso');
    END IF;
END MODIFICAR_RECURSO;
/


CREATE OR REPLACE PROCEDURE ELIMINAR_RECURSO (
    p_id IN RECURSOS.ID%TYPE
) AS
BEGIN
    DELETE FROM RECURSOS
    WHERE ID = p_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'No existe ese recurso');
    END IF;
END ELIMINAR_RECURSO;
/


CREATE OR REPLACE FUNCTION BUSCAR_RECURSO (
    p_id IN RECURSOS.ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.CATEGORIA_ID,
               r.DESCRIPCION,
               c.DESCRIPCION AS CATEGORIA_DESCRIPCION
        FROM RECURSOS r
        JOIN CATEGORIAS c ON c.ID = r.CATEGORIA_ID
        WHERE r.ID = p_id;

    RETURN v_cursor;
END BUSCAR_RECURSO;
/


CREATE OR REPLACE FUNCTION LISTAR_RECURSOS
RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.CATEGORIA_ID,
               r.DESCRIPCION,
               c.DESCRIPCION AS CATEGORIA_DESCRIPCION
        FROM RECURSOS r
        JOIN CATEGORIAS c ON c.ID = r.CATEGORIA_ID
        ORDER BY r.ID;

    RETURN v_cursor;
END LISTAR_RECURSOS;
/


CREATE OR REPLACE FUNCTION LISTAR_RECURSOS_POR_CATEGORIA (
    p_categoria_id IN RECURSOS.CATEGORIA_ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.CATEGORIA_ID,
               r.DESCRIPCION,
               c.DESCRIPCION AS CATEGORIA_DESCRIPCION
        FROM RECURSOS r
        JOIN CATEGORIAS c ON c.ID = r.CATEGORIA_ID
        WHERE r.CATEGORIA_ID = p_categoria_id
        ORDER BY r.ID;

    RETURN v_cursor;
END LISTAR_RECURSOS_POR_CATEGORIA;
/

-- INSERTAR RESERVA
-- p_id devuelve el ID generado.

CREATE OR REPLACE PROCEDURE INSERTAR_RESERVA (
    p_funcionario_id IN RESERVAS.FUNCIONARIO_ID%TYPE,
    p_descripcion   IN RESERVAS.DESCRIPCION%TYPE,
    p_actividad     IN RESERVAS.ACTIVIDAD%TYPE,
    p_fecha         IN RESERVAS.FECHA%TYPE,
    p_hora_inicio   IN RESERVAS.HORA_INICIO%TYPE,
    p_hora_fin      IN RESERVAS.HORA_FIN%TYPE,
    p_id            OUT RESERVAS.ID%TYPE
) AS
    v_numero NUMBER;
BEGIN
    IF TRIM(p_funcionario_id) IS NULL
       OR TRIM(p_descripcion) IS NULL
       OR TRIM(p_actividad) IS NULL
       OR p_fecha IS NULL THEN
        RAISE_APPLICATION_ERROR(
            -20010, 'Complete los datos de la reserva'
        );
    END IF;

    IF p_hora_inicio IS NULL OR p_hora_fin IS NULL
       OR NOT REGEXP_LIKE(
           p_hora_inicio, '^([01][0-9]|2[0-3]):[0-5][0-9]$'
       )
       OR NOT REGEXP_LIKE(
           p_hora_fin, '^([01][0-9]|2[0-3]):[0-5][0-9]$'
       ) THEN
        RAISE_APPLICATION_ERROR(
            -20011, 'Use horas con formato HH:mm'
        );
    END IF;

    IF p_hora_fin <= p_hora_inicio THEN
        RAISE_APPLICATION_ERROR(
            -20012, 'La hora final debe ser posterior a la inicial'
        );
    END IF;

    SELECT SEQ_RESERVAS.NEXTVAL
    INTO v_numero
    FROM DUAL;

    IF v_numero > 999999 THEN
        RAISE_APPLICATION_ERROR(
            -20013, 'Se agoto el formato de IDs de reservas'
        );
    END IF;

    p_id := 'RES-' || LPAD(TO_CHAR(v_numero), 6, '0');

    INSERT INTO RESERVAS (
        ID, FUNCIONARIO_ID, DESCRIPCION, ACTIVIDAD,
        FECHA, HORA_INICIO, HORA_FIN, ESTADO
    ) VALUES (
        p_id, p_funcionario_id, p_descripcion, p_actividad,
        TRUNC(p_fecha), p_hora_inicio, p_hora_fin, 'ACTIVA'
    );
END INSERTAR_RESERVA;
/


-- RECURSOS DISPONIBLES POR CATEGORIA, FECHA Y HORARIO

CREATE OR REPLACE FUNCTION RECURSOS_DISPONIBLES (
    p_categoria_id IN RECURSOS.CATEGORIA_ID%TYPE,
    p_fecha        IN RESERVAS.FECHA%TYPE,
    p_hora_inicio  IN RESERVAS.HORA_INICIO%TYPE,
    p_hora_fin     IN RESERVAS.HORA_FIN%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    IF p_categoria_id IS NULL OR p_fecha IS NULL THEN
        RAISE_APPLICATION_ERROR(
            -20014, 'Indique categoria y fecha'
        );
    END IF;

    IF p_hora_inicio IS NULL OR p_hora_fin IS NULL
       OR NOT REGEXP_LIKE(
           p_hora_inicio, '^([01][0-9]|2[0-3]):[0-5][0-9]$'
       )
       OR NOT REGEXP_LIKE(
           p_hora_fin, '^([01][0-9]|2[0-3]):[0-5][0-9]$'
       ) THEN
        RAISE_APPLICATION_ERROR(
            -20011, 'Use horas con formato HH:mm'
        );
    END IF;

    IF p_hora_fin <= p_hora_inicio THEN
        RAISE_APPLICATION_ERROR(
            -20012, 'La hora final debe ser posterior a la inicial'
        );
    END IF;

    OPEN v_cursor FOR
        SELECT r.ID,
               r.CATEGORIA_ID,
               r.DESCRIPCION,
               c.DESCRIPCION AS CATEGORIA_DESCRIPCION
        FROM RECURSOS r
        JOIN CATEGORIAS c ON c.ID = r.CATEGORIA_ID
        WHERE r.CATEGORIA_ID = p_categoria_id
          AND NOT EXISTS (
              SELECT 1
              FROM RESERVA_RECURSOS rr
              JOIN RESERVAS re ON re.ID = rr.RESERVA_ID
              WHERE rr.RECURSO_ID = r.ID
                AND re.ESTADO = 'ACTIVA'
                AND re.FECHA = TRUNC(p_fecha)
                AND p_hora_inicio < re.HORA_FIN
                AND p_hora_fin > re.HORA_INICIO
          )
        ORDER BY r.ID;

    RETURN v_cursor;
END RECURSOS_DISPONIBLES;
/


-- ASIGNAR UN RECURSO A UNA RESERVA
-- Si se asignan varios, el Backend debe hacerlo en orden de ID.
-- FOR UPDATE mantiene el bloqueo hasta el commit o rollback.

CREATE OR REPLACE PROCEDURE ASIGNAR_RECURSO (
    p_reserva_id IN RESERVA_RECURSOS.RESERVA_ID%TYPE,
    p_recurso_id IN RESERVA_RECURSOS.RECURSO_ID%TYPE
) AS
    v_fecha      RESERVAS.FECHA%TYPE;
    v_inicio     RESERVAS.HORA_INICIO%TYPE;
    v_fin        RESERVAS.HORA_FIN%TYPE;
    v_estado     RESERVAS.ESTADO%TYPE;
    v_recurso_id RECURSOS.ID%TYPE;
    v_cantidad   NUMBER;
BEGIN
    BEGIN
        SELECT FECHA, HORA_INICIO, HORA_FIN, ESTADO
        INTO v_fecha, v_inicio, v_fin, v_estado
        FROM RESERVAS
        WHERE ID = p_reserva_id
        FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20015, 'No existe esa reserva'
            );
    END;

    IF v_estado <> 'ACTIVA' THEN
        RAISE_APPLICATION_ERROR(
            -20016, 'La reserva esta cancelada'
        );
    END IF;

    BEGIN
        SELECT ID
        INTO v_recurso_id
        FROM RECURSOS
        WHERE ID = p_recurso_id
        FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20017, 'No existe ese recurso'
            );
    END;

    -- Comprobar disponibilidad despues de bloquear el recurso.
    SELECT COUNT(*)
    INTO v_cantidad
    FROM RESERVA_RECURSOS rr
    JOIN RESERVAS re ON re.ID = rr.RESERVA_ID
    WHERE rr.RECURSO_ID = p_recurso_id
      AND re.ESTADO = 'ACTIVA'
      AND re.FECHA = v_fecha
      AND v_inicio < re.HORA_FIN
      AND v_fin > re.HORA_INICIO;

    IF v_cantidad > 0 THEN
        RAISE_APPLICATION_ERROR(
            -20018, 'El recurso ya esta reservado en ese horario'
        );
    END IF;

    INSERT INTO RESERVA_RECURSOS (RESERVA_ID, RECURSO_ID)
    VALUES (p_reserva_id, p_recurso_id);
END ASIGNAR_RECURSO;
/


-- BUSCAR RESERVA POR ID

CREATE OR REPLACE FUNCTION BUSCAR_RESERVA (
    p_id IN RESERVAS.ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.FUNCIONARIO_ID,
               f.NOMBRE AS FUNCIONARIO_NOMBRE,
               r.DESCRIPCION,
               r.ACTIVIDAD,
               r.FECHA,
               r.HORA_INICIO,
               r.HORA_FIN,
               r.ESTADO
        FROM RESERVAS r
        JOIN FUNCIONARIOS f ON f.ID = r.FUNCIONARIO_ID
        WHERE r.ID = p_id;

    RETURN v_cursor;
END BUSCAR_RESERVA;
/


-- LISTAR TODAS LAS RESERVAS

CREATE OR REPLACE FUNCTION LISTAR_RESERVAS
RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.FUNCIONARIO_ID,
               f.NOMBRE AS FUNCIONARIO_NOMBRE,
               r.DESCRIPCION,
               r.ACTIVIDAD,
               r.FECHA,
               r.HORA_INICIO,
               r.HORA_FIN,
               r.ESTADO
        FROM RESERVAS r
        JOIN FUNCIONARIOS f ON f.ID = r.FUNCIONARIO_ID
        ORDER BY r.ID;

    RETURN v_cursor;
END LISTAR_RESERVAS;
/


-- LISTAR RESERVAS DE UN FUNCIONARIO

CREATE OR REPLACE FUNCTION RESERVAS_POR_FUNCIONARIO (
    p_funcionario_id IN RESERVAS.FUNCIONARIO_ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.FUNCIONARIO_ID,
               f.NOMBRE AS FUNCIONARIO_NOMBRE,
               r.DESCRIPCION,
               r.ACTIVIDAD,
               r.FECHA,
               r.HORA_INICIO,
               r.HORA_FIN,
               r.ESTADO
        FROM RESERVAS r
        JOIN FUNCIONARIOS f ON f.ID = r.FUNCIONARIO_ID
        WHERE r.FUNCIONARIO_ID = p_funcionario_id
        ORDER BY r.ID;

    RETURN v_cursor;
END RESERVAS_POR_FUNCIONARIO;
/


-- LISTAR RECURSOS ASIGNADOS A UNA RESERVA

CREATE OR REPLACE FUNCTION RECURSOS_POR_RESERVA (
    p_reserva_id IN RESERVAS.ID%TYPE
) RETURN P2_TYPES.REF_CURSOR AS
    v_cursor P2_TYPES.REF_CURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT r.ID,
               r.CATEGORIA_ID,
               r.DESCRIPCION,
               c.DESCRIPCION AS CATEGORIA_DESCRIPCION
        FROM RESERVA_RECURSOS rr
        JOIN RECURSOS r ON r.ID = rr.RECURSO_ID
        JOIN CATEGORIAS c ON c.ID = r.CATEGORIA_ID
        WHERE rr.RESERVA_ID = p_reserva_id
        ORDER BY r.ID;

    RETURN v_cursor;
END RECURSOS_POR_RESERVA;
/


-- CANCELAR RESERVA
-- Conserva la regla de tu Proyecto 1:
-- solo cancelar reservas de dias posteriores al actual.

CREATE OR REPLACE PROCEDURE CANCELAR_RESERVA (
    p_id             IN RESERVAS.ID%TYPE,
    p_funcionario_id IN RESERVAS.FUNCIONARIO_ID%TYPE
) AS
    v_fecha  RESERVAS.FECHA%TYPE;
    v_estado RESERVAS.ESTADO%TYPE;
BEGIN
    BEGIN
        SELECT FECHA, ESTADO
        INTO v_fecha, v_estado
        FROM RESERVAS
        WHERE ID = p_id
          AND FUNCIONARIO_ID = p_funcionario_id
        FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20019, 'No existe esa reserva para el funcionario'
            );
    END;

    IF v_estado = 'CANCELADA' THEN
        RAISE_APPLICATION_ERROR(
            -20016, 'La reserva ya estaba cancelada'
        );
    END IF;

    IF v_fecha <= TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(
            -20020, 'Solo se pueden cancelar reservas futuras'
        );
    END IF;

    UPDATE RESERVAS
    SET ESTADO = 'CANCELADA'
    WHERE ID = p_id;
END CANCELAR_RESERVA;
/

CREATE OR REPLACE PROCEDURE CAMBIAR_CLAVE (
    p_id             IN USUARIOS.ID%TYPE,
    p_clave_actual   IN USUARIOS.CLAVE%TYPE,
    p_clave_nueva    IN USUARIOS.CLAVE%TYPE
) AS
BEGIN
    IF TRIM(p_clave_nueva) IS NULL THEN
        RAISE_APPLICATION_ERROR(
            -20021, 'La nueva clave no puede estar vacia'
        );
    END IF;

    UPDATE USUARIOS
    SET CLAVE = p_clave_nueva
    WHERE ID = p_id
      AND CLAVE = p_clave_actual;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(
            -20022, 'ID o clave actual incorrectos'
        );
    END IF;
END CAMBIAR_CLAVE;
/

SELECT banner FROM v$version;
