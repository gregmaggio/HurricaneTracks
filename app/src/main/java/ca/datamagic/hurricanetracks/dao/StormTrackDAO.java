package ca.datamagic.hurricanetracks.dao;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormTrackDTO;

public class StormTrackDAO extends BaseDAO {
    public List<StormTrackDTO> tracks(String basin, Integer year, Integer stormNo) throws IOException, InterruptedException {
        List<StormTrackDTO> stormTracks = new ArrayList<StormTrackDTO>();
        if ((basin != null) && (basin.length() > 0) && (year != null) && (stormNo != null)) {
            String query = MessageFormat.format("SELECT number, name, iso_time, EXTRACT(YEAR FROM iso_time AT TIME ZONE \"UTC\") AS iso_year, EXTRACT(MONTH FROM iso_time AT TIME ZONE \"UTC\") AS iso_month, EXTRACT(DAY FROM iso_time AT TIME ZONE \"UTC\") AS iso_day, EXTRACT(HOUR FROM iso_time AT TIME ZONE \"UTC\") AS iso_hours, EXTRACT(MINUTE FROM iso_time AT TIME ZONE \"UTC\") AS iso_minutes, EXTRACT(SECOND FROM iso_time AT TIME ZONE \"UTC\") AS iso_seconds, latitude, longitude, usa_status as status, usa_wind as wind, usa_pressure as pressure  FROM `bigquery-public-data.noaa_hurricanes.hurricanes` WHERE basin = {0} AND season = {1} AND number = {2} ORDER BY iso_time", "'" + basin + "'", "'" + Integer.toString(year) + "'", Integer.toString(stormNo));
            TableResult result = runQuery(query);
            int trackNo = 1;
            for (FieldValueList row : result.iterateAll()) {
                Double wind = null;
                Double pressure = null;
                String status = null;
                if (!row.get("wind").isNull()) {
                    wind = row.get("wind").getDoubleValue();
                }
                if (!row.get("pressure").isNull()) {
                    pressure = row.get("pressure").getDoubleValue();
                }
                if (!row.get("status").isNull()) {
                    status = row.get("status").getStringValue();
                }
                stormTracks.add(new StormTrackDTO(
                        (int) row.get("number").getLongValue(),
                        row.get("name").getStringValue(),
                        trackNo++,
                        (int) row.get("iso_year").getLongValue(),
                        (int) row.get("iso_month").getLongValue(),
                        (int) row.get("iso_day").getLongValue(),
                        (int) row.get("iso_hours").getLongValue(),
                        (int) row.get("iso_minutes").getLongValue(),
                        (int) row.get("iso_seconds").getLongValue(),
                        row.get("latitude").getDoubleValue(),
                        row.get("longitude").getDoubleValue(),
                        wind,
                        pressure,
                        status));
            }
        }
        return stormTracks;
    }
}
