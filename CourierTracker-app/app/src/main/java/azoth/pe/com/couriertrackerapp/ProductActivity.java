package azoth.pe.com.couriertrackerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import azoth.pe.com.couriertrackerapp.utils.ProductoParcelable;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ProductoParcelable datos = getIntent().getExtras().getParcelable("PR");
        TextView txt = (TextView) this.findViewById(R.id.nombreProducto);
        txt.setText(datos.getDescripcion());
        txt.setTextSize(20);

        //txt.setText("ESTO ES UNA PRUEBA Y EL ID DEL PRODUCTO ES..." + i );
    }

}
