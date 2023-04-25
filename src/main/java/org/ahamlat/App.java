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
                if (!size.isEmpty()) {
                    long sizeLong = Long.parseLong(size);
                    System.out.println("Column family '" + new String(cfName, StandardCharsets.UTF_8) + "' size: " +formatOutputSize(sizeLong));
                }
                // System.out.println("SST table : "+ rocksdb.getProperty(cfHandle, "rocksdb.sstables"));
                System.out.println("Number of live snapshots : "+ rocksdb.getProperty(cfHandle, "rocksdb.num-snapshots"));
                String totolSstFilesSize = rocksdb.getProperty(cfHandle, "rocksdb.total-sst-files-size");
                if (!totolSstFilesSize.isEmpty() && !totolSstFilesSize.isBlank()) {
                    System.out.println("Total size of SST Files : "+formatOutputSize(Long.parseLong(totolSstFilesSize)));
                }
                String liveSstFilesSize = rocksdb.getProperty(cfHandle, "rocksdb.live-sst-files-size");
                if (!liveSstFilesSize.isEmpty() && !liveSstFilesSize.isBlank()) {
                    System.out.println("Size of live SST Filess : "+formatOutputSize(Long.parseLong(liveSstFilesSize)));
                }
            }
        } finally {
            for (ColumnFamilyHandle cfHandle : cfHandles) {
                cfHandle.close();
            }
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

}
