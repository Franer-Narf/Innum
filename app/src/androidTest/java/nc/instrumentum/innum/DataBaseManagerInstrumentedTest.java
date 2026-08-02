package nc.instrumentum.innum;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DataBaseManagerInstrumentedTest {

    private static final String DATABASE_NAME = "productslist";

    private Context context;
    private DataBaseManager database;

    @Before
    public void setUp() {
        context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        // Cada prueba comienza con una base de datos vacía.
        context.deleteDatabase(DATABASE_NAME);

        database = new DataBaseManager(context);
        database.getWritableDatabase();

        deleteDedicatedExport();
    }

    @After
    public void tearDown() {
        if (database != null) {
            database.close();
        }

        context.deleteDatabase(DATABASE_NAME);
        deleteDedicatedExport();
    }

    @Test
    public void listAndProductAreStoredTogether() {
        int listId = database.createList("Compra semanal");

        assertTrue(listId > 0);

        int productId = database.setProducts(
                "Leche",
                2,
                listId
        );

        assertTrue(productId > 0);

        ArrayList<ListClass> lists = database.getLists();

        assertEquals(1, lists.size());
        assertEquals(listId, lists.get(0).getIdL());
        assertEquals("Compra semanal", lists.get(0).getNameL());

        ArrayList<Product> products = database.getProducts(listId);

        assertEquals(1, products.size());
        assertEquals(productId, products.get(0).getId());
        assertEquals("Leche", products.get(0).getObj());
        assertEquals(2, products.get(0).getNum());
        assertEquals(listId, products.get(0).getIdList());
    }

    @Test
    public void dedicatedExportContainsOnlySelectedList()
            throws Exception {

        int selectedListId = database.createList("Casa");
        database.setProducts("Pan", 2, selectedListId);

        int otherListId = database.createList("Trabajo");
        database.setProducts("Café", 1, otherListId);

        File exportFile = database.exportDedicatedJson(
                context,
                selectedListId
        );

        assertNotNull(exportFile);
        assertTrue(exportFile.exists());
        assertTrue(exportFile.length() > 0);

        String jsonText = new String(
                Files.readAllBytes(exportFile.toPath()),
                StandardCharsets.UTF_8
        );

        JSONObject root = new JSONObject(jsonText);

        assertEquals("Innum", root.getString("app"));
        assertEquals(1, root.getInt("schema_version"));

        JSONObject tables = root.getJSONObject("tables");

        JSONArray lists = tables.getJSONArray("productlist");
        assertEquals(1, lists.length());
        assertEquals(
                selectedListId,
                lists.getJSONObject(0).getInt("idpl")
        );
        assertEquals(
                "Casa",
                lists.getJSONObject(0).getString("titlelist")
        );

        JSONArray products = tables.getJSONArray("products");
        assertEquals(1, products.length());
        assertEquals(
                "Pan",
                products.getJSONObject(0).getString("object")
        );
        assertEquals(
                2,
                products.getJSONObject(0).getInt("cuantity")
        );
        assertEquals(
                selectedListId,
                products.getJSONObject(0).getInt("idpl")
        );

        assertFalse(jsonText.contains("Trabajo"));
        assertFalse(jsonText.contains("Café"));
    }

    private void deleteDedicatedExport() {
        if (context == null) {
            return;
        }

        File exportFile = new File(
                new File(context.getCacheDir(), "shared"),
                "innum_export_list.json"
        );

        if (exportFile.exists()) {
            exportFile.delete();
        }
    }
}