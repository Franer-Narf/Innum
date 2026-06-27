package nc.instrumentum.innum;

// Android
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonWriter;

// AndroidX
import androidx.annotation.Nullable;

// Java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if (i < 2) {
            resetDatabase(db);
        }
    }

    private void resetDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS productlist");

        onCreate(db);
    }

    public int createList(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = null;

        try {
            if (!existList(title)) {
                db.execSQL("INSERT INTO productlist (titlelist) VALUES (?)", new String[]{title});
                cur = db.rawQuery("SELECT idpl FROM productlist WHERE titlelist = ?", new String[]{title});
                if(cur.moveToFirst()) {
                    return cur.getInt(0);
                }
            } else if (existList(title)){
                String originalTitle = title;
                int copyNumber = 0;

                while (existList(title)) {
                    title = originalTitle + " " + "(" + copyNumber + ")";
                    copyNumber++;
                }
                return createList(title);
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
                cur = db.rawQuery("SELECT id FROM products WHERE object = ? AND idpl = ?", new String[]{prdct,String.valueOf(lstCd)});
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
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE idpl = ?", new String[]{String.valueOf(listCode)});
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
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE object = ? AND idpl = ?", new String[]{name,String.valueOf(listCode)});
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
        Cursor cur = db.rawQuery("SELECT * FROM productList WHERE titlelist = ?", new String[]{name});
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
        db.execSQL("DELETE FROM products WHERE id = ? AND idpl = ?", new Object[]{id,listCode});
        if (existIdProducts(id, listCode)) {
            return false;
        }
        return true;
    }

    public boolean deleteAllProducts (int listCode) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM productlist WHERE idpl = ?", new Object[]{listCode});
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
        Cursor cur = db.rawQuery("SELECT * FROM products WHERE id = ? AND idpl = ?", new String[]{String.valueOf(id),String.valueOf(listCode)});
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

    public String getNameList (int listCode) {
        String title = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT titlelist FROM productlist WHERE idpl = ?", new String[]{String.valueOf(listCode)});
        try {
            if (cur.moveToFirst()){
                title = cur.getString(0);
            }
            return title;
        } finally {
            cur.close();
        }
    }

    //Export for all list and their content.
    public File exportJson(Context context) {
        File sharedFolder = new File(context.getCacheDir(), "shared");
        File exportFile = new File(sharedFolder, "innum_export.json");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;

        try {
            if (!sharedFolder.exists()) {
                sharedFolder.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(exportFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            JsonWriter writer = new JsonWriter(osw);

            writer.setIndent("  ");

            writer.beginObject();

            writer.name("app").value("Innum");
            writer.name("schema_version").value(1);
            writer.name("exported_at").value(System.currentTimeMillis());

            writer.name("tables");
            writer.beginObject();

            writer.name("productlist");
            writer.beginArray();

            cur = db.rawQuery("SELECT idpl, titlelist FROM productlist ORDER BY idpl", null);

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    writer.beginObject();

                    writer.name("idpl").value(cur.getInt(0));
                    writer.name("titlelist").value(cur.getString(1));

                    writer.endObject();

                    cur.moveToNext();
                }

                cur.close();
                cur = null;
            }

            writer.endArray();

            writer.name("products");
            writer.beginArray();

            cur = db.rawQuery("SELECT id, object, cuantity, idpl FROM products ORDER BY id", null);

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    writer.beginObject();

                    writer.name("id").value(cur.getInt(0));
                    writer.name("object").value(cur.getString(1));
                    writer.name("cuantity").value(cur.getInt(2));
                    writer.name("idpl").value(cur.getInt(3));

                    writer.endObject();

                    cur.moveToNext();
                }

                cur.close();
                cur = null;
            }

            writer.endArray();

            writer.endObject(); // tables
            writer.endObject(); // root

            writer.close();

            return exportFile;

        } catch (Exception e) {
            return null;

        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    //Dedicated export for a specific list.
    public File exportDedicatedJson(Context context, int listCode) {
        File sharedFolder = new File(context.getCacheDir(), "shared");
        File exportFile = new File(sharedFolder, "innum_export_list.json");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;

        try {
            if (!sharedFolder.exists()) {
                sharedFolder.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(exportFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            JsonWriter writer = new JsonWriter(osw);

            writer.setIndent("  ");

            writer.beginObject();

            writer.name("app").value("Innum");
            writer.name("schema_version").value(1);
            writer.name("exported_at").value(System.currentTimeMillis());

            writer.name("tables");
            writer.beginObject();

            writer.name("productlist");
            writer.beginArray();

            cur = db.rawQuery("SELECT idpl, titlelist FROM productlist WHERE idpl = ? ORDER BY idpl", new String[]{String.valueOf(listCode)});

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    writer.beginObject();

                    writer.name("idpl").value(cur.getInt(0));
                    writer.name("titlelist").value(cur.getString(1));

                    writer.endObject();

                    cur.moveToNext();
                }

                cur.close();
                cur = null;
            }

            writer.endArray();

            writer.name("products");
            writer.beginArray();

            cur = db.rawQuery("SELECT id, object, cuantity, idpl FROM products WHERE idpl = ? ORDER BY id", new String[]{String.valueOf(listCode)});

            if (cur != null) {
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    writer.beginObject();

                    writer.name("id").value(cur.getInt(0));
                    writer.name("object").value(cur.getString(1));
                    writer.name("cuantity").value(cur.getInt(2));
                    writer.name("idpl").value(cur.getInt(3));

                    writer.endObject();

                    cur.moveToNext();
                }

                cur.close();
                cur = null;
            }

            writer.endArray();

            writer.endObject(); // tables
            writer.endObject(); // root

            writer.close();

            return exportFile;

        } catch (Exception e) {
            return null;

        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    public boolean importJson(Context context, Uri uri) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            InputStream is = context.getContentResolver().openInputStream(uri);

            if (is == null) {
                return false;
            }

            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            JsonReader reader = new JsonReader(isr);

            String appName = "";
            int schemaVersion = -1;

            HashMap<Integer, Integer> idplMap = new HashMap<>();

            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();

                if (name.equals("app")) {
                    appName = reader.nextString();

                } else if (name.equals("schema_version")) {
                    schemaVersion = reader.nextInt();

                } else if (name.equals("exported_at")) {
                    reader.skipValue();

                } else if (name.equals("tables")) {
                    if (!appName.equals("Innum") || schemaVersion != 1) {
                        reader.close();
                        return false;
                    }

                    reader.beginObject();

                    while (reader.hasNext()) {
                        String tableName = reader.nextName();

                        if (tableName.equals("productlist")) {
                            reader.beginArray();

                            while (reader.hasNext()) {
                                int idpl = -1;
                                String titlelist = "";

                                reader.beginObject();

                                while (reader.hasNext()) {
                                    String fieldName = reader.nextName();

                                    if (fieldName.equals("idpl")) {
                                        idpl = reader.nextInt();

                                    } else if (fieldName.equals("titlelist")) {
                                        titlelist = reader.nextString();

                                    } else {
                                        reader.skipValue();
                                    }
                                }

                                reader.endObject();

                                //Upgrade of importing lists.
                                int newIdpl = createList(titlelist);

                                if (newIdpl == -1) {
                                    return false;
                                }

                                idplMap.put(idpl, newIdpl);
                            }

                            reader.endArray();

                        } else if (tableName.equals("products")) {
                            reader.beginArray();

                            while (reader.hasNext()) {
                                int id = -1;
                                String object = "";
                                int cuantity = 1;
                                int idpl = -1;

                                reader.beginObject();

                                while (reader.hasNext()) {
                                    String fieldName = reader.nextName();

                                    if (fieldName.equals("id")) {
                                        id = reader.nextInt();

                                    } else if (fieldName.equals("object")) {
                                        object = reader.nextString();

                                    } else if (fieldName.equals("cuantity")) {
                                        cuantity = reader.nextInt();

                                    } else if (fieldName.equals("idpl")) {
                                        idpl = reader.nextInt();

                                    } else {
                                        reader.skipValue();
                                    }
                                }

                                reader.endObject();

                                Integer newIdpl = idplMap.get(idpl);

                                if (newIdpl == null) {
                                    return false;
                                }

                                int newId = setProducts(object, cuantity, newIdpl);

                                if (newId == -1) {
                                    return false;
                                }
                            }

                            reader.endArray();

                        } else {
                            reader.skipValue();
                        }
                    }

                    reader.endObject();

                } else {
                    reader.skipValue();
                }
            }

            reader.endObject();
            reader.close();

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            return false;

        } finally {
            db.endTransaction();
        }
    }
}
