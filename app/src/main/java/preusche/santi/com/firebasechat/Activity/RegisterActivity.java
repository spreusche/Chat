package preusche.santi.com.firebasechat.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import preusche.santi.com.firebasechat.Entidades.Firebase.User;
import preusche.santi.com.firebasechat.Persistencia.UserDAO;
import preusche.santi.com.firebasechat.R;
import preusche.santi.com.firebasechat.Utilidades.Constants;

/**
 * Created by user on 19/02/2018. 19
 */

public class RegisterActivity extends AppCompatActivity {

    //View
    private CircleImageView profilePic;

    //Text
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtRepeatedPassword;
    private EditText txtBirthDate;

    //Button
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Button btnRegister;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    //ImpagePicker
    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;

    //String
    private String pickerPath;

    //Uri
    private Uri profilePicURI;

    //vars
    private long birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profilePic = findViewById(R.id.profilePic);
        txtName = findViewById(R.id.idRegisterName);
        txtEmail = findViewById(R.id.idRegisterEmail);
        txtPassword = findViewById(R.id.idRegisterPassword);
        txtRepeatedPassword = findViewById(R.id.idRegisterRepeatedPassword);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnRegister = findViewById(R.id.idRegisterRegister);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        imagePicker = new ImagePicker(this);
        cameraPicker = new CameraImagePicker(this);

        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);

        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                if(!list.isEmpty()){
                    String path = list.get(0).getOriginalPath();
                    profilePicURI = Uri.parse(path);
                    profilePic.setImageURI(profilePicURI);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(RegisterActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        cameraPicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                String path = list.get(0).getOriginalPath();
                profilePicURI = Uri.fromFile(new File(path));
                profilePic.setImageURI(profilePicURI);
            }

            @Override
            public void onError(String s) {
                Toast.makeText(RegisterActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                dialog.setTitle("Foto de perfil");

                String[] items = {"Galeria","Camara"};

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                imagePicker.pickImage();
                                break;
                            case 1:
                                pickerPath = cameraPicker.pickImage();
                                break;
                        }
                    }
                });

                AlertDialog dialogConstruido = dialog.create();
                dialogConstruido.show();

            }
        });

        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                        Calendar calendarResultado = Calendar.getInstance();
                        calendarResultado.set(Calendar.YEAR,year);
                        calendarResultado.set(Calendar.MONTH,mes);
                        calendarResultado.set(Calendar.DAY_OF_MONTH,dia);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = calendarResultado.getTime();
                        String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                        birthDate = date.getTime();
                        txtBirthDate.setText(fechaDeNacimientoTexto);
                    }
                },calendar.get(Calendar.YEAR)-18,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = txtEmail.getText().toString();
                final String name = txtName.getText().toString();
                if(isValidEmail(email) && validarContraseña() && validateName(name)){
                    String password = txtPassword.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        final String sex;

                                        if(rbMale.isChecked()){
                                            sex = "Hombre";
                                        }else{
                                            sex = "Mujer";
                                        }

                                        if(profilePicURI !=null) {

                                            UserDAO.getInstance().uploadPhotoUri(profilePicURI, new UserDAO.IReturnUrlPicture() {
                                                @Override
                                                public void returnUrlString(String url) {
                                                    Toast.makeText(RegisterActivity.this, "Se registro correctamente.", Toast.LENGTH_SHORT).show();
                                                    User user = new User();
                                                    user.setEmail(email);
                                                    user.setName(name);
                                                    user.setBirthDate(birthDate);
                                                    user.setSex(sex);
                                                    user.setProfilePicURL(url);
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    DatabaseReference reference = database.getReference("Usuarios/" + currentUser.getUid());
                                                    reference.setValue(user);
                                              //      database.getReference("Usuarios/" + currentUser.getUid() + "/Amigos").setValue(usuario);

                                                    finish();



                                                }
                                            });

                                        }else{
                                            Toast.makeText(RegisterActivity.this, "Se registro correctamente.", Toast.LENGTH_SHORT).show();
                                            User user = new User();
                                            user.setEmail(email);
                                            user.setName(name);
                                            user.setBirthDate(birthDate);
                                            user.setSex(sex);
                                            user.setProfilePicURL(Constants.DEFAULT_URI_PROFILE_PIC);
                                            FirebaseUser currentUser = mAuth.getCurrentUser();
                                            DatabaseReference reference = database.getReference("Usuarios/"+currentUser.getUid());
                                            reference.setValue(user);
                                         //   database.getReference("Usuarios/" + currentUser.getUid() + "/Amigos/" + currentUser.getUid()).setValue(usuario);
                                            finish();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Error al registrarse.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "Validaciones funcionando.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Glide.with(this).load(Constants.DEFAULT_URI_PROFILE_PIC).into(profilePic);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == RESULT_OK){
            imagePicker.submit(data);
        }else if(requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            cameraPicker.reinitialize(pickerPath);
            cameraPicker.submit(data);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validarContraseña(){
        String password, repeatedPassword;
        password = txtPassword.getText().toString();
        repeatedPassword = txtRepeatedPassword.getText().toString();
        if(password.equals(repeatedPassword)){
            if(password.length()>=6 && password.length()<=16){
                return true;
            }else return false;
        }else return false;
    }

    public boolean validateName(String name){
        return !name.isEmpty();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}
