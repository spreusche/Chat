package preusche.santi.com.firebasechat.Persistencia;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import preusche.santi.com.firebasechat.Entidades.Firebase.Usuario;
import preusche.santi.com.firebasechat.Entidades.Logica.LUsuario;
import preusche.santi.com.firebasechat.Utilidades.Constantes;

/**
 * Created by user on 28/08/2018. 28
 */
public class UsuarioDAO {

    public interface IDevolverUsuario{
        public void devolverUsuario(LUsuario lUsuario);
        public void devolverError(String error);
    }

    public interface IDevolverUrlFoto{
        public void devolerUrlString(String url);
    }

    private static UsuarioDAO usuarioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceUsuarios;
    private StorageReference referenceFotoDePerfil;

    public static UsuarioDAO getInstancia(){
        if(usuarioDAO==null) usuarioDAO = new UsuarioDAO();
        return usuarioDAO;
    }

    private UsuarioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceUsuarios = database.getReference(Constantes.NODO_USUARIOS);
        referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/"+getKeyUsuario());
    }

    public String getKeyUsuario(){
        return FirebaseAuth.getInstance().getUid();
    }

    public boolean isUsuarioLogeado(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser!=null;
    }

    public long fechaDeCreacionLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp();
    }

    public long fechaDeUltimaVezQueSeLogeoLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
    }

    public void obtenerInformacionDeUsuarioPorLLave(final String key, final IDevolverUsuario iDevolverUsuario){
        referenceUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(key,usuario);
                iDevolverUsuario.devolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iDevolverUsuario.devolverError(databaseError.getMessage());
            }
        });

    }
    //Antes deberia validar que el nuevo contacto no sea ya un contacto, sino: quilombo
    //y si no existe dicho email esta funcion te avisa con un Toast
    //Deberia agregarle el contacto al usuario que la llama, y a quien lo recibe.
    //es decir: al pet owner y a la vet.

    /**
     * a ver ideas:
     * actualiza la lista de quien agrega contacto: listo, usando setContactos y currentUser y eso.
     * actualiza la lista de la persona agregada: similar al añadeFotoDefecto o como se llame el metodo de abajo
     *                                            osea, voy por los usuarios de la BD, encuentro el que tiene el
     *                                            parametro email enviado, y le meto el email de la persona que lo agrego.
     *
     * @param email
     */

    private String emailLlamador; //Feo pero es una solucion i believe
    private String emailBuscado;

    public void agregarContacto(String email){
        emailBuscado = email;

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            emailLlamador = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            System.out.println("Agregar contacto 1er if entro");
        }
        else{
            System.out.println("Agregar contacto 1er if no entro");
           return;
        }
        referenceUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LUsuario> lUsuarioList = new ArrayList<>();
                String UidBuscado = "";

                System.out.println("Agregar contacto antes de crear la lista de lUsuarios");


                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    Usuario usuario = childDataSnapShot.getValue(Usuario.class);
                    LUsuario lUsuario = new LUsuario(childDataSnapShot.getKey(),usuario);
                    lUsuarioList.add(lUsuario);
                }

                System.out.println("Agregar contacto: termino de crear la lista");
                System.out.println("Agregar contacto: arranca a buscar el emailBuscado");
                for(LUsuario lUsuario : lUsuarioList){
                    if(lUsuario.getUsuario().getCorreo().equals(emailBuscado)){

                        System.out.println("Agregar contacto: encontro el email buscado");

                        //me fijo si ya lo tiene de contacto, si es asi, retorno
                        //si este lo tiene de contacto, entonces el otro tambien
                        if(lUsuario.getUsuario().getContacts().contains(getKeyUsuario())){
                            System.out.println("YA EXISTIA EL USUARIO");
                            return;
                        }

                        List<String> contactsActualizadaDelBuscado = lUsuario.getUsuario().getContacts();

                        System.out.println("Agregar contacto: pone la lista vieja");

                        //Agregamos a la lista del usuario buscado, el uid de quien lo busco
                        contactsActualizadaDelBuscado.add(getKeyUsuario());

                        System.out.println("Agregar contacto: agrega el nuevo elemento a contactsActualizada");

                        lUsuario.getUsuario().setContacts(contactsActualizadaDelBuscado);

                        System.out.println("Agregar contacto: mete la lista en el usuario");


                        referenceUsuarios.child(lUsuario.getKey()).child(Constantes.NODO_CONTACTS).setValue(contactsActualizadaDelBuscado);

                        System.out.println("Agregar contacto: la pone en el nodo con referenceUsuarios");


                        UidBuscado = lUsuario.getKey();

                        System.out.println("Agregar contacto: obtuve el UidBuscado: " + UidBuscado);


                    }
                }

                System.out.println("Agregar contacto: estoy por arrancar el for ineficiente");


                //Si modifique el UidBuscado, entonces es pq SI se encontro
                    if(!UidBuscado.equals("")){

                        System.out.println("Agregar contacto: compare el Uid con vacio y arranco el for");


                        //Y ACA SE VIENE LA INEFICIENCIA MAXIMA
                        //Pasa que nose como acceder a la informacion de mi Usuario actual
                        for(LUsuario lUsuario : lUsuarioList){
                            if(lUsuario.getUsuario().getCorreo().equals(emailLlamador)){

                                System.out.println("Agregar contacto: encontre el emailLlamador");


                                List<String> contactsActualizadaDelUsuario = lUsuario.getUsuario().getContacts();

                                System.out.println("Agregar contacto: hago la nueva lista");


                                contactsActualizadaDelUsuario.add(UidBuscado);

                                System.out.println("Agregar contacto: le agrego el UidBuscado a la nueva lista");

                                lUsuario.getUsuario().setContacts(contactsActualizadaDelUsuario);

                                System.out.println("Agregar contacto:seteo los nuevo contactos con la nueva lista");


                                referenceUsuarios.child(lUsuario.getKey()).child(Constantes.NODO_CONTACTS).setValue(contactsActualizadaDelUsuario);

                                System.out.println("Agregar contacto: agregue al nodo la nueva lista y termine");



                                return; //porque no quiero bubscar mas y ya termine de agregar todoo
                            }
                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void añadirFotoDePerfilALosUsuariosQueNoTienenFoto(){
        referenceUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override   //Aca tenemos todos los usuarios que estan registrados en nuestra aplicacion
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LUsuario> lUsuariosLista = new ArrayList<>();
                //Aca creo una lista con TODOS los usuarios de la BD
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    Usuario usuario = childDataSnapShot.getValue(Usuario.class);
                    LUsuario lUsuario = new LUsuario(childDataSnapShot.getKey(),usuario);
                    lUsuariosLista.add(lUsuario);
                }
                //Aca voy user por user, y me fijo si tiene foto o no y se las agrego en dicho caso
                for(LUsuario lUsuario : lUsuariosLista){
                    if(lUsuario.getUsuario().getFotoPerfilURL()==null){
                        referenceUsuarios.child(lUsuario.getKey()).child("fotoPerfilURL").setValue(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void subirFotoUri(Uri uri, final IDevolverUrlFoto iDevolverUrlFoto){
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        final StorageReference fotoReferencia = referenceFotoDePerfil.child(nombreFoto);
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    iDevolverUrlFoto.devolerUrlString(uri.toString());
                }
            }
        });
    }

}
