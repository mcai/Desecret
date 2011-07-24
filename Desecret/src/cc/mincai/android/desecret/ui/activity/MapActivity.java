package cc.mincai.android.desecret.ui.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import cc.mincai.android.desecret.R;
import cc.mincai.android.desecret.model.Location;
import cc.mincai.android.desecret.ui.view.MapLocationOverlay;
import com.google.android.maps.MapView;

public class MapActivity extends com.google.android.maps.MapActivity implements GestureDetector.OnGestureListener {
    private MapView mapView;

    private GestureDetector detector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map);

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

        this.mapView.getController().setZoom(5);

        Location location = (Location) getIntent().getSerializableExtra("Location");

        this.mapView.getOverlays().add(new MapLocationOverlay(this, location));

        this.mapView.getController().animateTo(location.getPoint());
        this.mapView.getController().setCenter(location.getPoint());
        this.mapView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
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
}

