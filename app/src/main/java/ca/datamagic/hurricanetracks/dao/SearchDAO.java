package ca.datamagic.hurricanetracks.dao;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormKeyDTO;

public class SearchDAO extends BaseDAO {
    public List<StormKeyDTO> search(String searchText) throws IOException, InterruptedException {
        List<StormKeyDTO> stormKeys = new ArrayList<StormKeyDTO>();
        String query = MessageFormat.format("SELECT DISTINCT basin, season, number, name FROM `bigquery-public-data.noaa_hurricanes.hurricanes` WHERE name like {0} ORDER BY basin, season, number, name LIMIT 25", "'" + searchText.trim().toUpperCase() + "%'");
        TableResult result = runQuery(query);
        for (FieldValueList row : result.iterateAll()) {
            String basin = row.get("basin").getStringValue();
            Integer year = Integer.parseInt(row.get("season").getStringValue());
            Integer stormNo = (int)row.get("number").getLongValue();
            String stormName = row.get("name").getStringValue();
            String stormKey = MessageFormat.format("{0}-{1}-{2}", basin, year, stormNo);
            stormKeys.add(new StormKeyDTO(stormKey, basin, year, stormNo, stormName));
        }
        return stormKeys;
    }
}
