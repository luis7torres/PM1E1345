package com.example.pm1e1345;

import static Configuracion.Transacciones.nombre;
import static Configuracion.Transacciones.nota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import Configuracion.SQLiteConexion;
import Configuracion.Transacciones;
import Models.Contactos;

public class SalvadosActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Contactos> listUser;
    static EditText id;
    ArrayList<String> ArregloUser;

    Button btnAtras, btnimg, btneliminar, btnactualizar, btncompartir;
    private String telefono;

    String idp = "0";
    private static final int REQUEST_CALL = 1;
    private boolean Selected = false;
    Contactos contacto2;
    int position2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvados);

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        listView = findViewById(R.id.listUsuario);
        id = (EditText) findViewById(R.id.txtcid);
        btnAtras = (Button) findViewById(R.id.btnAtras);
        btneliminar = (Button) findViewById(R.id.btneliminar);
        btnimg = (Button) findViewById(R.id.btnVerimg);
        btnactualizar = (Button) findViewById(R.id.btnActualizar);
        btncompartir = (Button) findViewById(R.id.btnCompartir);

        //Evento para el boton Atras
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        GetContactos();

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, ArregloUser);
        listView.setAdapter(adp);


        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> sele1, View selec2, int position, long select3) {

                telefono =""+listUser.get(position).getTelefono();
                Selected = true;

                //Evento del boton Eliminar Contacto
                btneliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteConexion conexion = new SQLiteConexion(SalvadosActivity.this, Transacciones.DBName, null, 1);
                        SQLiteDatabase db = conexion.getWritableDatabase();
                        String sql = "DELETE FROM contactos WHERE id=" + listUser.get(position).getId();
                        db.execSQL(sql);
                        Intent i = new Intent(SalvadosActivity.this, SalvadosActivity.class);
                        startActivity(i);
                        finish();
                    }

                });

                //Evento del boton Actualizar Contacto
                btnactualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //actualizarContacto
                        Intent intent = new Intent(getApplicationContext(), ActualizarActivity.class);
                        startActivity(intent);
                    }
                });


                btnimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("id", listUser.get(position).getId());
                        startActivity(intent);
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder builder= new AlertDialog.Builder(SalvadosActivity.this);
                        builder.setMessage("¿Quiere hacer una llamada?");
                        builder.setTitle("Accion");

                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mostrarnumero();

                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(SalvadosActivity.this,"LLamada no realizada", Toast.LENGTH_LONG).show();

                            }
                        });

                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }
                });



                //Evento para compartir el contacto en redes sociales
                btncompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, listUser.get(position).getId() + ": " + listUser.get(position).getTelefono());
                        share.putExtra(Intent.EXTRA_TEXT, listUser.get(position).getTelefono());
                        startActivity(Intent.createChooser(share, "COMPARTIR"));
                    }
                });
            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }//FIN DEL onCreate

    //Metodo para actualizar contactos
    /*public void actualizarContacto(Contactos contacto, int position) {
        SQLiteConexion conexion = new SQLiteConexion(SalvadosActivity.this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", contacto.getNombre());
        values.put("telefono", contacto.getTelefono());
        values.put("nota", contacto.getNota());

        String whereClause = "id=?";
        String[] whereArgs = new String[]{String.valueOf(contacto.getId())};

        int rowsUpdated = db.update("contactos", values, whereClause, whereArgs);

        if (rowsUpdated > 0) {
            Intent i = new Intent(SalvadosActivity.this, SalvadosActivity.class);
            startActivity(i);
            finish();
        }
    }*/

    private void GetContactos() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBName, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contact;
        listUser = new ArrayList<Contactos>();

        Cursor cursor = db.rawQuery(Transacciones.SelectAllPersonas, null);
        while (cursor.moveToNext()) {
            contact = new Contactos();
            contact.setId(cursor.getInt(0));
            contact.setPais(cursor.getString(1));
            contact.setNombre(cursor.getString(2));
            contact.setTelefono(String.valueOf(cursor.getInt(3)));
            contact.setNota(cursor.getString(4));
            listUser.add(contact);
        }

        cursor.close();
        FillList();
    }

    private void FillList() {

        ArregloUser = new ArrayList<String>();

        for (int i = 0;  i < listUser.size(); i++){

            ArregloUser.add(listUser.get(i).getId() + " | "
                    +listUser.get(i).getNombre() + " | "
                    +listUser.get(i).getTelefono());

        }
    }

    private void mostrarnumero() {
        String numero = telefono;
        if (Selected) {
            if (ContextCompat.checkSelfPermission(SalvadosActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SalvadosActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String n = "tel:" + numero;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(n)));
            }
        } else {
            Toast.makeText(SalvadosActivity.this, "Seleccione Un Contacto", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarnumero();
            } else {
                Toast.makeText(this, "NO TIENE ACCESO", Toast.LENGTH_SHORT).show();
            }
        }
    }


}