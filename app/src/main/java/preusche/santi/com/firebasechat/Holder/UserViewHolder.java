package preusche.santi.com.firebasechat.Holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import preusche.santi.com.firebasechat.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView civProfilePic;
    private TextView txtUserName;
    private LinearLayout principalLayout;
    private TextView circle;
    private TextView lastMessage;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        civProfilePic = itemView.findViewById(R.id.civProfilePic);
        txtUserName =itemView.findViewById(R.id.txtUsername);
        principalLayout = itemView.findViewById(R.id.principalLayout);
        circle = itemView.findViewById(R.id.dotNotification);
    }

    public CircleImageView getCivProfilePic() {
        return civProfilePic;
    }

    public void setCivProfilePic(CircleImageView civProfilePic) {
        this.civProfilePic = civProfilePic;
    }

    public TextView getTxtUserName() {
        return txtUserName;
    }

    public void setTxtUserName(TextView txtUserName) {
        this.txtUserName = txtUserName;
    }

    public LinearLayout getPrincipalLayout() {
        return principalLayout;
    }

    public void setPrincipalLayout(LinearLayout principalLayout) {
        this.principalLayout = principalLayout;
    }

    public TextView getCircle() {
        return circle;
    }

    public void setCircle(TextView circle) {
        this.circle = circle;
    }

    public TextView getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TextView lastMessage) {
        this.lastMessage = lastMessage;
    }
}
