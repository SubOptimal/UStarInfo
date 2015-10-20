package sub.optimal.ustar;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * Parse and print header information of UStar files.
 */
public class UStarInfo {

    private static final String format = "%18s: %s%n";
    private static final String formatChkSum = "%18s: %06o%n";
    private static final String formatHeaderOffset = "%18s: %s  dec.  %<H hex%n";

    /**
     * Main method.
     *
     * @param args tar file name
     * @throws IOException if accessing the file fails
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.printf("usage: %s tar_file%n", UStarInfo.class.getName());
            return;
        }

        File tarFile = new File((args[0]));
        long fileSize = tarFile.length();

        try (RandomAccessFile ra = new RandomAccessFile(tarFile, "r")) {
            long currentOffset = 0;
            while (currentOffset < fileSize - HeaderField.HEADER_BLOCK.getLength()) {

                System.out.printf(formatHeaderOffset, "header offset", currentOffset);
                ra.seek(currentOffset);

                byte[] headerBytes = readHeaderBlock(ra);
                if (blockIsEpmty(headerBytes)) {
                    System.out.printf("empty block reached %d byte(s) before end", fileSize - currentOffset);
                    break;
                }
                Header header = Header.getInstance(headerBytes);
                showInfo(header, HeaderChecksum.compute(headerBytes));

                if (!(header.isPosix() || header.isGnu())) {
                    System.out.println("ERROR: UStar magic string was not found in this block");
                    break;
                }

                currentOffset += nextHeaderOffset(header);
                System.out.println();
            }
        }
    }

    /**
     * Print the information from the header block.
     *
     * @param header the tar header
     * @param checksum the computed checksum of this block
     */
    private static void showInfo(Header header, HeaderChecksum checksum) {
        System.out.printf(format, "format", getTarFormat(header));
        System.out.printf(format, "UStar version", header.getUstarVersion());
        System.out.printf(format, "file name", header.getFileName());
        System.out.printf(format, "file mode", header.getFileMode());
        System.out.printf(format, "owner ID", header.getOwnerID());
        System.out.printf(format, "group ID", header.getGroupID());
        System.out.printf(format, "size (octal)", header.getFileSize());
        String mtimeStr = formatMTime(header.getMtime());
        if (mtimeStr.isEmpty()) {
            mtimeStr = "ERROR: mtime field is invalid " + header.getMtime();
        }
        System.out.printf(format, "mtime", mtimeStr);
        System.out.printf(format, "checksum (header)", header.getHeaderChecksum());
        if (!checkSumIsValid(header.getHeaderChecksum(), checksum)) {
            System.out.println("ERROR: checksum in the header block is not equal to the computed ones");
            System.out.printf(format, "checksum unsigned", checksum.getUnSignedOctal());
            System.out.printf(format, "checksum signed", checksum.getSignedOctal());
        }
        System.out.printf(format, "typeFlag", header.getTypeFlag());
        System.out.printf(format, "link name", header.getLinkName());
        System.out.printf(format, "owner name", header.getOwnerName());
        System.out.printf(format, "group name", header.getGroupName());
        System.out.printf(format, "device major", header.getDeviceMajor());
        System.out.printf(format, "device minor", header.getDeviceMinor());
        System.out.printf(format, "file name prefix", header.getFileNamePrefix());
    }

    /**
     * Return the specific UStar format.<br>
     * Currently supported "POSIX tar" and "GNU tar".
     *
     * @param header parsed header block
     * @return verbose format descritpion
     * @see https://github.com/file/file/blob/master/magic/Magdir/archive
     */
    private static String getTarFormat(Header header) {
        String tarFormat;
        if (header.isPosix()) {
            tarFormat = "POSIX tar";
        } else if (header.isGnu()) {
            tarFormat = "POSIX tar (GNU)";
        } else {
            tarFormat = "unknown: '" + header.getMagicString() + "'";
        }
        return tarFormat;
    }

    /**
     * Compute the offset to the next header block.
     *
     * @param header parsed header block
     * @return offset to the next header block, relative to the offset of the
     * current block
     * @throws NumberFormatException if the file size in the current vblock is
     * not a valid value
     */
    private static long nextHeaderOffset(Header header) throws NumberFormatException {
        final Long sizeInBytes = Long.valueOf(header.getFileSize(), 8);
        long dataBlocks = sizeInBytes / HeaderField.HEADER_BLOCK.getLength();
        long dataRemainder = sizeInBytes % HeaderField.HEADER_BLOCK.getLength();
        if (dataRemainder != 0) {
            dataBlocks++;
        }
        long nextHeader = HeaderField.HEADER_BLOCK.getLength() + dataBlocks * HeaderField.HEADER_BLOCK.getLength();
        return nextHeader;
    }

    /**
     * Read a block of bytes with the lenght of
     * {@link  HeaderField#HEADER_BLOCK}.
     *
     * @param ra the random access file
     * @return byte array with the length of {@link  HeaderField#HEADER_BLOCK}
     * @throws IOException if the read of the file fails
     */
    private static byte[] readHeaderBlock(RandomAccessFile ra) throws IOException {
        byte[] headerBytes = new byte[HeaderField.HEADER_BLOCK.getLength()];
        byte[] buffer = new byte[HeaderField.HEADER_BLOCK.getLength()];
        int totalReadBytes = 0;
        while (totalReadBytes < HeaderField.HEADER_BLOCK.getLength()) {
            int readBytes = ra.read(buffer, 0, HeaderField.HEADER_BLOCK.getLength() - totalReadBytes);

            if (readBytes < 0) {
                break;
            }

            System.arraycopy(buffer, 0, headerBytes, totalReadBytes, readBytes);
            totalReadBytes += readBytes;
        }
        return headerBytes;
    }

    /**
     * Check if the checksum provided in the header match the computed
     * checksum.<br>
     * For historical reasons the checksum must match either the unsigned or the
     * signed checksum.
     *
     * @param headerChecksum the checksum from the header in octal format
     * @param checksum computed checksums
     * @return {@code true} the checksum in the header and one of the cmputed
     * checksums are equal
     */
    private static boolean checkSumIsValid(String headerChecksum, HeaderChecksum checksum) {
        return headerChecksum.equals(checksum.getUnSignedOctal())
                || headerChecksum.equals(checksum.getSignedOctal());
    }

    /**
     * Check if the block contains only zero bytes.
     *
     * @param bytes block of header bytes
     * @return {@code true} if the block contains only bytes with zero value
     */
    private static boolean blockIsEpmty(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert the octal mtime timestamp into format "dd.MM.YYYY HH:mm:ss".
     *
     * @param mtime octal string representing the file last modification time as
     * seconds in epoch
     * @return the formated date or an emty string if the passed mtime is
     * invalid
     */
    private static String formatMTime(String mtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss 'UTC'");
        sdf.setLenient(false);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String mtimeStr;
        try {
            long seconds = Long.valueOf(mtime, 8);
            mtimeStr = sdf.format(new Date(SECONDS.toMillis(seconds)));
        } catch (NumberFormatException e) {
            mtimeStr = "";
        }
        return mtimeStr;
    }
}
