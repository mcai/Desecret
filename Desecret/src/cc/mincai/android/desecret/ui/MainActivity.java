package cc.mincai.android.desecret.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import cc.mincai.android.desecret.R;
import cc.mincai.android.desecret.model.Location;
import cc.mincai.android.desecret.model.LocationChangedEvent;
import cc.mincai.android.desecret.model.MessageReceivedEvent;
import cc.mincai.android.desecret.model.UserEventMessageContent;
import cc.mincai.android.desecret.service.MasterService;
import cc.mincai.android.desecret.util.Action1;
import com.google.android.maps.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MapActivity implements GestureDetector.OnGestureListener {
    private TextView textViewLatitude;
    private TextView textViewLongitude;

    private MapController mapController;
    private MapView mapView;

    private boolean registered;

    private List<Location> mapLocations;

    private GestureDetector detector;

    private MasterService masterService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.textViewLatitude = (TextView) findViewById(R.id.TextView02);
        this.textViewLongitude = (TextView) findViewById(R.id.TextView04);

        this.detector = new GestureDetector(this, this);

        this.mapView = (MapView) findViewById(R.id.mapView1);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setStreetView(true);

        this.mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });

        this.mapLocations = new ArrayList<Location>();

        this.mapView.getOverlays().add(new MapLocationOverlay(this, this.mapLocations));

        this.mapController = this.mapView.getController();
        this.mapController.setZoom(5); // Zoom 1 is world view
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onLocationChanged(cc.mincai.android.desecret.model.Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        this.textViewLatitude.setText(String.valueOf(lat));
        this.textViewLongitude.setText(String.valueOf(lng));

        int lat1 = (int) (location.getLatitude() * 1E6);
        int lng1 = (int) (location.getLongitude() * 1E6);
        GeoPoint point = new GeoPoint(lat1, lng1);
        this.mapController.animateTo(point);
        this.mapController.setCenter(point);

        this.mapLocations.add(new Location(lng, lat, "Unamed Location"));
        this.mapView.invalidate();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //TODO
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.register();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.deregister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.deregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.deregister();
    }

    private void register() {
        if (!this.registered) {
            this.bindService(
                    new Intent(this, MasterService.class),
                    new ServiceConnection() {
                        public void onServiceConnected(ComponentName className, IBinder binder) {
                            masterService = ((MasterService.MyBinder) binder).getService();

                            masterService.getMessageTransport().addListener(new Action1<MessageReceivedEvent>() {
                                @Override
                                public void apply(MessageReceivedEvent event) {
                                    if (event.getMessage().getContent() instanceof UserEventMessageContent && ((UserEventMessageContent) event.getMessage().getContent()).getUserEvent() instanceof LocationChangedEvent) {
                                        onLocationChanged(((LocationChangedEvent) ((UserEventMessageContent) event.getMessage().getContent()).getUserEvent()).getNewLocation());
                                    }
                                }
                            });
                        }

                        public void onServiceDisconnected(ComponentName className) {
                            masterService = null;
                        }
                    },
                    Context.BIND_AUTO_CREATE
            );

            this.registered = true;
        }
    }

    private void deregister() {
        if (this.registered) {
            this.registered = false;
        }
    }

    // TODO: create ListActivity and ListView to display addresses related to a specific gepoint
    public static List<Address> getAddresses(Context context, GeoPoint point) {
        Geocoder geocoder = new Geocoder(context);
        try {
            return geocoder.getFromLocation(point.getLatitudeE6() / 1e6,
                    point.getLongitudeE6() / 1e6, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

