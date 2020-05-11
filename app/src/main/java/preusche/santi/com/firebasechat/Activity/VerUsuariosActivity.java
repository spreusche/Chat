package preusche.santi.com.firebasechat.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Holder.UsuarioViewHolder;
import preusche.santi.com.firebasechat.Persistencia.UsuarioDAO;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constantes;


public class VerUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private FirebaseRecyclerAdapter adapter;

    private Button btnAgregarContacto;
    private EditText txtNuevoContactoEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        rvUsuarios = findViewById(R.id.rvUsuarios);

        btnAgregarContacto = (Button) findViewById(R.id.btnAgregarContacto);
        txtNuevoContactoEmail = (EditText) findViewById(R.id.txtNuevoContacto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUsuarios.setLayoutManager(linearLayoutManager);


        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("5");


                String emailIngresado = txtNuevoContactoEmail.getText().toString();
                UsuarioDAO.getInstancia().agregarContacto(emailIngresado);
                txtNuevoContactoEmail.setText(""); //limpio el lugar donde escribo
            }
        });




        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constantes.NODO_USUARIOS);

        FirebaseRecyclerOptions<Usuario> options =
                new FirebaseRecyclerOptions.Builder<Usuario>()
                        .setQuery(query, Usuario.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Usuario, UsuarioViewHolder>(options) {
            @Override
            public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_usuario, parent, false);
                return new UsuarioViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsuarioViewHolder holder, int position, final Usuario model) {
                Glide.with(VerUsuariosActivity.this).load(model.getFotoPerfilURL()).into(holder.getCivFotoPerfil());
                holder.getTxtNombreUsuario().setText(model.getNombre());

                final LUsuario lUsuario = new LUsuario(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getLayoutPrincipal().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VerUsuariosActivity.this,MensajeriaActivity.class);
                        intent.putExtra("key_receptor",lUsuario.getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        rvUsuarios.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
