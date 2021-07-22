package ca.datamagic.hurricanetracks.dao;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.BasinDTO;

public class BasinDAO extends BaseDAO {
    public static final List<BasinDTO> basins = new ArrayList<BasinDTO>(){{
        add(new BasinDTO("NA", "North Atlantic",27.042943892507832,-64.06562896264852));
        add(new BasinDTO("EP", "Eastern North Pacific",17.34682960352381,-101.40464744010902));
        add(new BasinDTO("WP", "Western North Pacific",20.393590910059267,133.57668140439355));
        add(new BasinDTO("NI", "North Indian",17.770978228587104,81.49652796447191));
        add(new BasinDTO("SI", "South Indian",-17.595758564542496,76.92105309952329));
        add(new BasinDTO("SP", "Southern Pacific",-19.537684973260493,123.76518808351108));
        add(new BasinDTO("SA", "South Atlantic",-25.933099999999992,-41.72111596638654));
    }};

    public List<BasinDTO> basins() {
        return basins;
    }

    public List<Integer> years(String basin) throws IOException, InterruptedException {
        List<Integer> years = new ArrayList<Integer>();
        if ((basin != null) && (basin.length() > 0)) {
            String query = MessageFormat.format("SELECT DISTINCT season FROM `bigquery-public-data.noaa_hurricanes.hurricanes` WHERE basin = {0} ORDER BY season", "'" + basin + "'");
            TableResult result = runQuery(query);
            for (FieldValueList row : result.iterateAll()) {
                years.add(new Integer(row.get("season").getStringValue()));
            }
        }
        return years;
    }
}
