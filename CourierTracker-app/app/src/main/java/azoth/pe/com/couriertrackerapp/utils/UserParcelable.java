package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class UserParcelable implements Parcelable {
    private String correo;
    private String pass;
    private String nombres;
    private String apellidos;
    private String dni;
    private int rango;
    private boolean activo;


    public UserParcelable() {
    }

    public UserParcelable( String pass, String nombres, String apellidos, String dni, String correo, int rango, Boolean activo) {
        super();
        this.pass = pass;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correo = correo;
        this.rango = rango;
        this.setActivo(activo);
    }
    protected UserParcelable(Parcel in) {
        pass = in.readString();
        nombres = in.readString();
        apellidos = in.readString();
        dni = in.readString();
        correo = in.readString();
        rango = in.readInt();
        activo = in.readByte() != 0;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getRango() {
        return rango;
    }

    public void setRango(int rango) {
        this.rango = rango;
    }

    public Boolean isActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pass);
        dest.writeString(nombres);
        dest.writeString(apellidos);
        dest.writeString(dni);
        dest.writeString(correo);
        dest.writeInt(rango);
        dest.writeByte((byte) (activo ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserParcelable> CREATOR = new Parcelable.Creator<UserParcelable>() {
        @Override
        public UserParcelable createFromParcel(Parcel in) {
            return new UserParcelable(in);
        }

        @Override
        public UserParcelable[] newArray(int size) {
            return new UserParcelable[size];
        }
    };
}
