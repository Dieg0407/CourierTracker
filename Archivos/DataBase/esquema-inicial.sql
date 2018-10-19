create database alkorcou_courier_tracker;

create table alkorcou_courier_tracker.rangos(
	id_rango int,
    descripcion varchar(60) not null,
    primary key(id_rango)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

insert into alkorcou_courier_tracker.rangos (id_rango, descripcion)
values (1,'ADMINISTRADOR'),(2,'REPARTIDOR INTERNO'),(3,'REPARTIDOR EXTERNO'),(4,'REGISTRADOR');

create table alkorcou_courier_tracker.estados(
	id_estado int,
    descripcion varchar(60) not null,
    primary key(id_estado)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

insert into alkorcou_courier_tracker.estados (id_estado,descripcion)
values (-1,'ANULADO'),(0,'NO SE ENTREGO'),(1,'REGISTRADO'),(2,'EN RUTA'),(3,'ENTREGA EXITOSA');

create table alkorcou_courier_tracker.usuarios(
	correo varchar(80),
    pass varchar(120) not null,
    nombres varchar (80),
    apellidos varchar (80),
    dni char(8),
    id_rango int,
    activo boolean,
    
    primary key (correo),
    foreign key (id_rango) references rangos(id_rango)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

insert into alkorcou_courier_tracker.usuarios 
values ('admin@local.ed','fJx4fZpw6ZfWu/wjq00qZe5E804TU01B',null,null,null,1,1);

create table alkorcou_courier_tracker.clientes(
	id_cliente int,
    nombres varchar (80) not null,
    apellidos varchar (80) not null,
    dni varchar (11) not null,
    celular varchar (12) not null,
    correo varchar (80),
    
    primary key (id_cliente)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

create table alkorcou_courier_tracker.productos(
	codigo varchar(10),
    numero int(11) ,
    descripcion varchar(200),
    direccion varchar (200),
    origen varchar(30),
    destino varchar(30),
    cliente_envio int,
    cliente_recepcion int,
    id_estado int,
    fec_creacion long ,
	fec_entregado long,
    
    primary key (codigo,numero),
    foreign key (cliente_envio) references clientes(id_cliente),
    foreign key (cliente_recepcion) references clientes(id_cliente),
    foreign key (id_estado) references estados(id_estado)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

create table alkorcou_courier_tracker.asignaciones(
	correo varchar(80),
    codigo varchar(10),
    numero int(11) ,
    
    foreign key (correo) references usuarios(correo),
    primary key (correo,codigo,numero)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

create table alkorcou_courier_tracker.imagenes(
	identificador int auto_increment,
    codigo varchar(10),
    numero int,
    image MEDIUMBLOB ,
    
    primary key (identificador)
)
CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;

drop table alkorcou_courier_tracker.imagenes;
select * from alkorcou_courier_tracker.imagenes;
insert into alkorcou_courier_tracker.imagenes (codigo,numero) values ('ct',1);