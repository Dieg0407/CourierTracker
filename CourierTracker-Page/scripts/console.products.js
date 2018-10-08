var productos;

var clienteEmisor = null;
var clienteReceptor = null;

function loadMenuProductos(){
	document.getElementById('contenido').innerHTML='';
	var menu = document.getElementById('menu');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'menu_producto.html', true);
	xhr.onload = function() {
		if(xhr.status === 200)
			menu.innerHTML = xhr.responseText;
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga del Menú de Productos');
		
	};
	xhr.send();
}
//NUEVO PRODUCTO
function enviarDatosProducto(){

	if(!(verificarDatosCliente(1) && verificarDatosCliente(2) && verificarDatosProducto()) )
		return;
	else{
		var tempClienteEmisor = clienteEmisor;
		var tempClienteReceptor = clienteReceptor;

		if(clienteEmisor === null){
			tempClienteEmisor = {
				dni:document.getElementById('inputDNIEmisor').value ,
				nombres:document.getElementById('inputNamesEmisor').value ,
				apellidos: document.getElementById('inputApellidosEmisor').value ,
				correo:document.getElementById('inputCorreoEmisor').value === '' ? null: document.getElementById('inputCorreoEmisor').value,
				celular:document.getElementById('inputCelularEmisor').value === '' ? null: document.getElementById('inputCelularEmisor').value
			};
		}
		else{
			tempClienteEmisor = {
				id : clienteEmisor.id,
				dni:document.getElementById('inputDNIEmisor').value ,
				nombres:document.getElementById('inputNamesEmisor').value ,
				apellidos: document.getElementById('inputApellidosEmisor').value ,
				correo:document.getElementById('inputCorreoEmisor').value === '' ? null: document.getElementById('inputCorreoEmisor').value,
				celular:document.getElementById('inputCelularEmisor').value === '' ? null: document.getElementById('inputCelularEmisor').value
			};
		}
		if(clienteReceptor === null){
			tempClienteReceptor = {
				dni:document.getElementById('inputDNIReceptor').value ,
				nombres:document.getElementById('inputNamesReceptor').value ,
				apellidos: document.getElementById('inputApellidosReceptor').value ,
				correo:document.getElementById('inputCorreoReceptor').value === '' ? null: document.getElementById('inputCorreoReceptor').value,
				celular:document.getElementById('inputCelularReceptor').value === '' ? null: document.getElementById('inputCelularReceptor').value
			};
		}
		else{
			tempClienteReceptor = {
				id : clienteReceptor.id,
				dni:document.getElementById('inputDNIReceptor').value ,
				nombres:document.getElementById('inputNamesReceptor').value ,
				apellidos: document.getElementById('inputApellidosReceptor').value ,
				correo:document.getElementById('inputCorreoReceptor').value === '' ? null: document.getElementById('inputCorreoReceptor').value,
				celular:document.getElementById('inputCelularReceptor').value === '' ? null: document.getElementById('inputCelularReceptor').value
			};
		}

		var producto = {
			codigo : document.getElementById('inputCodigo').value.toUpperCase(),
			descripcion: document.getElementById('inputDescripcion').value,
			direccion: document.getElementById('inputDireccion').value,
			origen : document.getElementById('inputOrigen').value,
			destino : document.getElementById('inputDestino').value,
			envio : tempClienteEmisor,
			recepcion : tempClienteReceptor,
			estado : {
				id : Number(document.getElementById('inputEstado').value)
			}
		};


		console.log(JSON.stringify(producto));
		
		var xhr = new XMLHttpRequest();
		xhr.open('POST',base_url+'TrackerAPI/insertProducto');
		xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		xhr.setRequestHeader('Accept', '*/*');
		xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
		xhr.onload = function(){
			if(xhr.status === 200){
				let response = JSON.parse(xhr.responseText);
				alert('Envío registrado. Código: ' + response.codigo);
			}
			else if(xhr.getResponseHeader("Content-Type") === 'application/json')
				alert(JSON.parse(xhr.responseText).message);
			else
				alert('Error en el Servidor');		
		};
		xhr.send(JSON.stringify(producto));
	}
	
	
}
function updateDatosProducto(){
	
	if(!(verificarDatosCliente(1) && verificarDatosCliente(2) && verificarDatosProducto()) )
		return;
	else{
		clienteEmisor = {
			id : clienteEmisor !== null ? clienteEmisor.id : -1,
			dni:document.getElementById('inputDNIEmisor').value ,
			nombres:document.getElementById('inputNamesEmisor').value ,
			apellidos: document.getElementById('inputApellidosEmisor').value ,
			correo:document.getElementById('inputCorreoEmisor').value === '' ? null: document.getElementById('inputCorreoEmisor').value,
			celular:document.getElementById('inputCelularEmisor').value === '' ? null: document.getElementById('inputCelularEmisor').value
		};
		clienteReceptor = {
			id : clienteReceptor !== null ? clienteReceptor.id : -1,
			dni:document.getElementById('inputDNIReceptor').value ,
			nombres:document.getElementById('inputNamesReceptor').value ,
			apellidos: document.getElementById('inputApellidosReceptor').value ,
			correo:document.getElementById('inputCorreoReceptor').value === '' ? null: document.getElementById('inputCorreoReceptor').value,
			celular:document.getElementById('inputCelularReceptor').value === '' ? null: document.getElementById('inputCelularReceptor').value
		};
		var producto = {
			numero : Number(document.getElementById('inputNumero').value),
			codigo : document.getElementById('inputCodigo').value.toUpperCase(),
			descripcion: document.getElementById('inputDescripcion').value,
			direccion: document.getElementById('inputDireccion').value,
			origen : document.getElementById('inputOrigen').value,
			destino : document.getElementById('inputDestino').value,
			envio : clienteEmisor,
			recepcion : clienteReceptor,
			estado : {
				id : Number(document.getElementById('inputEstado').value)
			}
		};

		var xhr = new XMLHttpRequest();
		xhr.open('POST',base_url+'TrackerAPI/updateProductoConsola');
		xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		xhr.setRequestHeader('Accept', '*/*');
		xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
		xhr.onload = function(){
			if(xhr.status === 200){
				//let response = JSON.parse(xhr.responseText);
				alert('Envío registrado. Código: ' + producto.codigo + '-'+producto.numero);
			}
			else if(xhr.getResponseHeader("Content-Type") === 'application/json')
				alert(JSON.parse(xhr.responseText).message);
			else
				alert('Error en el Servidor');		
		};
		//console.log(JSON.stringify(producto));
		xhr.send(JSON.stringify(producto));
	}
		
}
function anularProducto(jsonProducto){
	jsonProducto.estado.id = -1;
	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url+'TrackerAPI/updateProductoConsola');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){
			//let response = JSON.parse(xhr.responseText);
			alert('Envío anulado. Código: ' + jsonProducto.codigo+'-'+jsonProducto.numero);
			listarProductos(2);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');		
	};
	xhr.send(JSON.stringify(jsonProducto));
}


