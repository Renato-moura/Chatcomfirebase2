package com.fatecipg.chatcomfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.annotation.Nullable;

public class NovoUsuarioActivity extends AppCompatActivity {
    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private FirebaseAuth mAuth;
    private ImageView pictureImageView;
    private StorageReference pictureStorageReference;

    private static final int REC_CODE_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novousuario);
        loginNovoUsuarioEditText = findViewById(R.id. loginNovoUsuarioEditText );
        senhaNovoUsuarioEditText = findViewById(R.id. senhaNovoUsuarioEditText );
        pictureImageView = findViewById(R.id.pictureImageView );
        mAuth = FirebaseAuth. getInstance ();
    }

    public void criarNovoUsuario(View view) {
        String login = loginNovoUsuarioEditText.getText().toString();
        String senha = senhaNovoUsuarioEditText.getText().toString();
        Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(
                login,
                senha
        );
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(NovoUsuarioActivity.this,
                        getString(android.R.string.ok),
                        Toast.LENGTH_SHORT).show();
                finish(); // voltar do usuario
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace(); //mensagem de erro depois
            }
        });
    }

    private  void uploadPicture(Bitmap picture){
        pictureStorageReference =
                FirebaseStorage.getInstance().getReference(
                        String.format(
                                Locale.getDefault(),
                                "imagens/%s/profilePic.jpg",
                                loginNovoUsuarioEditText.getText().toString().replace("@","")
                        )
                );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();
        //upload da foto
        pictureStorageReference.putBytes(bytes);
    }
    public void tirarFoto(View view) {
        if (loginNovoUsuarioEditText.getText() != null&&
                !loginNovoUsuarioEditText.getText().toString().isEmpty()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REC_CODE_CAMERA);
             }else{
                Toast.makeText(this,getString(R.string.cant_take_pic), Toast.LENGTH_SHORT).show();
            }
        }else{
        Toast.makeText(
                this,
                getString(R.string.email_obrigatorio),
                Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        if(requestCode == REC_CODE_CAMERA){
            if(resultCode == Activity.RESULT_OK){
                Bitmap picture = (Bitmap)
                        data.getExtras().get("data");
                uploadPicture(picture);
                pictureImageView.setImageBitmap(picture);
            }else {
                Toast.makeText(this,getString(R.string.no_pic_taken), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
