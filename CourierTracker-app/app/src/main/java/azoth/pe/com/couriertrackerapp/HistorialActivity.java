package azoth.pe.com.couriertrackerapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.transcode.UnitTranscoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import azoth.pe.com.couriertrackerapp.utils.ContenedorParcelable;
import azoth.pe.com.couriertrackerapp.utils.ProductoParcelable;

public class HistorialActivity extends AppCompatActivity {

    private static ProductoParcelable[] productoParcelables;
    private LinearLayout linearScroll;
    private ProgressDialog progreso;

    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        this.url = sharedPreferences.getString("URL","http://52.91.61.121:8080/ct-back");
        requestQueue = Volley.newRequestQueue(this);

        if(this.getIntent() != null){
            ContenedorParcelable contenedorParcelable = getIntent().getExtras().getParcelable("PRODUCTOS");
            if(contenedorParcelable != null)
                productoParcelables = contenedorParcelable.getProductos();
        }

        this.linearScroll = (LinearLayout) findViewById(R.id.linearScroll);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHistorial();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadTable(linearScroll);
        //fab.setVisibility(View.VISIBLE);
    }

    private void loadHistorial(){
        //SE OBTIENEN LOS DATOS DE LA TABLA ANTES DE PASAR
        final String url = this.url + "/TrackerAPI/getProductos";
        progreso = ProgressDialog.show(this, "",
                "Cargando Historial...", true);

        StringRequest objectRequest = new StringRequest(Request.Method.POST,url,
                (String r) -> {
                    //Log.d("PRODUCTOS: ", r);
                    try{
                        ObjectMapper mapper = new ObjectMapper();
                        ArrayList<ProductoParcelable> temp = new ArrayList<>();
                        JSONArray array = new JSONArray(r);
                        for(int i = 0 ; i < array.length() ; i++){
                            String producto = array.getJSONObject(i).toString();
                            //.d("prod",producto);
                            temp.add(mapper.readValue(producto,ProductoParcelable.class));
                        }

                        productoParcelables = new ProductoParcelable[array.length()];
                        productoParcelables = temp.toArray(productoParcelables);

                        loadTable(linearScroll);

                        progreso.dismiss();

                        //Intent intent = new Intent(getApplicationContext(),HistorialActivity.class);
                        //intent.putExtra("PRODUCTOS", new ContenedorParcelable(prd));
                        //startActivity(intent);

                    }
                    catch (IOException | JSONException e){
                        progreso.dismiss();
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } ,
                (e) -> {
                    progreso.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", (sharedPreferences.getString("token","")));
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                params.put("Accept", "*/*");
                return params;
            }
        };
        requestQueue.add(objectRequest);

    }

    private void loadTable(LinearLayout nestedScrollView){
        nestedScrollView.removeAllViews();

        float d = this.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                NestedScrollView.LayoutParams.MATCH_PARENT, NestedScrollView.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int)(15*d),(int)(15*d),(int)(15*d),(int)(15*d));

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                NestedScrollView.LayoutParams.MATCH_PARENT, NestedScrollView.LayoutParams.WRAP_CONTENT, 1f
        );
        params1.setMargins((int)(2*d),(int)(2*d),(int)(2*d),(int)(2*d));

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                NestedScrollView.LayoutParams.WRAP_CONTENT, NestedScrollView.LayoutParams.MATCH_PARENT, 1f
        );
        params2.setMargins((int)(5*d),0,(int)(5*d),0);
        for(ProductoParcelable p : productoParcelables){
            CardView cardView = new CardView(nestedScrollView.getContext());

            LinearLayout linearLayout = new LinearLayout(cardView.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout linearLayout2 = new LinearLayout(linearLayout.getContext());
            linearLayout2.setOrientation(LinearLayout.VERTICAL);

            //DATOS DE LA TARJETA
            TextView txt1 = new TextView(linearLayout2.getContext());
            txt1.setText(String.format("%s-%d",p.getCodigo(),p.getNumero()));
            txt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            TextView txt2 = new TextView(linearLayout2.getContext());
            txt2.setText(String.format("Envia : %s %s", p.getEnvio().getApellidos(),p.getEnvio().getNombres()));
            txt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            TextView txt3 = new TextView(linearLayout2.getContext());
            txt3.setText(String.format("Recibe: %s %s", p.getRecepcion().getApellidos(),p.getRecepcion().getNombres()));
            txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

            linearLayout2.addView(txt1);
            linearLayout2.addView(txt2);
            linearLayout2.addView(txt3);

            //ICONO
            AppCompatImageView image = new AppCompatImageView(linearLayout.getContext());
            //image.setBackgroundResource(R.drawable.circle_red);
            switch (p.getEstado().getId()){
                case 0:
                    image.setImageResource(R.drawable.circle_red);
                    break;
                case 1:
                    image.setImageResource(R.drawable.circle_yellow);
                    break;
                case 2:
                    image.setImageResource(R.drawable.circle_green);
                    break;
                case 3:
                    image.setImageResource(R.drawable.circle_blue);
                    break;
                default:
                    image.setImageResource(R.drawable.circle_undefined);
                    break;
            }
            //image.setImageResource(R.drawable.circle_red);

            linearLayout.addView(linearLayout2,params1);
            linearLayout.addView(image,params2);

            cardView.addView(linearLayout);
            nestedScrollView.addView(cardView,params);
        }

    }
}
