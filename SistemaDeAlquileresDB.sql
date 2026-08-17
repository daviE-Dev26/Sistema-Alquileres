USE master;
GO

IF DB_ID('sistemaAlquileres') IS NOT NULL
BEGIN
    ALTER DATABASE sistemaAlquileres
    SET SINGLE_USER WITH ROLLBACK IMMEDIATE;

    DROP DATABASE sistemaAlquileres;
END
GO

CREATE DATABASE sistemaAlquileres;
GO

ALTER DATABASE sistemaAlquileres
SET RECOVERY SIMPLE;
GO

USE sistemaAlquileres;
GO

create table rol(
codrol int not null primary key identity(1,1),
nomrol varchar(20) not null,
estrol bit not null);

create table tipodocumento(
codtipdoc int not null primary key identity(1,1),
nomtipdoc varchar(20) not null,
esttipdoc bit not null);

create table usuario(
codusu int primary key identity(1,1),
nomusu varchar(40) not null,
apepusu varchar(40) not null,
apemusu varchar(40) not null,
docusu varchar (20) not null,
dirusu varchar(100) not null,
fecusu date not null,
celusu varchar(9) not null,
corusu varchar(40) not null,
passusu varchar(255) not null,
estusu varchar(20) not null,
codrol int not null foreign key references rol(codrol),
codtipdoc int not null foreign key references tipodocumento(codtipdoc));

CREATE TABLE sede (
    codsede INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL unique,
    direccion VARCHAR(200) NOT NULL,
    descripcion VARCHAR(500),
    estado BIT NOT NULL,
    codusu INT NOT NULL,

    CONSTRAINT FK_sede_usuario
        FOREIGN KEY (codusu)
        REFERENCES usuario(codusu)
);
create table piso(
    codpiso int identity(1,1) primary key,
    numero int not null,
    estado bit not null,
    codsede int not null,
	FOREIGN KEY(codsede)
    REFERENCES sede(codsede)
)
create table cuarto(
codcuar int not null primary key identity(1,1),
numcuar int not null,
etiqueta varchar(50) null,
passcuar varchar(255) not null unique,
dircuar varchar(100) not null,
preccuar money not null,
feccuar date not null,
descuar varchar(500) not null,
fotocuar varchar(200) not null,
estcuar varchar(20) not null,
habilitado bit NOT NULL default 1,
codusu int not null foreign key references usuario(codusu),
codsede int not null foreign key references sede(codsede),
codpiso int not null FOREIGN KEY REFERENCES piso(codpiso));

create table inquilino(
codinq int primary key identity(1,1),
nominq varchar(40) not null,
apepinq varchar(40) not null,
apeminq varchar(40) not null,
docinq varchar (20) not null,
fecreg date not null,
celinq varchar(9) not null,
corinq varchar(40) not null,
estinq bit not null,
codtipdoc int not null foreign key references tipodocumento(codtipdoc),
codusu int not null foreign key references usuario(codusu));

create table documento(
    coddoc int primary key identity(1,1),
    codinq int not null foreign key references inquilino(codinq),
	codusu int not null foreign key references usuario(codusu),
    tipdoc VARCHAR(50) not null,
    nomdoc VARCHAR(255) not null,
    rutadoc VARCHAR(500) not null,
    fechasubida datetime default GETDATE() not null,
	estdoc bit not null
);

CREATE TABLE inquilino_cuarto (
    codasig INT IDENTITY(1,1) PRIMARY KEY, -- Clave primaria independiente 🚀
    codinq INT NOT NULL,
    codcuar INT NOT NULL,
    fechin DATE NOT NULL,
    fechout DATE NULL,
    montoTotal MONEY NOT NULL,
    estado BIT NOT NULL, -- 1 = Activo, 0 = Finalizado
    codusu INT NOT NULL,
    
    FOREIGN KEY (codusu) REFERENCES usuario(codusu),
    FOREIGN KEY (codinq) REFERENCES inquilino(codinq),
    FOREIGN KEY (codcuar) REFERENCES cuarto(codcuar)
);
create table servicio(
	codserv	int primary key identity(1,1),
	tipserv varchar(50) not null,
	feching date default GETDATE() not null,
	monto	money not null,
	coment varchar(500) null,
	codusu int not null foreign key references usuario(codusu),
	codsede int not null foreign key references sede(codsede)
);
CREATE TABLE notificacion_morosidad (
    id_notif INT PRIMARY KEY IDENTITY(1,1),
    codinq INT NOT NULL,
    dias_mora INT NOT NULL,
    fecha_envio DATETIME NOT NULL DEFAULT GETDATE(),
    email_destino VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (codinq) REFERENCES inquilino(codinq)
);
CREATE TABLE pago_alquiler (
    codpago INT IDENTITY(1,1) PRIMARY KEY,
    codasig INT NOT NULL,
    codusu INT NOT NULL,
    cantidad_meses INT NOT NULL DEFAULT 1,
    monto DECIMAL(10,2) NOT NULL,
    fechaPago DATETIME DEFAULT GETDATE(),
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    origen_pago VARCHAR(30) NOT NULL,
    metodo_pago VARCHAR(40) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    culqi_charge_id VARCHAR(120),
    numero_operacion VARCHAR(100),
    observacion VARCHAR(300),
    fecha_registro DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_PAGO_CONTRATO
        FOREIGN KEY(codasig)
        REFERENCES inquilino_cuarto(codasig),
    CONSTRAINT FK_PAGO_USUARIO
        FOREIGN KEY(codusu)
        REFERENCES usuario(codusu)
);
ALTER TABLE pago_alquiler
ADD numero_comprobante VARCHAR(30);

