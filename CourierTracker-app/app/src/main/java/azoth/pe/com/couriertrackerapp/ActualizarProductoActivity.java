package azoth.pe.com.couriertrackerapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import azoth.pe.com.couriertrackerapp.utils.ProductoParcelable;

public class ActualizarProductoActivity extends AppCompatActivity {
    private ProgressDialog progreso;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private String url;

    private ProductoParcelable productoParcelable;

    private TextView txtViewCodigo;
    private TextView txtViewEstado;
    private Button btnUpdateRegistrado;
    private Button btnUpdateRuta;
    private Button btnUpdateEntregado;
    private Button btnUpdateNoEntregado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productoParcelable = this.getIntent().getExtras().getParcelable("producto");
        sharedPreferences = this.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        this.url = sharedPreferences.getString("URL","http://52.91.61.121:8080/ct-back");
        requestQueue = Volley.newRequestQueue(this);

        this.txtViewCodigo = (TextView) findViewById(R.id.txtViewCodigo);
        this.txtViewEstado = (TextView) findViewById(R.id.txtEstado);
        this.btnUpdateEntregado = (Button) findViewById(R.id.btnUpdEntregado);
        this.btnUpdateNoEntregado = (Button) findViewById(R.id.btnUpdNoEntregado);
        this.btnUpdateRegistrado = (Button) findViewById(R.id.btnUpdRegistrado);
        this.btnUpdateRuta = (Button) findViewById(R.id.btnUpdRuta);

        this.txtViewCodigo.setText(String.format("%s-%d",
                productoParcelable.getCodigo(),
                productoParcelable.getNumero()));

        switch (productoParcelable.getEstado().getId()){
            case 0:
                this.txtViewEstado.setText("No Entregado");
                break;
            case 1:
                this.txtViewEstado.setText("Registrado");
                break;
            case 2:
                this.txtViewEstado.setText("En Ruta");
                break;
            case 3:
                this.txtViewEstado.setText("Entregado");
                break;
            default:
                break;
        }

        this.btnUpdateEntregado.setOnClickListener(e -> sendUpdateRequest(3));
        this.btnUpdateNoEntregado.setOnClickListener( e -> sendUpdateRequest(0));
        this.btnUpdateRuta.setOnClickListener(e -> sendUpdateRequest(2));
        this.btnUpdateRegistrado.setOnClickListener( e -> sendUpdateRequest(1));
    }

    private void sendUpdateRequest(int estado){
        final String url = this.url + "/TrackerAPI/updateProducto";

        productoParcelable.getEstado().setId(estado);
        JSONObject jsonObject = null;
        try {
            progreso = ProgressDialog.show(this, "",
                    "Actualizando", true);
            jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(productoParcelable));

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    response -> {
                        Toast.makeText(getApplicationContext(), "Exito", Toast.LENGTH_SHORT).show();
                        switch (productoParcelable.getEstado().getId()){
                            case 0:
                                this.txtViewEstado.setText("No Entregado");
                                break;
                            case 1:
                                this.txtViewEstado.setText("Registrado");
                                break;
                            case 2:
                                this.txtViewEstado.setText("En Ruta");
                                break;
                            case 3:
                                this.txtViewEstado.setText("Entregado");
                                break;
                            default:
                                break;
                        }
                        progreso.dismiss();
                    },
                    error -> {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            try{
                                String jsonError = new String(networkResponse.data);
                                JSONObject json = new JSONObject(jsonError);
                                Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            catch(JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        error.printStackTrace();
                        progreso.dismiss();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    for(String key : super.getHeaders().keySet()){
                        //Log.d("HEADER PARAMS",String.format("%s:%s",key,super.getHeaders().keySet()));
                        params.put(key,super.getParams().get(key));
                    }
                    params.put("token", (sharedPreferences.getString("token","")));
                    //Log.d("HEADER PARAMS",(sharedPreferences.getString("token","")));
                    return params;
                }
            };
            requestQueue.add(jsonRequest);
        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (JsonProcessingException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }




    }
}
