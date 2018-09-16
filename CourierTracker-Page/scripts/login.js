/*CARGA DE PAGINA*/

var base_url = 'https://ct-back.herokuapp.com/';


window.addEventListener('load',function(){
	var accessToken = localStorage.getItem('accessToken');
	var expiresAt = localStorage.getItem('expiresAt');
	
	/*SI LAS VARIABLES DE SESION AUN SON VALIDAS...*/
	if(new Date().getTime() < expiresAt && expiresAt !== null && accessToken != null){
		verifyToken(accessToken);//SE CARGA EL TOKEN
	}
	//SI NO CARGAMOS EL FORMULARIO
	else{
		loadLoginForm();
	}
});

//FUNCION PARA VERIFICAR EL TOKEN
function verifyToken(token){
	//var base_url = 'https://54.90.70.220:443/ct-back/';
	xhr = new XMLHttpRequest();
	xhr.open('POST', base_url + 'AuthAPI/autenticarToken');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.onload = function() {
		//console.log(xhr.responseText);
		var json = JSON.parse(xhr.responseText);
		if(json.valido === true){	
			localStorage.setItem("alreadyVerified" , true);
			window.location.replace(window.location.href.replace('login.html','console.html'));
		}
		else{
			localStorage.setItem("accessToken" , null);
			localStorage.setItem("expiresAt" , null);
			localStorage.setItem('rango', null);
			notHandled();
		}
	};
	xhr.send('{"token":"'+token+'"}');
}

//CARGA EL LOGIN DESDE EL HTML FORM_LOGIN
function loadLoginForm(){
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'form_login.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){
			document.body.innerHTML = xhr.responseText;
			notHandled();
		}
		else{//CASO DE ALGUN ERROR
			document.body.innerHTML = '<h1>Ocurrio un error</h1>';
		}
	};
	xhr.send();
}

//SETEO EL EVENTO EN EL FORMULARIO
function notHandled(){
	var loginForm = document.getElementById('log-form');
	loginForm.addEventListener('submit', function(e){
		getToken(e);
	});		
}

function getToken(e){
	e.preventDefault();

	var userText = document.getElementById('inputId').value;
	var passText = document.getElementById('inputPassword').value;

	var content = {
		user : userText,
		password : passText
	};
	xhr = new XMLHttpRequest();
	xhr.open('POST', base_url + 'AuthAPI/getTokenJson');
	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
	xhr.setRequestHeader('Accept', '*/*');
	xhr.onload = function() {
			//console.log(xhr.responseText);
	  	var json = JSON.parse(xhr.responseText);
	  	if(json.token !== null && ( json.rango === 1 || json.rango === 4) ){

	  		localStorage.setItem('accessToken' , json.token);
	  		localStorage.setItem('expiresAt' , (new Date().getTime() * 1800000));
	  		localStorage.setItem('rango', json.rango);

	  		window.location.replace(window.location.href.replace('login.html','console.html'));
	  	
	  	}
	  	else{
	  		alert('Credenciales Inválidas');
	  	}
	};
	xhr.send(JSON.stringify(content));
}
