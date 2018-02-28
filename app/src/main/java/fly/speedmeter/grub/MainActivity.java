package fly.speedmeter.grub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.melnykov.fab.FloatingActionButton;


import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends ActionBarActivity implements LocationListener, GpsStatus.Listener {

    private SharedPreferences sharedPreferences;
    private LocationManager mLocationManager;
    private static Data data;
    private static int FrequencyValue ;

    private static int GraphIdPosition = 0;
    private static DataPoint[] values = new DataPoint[]{
        new DataPoint(0, 0), new DataPoint(1, 0), new DataPoint(2, 0), new DataPoint(3, 0),
                new DataPoint(4, 0), new DataPoint(5, 0), new DataPoint(6, 0), new DataPoint(7, 0),
                new DataPoint(8, 0), new DataPoint(9, 0), new DataPoint(10, 0), new DataPoint(11, 0),
                new DataPoint(12, 0), new DataPoint(13, 0), new DataPoint(14, 0), new DataPoint(15, 0),
                new DataPoint(16, 0), new DataPoint(17, 0), new DataPoint(18, 0), new DataPoint(19, 0),
                new DataPoint(20, 0), new DataPoint(21, 0), new DataPoint(22, 0), new DataPoint(23, 0),
                new DataPoint(24, 0), new DataPoint(25, 0), new DataPoint(26, 0), new DataPoint(27, 0),
                new DataPoint(28, 0), new DataPoint(29, 0)};
  //  for (int i=0; i<count; i++) {
    //    double x = i;
      //  double f = mRand.nextDouble()*0.15+0.3;
      //  double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
      //:  DataPoint v = new DataPoint(x, speedkai);
     //   values[i] = v;
    //}
      //  Log.d("speedkai = ", String.valueOf(speedkai));
    //Log.d("speed = ", .valueOf(speed));
       // return values;
    private static double speedkai;
    private static boolean AccelaratorInit = false;
    private static boolean AccelaratorInit2 = false;
    private static boolean AccelaratorInit3 = false;
    private static int outputValues = 1000 ;
    private static double OldSpeedValue;
    private static double NewSpeedValue;
    private static double AccelerationValue = 0.0;
    private static double OldTimeSaved;
    private static double NewTimeSaved;

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FloatingActionButton refresh;
    private ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
    private TextView satellite;
    private TextView status;
    private TextView accuracy;
    private TextView currentSpeed;
    private TextView maxSpeed;
    private TextView averageSpeed;
    private TextView Xvalue;
    private TextView Yvalue;
    private TextView distance;
    private Chronometer time;
    private SeekBar FrequencykBar;


    /*************google map******/
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(48.788736, 2.363908);
    private static final int DEFAULT_ZOOM = 20;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private final int mMaxEntries = 5;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
    /***************************************/

    //id seekbar = frequency
    private Data.onGpsServiceUpdate onGpsServiceUpdate;

    private boolean firstfix;

    /***************graphview*********************/
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;

    /*******************Graphview***************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new Data(onGpsServiceUpdate);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /**********************************/

        //Setting up the series for the GraphView to display
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);// remove horizontal x labels and line
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);// It will remove the background grids
        graph.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

        mSeries1 = new LineGraphSeries<DataPoint>();
        mSeries1.setDrawBackground(true);
        mSeries1.setColor(Color.CYAN);
        mSeries1.setThickness(4);
        mSeries1.setBackgroundColor(0x40FFFFFF);
        mSeries1.setTitle("Speed");

        mSeries2 = new LineGraphSeries<DataPoint>();
        mSeries2.setDrawBackground(true);
        mSeries2.setColor(Color.BLUE);
        mSeries2.setThickness(4);
        mSeries2.setBackgroundColor(0x20F0AFF);
        mSeries2.setTitle("Acceleration");

        graph.addSeries(mSeries1);
        graph.addSeries(mSeries2);


        //Setting GraphView Viewport layouts
        Viewport mViewport =  graph.getViewport();
        mViewport.setXAxisBoundsManual(true);
        mViewport.setYAxisBoundsManual(true);
        mViewport.setMinX(1);
        mViewport.setMaxX(31);
        mViewport.setScrollable(false);
        mViewport.setMinY(-15);
        mViewport.setMaxY(15.0);


        /************************************/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        refresh = (FloatingActionButton) findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);

        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {
                maxSpeed.setText(data.getMaxSpeed());
                distance.setText(data.getDistance());
                if (sharedPreferences.getBoolean("auto_average", false)) {
                    averageSpeed.setText(data.getAverageSpeedMotion());
                } else {
                    averageSpeed.setText(data.getAverageSpeed());
                }
            }
        };

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        satellite = (TextView) findViewById(R.id.satellite);
        status = (TextView) findViewById(R.id.status);
        accuracy = (TextView) findViewById(R.id.accuracy);
        maxSpeed = (TextView) findViewById(R.id.maxSpeed);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        distance = (TextView) findViewById(R.id.distance);
        Xvalue = (TextView) findViewById(R.id.xpositionvalue);
        Yvalue = (TextView) findViewById(R.id.ypositionvalue);
        time = (Chronometer) findViewById(R.id.time);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        /***********************************Seek bar*********************************/
        FrequencykBar = (SeekBar) findViewById(R.id.frequency);
        //Set the seekbar range between 0 and 255
        //seek bar settings//
        //sets the range between 0 and 255
        FrequencykBar.setMax(500);
        //set the seek bar progress to 1
        FrequencykBar.setKeyProgressIncrement(1);

        //FrequencyValue

        //Set the progress of the seek bar based on the system's brightness
        FrequencykBar.setProgress(FrequencyValue);

        FrequencykBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

            public void onStopTrackingTouch(SeekBar seekBar)

            {
                //Nothing handled here
            }

            public void onStartTrackingTouch(SeekBar seekBar)

            {

                //Nothing handled here

            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)

            {
                FrequencyValue = progress;
            }
            });

        /***********************************Seed bar*********************************/
        progressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);

        time.setText("00:00:00");
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if (data.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                } else {
                    time = data.getTime();
                }

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);

                if (data.isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });

    }

    public void onFabClick(View v) {
        if (!data.isRunning()) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            data.setRunning(true);
            time.setBase(SystemClock.elapsedRealtime() - data.getTime());
            time.start();
            data.setFirstTime(true);
            startService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(View.INVISIBLE);
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
            data.setRunning(false);
            status.setText("");
            stopService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(View.VISIBLE);
        }
    }

    public void onRefreshClick(View v) {
        resetData();
        stopService(new Intent(getBaseContext(), GpsServices.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstfix = true;
        if (!data.isRunning()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("data", "");
            data = gson.fromJson(json, Data.class);
        }
        if (data == null) {
            data = new Data(onGpsServiceUpdate);
        } else {
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }

        if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        } else {
            Log.w("MainActivity", "No GPS location provider found. GPS data display will not be available.");
        }

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }

        mLocationManager.addGpsStatusListener(this);

        /******************/
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries1.appendData(new DataPoint(GraphIdPosition++ , speedkai), true, 1000);
                mSeries2.appendData(new DataPoint(GraphIdPosition , AccelerationValue), true, 1000);
                //outputValues

                    mHandler.postDelayed(this, FrequencyValue); //seekbar value
                    //mHandler.post(this);
                    //outputValues = 0;

                //mHandler.postDelayed(this, 300);

                //Log.d("elapsedTime = ", String.valueOf(SystemClock.elapsedRealtime()));
            }
        };
        mHandler.postDelayed(mTimer1, 1000); //default value => 300
        /******************/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString("data", json);
        prefsEditor.commit();

        /*onpause */
        mHandler.removeCallbacks(mTimer1);
        /*onpause*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), GpsServices.class));
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            return true;
        }

        //continue
        if (id == R.id.action_graph2d) {
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            SpannableString s = new SpannableString(String.format("%.0f", location.getAccuracy()) + "m");
            s.setSpan(new RelativeSizeSpan(0.75f), s.length() - 1, s.length(), 0);
            accuracy.setText(s);

            if (firstfix) {
                status.setText("");
                fab.setVisibility(View.VISIBLE);
                if (!data.isRunning() && !maxSpeed.getText().equals("")) {
                    refresh.setVisibility(View.VISIBLE);
                }
                firstfix = false;
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            progressBarCircularIndeterminate.setVisibility(View.GONE);
            String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "km/h";
            speedkai = location.getSpeed() * 3.6;

            //String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "km/h";

            //get current position
            String LongitudeDevice = String.format(Locale.ENGLISH, "%.5f", location.getLongitude());
            String LattitudeDevice = String.format(Locale.ENGLISH, "%.5f", location.getLatitude());

            Xvalue.setText(LongitudeDevice);
            Yvalue.setText(LattitudeDevice);

            if(AccelerationValue > 2){  //2 is my limit acceleration
                Xvalue.setTextColor(Color.RED);
                Yvalue.setTextColor(Color.RED);
            }
            else{
                Xvalue.setTextColor(Color.GREEN);
                Yvalue.setTextColor(Color.GREEN);
            }

            //accelerator
            if(AccelaratorInit == false ){
                OldSpeedValue = speedkai;
                OldTimeSaved = SystemClock.elapsedRealtime()*1.0;
                AccelerationValue = 0;
                AccelaratorInit = true;
            }
            else if((OldTimeSaved + 100) < SystemClock.elapsedRealtime() && AccelaratorInit2 == false){
                NewSpeedValue = speedkai;
                NewTimeSaved = SystemClock.elapsedRealtime()*1.0;

                AccelerationValue = (NewSpeedValue - OldSpeedValue)/(NewTimeSaved - OldTimeSaved)*3600;

                AccelaratorInit2 = true;
                AccelaratorInit3 = false;
            }
            else if ( (NewTimeSaved+100) < SystemClock.elapsedRealtime() && AccelaratorInit3 == false){

                OldSpeedValue = speedkai;
                OldTimeSaved = SystemClock.elapsedRealtime()*1.0;

                AccelerationValue = (OldSpeedValue - NewSpeedValue)/(OldTimeSaved - NewTimeSaved)*3600;

                AccelaratorInit2 = false;
                AccelaratorInit3 = true;
            }
            Log.d("Acceleration = ", String.valueOf(AccelerationValue));

            if (sharedPreferences.getBoolean("miles_per_hour", false)) { // Convert to MPH
                speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6 * 0.62137119) + "mi/h";
            }
            Log.d("speed = ", speed);
            SpannableString s = new SpannableString(speed);
            s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
            currentSpeed.setText(s);
        }

    }

    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                int satsInView = 0;
                int satsUsed = 0;
                Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (GpsSatellite sat : sats) {
                    satsInView++;
                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }
                satellite.setText(String.valueOf(satsUsed) + "/" + String.valueOf(satsInView));
                if (satsUsed == 0) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                    data.setRunning(false);
                    status.setText("");
                    stopService(new Intent(getBaseContext(), GpsServices.class));
                    fab.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(View.INVISIBLE);
                    accuracy.setText("");
                    status.setText(getResources().getString(R.string.waiting_for_fix));
                    firstfix = true;
                }
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    public void showGpsDisabledDialog() {
        Dialog dialog = new Dialog(this, getResources().getString(R.string.gps_disabled), getResources().getString(R.string.please_enable_gps));

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        dialog.show();
    }

    public void resetData() {
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        refresh.setVisibility(View.INVISIBLE);
        time.stop();
        maxSpeed.setText("");
        averageSpeed.setText("");
        distance.setText("");
        time.setText("00:00:00");
        data = new Data(onGpsServiceUpdate);
    }

    public static Data getData() {
        return data;
    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    double mLastRandom = 2;
    Random mRand = new Random();

    private double getRandom() {
        return mLastRandom += mRand.nextDouble() * 0.5 - 0.25;

    }
}