CREATE TABLE detalle_pago_alquiler(
coddetalle INT IDENTITY(1,1) PRIMARY KEY,
codpago INT NULL,
codasig INT NOT NULL,
anio INT NOT NULL,
mes INT NOT NULL,
monto DECIMAL(10,2) NOT NULL,
estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
fecha_pago DATETIME NULL,
CONSTRAINT FK_DETALLE_PAGO
FOREIGN KEY(codpago)
REFERENCES pago_alquiler(codpago),
CONSTRAINT FK_DETALLE_CONTRATO
FOREIGN KEY(codasig)
REFERENCES inquilino_cuarto(codasig)
);
-- Agregar columna para diferenciar tipos de notificación
ALTER TABLE notificacion_morosidad 
ADD tipo_notificacion VARCHAR(20) NOT NULL DEFAULT 'MOROSIDAD';
SELECT
    c.codcuar,
    c.numcuar,
    c.codsede,
    s.nombre
FROM cuarto c
LEFT JOIN sede s
    ON c.codsede = s.codsede;
	SELECT *
FROM inquilino_cuarto;
INSERT INTO rol (nomrol, estrol)
VALUES ('Propietario', 1);

insert into tipodocumento(nomtipdoc, esttipdoc) values
('DNI', 1)

update inquilino_cuarto
set fechout = DATEADD(MONTH,1,fechin)
where fechout is null;
select *
from inquilino_cuarto

select*from inquilino
select*from usuario
select*from rol
select*from tipodocumento
select*from cuarto
select*from inquilino_cuarto
select*from sede
select*from piso
select*from servicio
select*from notificacion_morosidad
select*from detalle_pago_alquiler
select*from pago_alquiler
SELECT
codasig,
montoTotal,
estado,
codusu
FROM inquilino_cuarto;

SELECT
    codinq,
    dias_mora,
    tipo_notificacion,
    COUNT(*)
FROM notificacion_morosidad
GROUP BY
    codinq,
    dias_mora,
    tipo_notificacion
HAVING COUNT(*) > 1;
/*
UPDATE inquilino_cuarto
SET fechout = DATEADD(DAY, 30, GETDATE())  -- Cambia -3 por los días que quieras
WHERE codinq = 7 AND codcuar = 8;

SELECT 
    i.codinq,
    ic.codcuar,
    ic.fechout AS fecha_salida,
    i.nominq + ' ' + i.apepinq AS nombre_inquilino,
    DATEDIFF(DAY, ic.fechout, GETDATE()) AS dias_mora
FROM inquilino_cuarto ic
INNER JOIN inquilino i ON ic.codinq = i.codinq
WHERE ic.estado = 1
ORDER BY ic.fechout ASC;*/

SELECT codasig, codcuar, montoTotal
FROM inquilino_cuarto;

SELECT
SUM(montoTotal)
FROM inquilino_cuarto
WHERE estado = 1;

CREATE TABLE inquilino_historico(
    codhistinq INT IDENTITY(1,1) PRIMARY KEY,
    codinq INT NOT NULL,
    nominq VARCHAR(40) NOT NULL,
    apepinq VARCHAR(40) NOT NULL,
    apeminq VARCHAR(40) NOT NULL,
    docinq VARCHAR(20) NOT NULL,
    fecreg DATE NOT NULL,
    celinq VARCHAR(9) NOT NULL,
    corinq VARCHAR(40) NOT NULL,
    estinq BIT NOT NULL,
    codtipdoc INT NOT NULL,
    codusu INT NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_inquilino_historico_inquilino
        FOREIGN KEY (codinq)
        REFERENCES inquilino(codinq),
    CONSTRAINT FK_inquilino_historico_tipodocumento
        FOREIGN KEY (codtipdoc)
        REFERENCES tipodocumento(codtipdoc),
    CONSTRAINT FK_inquilino_historico_usuario
        FOREIGN KEY (codusu)
        REFERENCES usuario(codusu)
);

CREATE TABLE inquilino_cuarto_historico (
    codhistasig INT IDENTITY(1,1) PRIMARY KEY,
    codasig INT NOT NULL,
    codinq INT NOT NULL,
    codcuar INT NOT NULL,
    fechin DATE NOT NULL,
    fechout DATE NULL,
    montoTotal MONEY NOT NULL,
    estado BIT NOT NULL,
    codusu INT NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_ic_historico_inquilino
        FOREIGN KEY (codinq)
        REFERENCES inquilino(codinq),
    CONSTRAINT FK_ic_historico_cuarto
        FOREIGN KEY (codcuar)
        REFERENCES cuarto(codcuar),
    CONSTRAINT FK_ic_historico_usuario
        FOREIGN KEY (codusu)
        REFERENCES usuario(codusu)
);
select * from inquilino_cuarto_historico
select * from inquilino