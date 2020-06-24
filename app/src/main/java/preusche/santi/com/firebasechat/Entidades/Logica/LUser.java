package preusche.santi.com.firebasechat.Entidades.Logica;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import preusche.santi.com.firebasechat.Entidades.Firebase.User;
import preusche.santi.com.firebasechat.Persistencia.UserDAO;

/**
 * Created by user on 28/08/2018. 28
 */
public class LUser {

    private String key;
    private User user;

    public LUser(String key, User user) {
        this.key = key;
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreationDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UserDAO.getInstance().creationDateLong());
        return simpleDateFormat.format(date);
    }

    public String getLastLoginDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UserDAO.getInstance().lastLoginDateLong());
        return simpleDateFormat.format(date);
    }

}
