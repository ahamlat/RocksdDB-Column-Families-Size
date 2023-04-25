package org.ahamlat;

import org.bouncycastle.util.Arrays;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyMetaData;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.util.SizeUnit;

import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws RocksDBException {
        String dbPath = null;
        for (String arg : args) {
            if (arg.startsWith("--dbPath=")) {
                dbPath = arg.substring(9);
            }
        }
        if (dbPath != null) {
            RocksDB.loadLibrary();
            Options options = new Options();
            options.setCreateIfMissing(true);

            // Open the RocksDB database with multiple column families
            List<byte[]> cfNames = RocksDB.listColumnFamilies(options, dbPath);
            List<ColumnFamilyHandle> cfHandles = new ArrayList<>();
            List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
            for (byte[] cfName : cfNames) {
                cfDescriptors.add(new ColumnFamilyDescriptor(cfName));
            }
            boolean emptyColumnFamily;
            try (final RocksDB rocksdb = RocksDB.openReadOnly(dbPath, cfDescriptors, cfHandles)) {
                for (int i = 0; i < cfNames.size(); i++) {
                    emptyColumnFamily = false;
                    byte[] cfName = cfNames.get(i);
                    ColumnFamilyHandle cfHandle = cfHandles.get(i);
                    String size = rocksdb.getProperty(cfHandle, "rocksdb.estimate-live-data-size");
                    if (!size.isEmpty()) {
                        long sizeLong = Long.parseLong(size);
                        if (sizeLong == 0) emptyColumnFamily = true;
                        if (!emptyColumnFamily)
                            System.out.println("****** Column family '" + getNameById(cfName) + "' size: " + formatOutputSize(sizeLong) + " ******");
                    }
                    // System.out.println("SST table : "+ rocksdb.getProperty(cfHandle, "rocksdb.sstables"));
                    if (!emptyColumnFamily) {
                        System.out.println("Number of live snapshots : " + rocksdb.getProperty(cfHandle, "rocksdb.num-snapshots"));
                        String totolSstFilesSize = rocksdb.getProperty(cfHandle, "rocksdb.total-sst-files-size");
                        if (!totolSstFilesSize.isEmpty() && !totolSstFilesSize.isBlank()) {
                            System.out.println("Total size of SST Files : " + formatOutputSize(Long.parseLong(totolSstFilesSize)));
                        }
                        String liveSstFilesSize = rocksdb.getProperty(cfHandle, "rocksdb.live-sst-files-size");
                        if (!liveSstFilesSize.isEmpty() && !liveSstFilesSize.isBlank()) {
                            System.out.println("Size of live SST Filess : " + formatOutputSize(Long.parseLong(liveSstFilesSize)));
                        }

                        ColumnFamilyMetaData columnFamilyMetaData = rocksdb.getColumnFamilyMetaData(cfHandle);
                        long sizeBytes = columnFamilyMetaData.size();
                        System.out.println("Column family size (with getColumnFamilyMetaData) : " + formatOutputSize(sizeBytes));
                        System.out.println("");

                    }
                }
            } finally {
                for (ColumnFamilyHandle cfHandle : cfHandles) {
                    cfHandle.close();
                }
            }
        } else {
            System.out.println("Database path not provided");
        }
    }

    private static String formatOutputSize(long size) {
        if (size > (1024 * 1024 * 1024)) {
            long sizeInGiB = size/(1024 * 1024 * 1024);
            return sizeInGiB+ " GiB";
        }
        else if (size > (1024 * 1024)) {
            long sizeInMiB = size/(1024 * 1024);
            return sizeInMiB+ " MiB";
        }
        else if (size > 1024) {
            long sizeInKiB = size/1024;
            return sizeInKiB+ " KiB";
        } else {
            return size+ " B";
        }
    }

    public static String getNameById(byte[] id) {
        for (KeyValueSegmentIdentifier segment : KeyValueSegmentIdentifier.values()) {
            if (Arrays.areEqual(segment.getId(), id)) {
                return segment.getName();
            }
        }
        return null; // id not found
    }

}
