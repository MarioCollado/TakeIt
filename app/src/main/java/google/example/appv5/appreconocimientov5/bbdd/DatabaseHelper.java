package google.example.appv5.appreconocimientov5.bbdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DatabaseOptions.DB_NAME, null, DatabaseOptions.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table
        db.execSQL(DatabaseOptions.CREATE_USERS_TABLE_);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseOptions.USERS_TABLE);
        // Create tables again
        onCreate(db);
    }

    public User queryUser(String name, String password) {

        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(DatabaseOptions.USERS_TABLE,
                new String[]{DatabaseOptions.ID,
                             DatabaseOptions.USERNAME,
                             DatabaseOptions.PASSWORD},
                        /*DatabaseOptions.EMAIL + "=? and "*/
                  DatabaseOptions.USERNAME + "=? and "
                        + DatabaseOptions.PASSWORD + "=?",
                new String[]{name, password}, null, null, null, "1");
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            user = new User(cursor.getString(1), name,password);
        }

        return user;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseOptions.EMAIL, user.getEmail());
        values.put(DatabaseOptions.USERNAME, user.getEmail());
        values.put(DatabaseOptions.PASSWORD, user.getPassword());

        db.insert(DatabaseOptions.USERS_TABLE, null, values);
        db.close();

    }

}
