package org.ahamlat;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String PATH = "/data/besu/database";
    public static void main(String[] args) throws RocksDBException {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);

        // Open the RocksDB database with multiple column families
        List<byte[]> cfNames = RocksDB.listColumnFamilies(options, PATH );
        List<ColumnFamilyHandle> cfHandles = new ArrayList<>();
        List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
        for (byte[] cfName : cfNames) {
            cfDescriptors.add(new ColumnFamilyDescriptor(cfName));
        }
        try (final RocksDB rocksdb = RocksDB.openReadOnly (PATH, cfDescriptors,cfHandles)) {
            for (int i = 0; i < cfNames.size(); i++) {
                byte[] cfName = cfNames.get(i);
                ColumnFamilyHandle cfHandle = cfHandles.get(i);
                String size = rocksdb.getProperty(cfHandle, "rocksdb.estimate-live-data-size");
                System.out.println("Column family '" + new String(cfName, StandardCharsets.UTF_8) + "' size: " + size);
            }
        } finally {
            for (ColumnFamilyHandle cfHandle : cfHandles) {
                cfHandle.close();
            }
        }
    }}
