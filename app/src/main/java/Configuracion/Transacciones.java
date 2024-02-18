package Configuracion;

public class Transacciones {


    // Nombre de la base datos
    public static final String DBName = "Exa1.db";

    // Creacion de las tablas de base de datos
    public static final String TableContactos = "contactos";

    // Creacion de los campos de base de datos
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final  String nota = "nota";
    public static final String imagen = "imagen";

    // DDL Create
    public static final String CreateTablePersonas = "Create table "+ TableContactos +" ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, nombre TEXT, telefono TEXT, nota TEXT, "+
            "imagen BLOB )";

    // DDL Drop
    public static final String DropTablePersonas = "DROP TABLE IF EXISTS "+ TableContactos;

    // DML
    public static final String SelectAllPersonas = "SELECT * FROM " + TableContactos;
}
