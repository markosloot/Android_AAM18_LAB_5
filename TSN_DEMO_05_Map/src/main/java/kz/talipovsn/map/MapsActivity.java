package kz.talipovsn.map;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Карта
    private TextView textView; // Текстовый компонет

    private String markerTitle = ""; // Название выбранного маркера
    private String markerFileName = ""; // Имя файла с подробными данными выбранного маркера

    private final int SITYSCALE = 16; // Масштаб для отображения карты

    static final LatLng startMarker = new LatLng(52.273974205057506, 76.96508808368317); // Начальный маркер
    static final LatLng marker1 = new LatLng(52.27381411488606, 76.96595418029119); // Дом
    static final LatLng marker2 = new LatLng(52.27282043881382, 76.96426709627046); // Научная библиотека
    static final LatLng marker3 = new LatLng(52.27364850374857, 76.96414981235478); // Интернет-кафе Rampage
    static final LatLng marker4 = new LatLng(52.27340560630434, 76.9679931160529); // Магазин электроники Белый Ветер
    static final LatLng marker5 = new LatLng(52.27332489001987, 76.9620525526596); // Павлодарский медицинский колледж
    static final LatLng marker6 = new LatLng(52.27510712116781, 76.96203794526905); // Школа-Лицей №10

    static final String CONFIG_FILE_NAME = "Config"; // Имя файла настроек приложения
    private SharedPreferences sPref; // Переменная для работы с настройками программы

    private static final String NORMAL_MAP = "isNorm";
    private static final String SATELLITE_MAP = "isSputnik";
    private static final String RELIEF_MAP = "isRelef";
    private boolean isNormalMap = true;
    private boolean isSatelliteMap = false;
    private boolean isReliefMap = false;

    private static final float ALPHA = 0.8f; // Коэффициент прозрачности для маркеров

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Доступ к карте
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sPref = getSharedPreferences(CONFIG_FILE_NAME, MODE_PRIVATE);

        if (savedInstanceState != null) {
            // Вторичное создание окна после переворачивания экрана
            isNormalMap = savedInstanceState.getBoolean(NORMAL_MAP, isNormalMap);
            isReliefMap = savedInstanceState.getBoolean(RELIEF_MAP, isReliefMap);
            isSatelliteMap = savedInstanceState.getBoolean(SATELLITE_MAP, isSatelliteMap);
        } else {
            // Первый запуск программы до переворачивания экрана
            // Чтение данных с настроек программы
            isNormalMap = sPref.getBoolean(NORMAL_MAP, isNormalMap);
            isReliefMap = sPref.getBoolean(RELIEF_MAP, isReliefMap);
            isSatelliteMap = sPref.getBoolean(SATELLITE_MAP, isSatelliteMap);
        }

        textView = findViewById(R.id.textViewInfo); // Доступ к компоненту "textViewInfo"
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Обычный тип карты
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Добавление маркера на карту с текстом
        mMap.addMarker(new MarkerOptions().position(startMarker).title((getString(R.string.startMarker_title))));

        // Добавление маркера на карту с текстом, иконкой и полупрозрачностью
        mMap.addMarker(new MarkerOptions().position(marker1).title(getString(R.string.marker1_title)).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.home)).alpha(ALPHA).
                snippet(getString(R.string.marker1_txt_click)));

        mMap.addMarker(new MarkerOptions().position(marker2).title(getString(R.string.marker2_title)).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.library)).alpha(ALPHA).
                snippet(getString(R.string.marker2_txt_click)));

        // Добавление маркера на карту с текстом, иконкой и полупрозрачностью
        mMap.addMarker(new MarkerOptions().position(marker3).title((getString(R.string.marker3_title))).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.rampage)).alpha(ALPHA).
                snippet(getString(R.string.marker3_txt_click)));

        mMap.addMarker(new MarkerOptions().position(marker4).title((getString(R.string.marker4_title))).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.wind)).alpha(ALPHA).
                snippet(getString(R.string.marker4_txt_click)));

        mMap.addMarker(new MarkerOptions().position(marker5).title((getString(R.string.marker5_title))).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.college)).alpha(ALPHA).
                snippet(getString(R.string.marker5_txt_click)));

        mMap.addMarker(new MarkerOptions().position(marker6).title((getString(R.string.marker6_title))).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.lyceum)).alpha(ALPHA).
                snippet(getString(R.string.marker6_txt_click)));


        //Разрешение изменения масштаба карты
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Проверка на включенный GPS и позиционирование на карте
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Показать текущее местоположение по GPS
            mMap.setMyLocationEnabled(true);
        }

        // Переход просмотра на карте на нужный маркер c зумом
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMarker, SITYSCALE));

        // Инициализация стартового маркера
        onMarkerClick(getString(R.string.startMarker_id));

        // Обработчик нажатия на маркеры карты
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                MapsActivity.this.onMarkerClick(marker.getId());
                return true;
            }
        });
    }

    // Нажатие на маркер
    public void onMarkerClick(String idMarker) {
        switch (idMarker) {
            case "m0":
                doClickMarker(startMarker, getString(R.string.startMarker_info),
                        getString(R.string.startMarker_title), getString(R.string.startMarker_file));
                break;
            case "m1":
                doClickMarker(marker1, getString(R.string.marker1_info),
                        getString(R.string.marker1_title), getString(R.string.marker1_file));
                break;
            case "m2":
                doClickMarker(marker2, getString(R.string.marker2_info),
                        getString(R.string.marker2_title), getString(R.string.marker2_file));
                break;
            case "m3":
                doClickMarker(marker3, getString(R.string.marker3_info),
                        getString(R.string.marker3_title), getString(R.string.marker3_file));
                break;
            case "m4":
                doClickMarker(marker4, getString(R.string.marker4_info),
                        getString(R.string.marker4_title), getString(R.string.marker4_file));
                break;
            case "m5":
                doClickMarker(marker5, getString(R.string.marker5_info),
                        getString(R.string.marker5_title), getString(R.string.marker5_file));
                break;
            case "m6":
                doClickMarker(marker6, getString(R.string.marker6_info),
                        getString(R.string.marker6_title), getString(R.string.marker6_file));
                break;
        }
    }

    // Обработка нажатия на маркер
    public void doClickMarker(LatLng marker, String info, String markerTitle, String markerFileName) {
        this.markerTitle = markerTitle;
        this.markerFileName = markerFileName;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, SITYSCALE));
        findViewById(R.id.sv1).scrollTo(0, 0);
        if (Build.VERSION.SDK_INT >= 24) {
            textView.setText(Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(info));
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(NORMAL_MAP, isNormalMap);
        savedInstanceState.putBoolean(SATELLITE_MAP, isSatelliteMap);
        savedInstanceState.putBoolean(RELIEF_MAP, isReliefMap);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Сохранение настроек программы в файл настроек
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(NORMAL_MAP, isNormalMap);
        ed.putBoolean(SATELLITE_MAP, isSatelliteMap);
        ed.putBoolean(RELIEF_MAP, isReliefMap);
        ed.apply();
    }

    // Нажатие на кнопку маркера
    public void onClickButtonMarker(View view) {
        String idMarker = view.getTag().toString();
        onMarkerClick(idMarker);
    }

    //     Обработчик кнопки "Подробно"
    public void detailButtonClick(View view) {
        if (!markerFileName.equals("")) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(getString(R.string.tMarker), markerTitle);
            intent.putExtra(getString(R.string.mfile), markerFileName);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), R.string.selectOb, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem menuItemNorm = menu.findItem(R.id.normal_map);
        MenuItem menuItemSputnik = menu.findItem(R.id.satellite_map);
        MenuItem menuItemRelef = menu.findItem(R.id.relief_map);

        if (isNormalMap) {
            menuItemNorm.setChecked(isNormalMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (isSatelliteMap) {
            menuItemSputnik.setChecked(isSatelliteMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            menuItemRelef.setChecked(isReliefMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.normal_map) {
            isNormalMap = true;
            isSatelliteMap = false;
            isReliefMap = false;
            item.setChecked(isNormalMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }

        if (id == R.id.satellite_map) {
            isSatelliteMap = true;
            isNormalMap = false;
            isReliefMap = false;
            item.setChecked(isSatelliteMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        }

        if (id == R.id.relief_map) {
            isReliefMap = true;
            isNormalMap = false;
            isSatelliteMap = false;
            item.setChecked(isReliefMap);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
