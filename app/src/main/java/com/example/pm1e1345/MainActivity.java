package com.example.pm1e1345;

import static Configuracion.Transacciones.nombre;
import static Configuracion.Transacciones.nota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import Configuracion.SQLiteConexion;
import Configuracion.Transacciones;

public class MainActivity extends AppCompatActivity {

    EditText nombre, nota, telefono;
    Button btn_salvarContacto, btn_contactoSalvado;

    static final int peticion_camara = 100;
    static final int  peticion_foto = 102;
    String FotoPath;
    ImageView imageView;
    Button btntakefoto;

    Spinner comboPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = (EditText) findViewById(R.id.txtNombre);
        telefono = (EditText) findViewById(R.id.txtTelefono);
        nota = (EditText) findViewById(R.id.txtNota);
        comboPais = (Spinner) findViewById(R.id.idSpinnerPais);
        imageView = (ImageView) findViewById(R.id.imageView);
        btntakefoto = (Button)  findViewById(R.id.btnFoto);
        btn_salvarContacto = (Button) findViewById(R.id.btnSalvarContacto);
        btn_contactoSalvado = (Button) findViewById(R.id.btnContactoSalvado);

        //Este es un adaptador para que cargue los datos del spinner de paises
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.combo_paises,
                android.R.layout.simple_spinner_item);

        comboPais.setAdapter(adapter);

        //Evento para el boton de Foto
        btntakefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });

        //Evento para el boton de contacto salvado
        btn_contactoSalvado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), SalvadosActivity.class);
                startActivity(intent2);
            }
        });

        //Evento de para salvar el contacto
        btn_salvarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validacionAlerta()==true){
                    salvarContacto();
                    limpiarCampos();
                }
            }
        });
    }


    //Metodo para limpiar los campos al guardarse el contacto
    private void limpiarCampos()
    {
        nombre.setText("");
        telefono.setText("");
        nota.setText("");
    }

    private void validar_alerta(String message){
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Debe escribir un "+message);
        builder.setTitle("Alerta");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private boolean validacionAlerta(){
        if(nombre.getText().toString().isEmpty()){
            validar_alerta("nombre");
            return false;
        }else{
            if(telefono.getText().toString().isEmpty()){
                validar_alerta("telefeno");
                return false;
            }else{
                if(nota.getText().toString().isEmpty()){
                    validar_alerta("nota");
                    return false;
                }else{
                    return true;
                }
            }
        }
    }



    private void salvarContacto() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();


        try {
            // Si asumimos que el imageView es nuestro ImageView con la imagen que nos hemos tomado
            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageData = outputStream.toByteArray();
            outputStream.close();

            valores.put(Transacciones.pais, String.valueOf(comboPais.getSelectedItem()));
            valores.put(Transacciones.nombre, nombre.getText().toString());
            valores.put(Transacciones.telefono, telefono.getText().toString());
            valores.put(Transacciones.nota, nota.getText().toString());
            valores.put(Transacciones.imagen, imageData);

            Long resultado = db.insert(Transacciones.TableContactos, Transacciones.id, valores);

            Toast.makeText(getApplicationContext(), "Registro Ingresado con exito " + resultado.toString(),
                    Toast.LENGTH_LONG).show();

            db.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void Permisos()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    peticion_camara);
        }
        else
        {
            tomarfoto();
        }
    }

    private void tomarfoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, peticion_foto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
    int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_camara)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                tomarfoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == peticion_foto && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imagen);
        }
    }
}