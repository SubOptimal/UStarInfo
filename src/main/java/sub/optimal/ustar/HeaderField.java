package sub.optimal.ustar;

/**
 * Enum of all UStar header fields. Each field contains its offset and length.
 *
 * @see https://en.wikipedia.org/wiki/Tar_(computing)
 */
public enum HeaderField {

    HEADER_BLOCK(0, 512),
    FILE_NAME(0, 100),
    FILE_MODE(100, 8),
    OWNER_ID(108, 8),
    GROUP_ID(116, 8),
    FILE_SIZE(124, 12),
    MTIME(136, 12),
    HEADER_CHECKSUM(148, 8),
    TYPE_FLAG(156, 1),
    LINK_NAME(157, 100),
    MAGIC_STRING(257, 6),
    USTAR_VERSION(263, 2),
    OWNER_NAME(265, 32),
    GROUP_NAME(297, 32),
    DEVICE_MAJOR(329, 8),
    DEVICE_MINOR(337, 8),
    FILENAME_PREFIX(345, 8);

    private final int offset;
    private final int length;

    HeaderField(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    /**
     * Return the field offset in the header block.
     *
     * @return field offset in the header
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Return the length of the field.
     *
     * @return
     */
    public int getLength() {
        return length;
    }
}
