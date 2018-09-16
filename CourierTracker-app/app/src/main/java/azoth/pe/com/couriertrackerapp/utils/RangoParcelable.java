package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class RangoParcelable implements Parcelable {
    public RangoParcelable() {}
    private int id;
    private String descripcion;


    protected RangoParcelable(Parcel in) {
        id = in.readInt();
        descripcion = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(descripcion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RangoParcelable> CREATOR = new Creator<RangoParcelable>() {
        @Override
        public RangoParcelable createFromParcel(Parcel in) {
            return new RangoParcelable(in);
        }

        @Override
        public RangoParcelable[] newArray(int size) {
            return new RangoParcelable[size];
        }
    };

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


}
