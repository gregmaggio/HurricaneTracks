package ca.datamagic.hurricanetracks;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.async.AsyncTaskListener;
import ca.datamagic.hurricanetracks.async.AsyncTaskResult;
import ca.datamagic.hurricanetracks.async.BasinTask;
import ca.datamagic.hurricanetracks.async.SearchTask;
import ca.datamagic.hurricanetracks.async.StormTask;
import ca.datamagic.hurricanetracks.async.StormTrackTask;
import ca.datamagic.hurricanetracks.async.YearTask;
import ca.datamagic.hurricanetracks.dto.BasinDTO;
import ca.datamagic.hurricanetracks.dto.StormDTO;
import ca.datamagic.hurricanetracks.dto.StormKeyDTO;
import ca.datamagic.hurricanetracks.dto.StormTrackDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, SearchView.OnCloseListener{
    private static Logger _logger = LogFactory.getLogger(MainActivity.class);
    private static NumberFormat _dateNumberFormat = null;
    private Spinner _basin = null;
    private Spinner _year = null;
    private Spinner _storm = null;
    private GoogleMap _map = null;
    private List<BasinDTO> _basins = null;
    private String _currentBasin = null;
    private List<Integer> _currentYears = null;
    private Integer _currentYear = null;
    private List<StormDTO> _currentStorms = null;
    private StormDTO _allStorms = new StormDTO(0, "ALL", 0);
    private Integer _currentStorm = null;
    private LatLngBounds.Builder _builder = null;
    private Hashtable<Integer, Boolean> _loadedStormTracks = new Hashtable<Integer, Boolean>();
    private Marker _selectedMarker = null;
    private boolean _processing = false;
    private SearchManager _manager = null;
    private SearchView _search = null;
    private SearchTask _searchTask = null;

    static {
        _dateNumberFormat = new DecimalFormat();
        _dateNumberFormat.setMinimumIntegerDigits(2);
        _dateNumberFormat.setMaximumIntegerDigits(2);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _processing = true;

        initializeLogging();
        setContentView(R.layout.main_layout);

        _basin = findViewById(R.id.basin);
        _basin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (_processing) {
                    return;
                }
                if (_basin.getSelectedItemPosition() > -1) {
                    String selectedBasin = _basins.get(_basin.getSelectedItemPosition()).getName();
                    if (selectedBasin.compareToIgnoreCase(_currentBasin) != 0) {
                        _currentBasin = selectedBasin;
                        _currentYear = null;
                        _currentStorm = null;
                        _processing = true;
                        loadYears();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _year = findViewById(R.id.year);
        _year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (_processing) {
                    return;
                }
                if (_year.getSelectedItemPosition() > -1) {
                    Integer selectedYear = _currentYears.get(_year.getSelectedItemPosition());
                    if (selectedYear.intValue() != _currentYear.intValue()) {
                        _currentYear = selectedYear;
                        _currentStorm = null;
                        _processing = true;
                        loadStorms();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _storm = findViewById(R.id.storm);
        _storm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (_processing) {
                    return;
                }
                if (_storm.getSelectedItemPosition() > -1) {
                    StormDTO selectedStorm = _currentStorms.get(_storm.getSelectedItemPosition());
                    if (selectedStorm.getStormNo().intValue() != _currentStorm.intValue()) {
                        _currentStorm = selectedStorm.getStormNo();
                        _processing = true;
                        loadStormTracks();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadBasins();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        _manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        _search = (SearchView) menu.findItem(R.id.search).getActionView();
        _search.setSearchableInfo(_manager.getSearchableInfo(getComponentName()));
        _search.setIconified(true);
        _search.setIconifiedByDefault(true);
        _search.setFocusable(false);
        _search.setOnQueryTextListener(this);
        _search.setOnSuggestionListener(this);
        _search.setOnCloseListener(this);
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
        _map = googleMap;
        _map.setInfoWindowAdapter(new StormInfoWindowAdapter(getBaseContext()));
        UiSettings settings = _map.getUiSettings();
        settings.setZoomControlsEnabled(true);
        _processing = false;
    }

    private void loadBasinSpinner() {
        int selectedIndex = -1;
        String[] basins = new String[_basins.size()];
        for (int ii = 0; ii < _basins.size(); ii++) {
            basins[ii] = _basins.get(ii).getName();
            if (_currentBasin != null) {
                if (_currentBasin.compareToIgnoreCase(basins[ii]) == 0) {
                    selectedIndex = ii;
                }
            }
        }
        loadSpinner(_basin, basins, selectedIndex);
    }

    private void loadYearSpinner() {
        int selectedIndex = -1;
        String[] years = new String[_currentYears.size()];
        for (int ii = 0; ii < _currentYears.size(); ii++) {
            years[ii] = Integer.toString(_currentYears.get(ii).intValue());
            if (_currentYear != null) {
                if (_currentYear.intValue() == _currentYears.get(ii).intValue()) {
                    selectedIndex = ii;
                }
            }
        }
        loadSpinner(_year, years, selectedIndex);
    }

    private void loadStormSpinner() {
        int selectedIndex = -1;
        String[] storms = new String[_currentStorms.size()];
        for (int ii = 0; ii < _currentStorms.size(); ii++) {
            storms[ii] = _currentStorms.get(ii).getStormName();
            if (_currentStorm != null) {
                if (_currentStorm.intValue() == _currentStorms.get(ii).getStormNo().intValue()) {
                    selectedIndex = ii;
                }
            }
        }
        if (_currentStorm == null) {
            selectedIndex = 0;
        }
        loadSpinner(_storm, storms, selectedIndex);
    }

    private void loadSpinner(Spinner spinner, String[] items, int selectedIndex) {
        spinner.setAdapter(new ListViewAdapter(getBaseContext(), items));
        spinner.setSelection(selectedIndex);
    }

    private void setSpinnerSelection(Spinner spinner, String selectionText) {
        for (int ii = 0; ii < spinner.getAdapter().getCount(); ii++) {
            if (spinner.getAdapter().getItem(ii).toString().compareToIgnoreCase(selectionText) == 0) {
                spinner.setSelection(ii);
                break;
            }
        }
    }

    private void setBasinSelection(String basin) {
        setSpinnerSelection(_basin, basin);
    }

    private void setYearSelection(Integer year) {
        setSpinnerSelection(_year, Integer.toString(year.intValue()));
    }

    private void setStormSelection(String stormName) {
        setSpinnerSelection(_storm, stormName);
    }

    private void loadBasins() {
        BasinTask task = new BasinTask();
        task.addListener(new AsyncTaskListener<List<BasinDTO>>() {
            @Override
            public void completed(AsyncTaskResult<List<BasinDTO>> result) {
                try {
                    if (result.getResult() != null) {
                        _basins = result.getResult();
                        if (_currentBasin == null) {
                            _currentBasin = _basins.get(0).getName();
                        }
                        loadBasinSpinner();
                        loadYears();
                    } else {
                        // TODO
                        _processing = false;
                    }
                } catch (Throwable t) {
                    // TODO
                    _processing = false;
                }
            }
        });
        task.execute((Void)null);
    }

    private void loadYears() {
        YearTask task = new YearTask(_currentBasin);
        task.addListener(new AsyncTaskListener<List<Integer>>() {
            @Override
            public void completed(AsyncTaskResult<List<Integer>> result) {
                try {
                    if (result.getResult() != null) {
                        _currentYears = result.getResult();
                        if (_currentYear == null) {
                            _currentYear = _currentYears.get(_currentYears.size() - 1);
                        }
                        loadYearSpinner();
                        loadStorms();
                    } else {
                        // TODO
                        _processing = false;
                    }
                } catch (Throwable t) {
                    // TODO
                    _processing = false;
                }
            }
        });
        task.execute((Void)null);
    }

    private void loadStorms() {
        StormTask task = new StormTask(_currentBasin, _currentYear);
        task.addListener(new AsyncTaskListener<List<StormDTO>>() {
            @Override
            public void completed(AsyncTaskResult<List<StormDTO>> result) {
                try {
                    if (result.getResult() != null) {
                        _currentStorms = result.getResult();
                        _currentStorms.add(0, _allStorms);
                        if (_currentStorm == null) {
                            _currentStorm = _currentStorms.get(0).getStormNo();
                        }
                        loadStormSpinner();
                        loadStormTracks();
                    } else {
                        // TODO
                        _processing = false;
                    }
                } catch (Throwable t) {
                    // TODO
                    _processing = false;
                }
            }
        });
        task.execute((Void)null);
    }

    private void loadStormTracks() {
        _map.clear();
        _builder = new LatLngBounds.Builder();
        _loadedStormTracks.clear();
        _selectedMarker = null;
        if (_currentStorm.intValue() == 0) {
            for (int ii = 0; ii < _currentStorms.size(); ii++) {
                if (_currentStorms.get(ii).getStormNo().intValue() != 0) {
                    _loadedStormTracks.put(_currentStorms.get(ii).getStormNo(), Boolean.FALSE);
                    loadStormTracks(_currentStorms.get(ii).getStormNo());
                }
            }
        } else  {
            _loadedStormTracks.put(_currentStorm, Boolean.FALSE);
            loadStormTracks(_currentStorm.intValue());
        }
    }

    private void loadStormTracks(int stormNo) {
        StormTrackTask task = new StormTrackTask(_currentBasin, _currentYear, stormNo);
        task.addListener(new AsyncTaskListener<List<StormTrackDTO>>() {
            @Override
            public void completed(AsyncTaskResult<List<StormTrackDTO>> result) {
                try {
                    if (result.getResult() != null) {
                        if (result.getResult().size() > 0) {
                            setLoaded(result.getResult().get(0).getStormNo());
                            renderStormTracks(result.getResult());
                            moveCameraIfAllLoaded();
                        } else {
                            // TODO
                            _processing = false;
                        }
                    } else {
                        // TODO
                        _processing = false;
                    }
                } catch (Throwable t) {
                    // TODO
                    _processing = false;
                }
            }
        });
        task.execute((Void)null);
    }

    private void renderStormTracks(List<StormTrackDTO> tracks) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(1);
        polylineOptions.color(Color.BLACK);
        for (int ii = 0; ii < tracks.size(); ii++) {
            StormTrackDTO track = tracks.get(ii);
            String dateTime = MessageFormat.format("{0}-{1}-{2} {3}:{4}", Integer.toString(track.getYear().intValue()), _dateNumberFormat.format(track.getMonth().longValue()), _dateNumberFormat.format(track.getDay().longValue()), _dateNumberFormat.format(track.getHours().longValue()), _dateNumberFormat.format(track.getMinutes().longValue()));
            String status = track.getStatus();
            if (track.getCategory() != null) {
                status += " (Cat. " + track.getCategory().toString() + ")";
            }
            StringBuffer info = new StringBuffer();
            info.append("Date/Time: " + dateTime + "\n");
            info.append("Status: " + status + "\n");
            info.append("Wind Speed: " + track.getMaxWindSpeed() + "\n");
            info.append("Pressure: " + track.getMinPressure());
            LatLng latLng = new LatLng(track.getY(), track.getX());
            polylineOptions.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(track.getStormName());
            markerOptions.snippet(info.toString());
            _map.addMarker(markerOptions);
            _builder.include(latLng);
        }
        _map.addPolyline(polylineOptions);
    }

    private synchronized void setLoaded(Integer stormNo) {
        _loadedStormTracks.put(stormNo, Boolean.TRUE);
    }

    private synchronized void moveCameraIfAllLoaded() {
        boolean allLoaded = true;
        Enumeration<Boolean> elements = _loadedStormTracks.elements();
        while (elements.hasMoreElements()) {
            if (!elements.nextElement().booleanValue()) {
                allLoaded = false;
                break;
            }
        }
        if (allLoaded) {
            _map.moveCamera(CameraUpdateFactory.newLatLngBounds(_builder.build(), 50));
            _processing = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (_selectedMarker != null) {
            if (marker.equals(_selectedMarker)) {
                _selectedMarker = null;
                return true;
            }
        }
        _selectedMarker = marker;
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        _selectedMarker = null;
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
        if (_processing) {
            return true;
        }
        if ((newText == null) || (newText.length() < 1)) {
            return true;
        }
        if (_searchTask != null) {
            return true;
        }
        _searchTask = new SearchTask(newText);
        _searchTask.addListener(new AsyncTaskListener<List<StormKeyDTO>>() {
            @Override
            public void completed(AsyncTaskResult<List<StormKeyDTO>> result) {
                if (result.getThrowable() != null) {
                    if (_logger != null) {
                        _logger.log(Level.WARNING, "Error retrieving search results for text.", result.getThrowable());
                    }
                } else {
                    List<StormKeyDTO> searchResults = result.getResult();
                    _logger.info("searchResults: " + searchResults.size());
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
                    _search.setSuggestionsAdapter(new StormSearchAdapter(getBaseContext(), cursor));
                }
                _searchTask = null;
            }
        });
        _searchTask.execute((Void)null);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        if (_processing) {
            return true;
        }
        if (_searchTask != null) {
            return true;
        }
        Object suggestion = _search.getSuggestionsAdapter().getItem(position);
        Cursor cursor = (Cursor)suggestion;
        int basinIndex = cursor.getColumnIndex("basin");
        int yearIndex = cursor.getColumnIndex("year");
        int stormNoIndex = cursor.getColumnIndex("stormNo");
        int stormNameIndex = cursor.getColumnIndex("stormName");
        String basin = cursor.getString(basinIndex);
        Integer year = cursor.getInt(yearIndex);
        Integer stormNo = cursor.getInt(stormNoIndex);
        String stormName = cursor.getString(stormNameIndex);

        if (_currentBasin.compareToIgnoreCase(basin) != 0) {
            setBasinSelection(basin);
            _currentBasin = basin;
            _currentYear = year;
            _currentStorm = stormNo;
            _processing = true;
            loadYears();
            dismissSearch();
        } else if (_currentYear.intValue() != year.intValue()) {
            setYearSelection(year);
            _currentYear = year;
            _currentStorm = stormNo;
            _processing = true;
            loadStorms();
            dismissSearch();
        } else  if (_currentStorm.intValue() != stormNo.intValue()) {
            setStormSelection(stormName);
            _currentStorm = stormNo;
            _processing = true;
            loadStormTracks();
            dismissSearch();
        }
        return true;
    }

    private void dismissSearch() {
        _search.setIconified(true);
        _search.setQuery("", false);
        _search.clearFocus();
        _search.onActionViewCollapsed();
    }
}
