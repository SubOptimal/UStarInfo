package sub.optimal.ustar;

/**
 * Class which hold the raw header fields.
 */
public class Header {

    private String fileName;
    private String fileMode;
    private String ownerID;
    private String groupID;
    private String fileSize;
    private String mtime;
    private String headerChecksum;
    private String typeFlag;
    private String linkName;
    private String magicString;
    private String ustarVersion;
    private String ownerName;
    private String groupName;
    private String deviceMajor;
    private String deviceMinor;
    private String fileNamePrefix;
    private boolean isPosix = false;
    private boolean isGnu = false;

    private Header() {
    }

    /**
     * Return a new instance of a header based on the passed byte array.
     *
     * @param bytes byte aray of the header block
     * @return initialized header instance
     */
    public static Header getInstance(byte[] bytes) {
        if (bytes.length != HeaderField.HEADER_BLOCK.getLength()) {
            throw new IllegalArgumentException("byte array must be of size "
                    + HeaderField.HEADER_BLOCK.getLength());
        }
        Header header = new Header();
        header.fileName = readField(bytes, HeaderField.FILE_NAME);
        header.fileMode = readField(bytes, HeaderField.FILE_MODE);
        header.ownerID = readField(bytes, HeaderField.OWNER_ID);
        header.groupID = readField(bytes, HeaderField.GROUP_ID);
        header.fileSize = readField(bytes, HeaderField.FILE_SIZE);
        header.mtime = readField(bytes, HeaderField.MTIME);
        header.headerChecksum = readField(bytes, HeaderField.HEADER_CHECKSUM);
        header.typeFlag = readField(bytes, HeaderField.TYPE_FLAG);
        header.linkName = readField(bytes, HeaderField.LINK_NAME);
        header.magicString = readField(bytes, HeaderField.MAGIC_STRING);
        header.ustarVersion = readField(bytes, HeaderField.USTAR_VERSION);
        header.ownerName = readField(bytes, HeaderField.OWNER_NAME);
        header.groupName = readField(bytes, HeaderField.GROUP_NAME);
        header.deviceMajor = readField(bytes, HeaderField.DEVICE_MAJOR);
        header.deviceMinor = readField(bytes, HeaderField.DEVICE_MINOR);
        header.fileNamePrefix = readField(bytes, HeaderField.FILENAME_PREFIX);

        header.isPosix = "ustar".equals(header.magicString);
        header.isGnu = "ustar ".equals(header.magicString) && header.ustarVersion.startsWith(" ");
        return header;
    }

    /**
     * Read a header field.
     *
     * @param headerBytes byte array of the header block
     * @param field the field to be read from the byte array
     * @return the raw field value as ASCII string
     */
    private static String readField(byte[] headerBytes, HeaderField field) {
        StringBuilder sb = new StringBuilder(field.getLength());
        int endOffset = field.getOffset() + field.getLength();

        for (int i = field.getOffset(); i < endOffset; i++) {
            if (headerBytes[i] == 0) {
                break;
            }
            sb.append((char) headerBytes[i]);
        }
        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileMode() {
        return fileMode;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getMtime() {
        return mtime;
    }

    public String getHeaderChecksum() {
        return headerChecksum;
    }

    public String getTypeFlag() {
        return typeFlag;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getMagicString() {
        return magicString;
    }

    public boolean isPosix() {
        return isPosix;
    }

    public boolean isGnu() {
        return isGnu;
    }

    public String getUstarVersion() {
        return ustarVersion;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDeviceMajor() {
        return deviceMajor;
    }

    public String getDeviceMinor() {
        return deviceMinor;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }
}
