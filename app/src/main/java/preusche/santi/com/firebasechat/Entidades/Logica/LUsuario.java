package preusche.santi.com.firebasechat.Entidades.Logica;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Persistencia.UsuarioDAO;

/**
 * Created by user on 28/08/2018. 28
 */
public class LUsuario {

    private String key; //Este es el uid/la key esa rara que le pone firebase
    private Usuario usuario;

    public LUsuario(String key, Usuario usuario) {
        this.key = key;
        this.usuario = usuario;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String obtenerFechaDeCreacion(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstancia().fechaDeCreacionLong());
        return simpleDateFormat.format(date);
    }

    public String obtenerFechaDeUltimaVezQueSeLogeo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstancia().fechaDeUltimaVezQueSeLogeoLong());
        return simpleDateFormat.format(date);
    }
//---------CUIDADO, esto solo lo agrega al usuario, no agrega a la BD, verlo luego en detalle no va
    public void addContacto(String contacto){
        usuario.getContacts().add(contacto);
    }



}
