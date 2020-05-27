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
import preusche.santi.com.firebasechat.Adaptadores.MensajeriaAdaptador;
import preusche.santi.com.firebasechat.Entidades.Firebase.Mensaje;
import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LMensaje;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Persistencia.MensajeriaDAO;
import preusche.santi.com.firebasechat.Persistencia.UsuarioDAO;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constantes;

public class MensajeriaActivity extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private MensajeriaAdaptador adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;

    private FirebaseAuth mAuth;
    private String NOMBRE_USUARIO;

    private String KEY_RECEPTOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria);

        setTitle("Chat");

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            KEY_RECEPTOR = bundle.getString("key_receptor");
        }else{
            finish();
        }

        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new MensajeriaAdaptador(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        System.out.println(KEY_RECEPTOR);

//        nombre.setText("Teemo");
//        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/kevin-7ad2a.appspot.com/o/Fotos%2FFotoPerfil%2FYxiSpyoaRlcfrJcAeZ3b3MRDR463%2F776.36-20-04-22-05-2020?alt=media&token=475b9fcb-229e-446c-b537-f25a40a995d0").into(fotoPerfil);

        FirebaseDatabase.getInstance().getReference(Constantes.NODO_USUARIOS + "/" + KEY_RECEPTOR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario u = dataSnapshot.getValue(Usuario.class);
                nombre.setText(u.getNombre());
                Glide.with(MensajeriaActivity.this).load(u.getFotoPerfilURL()).into(fotoPerfil);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensajeEnviar = txtMensaje.getText().toString();
                if(!mensajeEnviar.isEmpty()){
                    Mensaje mensaje = new Mensaje();
                    mensaje.setMensaje(mensajeEnviar);
                    mensaje.setContieneFoto(false);
                    mensaje.setKeyEmisor(UsuarioDAO.getInstancia().getKeyUsuario());
                    MensajeriaDAO.getInstancia().nuevoMensaje(UsuarioDAO.getInstancia().getKeyUsuario(),KEY_RECEPTOR,mensaje);

                    FirebaseDatabase.getInstance().getReference("Usuarios/" + KEY_RECEPTOR + "/MensajesNuevos/" + mAuth.getCurrentUser().getEmail().replace(".",",")).setValue(mensajeEnviar);


                    txtMensaje.setText("");
                }
            }
        });


        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        FirebaseDatabase.
                getInstance().
                getReference(Constantes.NODO_MENSAJES).
                child(UsuarioDAO.getInstancia().getKeyUsuario()).
                child(KEY_RECEPTOR).addChildEventListener(new ChildEventListener() {

            //traer la informacion del usuario
            //guardamos la informacion del usuario en una lista temporal
            //obtenemos la informacion guardada  por la llave
            Map<String, LUsuario> mapUsuariosTemporales = new HashMap<>();

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);
                final LMensaje lMensaje = new LMensaje(dataSnapshot.getKey(),mensaje);
                final int posicion = adapter.addMensaje(lMensaje);

                if(mapUsuariosTemporales.get(mensaje.getKeyEmisor())!=null){
                    lMensaje.setlUsuario(mapUsuariosTemporales.get(mensaje.getKeyEmisor()));
                    adapter.actualizarMensaje(posicion,lMensaje);
                }else{
                    UsuarioDAO.getInstancia().obtenerInformacionDeUsuarioPorLLave(mensaje.getKeyEmisor(), new UsuarioDAO.IDevolverUsuario() {
                        @Override
                        public void devolverUsuario(LUsuario lUsuario) {
                            mapUsuariosTemporales.put(mensaje.getKeyEmisor(),lUsuario);
                            lMensaje.setlUsuario(lUsuario);
                            adapter.actualizarMensaje(posicion,lMensaje);
                        }

                        @Override
                        public void devolverError(String error) {
                            Toast.makeText(MensajeriaActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
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
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
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
                        Mensaje mensaje = new Mensaje();
                        mensaje.setMensaje("El uuario ha enviado una foto");
                        mensaje.setUrlFoto(uri.toString());
                        mensaje.setContieneFoto(true);
                        mensaje.setKeyEmisor(UsuarioDAO.getInstancia().getKeyUsuario());
                        MensajeriaDAO.getInstancia().nuevoMensaje(UsuarioDAO.getInstancia().getKeyUsuario(),KEY_RECEPTOR,mensaje);
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
