var usuarios;

function loadMenuUsuarios(){
	document.getElementById('contenido').innerHTML='';
	var menu = document.getElementById('menu');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'menu_usuario.html', true);
	xhr.onload = function() {
		if(xhr.status === 200)
			menu.innerHTML = xhr.responseText;
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga del Menú de Usuarios');
		
	};
	xhr.send();

}

/*EVENTOS DE LOS MENUS*/
/*PARA CREAR USUARIOS*/
function cargarFormularioUsuario(jsonDatos){

	//console.log(jsonDatos);

	var formulario = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'form_usuario.html', true);	
	xhr.onload = function() {
		if(xhr.status === 200){			
			formulario.innerHTML = xhr.responseText;
			if(jsonDatos !== null)
				cargarDatosFormularioUsuario(jsonDatos);

			var form_usuario = document.getElementById('user-form');
			form_usuario.addEventListener('submit',function(e){
				e.preventDefault();
				if(jsonDatos !== null)
					actualizarDatosUsuario(true);
				else
					enviarDatosUsuario();
			});
		}
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga del formulario de usuarios');
		
	};
	xhr.send();
}

function cargarDatosFormularioUsuario(jsonDatos){
	//document.getElementById('inputIdUser').value = jsonDatos.id;
	document.getElementById('inputPasswordUser').value = jsonDatos.pass;
	document.getElementById('inputPasswordAgainUser').value = jsonDatos.pass;
	document.getElementById('inputNamesUser').value = jsonDatos.nombres;
	document.getElementById('inputApellidosUser').value = jsonDatos.apellidos;
	document.getElementById('inputDNIUser').value = jsonDatos.dni;
	document.getElementById('inputCorreoUser').value = jsonDatos.correo;
	document.getElementById('inputRangoUser').value = String(jsonDatos.rango);
	document.getElementById('inputEstadoUser').value = String(jsonDatos.activo);
}

function enviarDatosUsuario(){
	//ENVIO
	var user = {
		correo : document.getElementById('inputCorreoUser').value.trim(),
		pass : document.getElementById('inputPasswordUser').value.trim(),
		nombres : document.getElementById('inputNamesUser').value.trim(),
		apellidos : document.getElementById('inputApellidosUser').value.trim(),
		dni : document.getElementById('inputDNIUser').value === ""? null: document.getElementById('inputDNIUser').value ,
		rango : Number(document.getElementById('inputRangoUser').value),
		activo : document.getElementById('inputEstadoUser').value === 'true'	
	};

	//console.log(JSON.stringify(user));

	let x = verificarDatosUsuario(false);
	//console.log(x);
	//console.log(x.state);
	if(x.state === true){
		//POST
		//console.log(localStorage.getItem('accessToken'));
		var xhr= new XMLHttpRequest();
		xhr.open('POST', base_url+'TrackerAPI/insertUsuario', true);
		xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		xhr.setRequestHeader('Accept', '*/*');
		xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
		xhr.onload = function(){
			//console.log(xhr.responseType);

			if(xhr.status === 200)
				alert('Usuario registrado con exito');
			else if(xhr.getResponseHeader("Content-Type") === 'application/json')
				alert(JSON.parse(xhr.responseText).message);
			else
				alert('Error en el Servidor');
		};
		xhr.send(JSON.stringify(user));
	}
	else
		alert(x.mensaje);
}
function actualizarDatosUsuario(newPassword){
	//ENVIO
	var user = {
		//id : document.getElementById('inputIdUser').value.trim(),
		pass : document.getElementById('inputPasswordUser').value.trim(),
		nombres : document.getElementById('inputNamesUser').value.trim(),
		apellidos : document.getElementById('inputApellidosUser').value.trim(),
		dni : document.getElementById('inputDNIUser').value === ""? null: document.getElementById('inputDNIUser').value ,
		correo : document.getElementById('inputCorreoUser').value.trim(),
		rango : Number(document.getElementById('inputRangoUser').value),
		activo : document.getElementById('inputEstadoUser').value === 'true'	
	};


	var datos = {
		usuario : user,
		new_password : newPassword
	};

	let x = verificarDatosUsuario(false);
	//console.log(x);
	//console.log(x.state);

	if(x.state === true){
		//POST
		//console.log(localStorage.getItem('accessToken'));
		var xhr= new XMLHttpRequest();
		xhr.open('POST', base_url+'TrackerAPI/updateUsuario');
		xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		xhr.setRequestHeader('Accept', '*/*');
		xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
		xhr.onload = function(){
			if(xhr.status === 200)
				alert('Usuario actualizado con exito');
			
			else if(xhr.getResponseHeader("Content-Type") === 'application/json')
				alert(JSON.parse(xhr.responseText).message);
			else
				alert('Error en el Servidor');
		};
		xhr.send(JSON.stringify(datos));
	}
	else
		alert(x.mensaje);
}


