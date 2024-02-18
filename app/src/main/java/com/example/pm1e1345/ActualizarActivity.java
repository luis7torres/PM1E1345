package com.example.pm1e1345;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import Configuracion.SQLiteConexion;
import Configuracion.Transacciones;

public class ActualizarActivity extends AppCompatActivity {

    EditText nombre, telefono, nota;
    Spinner comboPais;
    ImageView imageView;

    Button btn_actualizarContacto2, btn_Salvados;

    String id;

    byte[] Foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        nombre = (EditText) findViewById(R.id.txtNombreAc);
        telefono = (EditText) findViewById(R.id.txtTelefonoAc);
        nota = (EditText) findViewById(R.id.txtNotaAc);
        comboPais = (Spinner) findViewById(R.id.idSpinnerAc);
        imageView = (ImageView) findViewById(R.id.imageView2);
        btn_actualizarContacto2 = (Button) findViewById(R.id.btnActualizarContactos);
        btn_Salvados = (Button) findViewById(R.id.btnLosSalvados);

        //Llamamos al metodo traerDatos
        Bundle recibir_id = getIntent().getExtras();
        id = recibir_id.getString("id_cont");
        traerDatos();

        //Evento del boton Actualizar
        btn_actualizarContacto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActualizarContacto();
            }
        });


    }

    //Metodo para llenar los campos con los datos del contacto seleccionado
    private void traerDatos()
    {
        SQLiteConexion conexion = new SQLiteConexion(ActualizarActivity.this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] params = {id};
        String[] fields = {Transacciones.nombre, Transacciones.telefono, Transacciones.nota, Transacciones.imagen};
        String WhereCondition = Transacciones.id + "=?";
        try {
            Cursor cdata = db.query(Transacciones.TableContactos, fields, WhereCondition, params, null,null,null);
            cdata.moveToFirst();
            nombre.setText(cdata.getString(0));
            telefono.setText(cdata.getString(1));
            nota.setText(cdata.getString(2));
            Foto = cdata.getBlob(3);
            Bitmap bmpNew = BitmapFactory.decodeByteArray(Foto, 0, Foto.length);
            imageView.setImageBitmap(bmpNew);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error, no se encontro el contacto.", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para actualizar el contacto
    private void ActualizarContacto()
    {
        SQLiteConexion conexion = new SQLiteConexion(ActualizarActivity.this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] params = {id};
        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombre, nombre.getText().toString());
        valores.put(Transacciones.telefono, telefono.getText().toString());
        valores.put(Transacciones.nota, nota.getText().toString());

        db.update(Transacciones.TableContactos, valores, Transacciones.id+"=?", params);
        Toast.makeText(this, "Se actualizo correctamente.", Toast.LENGTH_SHORT).show();
        db.close();

        Intent intent = new Intent(getApplicationContext(), SalvadosActivity.class);
        startActivity(intent);
    }
}