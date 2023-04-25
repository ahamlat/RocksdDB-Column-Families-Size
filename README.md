# RocksdDB-Column-Families-Size

RocksDB Column Families Size Checker is a Java application that allows you to easily retrieve the size of each column family in a RocksDB database. With this tool, you can quickly identify which column families are taking up the most space and optimize your database accordingly.

**Prerequisites**

Before you can use this application, you must have the following software installed on your system:

* Java 17
* Maven 3.x

**Installing**

To install the application, follow these steps:

* Clone the repository to your local machine.
* Open a terminal and navigate to the project directory.
* Run the following command to build the project:

```
mvn clean compile assembly:single
```

* The compiled jar file will be located in the target directory.

**Running**

To run the application, use the following command:
```
java -jar target/RocksDB-Column-Families-Size-1.0-SNAPSHOT-jar-with-dependencies.jar --dbPath=/path/to/rocksdb
```

