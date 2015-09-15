package nl.nc.ahlp;

import nl.nc.ahlp.impl.LogEntry;

public interface LogParserListener {
	void update(LogEntry request);
}
