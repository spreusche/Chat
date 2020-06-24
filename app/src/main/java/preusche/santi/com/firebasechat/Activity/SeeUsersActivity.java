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

import preusche.santi.com.firebasechat.Entidades.Firebase.User;
import preusche.santi.com.firebasechat.Entidades.Logica.LUser;
import preusche.santi.com.firebasechat.Holder.UserViewHolder;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constants;

public class SeeUsersActivity extends AppCompatActivity {

    //view
    private RecyclerView rvUsers;

    //String
    private  String lastMessage;
    private  String uidByEmail;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;


    //Buttons
    private Button addUserBtn;

    //ImageView
  //  private ImageView mDeleteImage;

    //Text
    private EditText txtNewEmail;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_users);

        rvUsers = findViewById(R.id.rvUsers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUsers.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference(Constants.USERS_NODE);

        addUserBtn = (Button) findViewById(R.id.btnAdd);
        txtNewEmail = (EditText) findViewById(R.id.txtAddUser);

     //   mDeleteImage = (ImageView) findViewById(R.id.defautlDeleteID);

        setTitle("Contactos");

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvUsers);

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //email a buscar (no te se traducirlo directamente)
                final String wantedEmail = txtNewEmail.getText().toString();
                if(!wantedEmail.isEmpty()){

                    usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String myEmail =  dataSnapshot.child(mAuth.getUid()).child(Constants.EMAIL).getValue().toString();
                            String myName =  dataSnapshot.child(mAuth.getUid()).child(Constants.NAME).getValue().toString();
                            Long myBirthDay = (Long) dataSnapshot.child(mAuth.getUid()).child(Constants.BIRTH_DAY).getValue();
                            String myPic =  dataSnapshot.child(mAuth.getUid()).child(Constants.PROFILE_PIC_URL).getValue().toString();
                            String mySex =  dataSnapshot.child(mAuth.getUid()).child(Constants.SEX).getValue().toString();

                         //String fotoPerfilURL, String nombre, String correo, long fechaDeNacimiento, String genero
                            User u;
                            User me = new User(myPic, myName, myEmail, myBirthDay, mySex);

                            for(DataSnapshot ds  : dataSnapshot.getChildren()){
                                u = ds.getValue(User.class);

                                //Agrego al usuario nuevo
                                if(u.getEmail().equals(wantedEmail)){

                                    database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" +  ds.getKey()).setValue(u);
                                    database.getReference("Usuarios/" + ds.getKey() + "/Amigos/" +  mAuth.getUid()).setValue(me);



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
                    Toast.makeText(SeeUsersActivity.this, "Debe ingresar un mail valido", Toast.LENGTH_SHORT).show();
            }
        });



//ONDA, AL AGREGAR UN USUARIO, COMO QUE SE RESETEAN LOS IDS DE LOS BOTONES DE ELIMINAR
        //POR LO TANTO, HACER QUE COINCIDAN NO DEBERIA SER MUY RARO NO?
        //DENTRO DEL BINDHOLDERNOSEQUEVERGA HACE UN .setId(ActualPosition);

//MAGIA DE KEVIN

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS_NODE).child(mAuth.getUid()).child("Amigos");


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();






        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_user, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserViewHolder holder, int position, final User model) {
                Glide.with(SeeUsersActivity.this).load(model.getProfilePicURL()).into(holder.getCivProfilePic());
                holder.getTxtUserName().setText(model.getName());

               // String lastMsg = getLastMsg(mAuth.getUid(), getUidByEmail(model.getCorreo()));
                // holder.getLastMessage().setText(lastMsg);

                DatabaseReference ref = database.getReference();
                ref.addValueEventListener(new ValueEventListener() {
                    //Al ser addValueEventListener, como que no es una vez, sino que lo hace todo el tiempo
                    //De esta forma, al mandar un mensaje nuevo, aparece la estrellita instantaneamente
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(Constants.USERS_NODE).child(mAuth.getUid()).child("MensajesNuevos").child(model.getEmail().replace(".", ",")).getValue() != null) {
                                // The child does exist
                                holder.getCircle().setVisibility(View.VISIBLE);
                                System.out.println(snapshot.getValue().toString());
                            }else {


                                holder.getCircle().setVisibility(View.GONE);
                            }

                           // snapshot.child("Mensajes").child(mAuth.getUid()).child()


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                final LUser lUser = new LUser(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getPrincipalLayout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SeeUsersActivity.this, MessengerActivity.class);
                        intent.putExtra("key_receptor", lUser.getKey());

                        database.getReference("Usuarios/" + mAuth.getUid() + "/MensajesNuevos/" + model.getEmail().replace(".", ",")).removeValue();

                        startActivity(intent);
                    }
                });

            }
        };
        rvUsers.setAdapter(adapter);


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
            deleteUser(position);



            //rvUsuarios.removeViewAt(position);
            adapter.notifyDataSetChanged();


        }
    };





    private void deleteUser(final int id){

        database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uidFriendToDelete;
                String myName = dataSnapshot.child(Constants.USERS_NODE).child(mAuth.getUid()).child(Constants.NAME).getValue().toString(); //conseguimos el nombre

                int i = 0;
                for(DataSnapshot ds : dataSnapshot.child(Constants.USERS_NODE).child(mAuth.getUid()).child("Amigos").getChildren()){
                    if(i == id){
                        uidFriendToDelete = ds.getKey();
                        //aca eliminamos al usuario
                        database.getReference("Usuarios/" + mAuth.getUid() + "/Amigos/" + uidFriendToDelete).removeValue();

                        //aca eliminamos NUESTRO chat con dicho usuario, PERO no eliminamos SU chat con nosotros, eso que lo haga el si quiere
                        //Vamos a mensajes/miUID/uidAEliminar
                        //ya que en firebase se guarda:
                        //Mensajes/idUsuario1/idUsuario2/nodos con mensajes
                        //idem con todos.

                        System.out.println((dataSnapshot.child(Constants.USERS_NODE).child(uidFriendToDelete).child("Amigos").hasChild(mAuth.getUid())));
                        //DEBERIAMOS VER SI EL OTRO AUN ME TIENE COMO AMIGO, SI NO ME TIENE, BORRO TODOO
                        if(!dataSnapshot.child(Constants.USERS_NODE).child(uidFriendToDelete).child("Amigos").hasChild(mAuth.getUid())){
                            //no me tiene, borro todoo lo que haya de chat entre nosotros por ambas partes
                            //como lo mio lo voy a borrar siempre, solo borro lo que se le haya quedado a el:
                            database.getReference("Mensajes/" + uidFriendToDelete + "/" + mAuth.getUid()).removeValue();

                            //debo eliminar mi ultimo mensaje nuevo que le habia mandado a la otra persona cuando ya me habia eliminado
                            database.getReference("Usuarios/" + uidFriendToDelete + "/MensajesNuevos/" + mAuth.getCurrentUser().getEmail().replace(".", ",")).removeValue();
                                                                                //no entiendo esos warnings, ondaa, getEmail no podria dar null en este caso.
                        }


                        //SI ME TIENE, SOLO BORRO LO MIO
                        database.getReference("Mensajes/" + mAuth.getUid() + "/" + uidFriendToDelete).removeValue();

                        //String amigoUid = uidFriendToDelete;
                       // System.out.println(amigoUid);

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
