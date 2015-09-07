# AHLP (Apache HTTPD Log Parser)

Currently this parser can only parse Apache HTTPD access logs. This is a work in progress.

## Java and SWT versions
- Java: 1.7
- SWT: 4.335(win32) - x86_64

## Issues
- Using List to store accesslog lines (Memory issues).
- The access log format is static. See: nl.nc.ahlp.impl.ApacheHttpdAccessLogParser.java