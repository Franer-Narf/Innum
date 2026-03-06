package nc.instrumentum.innum;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBaseManager extends SQLiteOpenHelper {
    public DataBaseManager(@Nullable Context context) {
        super(context, "productslist", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, object TEXT, cuantity INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean setProducts(String prdct, int cntt){

        SQLiteDatabase db = this.getReadableDatabase();

        if (!existProducts(prdct)) {

            db.execSQL("INSERT INTO products (object, cuantity) VALUES (?, ?)", new Object[]{prdct,cntt});
            return true;
        }
        return false;
    }

    public ArrayList<Product> getProducts () {
        ArrayList<Product> prdcts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products", null);
        try {
            if (cur != null) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    prdcts.add(new Product(cur.getInt(0), cur.getString(1), cur.getInt(2)));
                    cur.moveToNext();
                }
            }
            return prdcts;
        } finally {
            cur.close();
        }
    }

    public boolean existProducts (String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE object = '" + name + "'", null);
        try{
            if (cur != null) {
                cur.moveToLast();
                if (cur.getCount() > 0) {
                    return true;
                }
         }
        return false;
        } finally {
            cur.close();
        }
    }

    public boolean deleteProducts (int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM Products WHERE id="+id);
        if (existIdProducts(id)) {
            return false;
        }
        return true;
    }

    public boolean deleteAllProducts () {

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.execSQL("DELETE FROM Products");
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='Products'");
            return true;
        } finally {

        }
    }

    public boolean existIdProducts (int name) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE object = '"+ name +"'", null);
        try {
            if (cur != null) {
                cur.moveToLast();
                if (cur.getCount() > 0) {
                    return true;
                }
            }
            return false;
        } finally {
            cur.close();
        }
    }
}
