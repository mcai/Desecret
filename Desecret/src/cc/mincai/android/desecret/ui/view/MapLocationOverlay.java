package cc.mincai.android.desecret.ui.view;

import android.content.Context;
import android.graphics.*;
import cc.mincai.android.desecret.R;
import cc.mincai.android.desecret.model.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import java.util.Arrays;
import java.util.List;

public class MapLocationOverlay extends Overlay {
    private Bitmap iconBubble, iconShadow;
    private Paint paintInner, paintBorder, paintText, paintLine;
    private Location selectedMapLocation;
    private List<Location> mapLocations;

    public MapLocationOverlay(Context context, Location... mapLocations) {
        this(context, Arrays.asList(mapLocations));
    }

    public MapLocationOverlay(Context context, List<Location> mapLocations) {
        this.mapLocations = mapLocations;

        this.iconBubble = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble);
        this.iconShadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow);
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        boolean isRemovePriorPopup = this.selectedMapLocation != null;

        this.selectedMapLocation = this.getHitMapLocation(mapView, p);
        if (isRemovePriorPopup || this.selectedMapLocation != null) {
            mapView.invalidate();
        }

        return this.selectedMapLocation != null;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        this.drawMapLocations(canvas, mapView, shadow);

        for (int i = 0; i < this.mapLocations.size(); i++) {
            if (this.mapLocations.size() > 1 && i < this.mapLocations.size() - 1) {
                Location location = this.mapLocations.get(i);
                Location locationNext = this.mapLocations.get(i + 1);

                Point screenCoords = new Point();
                Point screenCoordsNext = new Point();

                mapView.getProjection().toPixels(location.getPoint(), screenCoords);
                mapView.getProjection().toPixels(locationNext.getPoint(), screenCoordsNext);

                canvas.drawLine(screenCoords.x, screenCoords.y, screenCoordsNext.x, screenCoordsNext.y, this.getPaintLine());
            }
        }

        this.drawInfoWindow(canvas, mapView, shadow);
    }

    private Location getHitMapLocation(MapView mapView, GeoPoint tapPoint) {
        for (Location location : this.mapLocations) {
            RectF hitTestRecr = new RectF();
            Point screenCoords = new Point();

            mapView.getProjection().toPixels(location.getPoint(), screenCoords);

            hitTestRecr.set(-this.iconBubble.getWidth() / 2, -this.iconBubble.getHeight(), this.iconBubble.getWidth() / 2, 0);
            hitTestRecr.offset(screenCoords.x, screenCoords.y);

            mapView.getProjection().toPixels(tapPoint, screenCoords);

            if (hitTestRecr.contains(screenCoords.x, screenCoords.y)) {
                tapPoint = null;
                return location;
            }
        }

        tapPoint = null;
        return null;
    }

    private void drawMapLocations(Canvas canvas, MapView mapView, boolean shadow) {
        for (Location location : this.mapLocations) {
            Point screenCoords = new Point();

            mapView.getProjection().toPixels(location.getPoint(), screenCoords);

            if (shadow) {
                canvas.drawBitmap(this.iconShadow, screenCoords.x, screenCoords.y - this.iconShadow.getHeight(), null);
            } else {
                canvas.drawBitmap(this.iconBubble, screenCoords.x - this.iconBubble.getWidth() / 2, screenCoords.y - this.iconBubble.getHeight(), null);
            }
        }
    }

    private void drawInfoWindow(Canvas canvas, MapView mapView, boolean shadow) {
        if (this.selectedMapLocation != null && !shadow) {
            Point selDestinationOffset = new Point();
            mapView.getProjection().toPixels(this.selectedMapLocation.getPoint(), selDestinationOffset);

            int INFO_WINDOW_WIDTH = 175;
            int INFO_WINDOW_HEIGHT = 40;

            RectF infoWindowRect = new RectF(0, 0, INFO_WINDOW_WIDTH, INFO_WINDOW_HEIGHT);
            int infoWindowOffsetX = selDestinationOffset.x - INFO_WINDOW_WIDTH / 2;
            int infoWindowOffsetY = selDestinationOffset.y - INFO_WINDOW_HEIGHT - this.iconBubble.getHeight();
            infoWindowRect.offset(infoWindowOffsetX, infoWindowOffsetY);

            canvas.drawRoundRect(infoWindowRect, 5, 5, this.getPaintInner());
            canvas.drawRoundRect(infoWindowRect, 5, 5, this.getPaintBorder());

            int TEXT_OFFSET_X = 10;
            int TEXT_OFFSET_Y = 15;

            canvas.drawText(this.selectedMapLocation.getDescription(), infoWindowOffsetX + TEXT_OFFSET_X, infoWindowOffsetY + TEXT_OFFSET_Y, this.getPaintText());
        }
    }

    public Paint getPaintInner() {
        if (this.paintInner == null) {
            this.paintInner = new Paint();
            this.paintInner.setARGB(225, 50, 50, 50);
            this.paintInner.setAntiAlias(true);
        }

        return this.paintInner;
    }

    public Paint getPaintBorder() {
        if (this.paintBorder == null) {
            this.paintBorder = new Paint();
            this.paintBorder.setARGB(255, 255, 255, 255);
            this.paintBorder.setAntiAlias(true);
            this.paintBorder.setStyle(Paint.Style.STROKE);
            this.paintBorder.setStrokeWidth(2);
        }

        return this.paintBorder;
    }

    public Paint getPaintText() {
        if (this.paintText == null) {
            this.paintText = new Paint();
            this.paintText.setARGB(255, 255, 255, 255);
            this.paintText.setAntiAlias(true);
        }

        return this.paintText;
    }

    public Paint getPaintLine() {
        if (this.paintLine == null) {
            this.paintLine = new Paint();
            this.paintLine.setStrokeWidth(4);
            this.paintLine.setColor(Color.BLUE);
            this.paintLine.setARGB(150, 187, 249, 47);
            this.paintLine.setAntiAlias(true);
        }

        return this.paintLine;
    }
}
