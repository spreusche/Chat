package preusche.santi.com.firebasechat.Adaptadores;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import preusche.santi.com.firebasechat.Entidades.Logica.LMessage;
import preusche.santi.com.firebasechat.Entidades.Logica.LUser;
import preusche.santi.com.firebasechat.Holder.MessengerHolder;
import preusche.santi.com.firebasechat.Persistencia.UserDAO;
import preusche.santi.com.firebasechat.R;

/**
 * Created by user on 04/09/2017. 04
 */

public class MessengerAdapter extends RecyclerView.Adapter<MessengerHolder> {

    private List<LMessage> messageList = new ArrayList<>();
    private Context c;

    public MessengerAdapter(Context c) {
        this.c = c;
    }

    public int addMessage(LMessage lMessage){
        messageList.add(lMessage);
        //3 mensajes
        int position = messageList.size()-1;//3
        notifyItemInserted(messageList.size());
        return position;
    }

    public void updateMessage(int posicion, LMessage lMessage){
        messageList.set(posicion, lMessage);//2
        notifyItemChanged(posicion);
    }

    @Override
    public MessengerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(c).inflate(R.layout.card_view_messages_emisor,parent,false);
        }else{
            view = LayoutInflater.from(c).inflate(R.layout.card_view_messages_receptor,parent,false);
        }
        return new MessengerHolder(view);
    }

    @Override
    public void onBindViewHolder(MessengerHolder holder, int position) {

        LMessage lMessage = messageList.get(position);

        LUser lUser = lMessage.getlUser();

        if(lUser!=null){
            holder.getName().setText(lUser.getUser().getName());
            Glide.with(c).load(lUser.getUser().getProfilePicURL()).into(holder.getMessageProfilePic());
        }

        holder.getMessage().setText(lMessage.getMessage().getMessage());
        if(lMessage.getMessage().isContainsPhoto()){
            holder.getMessagePicture().setVisibility(View.VISIBLE);
            holder.getMessage().setVisibility(View.VISIBLE);
            Glide.with(c).load(lMessage.getMessage().getUrlPic()).into(holder.getMessagePicture());
        }else {
            holder.getMessagePicture().setVisibility(View.GONE);
            holder.getMessage().setVisibility(View.VISIBLE);
        }

        holder.getHour().setText(lMessage.messageCreationDate());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getlUser()!=null){
            if(messageList.get(position).getlUser().getKey().equals(UserDAO.getInstance().getUserKey())){
                return 1;
            }else{
                return -1;
            }
        }else{
            return -1;
        }
        //return super.getItemViewType(position);
    }
}
