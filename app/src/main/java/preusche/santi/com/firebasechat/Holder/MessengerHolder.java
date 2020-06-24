package preusche.santi.com.firebasechat.Holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import preusche.santi.com.firebasechat.R;

/**
 * Created by user on 04/09/2017. 04
 */

public class MessengerHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView message;
    private TextView hour;
    private CircleImageView messageProfilePic;
    private ImageView messagePicture;

    public MessengerHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.nameMessage);
        message = (TextView) itemView.findViewById(R.id.messageMessage);
        hour = (TextView) itemView.findViewById(R.id.hourMessage);
        messageProfilePic = (CircleImageView) itemView.findViewById(R.id.profilePicMessage);
        messagePicture = (ImageView) itemView.findViewById(R.id.messagePicture);
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getMessage() {
        return message;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

    public TextView getHour() {
        return hour;
    }

    public void setHour(TextView hour) {
        this.hour = hour;
    }

    public CircleImageView getMessageProfilePic() {
        return messageProfilePic;
    }

    public void setMessageProfilePic(CircleImageView messageProfilePic) {
        this.messageProfilePic = messageProfilePic;
    }

    public ImageView getMessagePicture() {
        return messagePicture;
    }

    public void setMessagePicture(ImageView messagePicture) {
        this.messagePicture = messagePicture;
    }
}
