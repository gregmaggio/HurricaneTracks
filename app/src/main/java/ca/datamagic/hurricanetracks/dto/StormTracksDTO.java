package ca.datamagic.hurricanetracks.dto;

import java.util.ArrayList;
import java.util.List;

public class StormTracksDTO {
    private List<List<StormTrackDTO>> _tracks = new ArrayList<List<StormTrackDTO>>();

    public void add(List<StormTrackDTO> tracks) {
        _tracks.add(tracks);
    }

    public int size() {
        return _tracks.size();
    }

    public List<StormTrackDTO> get(int index) {
        return _tracks.get(index);
    }

    public void clear() {
        _tracks.clear();
    }
}
