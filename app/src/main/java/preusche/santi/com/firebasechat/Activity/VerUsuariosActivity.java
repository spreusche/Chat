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
import android.widget.LinearLayout;
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
import java.util.List;

import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Holder.UsuarioViewHolder;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constantes;

public class VerUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;


    private int idGenerator;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference usuariosReference;

    //Buttons
    private Button addUserBtn;
   // private Button deleteContactBtn;
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
        //deleteContactBtn = (Button) findViewById(R.id.);

        txtEmailNuevo = (EditText) findViewById(R.id.txtAgregarUsuario);

        setTitle("Contactos");


      /*  deleteContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerUsuariosActivity.this, "Aprete eliminar", Toast.LENGTH_SHORT).show();
                System.out.println("Aprete el boton nomas");
            }
        });*/

      database.getReference().child("Usuarios").child(mAuth.getUid()).child("Amigos").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              idGenerator = (int) dataSnapshot.getChildrenCount();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


        System.out.println("idGen: " + idGenerator);


      for(int i = 0; i < idGenerator; i++){
          //Boton dinamico
          Button myButton = new Button(VerUsuariosActivity.this);
          myButton.setText("Eliminar");
          myButton.setId(idGenerator++);
          myButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  eliminarUsuario(v.getId());

                  idGenerator--;

                  ViewGroup layout = (ViewGroup) v.getParent();
                  if(null!=layout) //for safety only  as you are doing onClick
                      layout.removeView(v);

                  for(int i = 0; i < idGenerator; i++){
                      layout.getChildAt(i).setId(i);
                  }


              }
          });


          LinearLayout ll = (LinearLayout)findViewById(R.id.buttonLayout);
          ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
          ll.addView(myButton, lp);


      }



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
                                if(u.getCorreo().equals(emailABuscar)) {
                                    if (!ds.hasChild(mAuth.getUid() + "/Amigos/" + u.getNombre() + "~" + ds.getKey())) {

                                        database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" + u.getNombre() + "~" + ds.getKey()).setValue(u);
                                        database.getReference("Usuarios/" + ds.getKey() + "/Amigos/" + miNombre + "~" + mAuth.getUid()).setValue(yo);


                                        //Boton dinamico
                                        Button myButton = new Button(VerUsuariosActivity.this);
                                        myButton.setText("Eliminar");
                                        myButton.setId(idGenerator++);
                                        myButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                eliminarUsuario(v.getId());

                                                idGenerator--;

                                                ViewGroup layout = (ViewGroup) v.getParent();
                                                if (null != layout) //for safety only  as you are doing onClick
                                                    layout.removeView(v);

                                                for (int i = 0; i < idGenerator; i++) {
                                                    layout.getChildAt(i).setId(i);
                                                }


                                            }
                                        });


                                        LinearLayout ll = (LinearLayout) findViewById(R.id.buttonLayout);
                                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        ll.addView(myButton, lp);


                                        return;

                                    }
                                    else
                                        Toast.makeText(VerUsuariosActivity.this, "Ya tienes este conotacto", Toast.LENGTH_SHORT).show();
                                }
                             /*   if(u.getCorreo().equals(mAuth.getCurrentUser().getEmail())){

                                }*/

                            }
                            Toast.makeText(VerUsuariosActivity.this, "No existe el usuario", Toast.LENGTH_SHORT).show();

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

    private void eliminarUsuario(final int id){

        database.getReference("Usuarios/" + mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uidConcatenadoAEliminar;
                String miNombre = dataSnapshot.child("nombre").getValue().toString(); //conseguimos el nombre

                int i = 0;
                for(DataSnapshot ds : dataSnapshot.child("Amigos").getChildren()){
                    if(i == id){
                        uidConcatenadoAEliminar = ds.getKey();
                        database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" + uidConcatenadoAEliminar).removeValue();
                        String amigoUid = uidConcatenadoAEliminar.subSequence(uidConcatenadoAEliminar.indexOf("~") + 1, uidConcatenadoAEliminar.length()).toString();
                        System.out.println(amigoUid);

                        database.getReference("Usuarios/" + amigoUid + "/Amigos/" + miNombre + "~" + mAuth.getUid()).removeValue();



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

}