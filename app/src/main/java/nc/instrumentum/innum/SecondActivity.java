package nc.instrumentum.innum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    protected ListView list_second;
    protected EditText text_second, text2_second;
    protected Button button_second, delbutton_second;
    protected TextView tV1_second;


    protected String textAux1="";
    protected String textAux2="";
    protected int intAux;
    protected ArrayList<Product>  buyingList = new ArrayList<>();
    protected ArrayAdapter<Product> adaptador;

    protected DataBaseManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        list_second = (ListView) findViewById(R.id.list_second);
        text_second = (EditText) findViewById(R.id.text_second);
        text2_second = (EditText) findViewById(R.id.text2_second);
        button_second = (Button) findViewById(R.id.button_second);
        delbutton_second = (Button) findViewById(R.id.delbutton_second);
        tV1_second = (TextView) findViewById(R.id.tV1_second);

        dbm = new DataBaseManager(this);
        buyingList = dbm.getProducts();

        adaptador = new ArrayAdapter<>(SecondActivity.this, android.R.layout.simple_list_item_1, buyingList);

        list_second.setAdapter(adaptador);

        list_second.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Product p = buyingList.get(i);
                if (dbm.deleteProducts(p.getId())) {
                    buyingList.remove(i);
                    adaptador.notifyDataSetChanged();
                }
                return true;
            }
        });

        button_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textAux1 = text_second.getText().toString();
                textAux2 = text2_second.getText().toString();
                if (textAux1.equalsIgnoreCase("")) {
                    Toast.makeText(SecondActivity.this, getString(R.string.string_rellenar), Toast.LENGTH_SHORT).show();
                } else {
                    if (textAux2.equalsIgnoreCase("")) {
                        textAux2 = "1";
                    }
                    try {
                        intAux = Integer.parseInt(textAux2);
                    } catch(NumberFormatException e) {
                        intAux = 2147483647;
                        Toast.makeText(SecondActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    text_second.setText("");
                    text2_second.setText("");
                    if (dbm.setProducts(textAux1, intAux)){
                        buyingList.add(new Product(textAux1,intAux));
                    } else {
                        Toast.makeText(SecondActivity.this, getString(R.string.string_error), Toast.LENGTH_SHORT).show();
                    }
                    adaptador.notifyDataSetChanged();
                }
            }
        });

        delbutton_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!buyingList.isEmpty()) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                   builder.setMessage(getString(R.string.string_duda))
                           .setPositiveButton(getString(R.string.string_si), new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   if (dbm.deleteAllProducts()) {
                                       buyingList.clear();
                                       adaptador.notifyDataSetChanged();
                                   }
                               }
                           })
                           .setNegativeButton(getString(R.string.string_no), new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   // User cancels the dialog.
                               }
                           });
                   // Create the AlertDialog object and return it.
                   builder.create();
                   builder.show();
               } else {
                   Toast.makeText(SecondActivity.this, getString(R.string.string_error_vacio), Toast.LENGTH_SHORT).show();
               }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}