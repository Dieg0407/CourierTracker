
var base_url = 'https://ct-back.herokuapp.com/';

window.addEventListener('load',function(){
	if(localStorage.getItem('alreadyVerified') === true){
		localStorage.setItem('alreadyVerified',null);
		loadConsoleContent();
	}
	else{
		var accessToken = localStorage.getItem('accessToken');
		var expiresAt = localStorage.getItem('expiresAt');
		var rango = localStorage.getItem('rango');
		/*SI LAS VARIABLES DE SESION AUN SON VALIDAS...*/
		if(new Date().getTime() < expiresAt && expiresAt !== null && accessToken != null){
			verifyToken(accessToken);//SE CARGA EL TOKEN
		}
		else{
			window.location.replace(window.location.href.replace('console.html','login.html'));
		}
	}
	
});

function loadConsoleContent(){
	var xhr= new XMLHttpRequest();
	xhr.open('GET', 'content_console.html', true);
	xhr.onload = function() {
		if(xhr.status === 200){
			document.body.innerHTML = xhr.responseText;
			var list = document.getElementById('navegacion-principal');
			//console.log("RANGO: " + localStorage.getItem('rango'));
			if(localStorage.getItem('rango') === '1' ){
				
				//console.log("entro al if");
				var head = document.getElementsByTagName('head')[0];
				var script = document.createElement('script');
				script.type = 'text/javascript';
				script.src = "../scripts/console.users.js";
				head.appendChild(script);

				//document.head += ('<script src = "../scripts/console.users.js" type="text/javascript"></script>');
				list.innerHTML += ('<li class="nav-item" onclick="loadMenuUsuarios()"><a class="nav-link" href="#">Usuarios</a></li>');
			}
			var navbar = document.getElementById('navbarNav');
			navbar.innerHTML += '<ul onclick="logout()" class="nav navbar-nav navbar-right" style = "position:absolute;right:10px;"><li><div class="btn-nav"><a class="btn btn-primary btn-small navbar-btn" href="#">Salir</a></div></li></ul>'

		}
		else{//CASO DE ALGUN ERROR
			document.body.innerHTML = '<h1>Ocurrio un error</h1>';
		}
	};
	xhr.send();
}

function logout(){
	localStorage.setItem('accessToken' , null);
	localStorage.setItem('expiresAt' , null);
	localStorage.setItem('rango', null);
	localStorage.setItem('alreadyVerified', null);

	window.location.replace(window.location.href.replace('console.html','login.html'));
}

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
			loadConsoleContent();
		}
		else{
			localStorage.setItem("accessToken" , null);
			localStorage.setItem("expiresAt" , null);
			localStorage.setItem('rango', null);
			window.location.replace(window.location.href.replace('console.html','login.html'));
		}
	};
	xhr.send('{"token":"'+token+'"}');
}
