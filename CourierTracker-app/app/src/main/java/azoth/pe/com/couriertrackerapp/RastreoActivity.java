package azoth.pe.com.couriertrackerapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import azoth.pe.com.couriertrackerapp.utils.ProductoParcelable;

public class RastreoActivity extends AppCompatActivity {

    private ProgressDialog progreso;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private String url;

    private EditText codTextField;
    private EditText nroTextField;
    private ImageButton searchButton;
    private LinearLayout linearContent;

    private ProductoParcelable productoParcelable;

    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rastreo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = this.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        this.url = sharedPreferences.getString("URL","http://52.91.61.121:8080/ct-back");
        requestQueue = Volley.newRequestQueue(this);

        codTextField = (EditText)findViewById(R.id.codTextField);
        nroTextField = (EditText)findViewById(R.id.nroTextField);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        linearContent = (LinearLayout) findViewById(R.id.rastreoContainer);

        density = this.getResources().getDisplayMetrics().density;
        searchButton.setOnClickListener(e -> {
            this.search();
        });

        if(savedInstanceState != null){
            productoParcelable = savedInstanceState.getParcelable("prd_saved");
            loadProducto();
        }
    }

    private void search(){
        final String url = this.url + "/TrackerAPI/getProducto";

        progreso = ProgressDialog.show(this, "",
                "Iniciando Búsqueada", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                (r) -> {
                    //Log.d("ANS",r);
                    ObjectMapper mapper = new ObjectMapper( );
                    try{
                        this.productoParcelable = mapper.readValue(r,ProductoParcelable.class);
                        this.loadProducto();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progreso.dismiss();
                },
                (error) -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        try{
                            String jsonError = new String(networkResponse.data);
                            JSONObject jsonObject = new JSONObject(jsonError);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{

                //Log.d("TEST",String.format("%s %s",codTextField.getText().toString(),nroTextField.getText().toString()));
                Map<String, String> params = new HashMap<String, String>();
                params.put("numero", nroTextField.getText().toString());
                params.put("codigo", codTextField.getText().toString().toUpperCase());
                return params;
            }

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
        requestQueue.add(stringRequest);
    }

    private void loadProducto(){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int)(5*this.density),(int)(10*this.density),(int)(5*this.density),(int)(10*this.density));
        //CARD PARA EL DESTINO
        this.linearContent.addView(this.getCardClientes(),params);
        //this.getCardClientes();

        //CARD PARA LOCALIDAD
        this.linearContent.addView(this.getCardLocacion(),params);

        //CARD PARA LA DESCRIPCION
        this.linearContent.addView(this.getCardDescripcion(),params);

        //BTN DE ENVIO
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.gravity = Gravity.CENTER;
        params2.setMargins(0,(int)(20*this.density),0,(int)(10*this.density));
        Button btn = new Button(linearContent.getContext());
        btn.setText("Inicio de Recorrido");
        btn.setTextSize(10);
        btn.setBackgroundColor(Color.YELLOW);

        btn.setOnClickListener( e -> {
            Intent intent = new Intent(getApplicationContext(),ActualizarProductoActivity.class);
            intent.putExtra("producto",this.productoParcelable);
            startActivity(intent);
        });

        this.linearContent.addView(btn,params2);
    }
    private CardView getCardClientes(){

        //WIDTH , HEIGHT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int)(5*this.density),0,(int)(5*this.density),0);

        CardView cardClienteDestino = new CardView(this.linearContent.getContext());
        LinearLayout layoutClienteDestino = new LinearLayout(cardClienteDestino.getContext());


        layoutClienteDestino.setOrientation(LinearLayout.VERTICAL);
        TextView nombres = new TextView(layoutClienteDestino.getContext());
        nombres.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nombres.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        nombres.setText(String.format("%s %s",
                productoParcelable.getRecepcion().getNombres(),
                productoParcelable.getRecepcion().getApellidos()));
        layoutClienteDestino.addView(nombres);

        if(productoParcelable.getRecepcion().getCelular() != null){
            TextView datosAdicionales = new TextView(layoutClienteDestino.getContext());
            datosAdicionales.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            datosAdicionales.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            datosAdicionales.setText(
                    productoParcelable.getRecepcion().getCelular());
            layoutClienteDestino.addView(datosAdicionales);
        }

        if(productoParcelable.getDireccion() != null){
            TextView datosAdicionales = new TextView(layoutClienteDestino.getContext());
            datosAdicionales.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            datosAdicionales.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            datosAdicionales.setText(
                    productoParcelable.getDireccion());
            layoutClienteDestino.addView(datosAdicionales);
        }
        cardClienteDestino.addView(layoutClienteDestino,params);
        //this.linearContent.addView(cardClienteDestino);
        return cardClienteDestino;
    }

    private CardView getCardLocacion(){
        //WIDTH , HEIGHT
        CardView.LayoutParams params = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int)(5*this.density),0,(int)(5*this.density),0);

        CardView cardLocacion = new CardView(this.linearContent.getContext());
        TextView localidad = new TextView(cardLocacion .getContext());
        localidad.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        localidad.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        localidad.setText(String.format("%s - %s",
                (this.productoParcelable.getOrigen() == null)? "":this.productoParcelable.getOrigen(),
                (this.productoParcelable.getDestino() == null)? "":this.productoParcelable.getDestino()));

        cardLocacion.addView(localidad,params);
        return cardLocacion;
    }

    private CardView getCardDescripcion(){
        //WIDTH , HEIGHT
        CardView.LayoutParams params = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int)(5*this.density),0,(int)(5*this.density),0);

        CardView cardDescripcion= new CardView(this.linearContent.getContext());
        TextView descripcion = new TextView(cardDescripcion .getContext());
        descripcion.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        descripcion.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        descripcion.setText(
                (this.productoParcelable.getDescripcion() == null)? "Sin descripción":this.productoParcelable.getDescripcion());

        cardDescripcion.addView(descripcion,params);
        return cardDescripcion;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("prd_saved", this.productoParcelable);
        super.onSaveInstanceState(outState);
    }
}
