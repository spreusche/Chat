package preusche.santi.com.firebasechat.Entidades.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 19/02/2018. 19
 */

public class Usuario {

    private String fotoPerfilURL;
    private String nombre;
    private String correo;
    private long fechaDeNacimiento;
    private String genero;

    private List<String> Contacts = new ArrayList<>();

    public Usuario() {
    }

    public String getFotoPerfilURL() {
        return fotoPerfilURL;
    }

    public void setFotoPerfilURL(String fotoPerfilURL) {
        this.fotoPerfilURL = fotoPerfilURL;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(long fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    //-------------------------------------------------------------------NUEVO
    public List<String> getContacts() {
        return Contacts;
    }

    //Me va a servir
    public void setContacts(List<String> contacts) {
        this.Contacts = contacts;
    }
}
