package azoth.pe.com.couriertrackerapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import azoth.pe.com.couriertrackerapp.utils.UserParcelable;

public class LoginActivity extends AppCompatActivity {


    private Button acceder;
    private TextInputEditText email;
    private TextInputEditText password;
    private ProgressDialog progreso;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = this.getSharedPreferences("credentials", Context.MODE_PRIVATE);

        //SETTING THE URL
        sharedPreferences.edit().putString("URL","https://ct-back.herokuapp.com").commit();

        this.url = sharedPreferences.getString("URL","http://52.91.61.121:8080/ct-back");

        String token = sharedPreferences.getString("token",null);

        if(token != null){
            //PASA A LA SIGUIENTE VISTA
            Log.i("Info","Las credenciales son válidas");
            try {
                progreso = ProgressDialog.show(this, "",
                        "Loading. Please wait...", true);

                JSONObject object = new JSONObject();
                object.put("token",sharedPreferences.getString("token",""));
                //object.put("state","success");
                settingCredentials(object);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            //this.finish();
        }
        else{
            setContentView(R.layout.activity_login);

            email = (TextInputEditText)findViewById(R.id.etusuario);
            password = (TextInputEditText)findViewById(R.id.etpass);
            acceder = (Button)findViewById(R.id.btn_acceder);
            acceder.setOnClickListener((b) ->{
                acceder();
            });

        }
    }

    private void acceder() {
        if (!validar())
            return;

        requestQueue.getCache().clear();

        progreso = new ProgressDialog(this);
        progreso.setMessage("Iniciando");
        progreso.show();

        String id = email.getText().toString(), pass = password.getText().toString();
        String url = this.url + "/AuthAPI/getToken";

        StringRequest objectRequest = new StringRequest(Request.Method.POST,url,
                (r) -> {
                    //Log.i("Respuesta: ",""+r);
                    try{
                        JSONObject respuesta = new JSONObject(r);
                        //int rango = respuesta.getInt("rango");
                        email.setText("");
                        password.setText("");
                        settingCredentials(respuesta);

                    }
                    catch(JSONException e){
                        this.sharedPreferences.edit().putString("token",null).commit();
                        Toast.makeText(getApplicationContext(), "Error en la respuesta del servicio", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                        this.sharedPreferences.edit().putString("token",null).commit();
                        Toast.makeText(getApplicationContext(), "Error en la decodificación del Token", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        this.sharedPreferences.edit().putString("token",null).commit();
                        Toast.makeText(getApplicationContext(), "Error en el charset del token", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        this.sharedPreferences.edit().putString("token",null).commit();
                        Toast.makeText(getApplicationContext(), "Error en el charset del token", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    progreso.dismiss();
                } ,
                (e) -> {
                    this.sharedPreferences.edit().putString("token",null).commit();
                    Toast.makeText(getApplicationContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    progreso.dismiss();
                }) {
            //AQUI VAN LOS PARAMETROS DE MI PETICION
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", id);
                params.put("password", pass);

                //Log.d("datos", id + "::"+pass);
                return params;
            }
        };
        requestQueue.add(objectRequest);
    }

    private void settingCredentials(JSONObject respuesta) throws JSONException, ArrayIndexOutOfBoundsException,  IOException{
        requestQueue.getCache().clear();

        this.sharedPreferences.edit().putString("token",respuesta.getString("token")).commit();

        String body = respuesta.getString("token").split("\\.")[1];
        body = new String(Base64.decode(body.getBytes("utf-8"),Base64.DEFAULT),"utf-8");

        String finalBody = body;
        ObjectMapper mapper = new ObjectMapper();



        //CARGO LOS DATOS DEL USUARIO
        UserParcelable user = mapper.readValue(finalBody,UserParcelable.class);
        Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_SHORT).show();

        if(progreso != null)
            progreso.dismiss();

        Intent intent = new Intent(getApplicationContext(),ContentActivity.class);
        intent.putExtra("DATA_USER", user);
        startActivity(intent);
        finish();


        /*
        StringRequest objectRequest = new StringRequest(Request.Method.POST,url,
                (r) -> {
                    Log.i("Respuesta 2da llamada: ",""+r);

                    try{
                        JSONObject rs = new JSONObject(r);
                        if(respuesta.getString("state").equals("success"))
                            loadInfo(rs, finalBody);
                        else{
                            Toast.makeText(getApplicationContext(),"Error de autenticacion",Toast.LENGTH_SHORT).show();
                            this.sharedPreferences.edit().putString("token",null).commit();
                            this.finish();
                            this.startActivity(this.getIntent());
                        }
                    }
                    catch(JSONException e){
                        Toast.makeText(getApplicationContext(), "Error en la respuesta del servicio", Toast.LENGTH_SHORT).show();
                        this.sharedPreferences.edit().putString("token",null).commit();
                        this.finish();
                        this.startActivity(this.getIntent());
                        e.printStackTrace();
                    }
                } ,
                (e) -> {
                    try {
                        loadInfo(null, finalBody);
                    }
                    catch (JSONException e1) {
                        Toast.makeText(getApplicationContext(), "Error en la respuesta del servicio", Toast.LENGTH_SHORT).show();
                        this.sharedPreferences.edit().putString("token",null).commit();
                        this.finish();
                        this.startActivity(this.getIntent());
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }) {
            //AQUI VAN LOS PARAMETROS DE MI PETICION
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", (sharedPreferences.getString("token","")));
                return params;
            }
        };
        requestQueue.add(objectRequest);
        */
    }
    /*
    private void loadInfo(JSONObject respuesta, String body) throws  JSONException{

        //CARGO LOS DATOS DEL USUARIO
        JSONObject usuario = new JSONObject(body);
        UserParcelable user = new UserParcelable();
        user.setId(usuario.getString("id"));
        user.setPass(usuario.getString("pass"));
        user.setNombre(usuario.getString("nombres"));
        user.setEmail(usuario.getString("email"));
        user.setImage("");

        ProductoParcelable[] datos = new ProductoParcelable[0];

        //SACO LOS DATOS DE LA TABLA
        if(respuesta != null){
            JSONArray array = respuesta.getJSONArray("pedidos");
            datos = new ProductoParcelable[array.length()];
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject pedido = array.getJSONObject(i);
                datos[i] = new ProductoParcelable(pedido.getInt("id"),pedido.getString("desc"));
               // Log.d("DEBUG",pedido.getInt("id") + ":" + pedido.getString("desc"));
            }
        }
        Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_SHORT).show();

        if(progreso != null)
            progreso.dismiss();

        Intent intent = new Intent(getApplicationContext(),ContentActivity.class);
        intent.putExtra("DATA_USER",new ContentParcelable(user,datos));
        startActivity(intent);
        finish();
    }
    */

    private boolean validar(){
        boolean valid = true;

        String sEmail = this.email.getText().toString();
        String sPassword = this.password.getText().toString();

        if (sEmail.isEmpty() ||  ( !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches() && !sEmail.matches("[a-zA-Z0-9]+"))) {
            email.setError("Introduzca una dirección de correo electrónico o ID válido");
            valid = false;
        } else {
            email.setError(null);
        }

        if (sPassword.isEmpty() || password.length() < 4 ) {
            password.setError("Son mas de 4 caracteres alfanuméricos");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;

    }
}
