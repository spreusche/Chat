package preusche.santi.com.firebasechat.Entidades.Firebase;

import com.google.firebase.database.ServerValue;

/**
 * Created by user on 04/09/2017. 04
 */

public class Message {

    private String message;
    private String urlPic;
    private boolean containsPhoto;
    private String keyEmisor;
    private Object createdTimestamp;
   // private boolean leido;

    public Message() {
        createdTimestamp = ServerValue.TIMESTAMP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrlPic() {
        return urlPic;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }

    public boolean isContainsPhoto() {
        return containsPhoto;
    }

    public void setContainsPhoto(boolean containsPhoto) {
        this.containsPhoto = containsPhoto;
    }

    public String getKeyEmisor() {
        return keyEmisor;
    }

    public void setKeyEmisor(String keyEmisor) {
        this.keyEmisor = keyEmisor;
    }

    public Object getCreatedTimestamp() {
        return createdTimestamp;
    }

   /* public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }*/
}
