package org.ahamlat;


import org.bouncycastle.util.Arrays;

public enum KeyValueSegmentIdentifier {
    BLOCKCHAIN(new byte[] {1}),
    WORLD_STATE(new byte[] {2}, new int[] {0, 1}),
    PRIVATE_TRANSACTIONS(new byte[] {3}),
    PRIVATE_STATE(new byte[] {4}),
    PRUNING_STATE(new byte[] {5}, new int[] {0, 1}),
    ACCOUNT_INFO_STATE(new byte[] {6}, new int[] {2}),
    CODE_STORAGE(new byte[] {7}, new int[] {2}),
    ACCOUNT_STORAGE_STORAGE(new byte[] {8}, new int[] {2}),
    TRIE_BRANCH_STORAGE(new byte[] {9}, new int[] {2}),
    TRIE_LOG_STORAGE(new byte[] {10}, new int[] {2}),
    GOQUORUM_PRIVATE_WORLD_STATE(new byte[] {11}),
    GOQUORUM_PRIVATE_STORAGE(new byte[] {12}),
    BACKWARD_SYNC_HEADERS(new byte[] {13}),
    BACKWARD_SYNC_BLOCKS(new byte[] {14}),
    BACKWARD_SYNC_CHAIN(new byte[] {15}),
    SNAPSYNC_MISSING_ACCOUNT_RANGE(new byte[] {16}),
    SNAPSYNC_ACCOUNT_TO_FIX(new byte[] {17}),
    CHAIN_PRUNER_STATE(new byte[] {18});

    private final byte[] id;
    private final int[] versionList;

    KeyValueSegmentIdentifier(final byte[] id) {
        this(id, new int[] {0, 1, 2});
    }

    KeyValueSegmentIdentifier(final byte[] id, final int[] versionList) {
        this.id = id;
        this.versionList = versionList;
    }

    public String getName() {
        return name();
    }

    public byte[] getId() {
        return id;
    }

    public boolean includeInDatabaseVersion(final int version) {
        return Arrays.contains(versionList, version);
    }
}
