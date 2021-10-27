package com.example.tccservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import me.abhinay.input.CurrencyEditText;

public class ActivityCadastrarAnuncio extends AppCompatActivity implements View.OnClickListener {



    private EditText campoTitulo, campoDescricao, campoTelefone;
    private CurrencyEditText campoValor;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoComponente;
    private Anuncio anuncio;
    private StorageReference storage;
    private android.app.AlertDialog dialog;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String>listaFotosRecuperacao = new ArrayList<>();
    private List<String>listaURLFotos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        storage = ConfiguracaoFirebade.getFirebaseStorage();

        Permissoes.validarPermissoes(permissoes, this, 1);


        iniciarComponentes();
        carregarSpinner();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;

        }
    }

    public void escolherImagem(int requestCode){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            Uri imagemSelecionada = data.getData();
            String caminhoimagem = imagemSelecionada.toString();

            if (requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
            }else if (requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }else if (requestCode == 3);{
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperacao.add(caminhoimagem);

        }

    }

    private void carregarSpinner(){

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,estados);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        String[] categoria = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,categoria);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoComponente.setAdapter(adapterCategoria);
    }



    private void iniciarComponentes (){
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoComponente = findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById (R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        Locale locale = new Locale("pt" ,"BR");
        campoValor.setTextLocale(locale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão Negada");
        builder.setMessage("Para acessar o app é necessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void salvarAnuncio() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anuncio")
                .setCancelable(false)
                .build();
        dialog.show();

        for (int i=0 ; i < listaFotosRecuperacao.size() ; i++) {
            String urlImagem = listaFotosRecuperacao.get(i);
            int tamanhoLista = listaFotosRecuperacao.size();

            salvarFotoStorege(urlImagem, tamanhoLista, i );

        }


    }

    private void salvarFotoStorege(String urlString, final int totalFotos, int contador) {

        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);

        String imagem1 = UUID.randomUUID().toString();
        final StorageReference imagemRef = imagemAnuncio.child(imagem1+".jpeg");



        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        //UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        String urlConvertida = firebaseUri.toString();

                        listaURLFotos.add(urlConvertida);

                        if (totalFotos == listaURLFotos.size()) {
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensagemError("Falha ao fazer upload");
                Log.i("iNFO", "Falha ao fazer upload" + e.getMessage());
            }
        });

    }

    private Anuncio configurarAnuncio(){

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoComponente.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }



    public void validarDadosAnuncio (View view){

      anuncio = configurarAnuncio();

      String valor = String.valueOf(campoValor.getText());

        if(listaFotosRecuperacao.size() != 0){
            if (!anuncio.getEstado().isEmpty()){
                if (!anuncio.getCategoria().isEmpty()){
                    if (!anuncio.getTitulo().isEmpty()){
                        if (!valor.isEmpty() && !anuncio.getValor().equals("0")){
                            if (!anuncio.getTelefone().isEmpty()){
                                if (!anuncio.getDescricao().isEmpty()){

                                    salvarAnuncio();

                                }else {
                                    mensagemError("Preencha a descricao");
                                }
                            }else {
                                mensagemError("Preencha o telefone");
                            }
                        }else {
                            mensagemError("Preencha o valor");
                        }
                    }else{
                        mensagemError("Preencha o titulo");
                    }
                }else{
                    mensagemError("Selecione o serviço");
                }
            }else {
                mensagemError("Selecione o campo estado");
            }
        }
    }

    private void mensagemError(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


}