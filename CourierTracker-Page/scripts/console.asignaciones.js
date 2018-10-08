
var productosAsignacion;
var usuariosRepartidores;

function loadMenuAsignaciones(){
	document.getElementById('contenido').innerHTML='';
	var menu = document.getElementById('menu');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'menu_asignacion.html', true);
	xhr.onload = function() {
		if(xhr.status === 200)
			menu.innerHTML = xhr.responseText;
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga del Menú de Asignaciones');
		
	};
	xhr.send();
}
//PARA LA ASIGNACION POR PRODUCTOS
function listarProductosAsignacion(){
	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url+'TrackerAPI/listProductos');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){
			var responseObject = JSON.parse(xhr.responseText);
			cargarTablaProductosAsignacion(responseObject);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');
	};
	xhr.send();
}
function cargarTablaProductosAsignacion(responseObject){
	var formulario = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'tabla_productos.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){			
			formulario.innerHTML = xhr.responseText;
			var table = document.getElementById("product-table");
			//AQUI SE CARGAN LOS USUARIOS
			for(let product in responseObject)
				table.innerHTML += createRowProductoAsignacion(responseObject[product],product);

			///var rows = table.getElementsByTagName('tr');
			
			productosAsignacion = responseObject;
		}
		else//CASO DE ALGUN ERROR
			alert('Error en la tabla de Productos');
		
	};
	xhr.send();
}

function createRowProductoAsignacion(product,indice){
	var style = '';
	if(product.estado.id === -1)
		style = 'style="color:red;"';

	return '<tr onclick = "eventRowProductoAsignacion('+indice+')" '+style+'><th scope="row">'+product.codigo+'-'
		+product.numero +'</th><td>'+product.descripcion+'</td><td>'+product.origen+'-'+product.destino+'</td>'
		+'<td>'+product.envio.nombres+' '+product.envio.apellidos+'</td><td>'+product.estado.descripcion+'</td></tr>';
}

function eventRowProductoAsignacion(idx){
	var prd = productosAsignacion[idx];
	//console.log(prd);
	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url+'TrackerAPI/listUsuariosAsignacion');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){	
			//console.log(xhr.responseText);
			var parseUsers = JSON.parse(xhr.responseText);
			//usuariosRepartidores = parseUsers;
			loadFormularioAsignacion(prd,parseUsers);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');
	};
	xhr.send(JSON.stringify({codigo:prd.codigo,numero:prd.numero}));
}
function loadFormularioAsignacion(producto, parseUsers){

	var contenido = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'form_asignacion.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){

			contenido.innerHTML = xhr.responseText;
			document.getElementById('inputCodigo').value = producto.codigo;
			document.getElementById('inputNumero').value = producto.numero;
			document.getElementById('inputDescripcion').value = producto.descripcion;

			var tabla = document.getElementById('user-table');

			var asignados = parseUsers.asignados;
			var sinAsignar = parseUsers.sin_asignar;

			var tmp = 0;
			usuariosRepartidores = [];
			if(asignados !== null){
				for(tmp in asignados){
					usuariosRepartidores.push(asignados[tmp]);
					tabla.innerHTML += createRowUsuariosAsignacion(asignados[tmp],tmp,1);
					//console.log('check'+usr);
					//document.getElementById('check'+usr).checked = true;
					//console.log(document.getElementById('check'+usr).checked);
				}
				tmp = Number(tmp)+1;
			}
			if(sinAsignar !== null){
				
				for(let usr in sinAsignar){
					usuariosRepartidores.push(sinAsignar[usr]);
					tabla.innerHTML += createRowUsuariosAsignacion(sinAsignar[usr],Number(usr)+Number(tmp),2);
				}
			}

			if(asignados !== null){
				for(let usr in asignados){
					//usuariosRepartidores.push(asignados[usr]);
					//tabla.innerHTML += createRowUsuariosAsignacion(asignados[usr],usr,1);
					//console.log('check'+usr);
					document.getElementById('check'+usr).checked = true;
					//console.log(document.getElementById('check'+usr).checked);
				}
			}

			//console.log(usuariosRepartidores);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');
		
	};
	xhr.send();
}

//tipo -> 1 para ASIGNADOS, 2 para SIN ASIGNAR
function createRowUsuariosAsignacion(usuario, indice){

	var tipoUsuario = '';

	switch(usuario.rango){
		case 1:
			tipoUsuario = 'ADMINISTRADOR';
			break;
		case 2:
			tipoUsuario = 'REPARTIDOR INTERNO';
			break;
		case 3:
			tipoUsuario = 'REPARTIDOR EXTERNO';
			break;
		case 4:
			tipoUsuario = 'REGISTRADOR';
			break;
	}

	return '<tr onclick = "eventRowUsuarioAsignacion('+indice+')" ><th scope="row">'+usuario.correo+'</th>'
		+'<td>'+usuario.nombres+'</td><td>'+usuario.apellidos+'</td>'
		+'<td>'+tipoUsuario+'</td><td><input id = "check'+indice+'" type = "checkbox" value = "'+usuario.correo+'"></td></tr>'
}

function eventRowUsuarioAsignacion(idx){
	document.getElementById('check'+idx).checked = !document.getElementById('check'+idx).checked; 
}

function sendAsignaciones(){
	var tempAsignados = [];
	var tempSinAsignar = [];

	var nro = Number(document.getElementById('inputNumero').value);
	var cod = document.getElementById('inputCodigo').value;

	for(let idx in usuariosRepartidores){
		console.log('check'+idx);
		if(document.getElementById('check'+idx).checked)
			tempAsignados.push(
				{
					correo_usuario : usuariosRepartidores[idx].correo,
					nro_producto : nro,
					codigo_producto : cod
				}
			);
		else
			tempSinAsignar.push(
				{
					correo_usuario : usuariosRepartidores[idx].correo,
					nro_producto : nro,
					codigo_producto : cod
				}
			);
	}

	var jsonDatos = {
		asignados : tempAsignados.length === 0 ? null : tempAsignados,
		sin_asignar : tempSinAsignar.length === 0 ? null : tempSinAsignar
	};



	xhr.open('POST',base_url+'TrackerAPI/asignarUsuariosProducto');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){	
			alert('Asignaciones realizadas');
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');
	};
	xhr.send(JSON.stringify(jsonDatos));
	console.log(jsonDatos);
}