/*PARA LOS PRODUCTOS*/
//1 -> nuevo producto
//2 -> modificar producto
function cargarFormularioProductos(jsonDatos){
	var formulario = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'form_producto.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){

			formulario.innerHTML = xhr.responseText;

			if(jsonDatos !== null){
				clienteEmisor = jsonDatos.envio;
				clienteReceptor = jsonDatos.recepcion;
				cargarDatosFormularioProducto(jsonDatos);
			}
			else{
				productos = null;
				clienteEmisor = null;
				clienteReceptor = null;
			}

			var form_producto = document.getElementById('product-form');
			form_producto.addEventListener('submit',function(e){
				e.preventDefault();
				if(jsonDatos !== null)
					//alert('Llamo al update');
					updateDatosProducto();
				else
					//alert('Llamo el envio');
					enviarDatosProducto();
				
			});

		}
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga del formulario de Productos');
		
	};
	xhr.send();
}
function cargarDatosFormularioProducto(jsonDatos){
	document.getElementById('codigo_form').innerHTML 
		+= ('<div class="form-group col-md-2">'+
				'<label for="inputNumero">Número</label>'+
				'<input type="text" class="form-control" value="CT" id="inputNumero" placeholder="Numero" readonly>'+
			'</div>'
	);
	document.getElementById('inputCodigo').readOnly  = true;
	document.getElementById('inputCodigo').value = jsonDatos.codigo.toUpperCase();
	document.getElementById('inputNumero').value = jsonDatos.numero;
	document.getElementById('inputDescripcion').value = jsonDatos.descripcion;
	document.getElementById('inputDireccion').value	= jsonDatos.direccion;
	document.getElementById('inputOrigen').value	= jsonDatos.origen;
	document.getElementById('inputDestino').value	= jsonDatos.destino;
	loadDatosCliente(jsonDatos.envio,1);
	loadDatosCliente(jsonDatos.recepcion,2);
	document.getElementById('inputEstado').value = String(jsonDatos.estado.id);

}

//PARA MODIFICAR Y ANULAR
function listarProductos(mode){
	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url+'TrackerAPI/listProductos');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){
			var responseObject = JSON.parse(xhr.responseText);
			cargarTablaProductos(mode,responseObject);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');
	};
	xhr.send();
}

