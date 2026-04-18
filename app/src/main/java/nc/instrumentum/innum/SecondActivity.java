package nc.instrumentum.innum;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
    protected MenuItem info_button, list_button;

    private boolean swipeHandler = false;
    protected float dX, dY;
    final static int thresholdH = 310;
    protected int touchPosition;
    protected String textAux1="";
    protected String textAux2="";
    protected String listTitleString;
    protected int intAux, idAux, listTitleId;
    protected ArrayList<Product>  buyingList = new ArrayList<>();
    protected ArrayAdapter<Product> adaptador;

    protected DataBaseManager dbm;
    protected Intent nextScreen;
    protected Bundle extra;
    protected GestureDetector gD;

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

        extra = getIntent().getExtras();
        if (extra!=null) {
            listTitleId = extra.getInt("ID_LIST");
            listTitleString = extra.getString("STRING_LIST");
            tV1_second.setText(listTitleString);
        }

        dbm = new DataBaseManager(this);
        buyingList = dbm.getProducts(listTitleId);

        adaptador = new ArrayAdapter<>(SecondActivity.this, android.R.layout.simple_list_item_1, buyingList);
        list_second.setAdapter(adaptador);

        //Delete by sweep section.
        gD =  new GestureDetector(SecondActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e){
                return true;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                if (e1 == null || e2 == null || swipeHandler) {
                    return false;
                }

                dX = e2.getX() - e1.getX();
                dY = e2.getY() - e1.getY();

                if (Math.abs(dX)>Math.abs(dY) && dX > thresholdH) {
                    touchPosition = list_second.pointToPosition((int) e1.getX(), (int) e1.getY());

                    if(touchPosition != ListView.INVALID_POSITION) {
                        if(dbm.deleteProducts(buyingList.get(touchPosition).getId(), buyingList.get(touchPosition).getIdList())) {
                            buyingList.remove(touchPosition);
                            adaptador.notifyDataSetChanged();
                            swipeHandler = true;
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //Reset behaviour.
        list_second.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    swipeHandler = false;
                }
                gD.onTouchEvent(event);
                return false;
            }
        });


        list_second.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle(R.string.string_edit_title);

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
                builder.setView(dialogView);

                EditText editName = dialogView.findViewById(R.id.edit_name_dialog);
                EditText editQuantity = dialogView.findViewById(R.id.edit_quantity_dialog);

                Product auxProduct = buyingList.get(i);

                int oldQuantity = auxProduct.getNum();

                builder.setPositiveButton(R.string.string_si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editName.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            if (!editQuantity.getText().toString().trim().equalsIgnoreCase("")) {
                                try {
                                    int newQuantity = Integer.parseInt(editQuantity.getText().toString().trim());
                                    dbm.updateProduct(auxProduct.getId(), auxProduct.getIdList(), newName, newQuantity);
                                    buyingList.set(i, new Product(auxProduct.getId(), newName, newQuantity, auxProduct.getIdList()));
                                    adaptador.notifyDataSetChanged();
                                } catch (Exception e) {
                                    Toast.makeText(SecondActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dbm.updateProduct(auxProduct.getId(), auxProduct.getIdList(), newName, oldQuantity);
                                buyingList.set(i, new Product(auxProduct.getId(), newName, oldQuantity, auxProduct.getIdList()));
                                adaptador.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(SecondActivity.this, R.string.string_error_vacio, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog object and return it.
                builder.create();
                builder.show();
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
                    idAux = dbm.setProducts(textAux1, intAux, listTitleId);
                    if (idAux!=-1){
                        buyingList.add(new Product(idAux, textAux1, intAux, listTitleId));
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
                                   if (dbm.deleteAllProducts(listTitleId)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        info_button = (MenuItem) findViewById(R.id.info_menu);
        list_button = (MenuItem) findViewById(R.id.listoflist_menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int auxItem = item.getItemId();

        if (auxItem==R.id.listoflist_menu) {
            nextScreen = new Intent(SecondActivity.this, ThirdActivity.class);
            startActivity(nextScreen);
            return true;
        } else if (auxItem == R.id.info_menu) {
            nextScreen = new Intent(SecondActivity.this, FirstActivity.class);
            startActivity(nextScreen);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}