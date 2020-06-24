package preusche.santi.com.firebasechat.Entidades.Logica;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

import preusche.santi.com.firebasechat.Entidades.Firebase.Message;

/**
 * Created by user on 28/08/2018. 28
 */
public class LMessage {

    private String key;
    private Message message;
    private LUser lUser;

    public LMessage(String key, Message message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public long getCreatedTimestampLong(){
        return (long) message.getCreatedTimestamp();
    }

    public LUser getlUser() {
        return lUser;
    }

    public void setlUser(LUser lUser) {
        this.lUser = lUser;
    }

    public String messageCreationDate(){
        Date date = new Date(getCreatedTimestampLong());
        PrettyTime prettyTime = new PrettyTime(new Date(),Locale.getDefault());
        return prettyTime.format(date);
        /*Date date = new Date(getCreatedTimestampLong());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());//a pm o am
        return sdf.format(date);*/
    }

}
