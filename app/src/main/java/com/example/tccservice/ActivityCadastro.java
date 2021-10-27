package com.example.tccservice;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tccservice.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ActivityCadastro extends AppCompatActivity{

    private EditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmailCadastro);
        campoSenha = findViewById(R.id.editSenhaCadastro);

    }

    public void cadastrarUsuario(Usuario usuario){

        autenticar = ConfiguracaoFirebade.getFirebaseAutenticcao();
        autenticar.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(ActivityCadastro.this,"Usuario cadastrado com sucesso!",Toast.LENGTH_SHORT).show();
                    finish();

                }else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Senha muito fraca";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail invalido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Conta já cadastrada";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(ActivityCadastro.this,excecao,Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void validarUsuario (View view){

        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoNome.isEmpty()){
            if (!textoEmail.isEmpty()){
                if (!textoSenha.isEmpty()){

                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario(usuario);

                }else {
                    Toast.makeText(ActivityCadastro.this,"Preencha a Senha",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(ActivityCadastro.this,"Preencha o E-mail",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(ActivityCadastro.this, "Preencha o nome",Toast.LENGTH_SHORT).show();
        }
    }
}