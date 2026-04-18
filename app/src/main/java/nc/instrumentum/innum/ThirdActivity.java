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
import android.widget.Adapter;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ThirdActivity extends AppCompatActivity {

    //Declarations
    protected boolean swipeHandler = false;
    private static final int thresholdH = 310;
    protected float dX, dY;
    protected int touchPosition, numberAux;
    protected String textAux;

    protected ArrayList<ListClass> pileList = new ArrayList<>();

    protected Button bAdd, bDel;
    protected EditText fText;
    protected ListView listV;
    protected MenuItem info_button, list_button;

    protected DataBaseManager dbm;
    protected ArrayAdapter<ListClass> adapterList;

    protected Intent nextScreen;
    protected Bundle extra;
    protected GestureDetector gD;

    //Logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbm = new DataBaseManager(this);
        pileList = dbm.getLists();

        bAdd = (Button) findViewById(R.id.button_add_third);
        bDel = (Button) findViewById(R.id.delbutton_third);
        fText = (EditText) findViewById(R.id.text_third);
        listV = (ListView) findViewById(R.id.list_third);

        adapterList = new ArrayAdapter<>(ThirdActivity.this, android.R.layout.simple_list_item_1, pileList);
        listV.setAdapter(adapterList);

        //Delete by sweep section.
        gD =  new GestureDetector(ThirdActivity.this, new GestureDetector.SimpleOnGestureListener() {
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
                    touchPosition = listV.pointToPosition((int) e1.getX(), (int) e1.getY());

                    if(touchPosition != ListView.INVALID_POSITION) {
                        if (dbm.deleteAllProducts(pileList.get(touchPosition).getIdL())) {
                            pileList.remove(touchPosition);
                            adapterList.notifyDataSetChanged();
                            swipeHandler = true;
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //Reset behaviour.
        listV.setOnTouchListener(new View.OnTouchListener() {
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

        //Open list section.
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nextScreen = new Intent(ThirdActivity.this, SecondActivity.class);
                extra = new Bundle();
                extra.putInt("ID_LIST", pileList.get(i).getIdL());
                extra.putString("STRING_LIST", pileList.get(i).getNameL());
                nextScreen.putExtras(extra);
                finish();
                startActivity(nextScreen);
            }
        });

        //Edit list section.
        listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
                builder.setTitle(R.string.string_edit_title);

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_list, null);
                builder.setView(dialogView);

                EditText editTitle = dialogView.findViewById(R.id.edit_title_dialog);

                builder.setPositiveButton(R.string.string_si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String textPopup = editTitle.getText().toString().trim();
                        if (!textPopup.isEmpty()) {
                            dbm.updateListTitle(pileList.get(i).getIdL(),textPopup);
                            pileList.set(i, new ListClass(pileList.get(i).getIdL(),textPopup));
                            adapterList.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ThirdActivity.this, R.string.string_error_vacio, Toast.LENGTH_SHORT).show();
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


        //Add new list.
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textAux = fText.getText().toString();
                if (!textAux.equalsIgnoreCase("")) {
                    try {
                        numberAux = dbm.createList(textAux);
                        if (numberAux!=-1) {
                            nextScreen = new Intent(ThirdActivity.this, SecondActivity.class);
                            extra = new Bundle();
                            extra.putInt("ID_LIST", numberAux);
                            extra.putString("STRING_LIST", textAux);
                            nextScreen.putExtras(extra);
                            finish();
                            startActivity(nextScreen);
                        } else {
                            Toast.makeText(ThirdActivity.this, getString(R.string.string_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ThirdActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ThirdActivity.this, getString(R.string.string_rellenar), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Delete all tables.
        bDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pileList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
                    builder.setMessage(getString(R.string.string_duda))
                            .setPositiveButton(getString(R.string.string_si), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (!dbm.deleteAllTables()) {
                                        Toast.makeText(ThirdActivity.this, getString(R.string.string_err), Toast.LENGTH_SHORT).show();
                                    } else {
                                        pileList.removeAll(pileList);
                                        adapterList.notifyDataSetChanged();
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
                }
                else {
                    Toast.makeText(ThirdActivity.this, R.string.string_error_vacio, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            nextScreen = new Intent(ThirdActivity.this, ThirdActivity.class);
            finish();
            startActivity(nextScreen);
            return true;
        } else if (auxItem == R.id.info_menu) {
            nextScreen = new Intent(ThirdActivity.this, FirstActivity.class);
            finish();
            startActivity(nextScreen);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}