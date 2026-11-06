DROP DATABASE IF EXISTS reservas;
CREATE DATABASE reservas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE reservas;

CREATE TABLE usuario (
    id        VARCHAR(30)  NOT NULL,
    clave     VARCHAR(50)  NOT NULL,
    rol       ENUM('ADMINISTRADOR', 'FUNCIONARIO') NOT NULL,
    nombre    VARCHAR(100) NULL,
    telefono  VARCHAR(30)  NULL,
    PRIMARY KEY (id)
);

CREATE TABLE categoria (
    id           VARCHAR(20)  NOT NULL,
    descripcion  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE recurso (
    id            VARCHAR(30)  NOT NULL,
    id_categoria  VARCHAR(20)  NOT NULL,
    descripcion   VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_categoria) REFERENCES categoria (id)
);

CREATE TABLE reserva (
    id              VARCHAR(20)  NOT NULL,
    id_funcionario  VARCHAR(30)  NOT NULL,
    actividad       VARCHAR(150) NOT NULL,
    descripcion     VARCHAR(255) NOT NULL,
    fecha           DATE         NOT NULL,
    hora_inicio     TIME         NOT NULL,
    hora_fin        TIME         NOT NULL,
    estado          ENUM('ACTIVA', 'CANCELADA') NOT NULL DEFAULT 'ACTIVA',
    PRIMARY KEY (id),
    FOREIGN KEY (id_funcionario) REFERENCES usuario (id)
);

CREATE TABLE reserva_recurso (
    id_reserva  VARCHAR(20) NOT NULL,
    id_recurso  VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_reserva, id_recurso),
    FOREIGN KEY (id_reserva) REFERENCES reserva (id),
    FOREIGN KEY (id_recurso) REFERENCES recurso (id)
);

INSERT INTO usuario (id, clave, rol, nombre, telefono) VALUES
    ('admin', '1234', 'ADMINISTRADOR', NULL, NULL),
    ('1234', '1234', 'FUNCIONARIO', 'Funcionario Demo', '8888-8888'),
    ('5678', '5678', 'FUNCIONARIO', 'Maria Mata', '7777-7777');

INSERT INTO categoria (id, descripcion) VALUES
    ('CAT-000001', 'Sala para 10 personas'),
    ('CAT-000002', 'Laptop windows');

INSERT INTO recurso (id, id_categoria, descripcion) VALUES
    ('238715', 'CAT-000002', 'Laptop #238715'),
    ('34343', 'CAT-000001', 'Sala 1 primer piso');
