import java.nio.charset.StandardCharsets;
import java.util.List;

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

    static byte[] getBytesAtOffset(byte[] data, int offset, int size) {
        if (data == null || offset < 0 || offset + size > data.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        byte[] extractedBytes = new byte[size];
        System.arraycopy(data, offset, extractedBytes, 0, size);
        return extractedBytes;
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

    static byte[] convertToSingleByteArray(List<byte[]> miiDataList) {
        int totalLength = miiDataList.stream().mapToInt(a -> a.length).sum();
        byte[] singleArray = new byte[totalLength];

        int offset = 0;
        for (byte[] array : miiDataList) {
            System.arraycopy(array, 0, singleArray, offset, array.length);
            offset += array.length;
        }

        return singleArray;
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

    static byte[] calculateCRC16XModem(byte[] data) {
        // https://github.com/gtrafimenkov/pycrc16/blob/master/python3x/crc16/crc16pure.py

        int[] CRC16XModemTable = {
                0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
                0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
                0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
                0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
                0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
                0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
                0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
                0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
                0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
                0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
                0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
                0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
                0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
                0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
                0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
                0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
                0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
                0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
                0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
                0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
                0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
                0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
                0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
                0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
                0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
                0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
                0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
                0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
                0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
                0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
                0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
                0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0
        };

        int crc = 0x0000;
        for (byte b : data) {
            crc = ((crc << 8) & 0xFF00) ^ CRC16XModemTable[((crc >> 8) & 0xFF) ^ (b & 0xFF)];
        }

        return new byte[]{(byte) ((crc >> 8) & 0xFF), (byte) (crc & 0xFF)};
    }

    private static int bufToUint16(byte[] data, int addr) {
        // Adapted from:
        // https://kazuki-4ys.github.io/web_apps/MiiInfoEditorCTR/utils.js

        return ((data[addr] & 0xFF) << 8) + (data[addr + 1] & 0xFF);
    }

    private static int bufToUint32(byte[] data, int addr) {
        // Adapted from:
        // https://kazuki-4ys.github.io/web_apps/MiiInfoEditorCTR/utils.js

        return ((data[addr] & 0xFF) << 24) | ((data[addr + 1] & 0xFF) << 16) | ((data[addr + 2] & 0xFF) << 8) | (data[addr + 3] & 0xFF);
    }

    private static byte[] convertToStudio(byte[] miiData) {
        // Adapted from:
        // https://kazuki-4ys.github.io/web_apps/MiiInfoEditorCTR/mii.js

        byte[] data = new byte[46];

        int[] WRINKLES = {0, 0, 0, 0, 5, 2, 3, 7, 8, 0, 9, 11};
        int[] MAKEUP = {0, 1, 6, 9, 0, 0, 0, 0, 0, 10, 0, 0};

        int tmpU16 = bufToUint16(miiData, 0);
        data[0x16] = (byte) ((tmpU16 >> 14) & 1);       // isGirl
        data[0x15] = (byte) ((tmpU16 >> 1) & 0xF);      // favColor
        data[0x1E] = miiData[0x16];                     // height
        data[2] = miiData[0x17];                        // weight

        tmpU16 = bufToUint16(miiData, 0x20);
        data[0x13] = (byte) (tmpU16 >> 13);             // faceShape
        data[0x11] = (byte) ((tmpU16 >> 10) & 7);       // skinColor
        int facialFeature = (tmpU16 >> 6) & 0xF;
        data[0x12] = (byte) (MAKEUP[facialFeature]);    // makeup
        data[0x14] = (byte) (WRINKLES[facialFeature]);  // wrinkles

        tmpU16 = bufToUint16(miiData, 0x22);
        data[0x1D] = (byte) (tmpU16 >> 9);              // hairStyle
        data[0x1B] = (byte) ((tmpU16 >> 6) & 7);        // hairColor
        if (data[0x1B] == 0) {
            data[0x1B] = 8;
        }
        data[0x1C] = (byte) ((tmpU16 >> 5) & 1);        // flipHair

        int tmpU32 = bufToUint32(miiData, 0x24);
        data[0xE] = (byte) ((tmpU32 >> 27) & 0x1F);     // eyebrowStyle
        data[0xC] = (byte) ((tmpU32 >> 22) & 0xF);      // eyebrowRotation
        data[0xB] = (byte) ((tmpU32 >> 13) & 7);        // eyebrowColor
        if (data[0xB] == 0) {
            data[0xB] = 8;
        }
        data[0xD] = (byte) ((tmpU32 >> 9) & 0xF);       // eyebrowScale
        data[0xA] = 3;                                  // eyebrowYScale
        data[0x10] = (byte) ((tmpU32 >> 4) & 0x1F);     // eyebrowYPosition
        data[0xF] = (byte) (tmpU32 & 0xF);              // eyebrowXSpacing

        tmpU32 = bufToUint32(miiData, 0x28);
        data[7] = (byte) ((tmpU32 >> 26) & 0x3F);       // eyeStyle
        data[5] = (byte) ((tmpU32 >> 21) & 7);          // eyeRotation
        data[9] = (byte) ((tmpU32 >> 16) & 0x1F);       // eyeYPosition
        data[4] = (byte) (((tmpU32 >> 13) & 7) + 8);    // eyeColor
        data[6] = (byte) ((tmpU32 >> 9) & 7);           // eyeScale
        data[3] = 3;                                    // eyeYScale
        data[8] = (byte) ((tmpU32 >> 5) & 0xF);         // eyeXSpacing

        tmpU16 = bufToUint16(miiData, 0x2C);
        data[0x2C] = (byte) (tmpU16 >> 12);             // noseStyle
        data[0x2B] = (byte) ((tmpU16 >> 8) & 0xF);      // noseScale
        data[0x2D] = (byte) ((tmpU16 >> 3) & 0x1F);     // noseYPosition

        tmpU16 = bufToUint16(miiData, 0x2E);
        data[0x26] = (byte) (tmpU16 >> 11);             // mouthStyle
        data[0x24] = (byte) (((tmpU16 >> 9) & 3) + 19); // mouthColor
        data[0x25] = (byte) ((tmpU16 >> 5) & 0xF);      // mouthScale
        data[0x23] = 3;                                 // mouthYScale
        data[0x27] = (byte) (tmpU16 & 0x1F);            // mouthYPosition

        tmpU16 = bufToUint16(miiData, 0x30);
        data[0x19] = (byte) (tmpU16 >> 12);             // glassesStyle
        data[0x17] = (byte) ((tmpU16 >> 9) & 7);        // glassesColor
        if (data[0x17] == 0) {
            data[0x17] = 8;
        } else if (data[0x17] < 6) {
            data[0x17] += 13;
        } else {
            data[0x17] = 0;
        }
        data[0x18] = (byte) ((tmpU16 >> 5) & 7);        // glassesScale
        data[0x1A] = (byte) (tmpU16 & 0x1F);            // glassesYPosition

        tmpU16 = bufToUint16(miiData, 0x32);
        data[0x29] = (byte) (tmpU16 >> 14);             // mustacheStyle
        data[1] = (byte) ((tmpU16 >> 12) & 3);          // beardStyle
        data[0] = (byte) ((tmpU16 >> 9) & 7);           // beardColor
        if (data[0] == 0) {
            data[0] = 8;
        }
        data[0x28] = (byte) ((tmpU16 >> 5) & 0xF);      // mustacheScale
        data[0x2A] = (byte) (tmpU16 & 0x1F);            // mustacheYPosition

        tmpU16 = bufToUint16(miiData, 0x34);
        data[0x20] = (byte) (tmpU16 >> 15);             // enableMole
        data[0x1F] = (byte) ((tmpU16 >> 11) & 0xF);     // moleScale
        data[0x22] = (byte) ((tmpU16 >> 6) & 0x1F);     // moleYPosition
        data[0x21] = (byte) ((tmpU16 >> 1) & 0x1F);     // moleXPosition

        return data;
    }

    public static String encodeStudio(byte[] miiData) {
        // Adapted from:
        // https://kazuki-4ys.github.io/web_apps/MiiInfoEditorCTR/mii.js

        byte[] studioData = convertToStudio(miiData);
        int checksum = 0;
        StringBuilder encoded = new StringBuilder(String.format("%02x", checksum));
        for (byte studioByte : studioData) {
            int encodedByte = (7 + (studioByte ^ checksum)) & 0xFF;
            checksum = encodedByte;
            encoded.append(String.format("%02x", encodedByte));
        }

        return encoded.toString();
    }
}