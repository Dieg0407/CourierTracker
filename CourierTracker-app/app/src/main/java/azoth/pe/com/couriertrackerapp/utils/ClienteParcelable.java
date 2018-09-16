package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ClienteParcelable implements Parcelable {

    public ClienteParcelable() {}

    private int id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String celular;
    private String correo;

    protected ClienteParcelable(Parcel in) {
        id = in.readInt();
        nombres = in.readString();
        apellidos = in.readString();
        dni = in.readString();
        celular = in.readString();
        correo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombres);
        dest.writeString(apellidos);
        dest.writeString(dni);
        dest.writeString(celular);
        dest.writeString(correo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClienteParcelable> CREATOR = new Creator<ClienteParcelable>() {
        @Override
        public ClienteParcelable createFromParcel(Parcel in) {
            return new ClienteParcelable(in);
        }

        @Override
        public ClienteParcelable[] newArray(int size) {
            return new ClienteParcelable[size];
        }
    };

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getCelular() {
        return celular;
    }
    public void setCelular(String celular) {
        this.celular = celular;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
