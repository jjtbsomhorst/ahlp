package nl.nc.ahlp;

import java.util.Map;

public interface LogParserListener {
	void update(Map<String, String> request);
}
