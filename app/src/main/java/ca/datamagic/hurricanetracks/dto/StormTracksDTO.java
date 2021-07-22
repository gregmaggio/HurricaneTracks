package ca.datamagic.hurricanetracks.dto;

import java.util.ArrayList;
import java.util.List;

public class StormTracksDTO {
    private List<List<StormTrackDTO>> tracks = new ArrayList<List<StormTrackDTO>>();

    public void add(List<StormTrackDTO> tracks) {
        this.tracks.add(tracks);
    }

    public int size() {
        return this.tracks.size();
    }

    public List<StormTrackDTO> get(int index) {
        return this.tracks.get(index);
    }

    public void clear() {
        this.tracks.clear();
    }
}
