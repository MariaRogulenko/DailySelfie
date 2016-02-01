package course.example.dailyselfie;

import android.app.Activity;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    protected static final String EXTRA_RES_ID = "POS";
    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;
    private static final long INITIAL_ALARM_DELAY = 1 * 10 * 1000L;
    protected static final long JITTER = 5000L;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GridView mainGridView;
    private ImageAdapter gridAdapter;
    private static final int WIDTH = 250;
    private static final int HEIGHT = 250;
    public List<Bitmap> mList;
    File directory;
    public String path;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
        }
        Log.i("counter2", " = " + counter );
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        path = directory.getAbsolutePath();

        // Find the ListView resource.
        mainGridView = (GridView) findViewById(R.id.gridview);


        mList = new ArrayList<Bitmap>();
        gridAdapter = new ImageAdapter(this, mList);
        //mainGridView.setAdapter(gridAdapter);

        mainGridView.setAdapter(gridAdapter);
        loadImageFromStorage(path);


        mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(MainActivity.this,
                        ImageViewActivity.class);

                // Add the ID of the thumbnail to display as an Intent Extra
                intent.putExtra(EXTRA_RES_ID, (int) id);
                intent.putExtra("BitmapImage", mList.get(position));

                // Start the ImageViewActivity
                startActivity(intent);
            }
        });

        Intent mNotificationReceiverIntent = new Intent(this, AlarmNotificationReceiver.class);
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, mNotificationReceiverIntent, 0);
        mAlarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY, INITIAL_ALARM_DELAY, mNotificationReceiverPendingIntent);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);

        // Set inexact repeating alarm
        mAlarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                INITIAL_ALARM_DELAY,
                mNotificationReceiverPendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            start();
        }

        return super.onOptionsItemSelected(item);
    }

    public void start() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView mImageView = new ImageView(this);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            gridAdapter.add(imageBitmap);
            saveToInternalStorage(imageBitmap);
            counter++;
            Log.i("counter", " = " + counter );
        }
    }

    public class ImageAdapter extends ArrayAdapter<Bitmap> {
        public ImageAdapter(Context context, List<Bitmap> list) {
            super(context, R.layout.img_layout, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView = new ImageView(this.getContext());
            mImageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
            mImageView.setImageBitmap(getItem(position));
            return mImageView;
        }
        @Override
        public long getItemId(int position) {
            return mList.get(position).getGenerationId();
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage){

        File mypath=new File(
                directory,"image_" + counter + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromStorage(String path) {
            boolean imadeExist = true;
            int index = 0;

            while (imadeExist) {
               try {
                   File imageFile = new File(path, "image_" + index + ".jpg");
                   if (imageFile.exists()) {
                       Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile));
                       mList.add(bitmap);
                       index++;
                   }
                   else {
                       imadeExist = false;
                       counter = index;
                   }
               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               }
           }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putInt("counter", counter);
    }
}
