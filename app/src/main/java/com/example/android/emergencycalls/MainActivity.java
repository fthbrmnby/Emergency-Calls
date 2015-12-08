package com.example.android.emergencycalls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> phoneNums;
    private ArrayList<Integer> icons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNums = readFile();

        // add PhoneStateListener
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);

        ListView view = (ListView) findViewById(R.id.list);
        View headerView = getLayoutInflater().inflate(
                R.layout.layout_header_item, null);
        Button police = (Button) headerView.findViewById(R.id.police_button);
        Button ambulance = (Button) headerView.findViewById(R.id.ambulance_button);
        Button fireDept = (Button) headerView.findViewById(R.id.fire_button);
        Button jandarma = (Button) headerView.findViewById(R.id.jandarma_button);
        ImageAdapterTest veriAdaptoru = new ImageAdapterTest(getApplicationContext(), phoneNums, icons);
        view.setAdapter(veriAdaptoru);
        view.addHeaderView(headerView, null, false);
        view.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                View headerView = view.findViewById(R.id.header);

                final float mTop = -headerView.getTop();
                float height = headerView.getHeight();
                if (mTop > height) {
                    // ignore
                    return;
                }
                //View imgView = headerView.findViewById(R.id.imageView);
                //imgView.setTranslationY(mTop / 2f);

            }
        });
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.name)).getText().toString();
                selected = selected.substring(0, 4);
                call(selected);
                Log.i("Clicked", selected);
            }
        });

        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("155");
            }
        });

        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("112");
            }
        });

        fireDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("110");
            }
        });

        jandarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("156");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {



        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void call(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        } catch (SecurityException e) {
            Log.e("HATA", e.toString());
        }
    }

    private ArrayList<String> readFile() {
        ArrayList<String> array = new ArrayList<>();
        BufferedReader br = null;
        String currentLine;


        try {
            InputStream is = getResources().getAssets().open("numbers.txt");
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while ((currentLine = br.readLine()) != null) {
                array.add(currentLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return array;
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }

}


class ImageAdapterTest extends ArrayAdapter<String> {
    ArrayList<String> numbers;
    ArrayList<Integer> icns;

    public ImageAdapterTest(Context context, ArrayList<String> str, ArrayList<Integer> icn) {
        super(context, 0, str);
        numbers = str;
        icns = icn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the VideoListData item for this position
        try {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_list_item, parent, false);
            }
            // Create content
            //ImageView icon = (ImageView) convertView.findViewById(R.id.iamge);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            //Log.i("Element", numbers.get(position));
            name.setText(numbers.get(position));
            //icon.setImageResource(icns.get(position));
        } catch (Exception e) {
            Log.e("HATA", e.toString());
        }
        return convertView;
    }
}

