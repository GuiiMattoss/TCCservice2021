package com.example.tccservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tccservice.ActivityCadastro;
import com.example.tccservice.ConfiguracaoFirebade;
import com.example.tccservice.R;
import com.example.tccservice.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Activitylogin extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebade.getFirebaseAutenticcao();

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

    }

    public void logarUsuario(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(),ActivityAnuncio.class));
                }else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário náo cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail ou senha invalido";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(Activitylogin.this,excecao,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarUsuario (View view){

        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            }else {
                Toast.makeText(Activitylogin.this,"Preencha a senha",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(Activitylogin.this, "Preencha o e-mail",Toast.LENGTH_SHORT).show();
        }
    }

    public  void chamarTelaPrincipal() {
        Intent intent = new Intent(Activitylogin.this, ActivityAnuncio.class);
        startActivity(intent);
    }


    public  void chamarTelaCadastro(View view) {
        Intent intent = new Intent(Activitylogin.this, ActivityCadastro.class);
        startActivity(intent);
    }
}