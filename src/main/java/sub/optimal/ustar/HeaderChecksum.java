package sub.optimal.ustar;

import java.util.Arrays;

public class HeaderChecksum {

    private long signed;
    private long unSigned;
    private String signedOctal;
    private String unSignedOctal;

    private HeaderChecksum() {
    }

    public static HeaderChecksum compute(byte[] header) {
        byte[] buffer = Arrays.copyOf(header, header.length);
        for (int i = 148; i < 148 + 7; i++) {
            buffer[i] = (byte) ' ';
        }

        HeaderChecksum hc = new HeaderChecksum();
        hc.signed = computeSigned(buffer);
        hc.unSigned = computeUnsigned(buffer);
        hc.signedOctal = String.format("%06o", hc.signed);
        hc.unSignedOctal = String.format("%06o", hc.unSigned);
        return hc;
    }

    private static long computeSigned(byte[] header) {
        long sum = 0;

        for (int i = 0; i < header.length; ++i) {
            sum += header[i];
        }

        return sum;
    }

    private static long computeUnsigned(byte[] header) {
        long sum = 0;

        for (int i = 0; i < header.length; ++i) {
            sum += 255 & header[i];
        }

        return sum;
    }

    public long getSigned() {
        return signed;
    }

    public long getUnSigned() {
        return unSigned;
    }

    public String getSignedOctal() {
        return signedOctal;
    }

    public String getUnSignedOctal() {
        return unSignedOctal;
    }

}
