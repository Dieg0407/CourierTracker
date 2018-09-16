package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class EstadoParcelable implements Parcelable {

    public EstadoParcelable() {}

    private int id;
    private String descripcion;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoParcelable(Parcel in){
        this.id = in.readInt();
        this.descripcion = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.descripcion);
    }

    public static final Creator<EstadoParcelable> CREATOR = new Creator<EstadoParcelable>() {
        @Override
        public EstadoParcelable createFromParcel(Parcel in) {
            return new EstadoParcelable(in);
        }

        @Override
        public EstadoParcelable[] newArray(int size) {
            return new EstadoParcelable[size];
        }
    };
}
