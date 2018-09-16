package azoth.pe.com.couriertrackerapp;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import azoth.pe.com.couriertrackerapp.utils.ContenedorParcelable;
import azoth.pe.com.couriertrackerapp.utils.ProductoParcelable;
import azoth.pe.com.couriertrackerapp.utils.UserParcelable;

public class ContentActivity extends AppCompatActivity {

    public static UserParcelable userParcelable;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    private ProgressDialog progreso;

    private Button btnRastreo;
    private Button btnHistorial;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        try{
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.INVISIBLE);
        }catch(Exception e){}
        */
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        this.url = sharedPreferences.getString("URL","http://52.91.61.121:8080/ct-back");
        requestQueue = Volley.newRequestQueue(this);

        if(userParcelable == null)
            userParcelable = getIntent().getExtras().getParcelable("DATA_USER");


        this.btnRastreo = (Button) findViewById(R.id.btnRastreo);
        this.btnHistorial = (Button) findViewById(R.id.btnHistorial);

        this.btnRastreo.setOnClickListener( e -> {loadRastreo();});
        this.btnHistorial.setOnClickListener( e -> {loadHistorial();});
        //loadTable();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTable();
            }
        });
        */
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        */
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

    }

    private void loadRastreo(){
        Intent intent = new Intent(getApplicationContext(),RastreoActivity.class);
        //intent.putExtra("PRODUCTOS", new ContenedorParcelable(prd));
        startActivity(intent);
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
                        ArrayList<ProductoParcelable> productoParcelables = new ArrayList<>();
                        JSONArray array = new JSONArray(r);
                        for(int i = 0 ; i < array.length() ; i++){
                            String producto = array.getJSONObject(i).toString();
                            //.d("prod",producto);
                            productoParcelables.add(mapper.readValue(producto,ProductoParcelable.class));
                        }

                        ProductoParcelable[] prd = new ProductoParcelable[array.length()];
                        prd = productoParcelables.toArray(prd);

                        progreso.dismiss();

                        Intent intent = new Intent(getApplicationContext(),HistorialActivity.class);
                        intent.putExtra("PRODUCTOS", new ContenedorParcelable(prd));
                        startActivity(intent);

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

    /*
    private void setPedido(int id){

        for(ProductoParcelable t : contentParcelable.getContenido()){
            if(t.getId() == id){

                Intent i = new Intent(getApplicationContext(),ProductActivity.class);
                i.putExtra("PR",t);
                startActivity(i);
                break;
            }
        }
    }

    private void loadTable() {

        //LOAD TABLE
        LinearLayout tabla = (LinearLayout) this.findViewById(R.id.main_linear);
        tabla.removeAllViews();

        for(ProductoParcelable t : contentParcelable.getContenido()){
            //
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(6, 6, 6, 6);

            LinearLayout linearLayout = new LinearLayout(tabla.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10,5,10,5);
            linearLayout.setBackgroundColor(0xFFD6D7D7);

            TextView view1 = new TextView(linearLayout.getContext());
            view1.setTextSize(30);
            view1.setText(t.getDescripcion());

            TextView view2 = new TextView(linearLayout.getContext());
            view2.setTextSize(10);
            view2.setText("Esta es una pequeña descripción");

            linearLayout.addView(view1);
            linearLayout.addView(view2);

            linearLayout.setOnClickListener( e -> {setPedido(t.getId());} );
            tabla.addView(linearLayout,params);


        }
    }

    private void requestTable(){
        String url = "http://174.129.146.1:8080/ct-back/api/getTable";
        StringRequest objectRequest = new StringRequest(Request.Method.POST,url,
                (r) -> {
                    try{
                        JSONObject rs = new JSONObject(r);
                        if(rs.getString("state").equals("success")){
                            ProductoParcelable[] datos = new ProductoParcelable[0];

                            JSONArray array = rs.getJSONArray("pedidos");
                            datos = new ProductoParcelable[array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject pedido = array.getJSONObject(i);
                                datos[i] = new ProductoParcelable(pedido.getInt("id"), pedido.getString("desc"));
                            }
                            ContentActivity.contentParcelable.setContenido(datos);
                            loadTable();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), rs.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error en el formato de respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } ,
                (e) -> {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error en la llamada al servicio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            //AQUI VAN LOS PARAMETROS DE MI PETICION
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", (sharedPreferences.getString("token","")));
                return params;
            }
        };
        requestQueue.add(objectRequest);
    }
    private TableRow getTableRow(TableLayout tabla){
        TableRow row = new TableRow(this);
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(20,5,5,20);
        row.setLayoutParams(tableRowParams);
        row.setBackgroundColor(0xfffee5);
        return row;
    }

    private TextView getTextView(TableRow row){
        TextView txt = new TextView(row.getContext());
        //TableLayout.LayoutParams tableTxtParams = new TableLayout.LayoutParams
          //      (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        //txt.setLayoutParams(tableTxtParams);
        txt.setTextSize(20);
        return txt;
    }

    private View getSeparator(TableRow row){
        View view = new View(row.getContext());
        ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(40,ViewGroup.LayoutParams.MATCH_PARENT);
        view.setBackgroundColor(Color.BLUE);

        //view.setLayoutParams(layout);
        return view;
    }
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    */
}
