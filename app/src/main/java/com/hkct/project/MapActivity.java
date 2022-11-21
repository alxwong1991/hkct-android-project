package com.hkct.project;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    /* <!-- MarkerDemo --> */
    // ==== Properties ====
    final static String TAG = "MainActivity===>";

    private MapView mapView;
    private MapboxMap mapboxMap;
    // import com.mapbox.mapboxsdk.maps.Style;
    private Style style;

    private Switch sw1, sw2, sw3;
    private boolean isSw1, isSw2, isSw3;
    private SymbolLayer sw1Layer, sw2Layer, sw3Layer;
    private LineLayer lineLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_map);

        // ==== MapBox ====
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);

        isSw1 = sw1.isChecked();
        isSw2 = sw2.isChecked();
        isSw3 = sw3.isChecked();

        // ==== Screen Management ====
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // ==== Toolbar setup ====
        setTitle("Maxbox");

    } // onCreate()

    @Override
    public void onMapReady(@NonNull MapboxMap m) {

        mapboxMap = m;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style s) {
                style = s;

                mapboxMap.getUiSettings().setCompassEnabled(true);
                mapboxMap.getUiSettings().setLogoEnabled(true);
                mapboxMap.getUiSettings().setAttributionEnabled(true);

                // ==== MapClickListener ====
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        Log.d(TAG,"onMapClick()");
                        Toast.makeText(getApplicationContext(),"onMapClick()",Toast.LENGTH_LONG).show();

                        return false;
                    }
                });

                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull LatLng point) {

                        Log.d(TAG,"onMapLongClick()");
                        Toast.makeText(getApplicationContext(),"onMapLongClick()",Toast.LENGTH_LONG).show();

                        return false;
                    }
                });

                /* Layer 0*/
                // draw_circle
                LatLng circleLL = new LatLng(22.293501, 114.172137);
                draw_circle(circleLL);

                // draw line (TST Clock Tower, Hong Kong Space Museum, Hong Kong Museum of Art
                List<LatLng> lineLatLngs = new ArrayList<>();
                lineLatLngs.add(new LatLng(22.293574, 114.1689735));
                lineLatLngs.add(new LatLng(22.293851,114.170324));
                lineLatLngs.add(new LatLng(22.294291,114.171900));
                draw_line_byLatLng(lineLatLngs);

                // ==== Layer marker ====
                initSw1Layer();
                initSw2Layer();
                initSw3Layer();

            } //onStyleLoaded()
        });

    } //onMapReady()

    // ==== Camera ====
    private void moveCamera(LatLng ll){
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
    }

    // ==== Simple draw circle and line ====
    // import com.mapbox.geojson.Point;
    private void draw_circle(LatLng ll){
        Log.d(TAG,"draw_circle("+ll.toString()+")");
        Style style = mapboxMap.getStyle();

        // create a fixed circle
        CircleManager circleManager = new CircleManager(mapView,mapboxMap,style);
        CircleOptions circleOptions = new CircleOptions()
                .withLatLng(ll)
                .withCircleColor(ColorUtils.colorToRgbaString(Color.RED))
                .withCircleRadius(14f)
                .withCircleOpacity(0.5f)
                .withDraggable(true);

        circleManager.create(circleOptions);
    } // draw_circle

    private void draw_line_byLatLng(List<LatLng> lls){
        Log.d(TAG,"draw_line_byLatLng("+lls.toString()+")");
        Style style = mapboxMap.getStyle();

        LineManager lineManager = new LineManager(mapView,mapboxMap,style);
        LineOptions lineOptions = new LineOptions()
                .withLineColor(ColorUtils.colorToRgbaString(Color.BLUE))
                .withLineWidth(5f)
                .withLineOpacity(0.5f)
                .withLatLngs(lls);    // accept list of LatLng

        lineManager.create(lineOptions);
    } // draw_line_byLatLng

    // ==== initial Marker Layer ====
    private void initSw1Layer(){
        Log.d(TAG,"initSw1Layer()");
        final String SW1_SOURCE_ID = "SW1_SOURCE_ID";
        final String SW1_ICON_ID = "SW1_ICON_ID";
        final String SW1_LAYER_ID = "SW1_LAYER_ID";

        //Hong Kong Clock Tower
        final List<Feature> list = new ArrayList<>();
        list.add(Feature.fromGeometry(Point.fromLngLat(114.1689735,22.293574)));

        style.addImage(SW1_ICON_ID, BitmapFactory.decodeResource(
                MapActivity.this.getResources(), R.drawable.red_marker));
        style.addSource(new GeoJsonSource(SW1_SOURCE_ID, FeatureCollection.fromFeatures(list)));

        sw1Layer = new SymbolLayer(SW1_LAYER_ID, SW1_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(SW1_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true),
                        iconOffset(new Float[] {0f, -9f})
                        // import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
                );

        style.addLayer(sw1Layer);
    } //initSw1Layer()

    private void initSw2Layer(){
        Log.d(TAG,"initSw2Layer()");
        final String SW2_SOURCE_ID = "SW2_SOURCE_ID";
        final String SW2_ICON_ID = "SW2_ICON_ID";
        final String SW2_LAYER_ID = "SW2_LAYER_ID";

        //Hong Kong Cultural Centre, Hong Kong Space Museum
        final List<Feature> list = new ArrayList<>();
        list.add(Feature.fromGeometry(Point.fromLngLat(114.170324, 22.293851)));
        list.add(Feature.fromGeometry(Point.fromLngLat(114.171900,22.294291)));

        style.addImage(SW2_ICON_ID,BitmapFactory.decodeResource(
                MapActivity.this.getResources(), R.drawable.blue_marker));
        style.addSource(new GeoJsonSource(SW2_SOURCE_ID, FeatureCollection.fromFeatures(list)));

        sw2Layer = new SymbolLayer(SW2_LAYER_ID, SW2_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(SW2_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true),
                        iconOffset(new Float[] {0f, -9f})
                );

        style.addLayer(sw2Layer);
    } //addSw2Marker()

    private void initSw3Layer(){
        final String LINE_LAYER = "LINE_LAYER";
        final String LINE_SOURCE = "LINE_SOURCE";

        //Hong Kong Museum of Art, Salisbury Garden, Starry Gallery
        final List<Point> points = new ArrayList<>();
        points.add(Point.fromLngLat(114.172137,22.293501));
        points.add(Point.fromLngLat(114.168749, 22.293765));
        points.add(Point.fromLngLat(114.168585,22.295405));

        Log.d(TAG,"addDashLineByPoints()->"+points.toString());
        style.addSource(new GeoJsonSource(LINE_SOURCE,
                FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                        LineString.fromLngLats(points)
                )})));

        lineLayer = new LineLayer(LINE_LAYER, LINE_SOURCE).withProperties(
                PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                //PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                PropertyFactory.lineColor(Color.RED)
        );
        style.addLayer(lineLayer);
    } //initSw3Layer()

    // ==== onClick ====
    public void sw1Click(View v){
        isSw1 = sw1.isChecked();
        Log.d(TAG,"sw1Click()->isSw1="+isSw1);
        if (isSw1){
            sw1Layer.setProperties(
                    visibility(Property.VISIBLE)
                    // import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;
            );
            //Hong Kong Clock Tower
            moveCamera(new LatLng(22.293574, 114.1689735));
        } else {
            sw1Layer.setProperties(
                    visibility(NONE)
            );
        }
    } //sw1Click()
    public void sw2Click(View v){
        isSw2 = sw2.isChecked();
        Log.d(TAG,"sw2Click()->isSw2="+isSw2);
        if (isSw2){
            sw2Layer.setProperties(
                    visibility(Property.VISIBLE)
            );
            //Hong Kong Cultural Centre
            moveCamera(new LatLng(22.293851, 114.170324));
        } else {
            sw2Layer.setProperties(
                    visibility(NONE)
                    // import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
            );
        }
    } //sw2Click()
    public void sw3Click(View v){
        isSw3 = sw3.isChecked();
        Log.d(TAG,"sw3Click()->isSw3="+isSw3);
        if (isSw3){
            lineLayer.setProperties(
                    visibility(Property.VISIBLE)
            );
            //Hong Kong Museum of Art
            moveCamera(new LatLng(22.293501, 114.172137));
        } else {
            lineLayer.setProperties(
                    visibility(NONE)
            );
        }
    } //sw3Click()
    /* <!-- MarkerDemo --> */

} //MainActivity