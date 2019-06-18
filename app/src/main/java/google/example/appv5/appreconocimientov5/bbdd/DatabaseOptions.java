package google.example.appv5.appreconocimientov5.bbdd;

/**
 * Created by Zoom on 10/1/2018.
 */

public class DatabaseOptions {

    public static final String DB_NAME = "local.db";
    public static final int DB_VERSION = 1;

    public static final String USERS_TABLE = "users";

    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String CREATE_USERS_TABLE_ =
            "CREATE TABLE  " + USERS_TABLE + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    USERNAME + " TEXT NOT NULL," +
                    EMAIL + " TEXT NOT NULL," +
                    PASSWORD + " TEXT );";

}
