package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ContenedorParcelable implements Parcelable {

    private ProductoParcelable[] productos;

    public ContenedorParcelable(ProductoParcelable[] productos){
        this.productos = productos;
    }

    public ContenedorParcelable(){}

    protected ContenedorParcelable(Parcel in) {
        productos = in.createTypedArray(ProductoParcelable.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(productos, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContenedorParcelable> CREATOR = new Creator<ContenedorParcelable>() {
        @Override
        public ContenedorParcelable createFromParcel(Parcel in) {
            return new ContenedorParcelable(in);
        }

        @Override
        public ContenedorParcelable[] newArray(int size) {
            return new ContenedorParcelable[size];
        }
    };

    public ProductoParcelable[] getProductos() {
        return productos;
    }

    public void setProductos(ProductoParcelable[] productos) {
        this.productos = productos;
    }
}

