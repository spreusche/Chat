package preusche.santi.com.firebasechat.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import preusche.santi.com.firebasechat.Entidades.Firebase.Mensaje;
import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Holder.UsuarioViewHolder;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constantes;

public class VerUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;

    private  String lastMessage;
    private  String uidByEmail;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference usuariosReference;


    //Buttons
    private Button addUserBtn;

    //ImageView
  //  private ImageView mDeleteImage;

    //Text
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

     //   mDeleteImage = (ImageView) findViewById(R.id.defautlDeleteID);

        setTitle("Contactos");

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvUsuarios);

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



//ONDA, AL AGREGAR UN USUARIO, COMO QUE SE RESETEAN LOS IDS DE LOS BOTONES DE ELIMINAR
        //POR LO TANTO, HACER QUE COINCIDAN NO DEBERIA SER MUY RARO NO?
        //DENTRO DEL BINDHOLDERNOSEQUEVERGA HACE UN .setId(ActualPosition);

//MAGIA DE KEVIN

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
            protected void onBindViewHolder(final UsuarioViewHolder holder, int position, final Usuario model) {
                Glide.with(VerUsuariosActivity.this).load(model.getFotoPerfilURL()).into(holder.getCivFotoPerfil());
                holder.getTxtNombreUsuario().setText(model.getNombre());

               // String lastMsg = getLastMsg(mAuth.getUid(), getUidByEmail(model.getCorreo()));
                // holder.getLastMessage().setText(lastMsg);

                DatabaseReference ref = database.getReference("Usuarios/" + mAuth.getUid() + "/MensajesNuevos/" + model.getCorreo().replace(".", ","));
                ref.addValueEventListener(new ValueEventListener() {
                    //Al ser addValueEventListener, como que no es una vez, sino que lo hace todo el tiempo
                    //De esta forma, al mandar un mensaje nuevo, aparece la estrellita instantaneamente
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                // The child does exist
                                holder.getCircle().setVisibility(View.VISIBLE);
                                System.out.println(snapshot.getValue().toString());
                            }else {

                                System.out.println("hola, fue null");
                                holder.getCircle().setVisibility(View.GONE);
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                final LUsuario lUsuario = new LUsuario(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getLayoutPrincipal().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VerUsuariosActivity.this,MensajeriaActivity.class);
                        intent.putExtra("key_receptor",lUsuario.getKey());

                        database.getReference("Usuarios/" + mAuth.getUid() + "/MensajesNuevos/" + model.getCorreo().replace(".", ",")).removeValue();

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



    //Los parametros referencian a "mover tipo hacer un drag" y "deslizar"
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            eliminarUsuario(position);

           // System.out.println(rvUsuarios.getChildAt(viewHolder.getAdapterPosition()).getId());

            //rvUsuarios.removeViewAt(position);
            adapter.notifyDataSetChanged();

            Toast.makeText(VerUsuariosActivity.this, "ELIMINA3", Toast.LENGTH_SHORT).show();
        }
    };





    private void eliminarUsuario(final int id){

        database.getReference("Usuarios/" + mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uidAmigoAEliminar;
                String miNombre = dataSnapshot.child("nombre").getValue().toString(); //conseguimos el nombre

                int i = 0;
                for(DataSnapshot ds : dataSnapshot.child("Amigos").getChildren()){
                    if(i == id){
                        uidAmigoAEliminar = ds.getKey();
                        database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" + uidAmigoAEliminar).removeValue();
                        String amigoUid = uidAmigoAEliminar;
                        System.out.println(amigoUid);

                        //elimino de MIS amigos, pero no de los suyos
                       // database.getReference("Usuarios/" + amigoUid + "/Amigos/" + miNombre + "~" + mAuth.getUid()).removeValue();



                        return;
                    }
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    /*private String getLastMsg(String keyActual, String keyChat){


       DatabaseReference chatRef = database.getReference(Constantes.NODO_MENSAJES + "/" + keyActual + "/" + keyChat );
       chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.getValue() != null) {
                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       lastMessage = ds.getValue(Mensaje.class).getMensaje();
                   }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        int lg = lastMessage.length();
        if(lg > 25){
            String msgCut = lastMessage.substring(0, 22);
            lastMessage = msgCut + "...";
        }
        return lastMessage;
    }*/
/*

    private String getUidByEmail(final String email){

        database.getReference("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue(Usuario.class).getCorreo().equals(email)){
                        uidByEmail = ds.getKey();
                        return;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return uidByEmail;
    }
*/




}
