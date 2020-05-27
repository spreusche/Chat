package preusche.santi.com.firebasechat.Holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import preusche.santi.com.firebasechat.R;

public class UsuarioViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView civFotoPerfil;
    private TextView txtNombreUsuario;
    private LinearLayout layoutPrincipal;
    private TextView circle;

    public UsuarioViewHolder(@NonNull View itemView) {
        super(itemView);
        civFotoPerfil = itemView.findViewById(R.id.civFotoPerfil);
        txtNombreUsuario =itemView.findViewById(R.id.txtNombreUsuario);
        layoutPrincipal = itemView.findViewById(R.id.layoutPrincipal);
        circle = itemView.findViewById(R.id.dotNotification);
    }

    public CircleImageView getCivFotoPerfil() {
        return civFotoPerfil;
    }

    public void setCivFotoPerfil(CircleImageView civFotoPerfil) {
        this.civFotoPerfil = civFotoPerfil;
    }

    public TextView getTxtNombreUsuario() {
        return txtNombreUsuario;
    }

    public void setTxtNombreUsuario(TextView txtNombreUsuario) {
        this.txtNombreUsuario = txtNombreUsuario;
    }

    public LinearLayout getLayoutPrincipal() {
        return layoutPrincipal;
    }

    public void setLayoutPrincipal(LinearLayout layoutPrincipal) {
        this.layoutPrincipal = layoutPrincipal;
    }

    public TextView getCircle() {
        return circle;
    }

    public void setCircle(TextView circle) {
        this.circle = circle;
    }
}
