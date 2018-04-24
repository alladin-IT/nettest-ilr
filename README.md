checkmynet.lu Server & Android App Source Code
===============

*checkmynet.lu* is an open source, multi-threaded bandwidth test written in Java and
C, consisting of:

- command line client
- Android client
- control Servlet based on Spring
- map Servlet based on Restlet
- statistics Servlet based on Restlet
- test server (written in C)
- qos test server (written in Java)

*checkmynet.lu* is released under the [Apache License, Version 2.0]
 
 [alladin-IT GmbH]: https://alladin.at/
 [Apache License, Version 2.0]: https://www.apache.org/licenses/LICENSE-2.0

The following projects are distributed in this release:

- **control-server** - Servlet acting as control server for the clients
- **mango-query-parser** - Library for parsing Mango queries (CouchDB)
- **nettest-shared** - common libraries and classes
- **nettest-shared-server** - common libraries and classes (server only)
- **spring-data-couchdb** - common libraries and classes
- **RMBTSharedCode** - common libraries and classes
- **RMBTUtil** - common libraries and classes
- **RMBTMapServer** - Servlet acting as map server
- **RMBTStatisticServer** - Servlet acting as statistics server
- **RMBTServer** - speed test server
- **RMBTQoSServer** - qos test server
- **RMBTClient** - client code used by *RMBTAndroid*, the command line client and the Applet
- **RMBTAndroid** - Android App


Dependencies
---------------

The following third party libraries are required dependencies:

### Google Play Services ###

- see <http://developer.android.com/google/play-services/setup.html>.

### Android Support Library ###

- see <http://developer.android.com/tools/extras/support-library.html>

### Spring Framework (Spring Boot) ###

- Apache 2.0 License
- Spring Boot see <https://projects.spring.io/spring-boot/>

### Guava ###

- Apache 2.0 License
- available at <https://code.google.com/p/guava-libraries/>

### Gson ###

- Apache 2.0 License
- see <https://github.com/google/gson>

### Joda-Time ###

- Apache 2.0 License
- see <http://www.joda.org/joda-time/>

### dnsjava ###

- BSD License
- available at <http://www.xbill.org/dnsjava/>

### PostgreSQL JDBC Driver ###

- BSD License
- available at <http://jdbc.postgresql.org/>

### JSON in Java ###

- MIT License (+ "The Software shall be used for Good, not Evil.")
- available at <http://www.json.org/java/index.html>

### Simple Logging Facade for Java (SLF4J) ###

- MIT License
- available at <http://www.slf4j.org/>

### JOpt Simple ###

- MIT License
- available at <http://pholser.github.com/jopt-simple/>


### Apache Commons ###

- Apache 2.0 License
- available at <http://commons.apache.org/>

### spymemcached ###

- MIT License
- see <https://github.com/couchbase/spymemcached>

### Restlet Framework ###

- Version: 2.1
- Licenses:
  - Apache 2.0
  - LGPL license version 3.0
  - LGPL license version 2.1 
  - CDDL license version 1.0 or
  - EPL license version 1.0
- available at <http://restlet.org/>

### PostGIS/ODBC ###

- Version: 2.1
- Licenses:
  - GPL license version 2.0 (for PostGIS)
  - LGPL license version 2.1 (for PostGIS/JDBC)
- available at <http://postgis.net/>

