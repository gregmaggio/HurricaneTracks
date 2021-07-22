package ca.datamagic.hurricanetracks;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.appcompat.widget.Toolbar;
import ca.datamagic.hurricanetracks.async.AsyncTaskListener;
import ca.datamagic.hurricanetracks.async.AsyncTaskResult;
import ca.datamagic.hurricanetracks.async.BasinTask;
import ca.datamagic.hurricanetracks.async.SearchTask;
import ca.datamagic.hurricanetracks.async.StormTask;
import ca.datamagic.hurricanetracks.async.StormTrackTask;
import ca.datamagic.hurricanetracks.async.Workflow;
import ca.datamagic.hurricanetracks.async.WorkflowListener;
import ca.datamagic.hurricanetracks.async.WorkflowStep;
import ca.datamagic.hurricanetracks.async.YearTask;
import ca.datamagic.hurricanetracks.dao.BaseDAO;
import ca.datamagic.hurricanetracks.dao.PreferencesDAO;
import ca.datamagic.hurricanetracks.dto.BasinDTO;
import ca.datamagic.hurricanetracks.dto.PreferencesDTO;
import ca.datamagic.hurricanetracks.dto.StormDTO;
import ca.datamagic.hurricanetracks.dto.StormKeyDTO;
import ca.datamagic.hurricanetracks.dto.StormTrackDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;
import ca.datamagic.hurricanetracks.util.IOUtils;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, SearchView.OnCloseListener{
    private static final Logger logger = LogFactory.getLogger(MainActivity.class);
    private static NumberFormat dateNumberFormat = null;
    private final BasinDropDownListener basinDropDownListener = new BasinDropDownListener();
    private Spinner basin = null;
    private final YearDropDownListener yearDropDownListener = new YearDropDownListener();
    private Spinner year = null;
    private final StormDropDownListener stormDropDownListener = new StormDropDownListener();
    private Spinner storm = null;
    private GoogleMap map = null;
    private final RefreshListener refreshListener = new RefreshListener();
    private BasinTask basinTask = null;
    private final BasinListener basinListener = new BasinListener();
    private List<BasinDTO> currentBasins = null;
    private String currentBasin = null;
    private YearTask yearTask = null;
    private final YearListener yearListener = new YearListener();
    private List<Integer> currentYears = null;
    private Integer currentYear = null;
    private StormTask stormTask = null;
    private final StormListener stormListener = new StormListener();
    private List<StormDTO> currentStorms = null;
    private Integer currentStorm = null;
    private StormTrackTask stormTrackTask = null;
    private final StormTrackListener stormTrackListener = new StormTrackListener();
    private List<StormTrackDTO> currentStormTracks = null;
    private LatLngBounds.Builder builder = null;
    private Marker selectedMarker = null;
    private boolean processing = false;
    private SearchManager manager = null;
    private SearchView search = null;
    private SearchTask searchTask = null;
    private ProgressBar progressBar = null;

    static {
        dateNumberFormat = new DecimalFormat();
        dateNumberFormat.setMinimumIntegerDigits(2);
        dateNumberFormat.setMaximumIntegerDigits(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLogging();
        setContentView(R.layout.main_layout);

        InputStream inputStream = null;
        try {
            inputStream = getResources().openRawResource(R.raw.bigquery);
            BaseDAO.setAppKey(IOUtils.readEntireStream(inputStream));
        } catch (Throwable t) {
            logger.warning("Exception: " + t.getMessage());
        }
        IOUtils.closeQuietly(inputStream);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        this.basin = findViewById(R.id.basin);
        this.basin.setOnItemSelectedListener(this.basinDropDownListener);
        this.year = findViewById(R.id.year);
        this.year.setOnItemSelectedListener(this.yearDropDownListener);
        this.storm = findViewById(R.id.storm);
        this.storm.setOnItemSelectedListener(this.stormDropDownListener);
        this.progressBar = findViewById(R.id.progressBar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.readPreferences();
        this.refresh();
    }

    private void initializeLogging() {
        try {
            File intPath = getFilesDir();
            String filesPath = intPath.getAbsolutePath();
            LogFactory.initialize(Level.ALL, filesPath, true);
        } catch (Throwable t) {
            // Do Nothing
        }
    }

    public void readPreferences() {
        PreferencesDAO dao = new PreferencesDAO(getBaseContext());
        PreferencesDTO preferences = dao.read();
        this.currentBasin = preferences.getBasin();
        this.currentYear = preferences.getYear();
        this.currentStorm = preferences.getStormNo();
    }

    public void writePreferences() {
        PreferencesDAO dao = new PreferencesDAO(getBaseContext());
        PreferencesDTO preferences = new PreferencesDTO();
        preferences.setBasin(this.currentBasin);
        preferences.setYear(this.currentYear);
        preferences.setStormNo(this.currentStorm);
        dao.write(preferences);
    }

    public void refresh() {
        if (this.processing) {
            return;
        }
        try {
            this.processing = true;
            this.basinTask = null;
            this.yearTask = null;
            this.stormTask = null;
            this.stormTrackTask = null;
            this.progressBar.setVisibility(View.VISIBLE);

            Workflow workflow = new Workflow();
            if (this.currentBasins == null) {
                this.basinTask = new BasinTask();
                workflow.addStep(new WorkflowStep(this.basinTask, this.basinListener));
            }
            if (this.currentYears == null) {
                this.yearTask = new YearTask(this.currentBasin);
                workflow.addStep(new WorkflowStep(this.yearTask, this.yearListener));
            }
            if (this.currentStorms == null) {
                this.stormTask = new StormTask(this.currentBasin, this.currentYear);
                workflow.addStep(new WorkflowStep(this.stormTask, this.stormListener));
            }
            if (this.currentStormTracks == null) {
                this.stormTrackTask = new StormTrackTask(this.currentBasin, this.currentYear, this.currentStorm);
                workflow.addStep(new WorkflowStep(this.stormTrackTask, this.stormTrackListener));
            }
            workflow.addListener(this.refreshListener);
            workflow.start();
        } catch (Throwable t) {
            // TODO: Show Error
            if (logger != null) {
                logger.log(Level.WARNING, "Unknown Exception in refresh.", t);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.search = (SearchView) menu.findItem(R.id.search).getActionView();
        this.search.setSearchableInfo(this.manager.getSearchableInfo(getComponentName()));
        this.search.setIconified(true);
        this.search.setIconifiedByDefault(true);
        this.search.setFocusable(false);
        this.search.setOnQueryTextListener(this);
        this.search.setOnSuggestionListener(this);
        this.search.setOnCloseListener(this);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setInfoWindowAdapter(new StormInfoWindowAdapter(getBaseContext()));
        UiSettings settings = this.map.getUiSettings();
        settings.setZoomControlsEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBasinSpinner() {
        int selectedIndex = -1;
        String[] basins = new String[0];
        if (this.currentBasins != null) {
            basins = new String[this.currentBasins.size()];
            for (int ii = 0; ii < this.currentBasins.size(); ii++) {
                String name = this.currentBasins.get(ii).getName();
                String description = this.currentBasins.get(ii).getDescription();
                basins[ii] = description;
                if (this.currentBasin != null) {
                    if (this.currentBasin.compareToIgnoreCase(name) == 0) {
                        selectedIndex = ii;
                    }
                }
            }
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
        }
        loadSpinner(this.basin, basins, selectedIndex);
        this.currentBasin = null;
        if ((this.currentBasins != null) && (selectedIndex > -1)) {
            this.currentBasin = this.currentBasins.get(selectedIndex).getName();
        }
    }

    private void loadYearSpinner() {
        int selectedIndex = -1;
        String[] years = new String[0];
        if (this.currentYears != null) {
            years = new String[this.currentYears.size()];
            for (int ii = 0; ii < this.currentYears.size(); ii++) {
                String year = Integer.toString(this.currentYears.get(ii).intValue());
                years[ii] = year;
                if (this.currentYear != null) {
                    if (this.currentYear.intValue() == this.currentYears.get(ii).intValue()) {
                        selectedIndex = ii;
                    }
                }
            }
            if (selectedIndex < 0) {
                selectedIndex = this.currentYears.size() - 1;
            }
        }
        loadSpinner(this.year, years, selectedIndex);
        this.currentYear = null;
        if ((this.currentYears != null) && (selectedIndex > -1)) {
            this.currentYear = this.currentYears.get(selectedIndex);
        }
    }

    private void loadStormSpinner() {
        int selectedIndex = -1;
        String[] storms = new String[this.currentStorms.size()];
        for (int ii = 0; ii < this.currentStorms.size(); ii++) {
            storms[ii] = this.currentStorms.get(ii).getStormName();
            if (this.currentStorm != null) {
                if (this.currentStorm.intValue() == this.currentStorms.get(ii).getStormNo().intValue()) {
                    selectedIndex = ii;
                }
            }
        }
        if (this.currentStorm == null) {
            selectedIndex = this.currentStorms.size() - 1;
        }
        loadSpinner(this.storm, storms, selectedIndex);
        this.currentStorm = null;
        if ((this.currentStorms != null) && (selectedIndex > -1)) {
            this.currentStorm = this.currentStorms.get(selectedIndex).getStormNo();
        }
    }

    private void loadSpinner(Spinner spinner, String[] items, int selectedIndex) {
        SpinnerAdapter adapter = new SpinnerAdapter(getBaseContext(), items);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
    }

    private void renderStormTracks(List<StormTrackDTO> tracks) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(1);
        polylineOptions.color(Color.BLACK);
        for (int ii = 0; ii < tracks.size(); ii++) {
            StormTrackDTO track = tracks.get(ii);
            String dateTime = MessageFormat.format("{0}-{1}-{2} {3}:{4}", Integer.toString(track.getYear().intValue()), dateNumberFormat.format(track.getMonth().longValue()), dateNumberFormat.format(track.getDay().longValue()), dateNumberFormat.format(track.getHours().longValue()), dateNumberFormat.format(track.getMinutes().longValue()));
            String status = track.getStatus();
            if (track.getCategory() != null) {
                status += " (Cat. " + track.getCategory().toString() + ")";
            }
            StringBuffer info = new StringBuffer();
            info.append("Date/Time: " + dateTime + "\n");
            info.append("Status: " + status + "\n");
            info.append("Wind Speed: " + track.getMaxWindSpeed() + "\n");
            info.append("Pressure: " + track.getMinPressure());
            LatLng latLng = new LatLng(track.getLatitude(), track.getLongitude());
            polylineOptions.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(track.getStormName());
            markerOptions.snippet(info.toString());
            this.map.addMarker(markerOptions);
            this.builder.include(latLng);
        }
        this.map.addPolyline(polylineOptions);
    }

    private void moveCamera() {
        this.map.moveCamera(CameraUpdateFactory.newLatLngBounds(this.builder.build(), 50));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (this.selectedMarker != null) {
            if (marker.equals(this.selectedMarker)) {
                this.selectedMarker = null;
                return true;
            }
        }
        this.selectedMarker = marker;
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.selectedMarker = null;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (this.processing) {
            return true;
        }
        if ((newText == null) || (newText.length() < 1)) {
            return true;
        }
        if (this.searchTask != null) {
            return true;
        }
        this.searchTask = new SearchTask(newText);
        this.searchTask.addListener(new AsyncTaskListener<List<StormKeyDTO>>() {
            @Override
            public void completed(AsyncTaskResult<List<StormKeyDTO>> result) {
                if (result.getThrowable() != null) {
                    if (logger != null) {
                        logger.log(Level.WARNING, "Error retrieving search results for text.", result.getThrowable());
                    }
                } else {
                    List<StormKeyDTO> searchResults = result.getResult();
                    logger.info("searchResults: " + searchResults.size());
                    String[] columns = new String[]{"_id", "stormKey", "basin", "year", "stormNo", "stormName"};
                    MatrixCursor cursor = new MatrixCursor(columns);
                    for (int ii = 0; ii < searchResults.size(); ii++) {
                        StormKeyDTO stormKey = searchResults.get(ii);
                        Object[] temp = new Object[]
                                {
                                        ii,
                                        stormKey.getStormKey(),
                                        stormKey.getBasin(),
                                        stormKey.getYear(),
                                        stormKey.getStormNo(),
                                        stormKey.getStormName()
                                };
                        cursor.addRow(temp);
                    }
                    search.setSuggestionsAdapter(new StormSearchAdapter(getBaseContext(), cursor));
                }
                searchTask = null;
            }
        });
        this.searchTask.execute((Void)null);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        if (this.processing) {
            return true;
        }
        if (this.searchTask != null) {
            return true;
        }
        Object suggestion = this.search.getSuggestionsAdapter().getItem(position);
        Cursor cursor = (Cursor)suggestion;
        int basinIndex = cursor.getColumnIndex("basin");
        int yearIndex = cursor.getColumnIndex("year");
        int stormNoIndex = cursor.getColumnIndex("stormNo");
        int stormNameIndex = cursor.getColumnIndex("stormName");
        String basin = cursor.getString(basinIndex);
        Integer year = cursor.getInt(yearIndex);
        Integer stormNo = cursor.getInt(stormNoIndex);
        String stormName = cursor.getString(stormNameIndex);

        this.currentBasins = null;
        this.currentBasin = basin;
        this.currentYears = null;
        this.currentYear = year;
        this.currentStorms = null;
        this.currentStorm = stormNo;
        this.currentStormTracks = null;
        refresh();
        dismissSearch();

        /*
        if (this.currentBasin.compareToIgnoreCase(basin) != 0) {
            this.currentBasin = basin;
            this.currentYear = year;
            this.currentStorm = stormNo;
            this.processing = true;
            refresh();
            dismissSearch();
        } else if (this.currentYear.intValue() != year.intValue()) {
            this.currentYear = year;
            this.currentStorm = stormNo;
            this.processing = true;
            refresh();
            dismissSearch();
        } else  if (this.currentStorm.intValue() != stormNo.intValue()) {
            this.currentStorm = stormNo;
            this.processing = true;
            refresh();
            dismissSearch();
        }
         */
        return true;
    }

    private void dismissSearch() {
        this.search.setIconified(true);
        this.search.setQuery("", false);
        this.search.clearFocus();
        this.search.onActionViewCollapsed();
    }

    private class BasinDropDownListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (processing) {
                return;
            }
            currentBasin = currentBasins.get(i).getName();
            currentBasins = null;
            currentYear = null;
            currentYears = null;
            currentStorm = null;
            currentStorms = null;
            currentStormTracks = null;
            refresh();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class YearDropDownListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (processing) {
                return;
            }
            currentYear = currentYears.get(i);
            currentStorm = null;
            currentStorms = null;
            currentStormTracks = null;
            refresh();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class StormDropDownListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (processing) {
                return;
            }
            currentStorm = currentStorms.get(i).getStormNo();
            currentStormTracks = null;
            refresh();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class RefreshListener implements WorkflowListener {
        @Override
        public void completed(boolean success) {
            writePreferences();
            processing = false;
            progressBar.setVisibility(View.GONE);
        }
    }

    private class BasinListener implements AsyncTaskListener<List<BasinDTO>> {
        @Override
        public void completed(AsyncTaskResult<List<BasinDTO>> result) {
            try {
                currentBasins = null;
                if (result.getThrowable() != null) {
                    logger.warning("Exception in BasinTask: " + result.getThrowable().getMessage());
                } else {
                    currentBasins = result.getResult();
                }
                loadBasinSpinner();
                yearTask.setBasin(currentBasin);
                stormTask.setBasin(currentBasin);
                stormTrackTask.setBasin(currentBasin);
            } catch (Throwable t) {
                logger.warning("Exception in BasinListener: " + t.getMessage());
            }
        }
    }

    private class YearListener implements AsyncTaskListener<List<Integer>> {
        @Override
        public void completed(AsyncTaskResult<List<Integer>> result) {
            try {
                currentYears = null;
                if (result.getThrowable() != null) {
                    logger.warning("Exception in YearTask: " + result.getThrowable().getMessage());
                } else {
                    currentYears = result.getResult();
                }
                loadYearSpinner();
                stormTask.setYear(currentYear);
                stormTrackTask.setYear(currentYear);
            } catch (Throwable t) {
                logger.warning("Exception in YearListener: " + t.getMessage());
            }
        }
    }

    private class StormListener implements AsyncTaskListener<List<StormDTO>> {
        @Override
        public void completed(AsyncTaskResult<List<StormDTO>> result) {
            try {
                currentStorms = null;
                if (result.getThrowable() != null) {
                    logger.warning("Exception in StormTask: " + result.getThrowable().getMessage());
                } else {
                    currentStorms = result.getResult();
                }
                loadStormSpinner();
                if (currentStorm != null) {
                    stormTrackTask.setStormNo(currentStorm);
                }
            } catch (Throwable t) {
                logger.warning("Exception in StormListener: " + t.getMessage());
            }
        }
    }

    private class StormTrackListener implements AsyncTaskListener<List<StormTrackDTO>> {
        @Override
        public void completed(AsyncTaskResult<List<StormTrackDTO>> result) {
            try {
                currentStormTracks = null;
                if (result.getThrowable() != null) {
                    logger.warning("Exception in StormTrackTask: " + result.getThrowable().getMessage());
                } else {
                    currentStormTracks = result.getResult();
                }
                map.clear();
                builder = new LatLngBounds.Builder();
                selectedMarker = null;
                if (currentStormTracks != null) {
                    renderStormTracks(currentStormTracks);
                    moveCamera();
                }
            } catch (Throwable t) {
                logger.warning("Exception in StormTrackListener: " + t.getMessage());
            }
        }
    }
}