function listarUsuarios(mode){
	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url+'TrackerAPI/listUsuarios');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200){
			var responseObject = JSON.parse(xhr.responseText);
			cargarTablaUsuarios(mode,responseObject);
		}
		else
			alert(JSON.parse(xhr.responseText).message);
	};
	xhr.send('{"id_usuario":""}');
}

/*PARA MODIFICAR USUARIOS*/
//MODE ->  1 PARA modificar : 2 para eliminar
//reponseObject -> Un JSON array con los JSON de los usuarios

function cargarTablaUsuarios(mode,responseObject){
	var formulario = document.getElementById('contenido');
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'tabla_usuarios.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){		
			
			formulario.innerHTML = xhr.responseText;
			var table = document.getElementById("user-table");
			//AQUI SE CARGAN LOS USUARIOS
			for(let user in responseObject)
				table.innerHTML += createRowUsuario(responseObject[user],user,mode);

			var rows = table.getElementsByTagName('tr');
			
			usuarios = responseObject;

		}
		else//CASO DE ALGUN ERROR
			alert('Error en la Carga de la tabla de usuarios');
		
	};
	xhr.send();
}
function createRowUsuario(usuario, indice, mode){
	//console.log(usuario);

	//console.log(JSON.stringify(usuario));
	var style = '';
	if(usuario.activo === false)
		style = 'style="color:red;"';
	return '<tr onclick = "rowEventUsuario('+indice+','+mode+')" '+style+'><th scope="row">'+usuario.correo 
		+'</th><td>'+usuario.nombres+'</td><td>'+usuario.apellidos+'</td></tr>';
}

function rowEventUsuario(indice,mode){
	//console.log(usuarios);
	
	if(mode === 1)
		cargarFormularioUsuario(usuarios[indice]);
	else if(mode === 2){
		var r = confirm("¿Desea desactivar este usuario?");
		if (r == true) 
			desactivarUsuario(usuarios[indice]);					
	}
}

function desactivarUsuario(user){
	user.activo = false;


	var datos = {
		usuario : user,
		new_password : true
	};


	var xhr = new XMLHttpRequest();
	xhr.open('POST',base_url + 'TrackerAPI/updateUsuario', true);
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.setRequestHeader('token', localStorage.getItem('accessToken'));
	xhr.onload = function(){
		if(xhr.status === 200)
			alert('Usuario desactivado con éxito');
		else if(xhr.status === 304) //SET AS NOT MODIFIED
			alert('Usuario no pudo ser desactivado');
		else if(xhr.getResponseHeader("Content-Type") === 'application/json')
			alert('Error: ' + JSON.parse(xhr.responseText).message );
		else
			alert('Error del servidor');
	};
	xhr.send(JSON.stringify(datos));

}

function verificarDatosUsuario(verificarPassword){
	var pass1 = document.getElementById('inputPasswordUser').value.trim();
	var pass2 = document.getElementById('inputPasswordAgainUser').value.trim();
	var email = document.getElementById('inputCorreoUser').value.trim();

	if(email.length === 0)
		return {state:false,mensaje:'El correo no puede estar vacío'};
	if(pass1 !== pass2)
		return {state:false,mensaje:'Las contraseñas no coinciden'};
	if(String(pass1).length < 8 )
		return {state:false,mensaje:'La contraseña debe tener 8 a mas dígitos'};
	if(!String(pass1).match('^[a-zA-Z0-9]{8,}$') && verificarPassword)
		return {state:false, mensaje:'La contraseña solo debe contener letras y números'};
	if(!String(email).match('\\S+@\\S+\\.\\S+'))
		return {state:false, mensaje: 'El correo debe de ser valido'};

	return {state: true};
}