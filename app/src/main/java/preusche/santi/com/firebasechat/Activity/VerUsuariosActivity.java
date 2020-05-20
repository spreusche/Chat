package preusche.santi.com.firebasechat.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Holder.UsuarioViewHolder;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constantes;

public class VerUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference usuariosReference;


    private Button addUserBtn;
    private EditText txtEmailNuevo;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        rvUsuarios = findViewById(R.id.rvUsuarios);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUsuarios.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usuariosReference = database.getReference(Constantes.NODO_USUARIOS);

        addUserBtn = (Button) findViewById(R.id.btnAgregar);
        txtEmailNuevo = (EditText) findViewById(R.id.txtAgregarUsuario);

        setTitle("Contactos");


        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailABuscar = txtEmailNuevo.getText().toString();
                if(!emailABuscar.isEmpty()){

                    usuariosReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         String miCorreo =  dataSnapshot.child(mAuth.getUid()).child("correo").getValue().toString();
                            String miNombre =  dataSnapshot.child(mAuth.getUid()).child("nombre").getValue().toString();
                            Long miNacimiento = (Long) dataSnapshot.child(mAuth.getUid()).child("fechaDeNacimiento").getValue();
                            String miFoto =  dataSnapshot.child(mAuth.getUid()).child("fotoPerfilURL").getValue().toString();
                            String miGenero =  dataSnapshot.child(mAuth.getUid()).child("genero").getValue().toString();

                         //String fotoPerfilURL, String nombre, String correo, long fechaDeNacimiento, String genero
                            Usuario u;
                            Usuario yo = new Usuario(miFoto, miNombre, miCorreo, miNacimiento, miGenero);

                            for(DataSnapshot ds  : dataSnapshot.getChildren()){
                                u = ds.getValue(Usuario.class);

                                //Agrego al usuario nuevo
                                if(u.getCorreo().equals(emailABuscar)){

                                    database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" +  ds.getKey()).setValue(u);
                                    database.getReference("Usuarios/" + ds.getKey() + "/Amigos/" +  mAuth.getUid()).setValue(yo);



                                }
                             /*   if(u.getCorreo().equals(mAuth.getCurrentUser().getEmail())){

                                }*/

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else
                    Toast.makeText(VerUsuariosActivity.this, "Debe ingresar un mail valido", Toast.LENGTH_SHORT).show();
            }
        });






        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constantes.NODO_USUARIOS).child(mAuth.getUid()).child("Amigos");

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
