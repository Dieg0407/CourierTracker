package azoth.pe.com.couriertrackerapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class ProductoParcelable implements Parcelable {

    public ProductoParcelable() {}

    private int numero;
    private String codigo;
    private String descripcion;
    private String direccion;
    private String origen;
    private String destino;
    private ClienteParcelable envio;
    private ClienteParcelable recepcion;
    private EstadoParcelable estado;
    private Timestamp fechaCreacion;

    protected ProductoParcelable(Parcel in) {
        numero = in.readInt();
        codigo = in.readString();
        descripcion = in.readString();
        direccion = in.readString();
        origen = in.readString();
        destino = in.readString();
        envio = in.readParcelable(ClienteParcelable.class.getClassLoader());
        recepcion = in.readParcelable(ClienteParcelable.class.getClassLoader());
        estado = in.readParcelable(EstadoParcelable.class.getClassLoader());
        fechaCreacion = (Timestamp) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numero);
        dest.writeString(codigo);
        dest.writeString(descripcion);
        dest.writeString(direccion);
        dest.writeString(origen);
        dest.writeString(destino);
        dest.writeParcelable(envio, flags);
        dest.writeParcelable(recepcion, flags);
        dest.writeParcelable(estado, flags);
        dest.writeSerializable(fechaCreacion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductoParcelable> CREATOR = new Creator<ProductoParcelable>() {
        @Override
        public ProductoParcelable createFromParcel(Parcel in) {
            return new ProductoParcelable(in);
        }

        @Override
        public ProductoParcelable[] newArray(int size) {
            return new ProductoParcelable[size];
        }
    };

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getOrigen() {
        return origen;
    }
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    public String getDestino() {
        return destino;
    }
    public void setDestino(String destino) {
        this.destino = destino;
    }
    public ClienteParcelable getEnvio() {
        return envio;
    }
    public void setEnvio(ClienteParcelable envio) {
        this.envio = envio;
    }
    public ClienteParcelable getRecepcion() {
        return recepcion;
    }
    public void setRecepcion(ClienteParcelable recepcion) {
        this.recepcion = recepcion;
    }
    public EstadoParcelable getEstado() {
        return estado;
    }
    public void setEstado(EstadoParcelable estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @JsonProperty("fecha_creacion")
    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
