package nc.instrumentum.innum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBaseManager extends SQLiteOpenHelper {
    public DataBaseManager(@Nullable Context context) {
        super(context, "productslist", null, 2);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS productlist (idpl INTEGER PRIMARY KEY AUTOINCREMENT, titlelist TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, object TEXT, cuantity INTEGER, idpl INTEGER," +
                "FOREIGN KEY (idpl) REFERENCES productlist(idpl) ON DELETE CASCADE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int createList(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = null;

        try {
            if (!existList(title)) {
                db.execSQL("INSERT INTO productlist (titlelist) VALUES (?)", new Object[]{title});
                cur = db.rawQuery("SELECT idpl FROM productlist WHERE titlelist='" + title + "'", null);
                if(cur.moveToFirst()) {
                    return cur.getInt(0);
                }
            }
            return -1;
        } finally {
            if(cur!=null) {
                cur.close();
            }
        }
    }

    public int setProducts(String prdct, int cntt, int lstCd){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = null;
        try {
            if (!existProducts(prdct, lstCd)) {
                db.execSQL("INSERT INTO products (object, cuantity, idpl) VALUES (?, ?, ?)", new Object[]{prdct,cntt,lstCd});
                cur = db.rawQuery("SELECT id FROM products WHERE object='" + prdct + "' AND idpl = " + lstCd, null);
                if(cur.moveToFirst()){
                    return cur.getInt(0);
            }
        }
        return -1;
        } finally {
            if(cur!=null) {
                cur.close();
            }
        }
    }

    public ArrayList<ListClass> getLists() {
        ArrayList<ListClass> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM productlist", null);
        try {
            if (cur != null) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    lst.add(new ListClass(cur.getInt(0), cur.getString(1)));
                    cur.moveToNext();
                }
            }
            return lst;
        } finally {
            cur.close();
        }
    }

    public ArrayList<Product> getProducts (int listCode) {
        ArrayList<Product> prdcts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE idpl = " + listCode, null);
        try {
            if (cur != null) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    prdcts.add(new Product(cur.getInt(0), cur.getString(1), cur.getInt(2), cur.getInt(3)));
                    cur.moveToNext();
                }
            }
            return prdcts;
        } finally {
            cur.close();
        }
    }

    public boolean updateListTitle(int idList, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titlelist", newTitle.trim());

        int rows = db.update(
                "productlist",
                values,
                "idpl = ?",
                new String[]{String.valueOf(idList)}
        );

        return rows > 0;
    }

    public boolean updateProduct(int idProduct, int listCode, String newName, int newCuantity) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("object", newName.trim());
        values.put("cuantity", newCuantity);

        int rows = db.update(
                "products",
                values,
                "id = ? AND idpl = ?",
                new String[]{String.valueOf(idProduct), String.valueOf(listCode)}
        );

        return rows > 0;
    }

    public boolean existProducts (String name, int listCode) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE object = '" + name + "' AND idpl = " + listCode, null);
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

    public boolean existList (String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM productList WHERE titlelist = '" + name + "'", null);
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

    public boolean deleteProducts (int id, int listCode) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM products WHERE id=" + id + " AND idpl = " + listCode);
        if (existIdProducts(id, listCode)) {
            return false;
        }
        return true;
    }

    public boolean deleteAllProducts (int listCode) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM productlist WHERE idpl = " + listCode);
            return true;
        } finally {
            //Nothing.
        }
    }

    public boolean deleteAllTables () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM products");
            db.execSQL("DELETE FROM productlist");

            db.execSQL("DELETE FROM sqlite_sequence WHERE name='products'");
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='productlist'");

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e){
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean existIdProducts (int id, int listCode) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE id = '"+ id +"' AND idpl = " + listCode, null);
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
