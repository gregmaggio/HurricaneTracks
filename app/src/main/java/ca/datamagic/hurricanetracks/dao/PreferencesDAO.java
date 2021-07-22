package ca.datamagic.hurricanetracks.dao;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

import ca.datamagic.hurricanetracks.dto.PreferencesDTO;

public class PreferencesDAO {
    private SharedPreferences preferences = null;

    public PreferencesDAO(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PreferencesDTO read() {
        PreferencesDTO dto = new PreferencesDTO();
        dto.setBasin(this.preferences.getString("basin", "NA"));
        dto.setYear(this.preferences.getInt("year", Calendar.getInstance().get(Calendar.YEAR)));
        int stormNo = this.preferences.getInt("stormNo", -1);
        if (stormNo > -1) {
            dto.setStormNo(stormNo);
        }
        return dto;
    }

    public void write(PreferencesDTO dto) {
        String basin = (dto.getBasin() != null) ? dto.getBasin() : "NA";
        Integer year = (dto.getYear() != null) ? dto.getYear() : Calendar.getInstance().get(Calendar.YEAR);
        Integer stormNo = (dto.getStormNo() != null) ? dto.getStormNo() : -1;
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString("basin", basin);
        editor.putInt("year", year);
        editor.putInt("stormNo", stormNo);
        editor.commit();
    }
}
