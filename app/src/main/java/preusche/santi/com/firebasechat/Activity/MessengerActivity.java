package preusche.santi.com.firebasechat.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import preusche.santi.com.firebasechat.Adaptadores.MessengerAdapter;
import preusche.santi.com.firebasechat.Entidades.Firebase.Message;
import preusche.santi.com.firebasechat.Entidades.Firebase.User;
import preusche.santi.com.firebasechat.Entidades.Logica.LMessage;
import preusche.santi.com.firebasechat.Entidades.Logica.LUser;
import preusche.santi.com.firebasechat.Persistencia.MessengerDAO;
import preusche.santi.com.firebasechat.Persistencia.UserDAO;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constants;

public class MessengerActivity extends AppCompatActivity {

    //View
    private CircleImageView profilePic;
    private TextView name;
    private RecyclerView rvMessages;

    //Text
    private EditText txtMessage;
    private ImageButton btnSendPic;

    //Button
    private Button btnSend;

    //Adapter
    private MessengerAdapter adapter;

    //Firebase
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    //Storage
    private StorageReference storageReference;

    //Constant
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;


    //String
    private String profilePicString;
    private String USER_NAME;
    private String KEY_RECEPTOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        setTitle("Chat");

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            KEY_RECEPTOR = bundle.getString("key_receptor");
        }else{
            finish();
        }

        profilePic = (CircleImageView) findViewById(R.id.profilePic);
        name = (TextView) findViewById(R.id.name);
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSendPic = (ImageButton) findViewById(R.id.btnSendPhoto);
        profilePicString = "";

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new MessengerAdapter(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(l);
        rvMessages.setAdapter(adapter);

        System.out.println(KEY_RECEPTOR);

        FirebaseDatabase.getInstance().getReference(Constants.USERS_NODE + "/" + KEY_RECEPTOR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                name.setText(u.getName());
                Glide.with(MessengerActivity.this).load(u.getProfilePicURL()).into(profilePic);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageToSend = txtMessage.getText().toString();
                if(!messageToSend.isEmpty()){
                    Message message = new Message();
                    message.setMessage(messageToSend);
                    message.setContainsPhoto(false);
                    message.setKeyEmisor(UserDAO.getInstance().getUserKey());
                    MessengerDAO.getInstance().newMessage(UserDAO.getInstance().getUserKey(), KEY_RECEPTOR, message);

                    FirebaseDatabase.getInstance().getReference("Usuarios/" + KEY_RECEPTOR + "/MensajesNuevos/" + mAuth.getCurrentUser().getEmail().replace(".",",")).setValue(messageToSend);


                    txtMessage.setText("");
                }
            }
        });


        btnSendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
            }
        });

        /*ESTO ES DE KEVIN PERO ANDA MAL
        //PORQUE LO QUE HACE ES QUE ME DEJA "CAMBIAR LA FOTO DE LOS DEMAS", PERO NO FUNCA, NO LO VEO NECESARIO
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MensajeriaActivity.this, "FOTOO", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
            }
        });

         */

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        FirebaseDatabase.
                getInstance().
                getReference(Constants.MESSAGES_NODE).
                child(UserDAO.getInstance().getUserKey()).
                child(KEY_RECEPTOR).addChildEventListener(new ChildEventListener() {

            //traer la informacion del usuario
            //guardamos la informacion del usuario en una lista temporal
            //obtenemos la informacion guardada  por la llave
            Map<String, LUser> mapTemporalUsers = new HashMap<>();

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Message message = dataSnapshot.getValue(Message.class);
                final LMessage lMessage = new LMessage(dataSnapshot.getKey(),message);
                final int position = adapter.addMessage(lMessage);

                if(mapTemporalUsers.get(message.getKeyEmisor()) != null){
                    lMessage.setlUser(mapTemporalUsers.get(message.getKeyEmisor()));
                    adapter.updateMessage(position,lMessage);
                }else{
                    UserDAO.getInstance().getUserInformationFromKey(message.getKeyEmisor(), new UserDAO.IReturnUser() {
                        @Override
                        public void returnUser(LUser lUser) {
                            mapTemporalUsers.put(message.getKeyEmisor(), lUser);
                            lMessage.setlUser(lUser);
                            adapter.updateMessage(position,lMessage);
                        }

                        @Override
                        public void returnError(String error) {
                            Toast.makeText(MessengerActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        verifyStoragePermissions(this);

    }

    private void setScrollbar(){
        rvMessages.scrollToPosition(adapter.getItemCount()-1);
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        Message message = new Message();
                        message.setMessage("Image");
                        message.setUrlPic(uri.toString());
                        message.setContainsPhoto(true);
                        message.setKeyEmisor(UserDAO.getInstance().getUserKey());
                        MessengerDAO.getInstance().newMessage(UserDAO.getInstance().getUserKey(),KEY_RECEPTOR,message);
                    }
                }
            });
        }/*else if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        fotoPerfilCadena = uri.toString();
                        MensajeEnviar m = new MensajeEnviar(NOMBRE_USUARIO+" ha actualizado su foto de perfil",uri.toString(),NOMBRE_USUARIO,fotoPerfilCadena,"2",ServerValue.TIMESTAMP);
                        databaseReference.push().setValue(m);
                        Glide.with(MensajeriaActivity.this).load(uri.toString()).into(fotoPerfil);
                    }
                }
            });
        }*/
    }

}
