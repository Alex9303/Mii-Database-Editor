import java.nio.charset.StandardCharsets;

public class Util {
    static byte[] overwriteBytes(byte[] originalBytes, int startIndex, byte[] newBytes) {
        if (originalBytes != null && newBytes != null && startIndex >= 0
                && startIndex + newBytes.length <= originalBytes.length) {
            byte[] resultBytes = originalBytes.clone();
            System.arraycopy(newBytes, 0, resultBytes, startIndex, newBytes.length);
            return resultBytes;
        } else {
            return originalBytes;
        }
    }

    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll("[\\s-]", "");

        if (!hexString.isEmpty() && hexString.length() % 2 == 0) {
            int byteCount = hexString.length() / 2;
            byte[] result = new byte[byteCount];

            for (int i = 0; i < byteCount; i++) {
                result[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
            }
            return result;
        } else {
            throw new IllegalArgumentException("Invalid hex string");
        }
    }

    static boolean verifyHeader(byte[] fileHeader) {
        // Make sure the header is correct
        // Expected file header: 52 4E 4F 44 (RNOF in ASCII)
        return fileHeader.length == 4 &&
                fileHeader[0] == 0x52 &&
                fileHeader[1] == 0x4E &&
                fileHeader[2] == 0x4F &&
                fileHeader[3] == 0x44;
    }

    static byte[] getBytesAtOffset(byte[] data, int offset, int size) {
        if (data == null || offset < 0 || offset + size > data.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        byte[] extractedBytes = new byte[size];
        System.arraycopy(data, offset, extractedBytes, 0, size);
        return extractedBytes;
    }

    public static boolean isMii(byte[] miiData) {
        if (miiData.length != 74) {
            return false;
        }
        for (byte b : miiData) {
            if (b != 0x00) {
                return true;
            }
        }
        return false;
    }

    private static String convertBinaryToUTF16(byte[] bytes) {
        if (bytes != null) {
            return new String(bytes, StandardCharsets.UTF_16BE);
        } else {
            return "";
        }
    }

    static String getMiiName(byte[] miiData) {
        byte[] nameData = getBytesAtOffset(miiData, 2, 20);
        int nameLength = 0;

        for (int i = 0; i < 10; i++) {
            int offset = i * 2;
            if (nameData[offset] == 0x00 && nameData[offset + 1] == 0x00) {
                break;
            } else {
                nameLength++;
            }
        }

        return convertBinaryToUTF16(getBytesAtOffset(nameData, 0, nameLength * 2));
    }

    static String byteArrayToHexString(byte[] bytes) {
        if (bytes != null) {
            StringBuilder hexStringBuilder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hexStringBuilder.append(String.format("%02X", b));
            }
            return hexStringBuilder.toString();
        } else {
            return "";
        }
    }

    private static byte[] repeatBytes(byte[] bytes, int count) {
        byte[] result = new byte[bytes.length * count];
        for (int i = 0; i < count; i++) {
            System.arraycopy(bytes, 0, result, i * bytes.length, bytes.length);
        }
        return result;
    }

    public static byte[] buildFile() {
        byte[] newFileBytes = new byte[779968];
        int offset = 0;

        // Write file header
        System.arraycopy(hexStringToByteArray("52 4E 4F 44"), 0, newFileBytes, offset, 4);
        offset += 4;

        // Write empty space for mii data
        System.arraycopy(repeatBytes(hexStringToByteArray("00"), 7400), 0, newFileBytes, offset, 7400);
        offset += 7400;

        // Write "80"
        System.arraycopy(hexStringToByteArray("80"), 0, newFileBytes, offset, 1);
        offset += 1;

        // Write 19 bytes of "00"
        System.arraycopy(repeatBytes(hexStringToByteArray("00"), 19), 0, newFileBytes, offset, 19);
        offset += 19;

        // Write file header for Mii Parade
        System.arraycopy(hexStringToByteArray("52 4E 48 44 FF FF FF FF"), 0, newFileBytes, offset, 8);
        offset += 8;

        // Write "00 00 00 00 00 00 00 00 7F FF 7F FF" 10000 times
        // Mii Parade data for storing Mii ID and System ID
        // Example: DE AD BE EF CA FE BA BE 7F FF 7F FF
        System.arraycopy(repeatBytes(hexStringToByteArray("00 00 00 00 00 00 00 00 7F FF 7F FF"), 10000), 0, newFileBytes, offset, 120000);
        offset += 120000;

        // Write 22 bytes of "00"
        System.arraycopy(repeatBytes(hexStringToByteArray("00"), 22), 0, newFileBytes, offset, 22);
        offset += 22;

        // Write CRC16XModem hash for an empty file
        System.arraycopy(hexStringToByteArray("9A FF"), 0, newFileBytes, offset, 2);
        offset += 2;

        // Write 652512 bytes of "00"
        // Mii data for the Mii Parade
        // Maximum amount of Mii Parade entries is unknown
        System.arraycopy(repeatBytes(hexStringToByteArray("00"), 652512), 0, newFileBytes, offset, 652512);

        return newFileBytes;
    }
}
