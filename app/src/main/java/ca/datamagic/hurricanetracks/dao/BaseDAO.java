package ca.datamagic.hurricanetracks.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.logging.LogFactory;
import ca.datamagic.hurricanetracks.util.IOUtils;

public class BaseDAO {
    private static Logger _logger = LogFactory.getLogger(BaseDAO.class);

    protected String get(String urlSpec) throws IOException {
        _logger.info("get: " + urlSpec);
        URL url = new URL(urlSpec);
        InputStream input = null;
        try {
            input = url.openStream();
            return IOUtils.readEntireStream(input);
        } finally {
            if (input != null) {
                IOUtils.closeQuietly(input);
            }
        }
    }
}