//PARA LA TABLA
function cargarTablaProductos(mode,responseObject){
	var formulario = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'tabla_productos.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){			
			formulario.innerHTML = xhr.responseText;
			var table = document.getElementById("product-table");
			//AQUI SE CARGAN LOS USUARIOS
			for(let product in responseObject)
				table.innerHTML += createRowProducto(responseObject[product],product,mode);

			///var rows = table.getElementsByTagName('tr');
			
			productos = responseObject;
		}
		else//CASO DE ALGUN ERROR
			alert('Error en la tabla de Productos');
		
	};
	xhr.send();
}
function createRowProducto(product,indice,mode){
	var style = '';
	if(product.estado.id === -1)
		style = 'style="color:red;"';

	return '<tr onclick = "eventRowProducto('+indice+','+mode+')" '+style+'><th scope="row">'+product.codigo+'-'
		+product.numero +'</th><td>'+product.descripcion+'</td><td>'+product.origen+'-'+product.destino+'</td>'
		+'<td>'+product.envio.nombres+' '+product.envio.apellidos+'</td><td>'+product.estado.descripcion+'</td></tr>';
}

function eventRowProducto(indice,mode){
	if(mode === 1)
		cargarFormularioProductos(productos[indice]);
	else if(mode === 2){
		var r = confirm("¿Desea anular este pedido?");
		if (r == true) 
			anularProducto(productos[indice]);					
	}
}

//PARA LOS CLIENTES

//1 para emisor, 2 para receptor
function getCliente(tipo){
	if(tipo === 1)
		requestDatosCliente(document.getElementById('inputDNIEmisor').value.trim(),tipo);
	else
		requestDatosCliente(document.getElementById('inputDNIReceptor').value.trim(),tipo);
}
function requestDatosCliente(DNI,tipo){
	var xhr = new XMLHttpRequest();
	xhr.open('POST', base_url+'TrackerAPI/getCliente', true);
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){
			var jsonCliente = JSON.parse(xhr.responseText);
			loadDatosCliente(jsonCliente, tipo);
		}
		else if(xhr.status === 204){
			var jsonCliente = {
				dni : DNI,
				nombres : null,
				apellidos : null,
				correo : null,
				celular : null,
			};
			loadDatosCliente(jsonCliente, tipo);
			if(tipo === 1)
				clienteEmisor = null;
			else
				clienteReceptor = null;

			alert('No se encontraron datos con el dni: '+ DNI);
		}
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert(JSON.parse(xhr.responseText).message);
		else
			alert('Error en el Servidor');

	};
	xhr.send(JSON.stringify({dni:DNI}));
}
//1 emisor
//2 receptor
function loadDatosCliente(jsonCliente, tipo){
	let sufijo = '';
	if(tipo === 1){
		sufijo = 'Emisor';
		clienteEmisor = jsonCliente;
	}
	else{
		sufijo = 'Receptor';
		clienteReceptor = jsonCliente;
	}
	document.getElementById('inputDNI'+sufijo).value = jsonCliente.dni;
	document.getElementById('inputNames'+sufijo).value = jsonCliente.nombres;
	document.getElementById('inputApellidos'+sufijo).value = jsonCliente.apellidos;
	document.getElementById('inputCorreo'+sufijo).value = jsonCliente.correo;
	document.getElementById('inputCelular'+sufijo).value = jsonCliente.celular;
}

//1 emisor
//2 receptor
function verificarDatosCliente(tipo){
	let sufijo = '';
	if(tipo === 1){
		sufijo = 'Emisor';
		//clienteEmisor = jsonCliente;
	}
	else{
		sufijo = 'Receptor';
		//clienteReceptor = jsonCliente;
	}
	var dni = document.getElementById('inputDNI'+sufijo).value;
	var nombres = document.getElementById('inputNames'+sufijo).value;
	var apellidos = document.getElementById('inputApellidos'+sufijo).value;

	if(dni.length === 0){
		alert('El dni/ruc del ' + sufijo + ' debe contener 8 o 11 numeros');
		return false;
	}
	else if( !dni.match('^\\d+$')){
		alert('El dni del '+sufijo+' debe de contener solo números');
		return false;
	}
	else if(nombres.length === 0){
		alert('Los nombres del '+sufijo+' no pueden estar vacíos');
		return false;
	}
	else if(apellidos.length === 0){
		alert('Los apellidos del ' + sufijo + ' no pueden estar vacíos');
		return false;
	}
	
	return true;
}

function verificarDatosProducto(){
	var codigo = document.getElementById('inputCodigo').value.toUpperCase();
	var direccion = document.getElementById('inputDireccion').value;
	var origen = document.getElementById('inputOrigen').value;
	var destino = document.getElementById('inputDestino').value;
	var estado = document.getElementById('inputEstado').value;

	if(codigo.trim().length === 0){
		alert('El código no puede estar vacío');
		return false;
	}
	else if(direccion.trim().length === 0){
		alert('La dirección no puede estar vacía');
		return false;
	}
	else if(origen === 'Departamento...'){
		alert('Debe seleccionar un departamento de origen');
		return false;
	}
	else if(destino === 'Departamento...'){
		alert('Debe seleccionar un departamento de destino');
		return false;
	}
	else if((document.getElementById('inputEstado').value) === 'Estado...'){
		alert('Debe seleccionar un estado para el envío');
		return false;
	}
	return true;
}