import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static Window window;
    private static byte[] fileBytes;
    private static final List<byte[]> miiDataList = IntStream.range(0, 100)
            .mapToObj(i -> new byte[74])
            .collect(Collectors.toList());
    public static int selectedMiiIndex = -1;


    public static void main(String[] args) {
        window = new Window();

//        window.addButtonLoadActionListener(e -> ButtonLoad_Click());
        window.addButtonActionListener(window.buttonLoad, e -> ButtonLoad_Click());
//        window.addButtonActionListener(window.buttonSave, e -> ButtonSave_Click());
//        window.addButtonActionListener(window.buttonClean, e -> ButtonClean_Click());

        window.addSpinnerActionListener(window.spinnerIndex, e -> NUDIndex_ValueChanged((Integer) window.spinnerIndex.getValue()));
    }

    public static void GridButton_Click(int index) {
        selectedMiiIndex = index;
        byte[] currentMiiData = miiDataList.get(selectedMiiIndex);

        String labelMiiText = "Empty Mii Slot at index " + selectedMiiIndex + ".";
        if (isMii(currentMiiData)) {
            labelMiiText = getMiiName(currentMiiData);
            window.textBox1.setEnabled(true);
            window.buttonLoadMii.setEnabled(true);
            window.buttonMiiDone.setEnabled(true);
            window.buttonClearMii.setEnabled(true);
            window.buttonSaveMii.setEnabled(true);
            window.textBoxClientID.setEnabled(true);
            window.buttonSetAllClientID.setEnabled(true);
            window.spinnerIndex.setEnabled(true);
            
            window.textBox1.setText(byteArrayToHexString(currentMiiData));
        } else {
            window.textBox1.setEnabled(false);
            window.buttonLoadMii.setEnabled(false);
            window.buttonMiiDone.setEnabled(false);
            window.buttonClearMii.setEnabled(false);
            window.buttonSaveMii.setEnabled(false);
            window.textBoxClientID.setEnabled(false);
            window.buttonSetClientID.setEnabled(false);
            window.buttonSetAllClientID.setEnabled(false);
            window.spinnerIndex.setEnabled(false);

            window.textBox1.setText("");
            window.textBoxClientID.setText("");
        }

        window.spinnerIndex.setValue(selectedMiiIndex);

        window.labelMii.setText(labelMiiText);
    }

    private static void ButtonLoad_Click() {
        File selectedFile = OpenFileDialog.openFileDialog("Select a file", new String[]{"dat"}, "DAT files");

        if (selectedFile == null) {
            System.out.println("No file selected.");
        } else if (selectedFile.length() != 779968) {
            System.out.println("Incorrect file size. Expected: 779968, got: " + selectedFile.length());
        } else {
            try {
                fileBytes = Files.readAllBytes(selectedFile.toPath());
                System.out.println("Successfully loaded file");

                if (verifyFile()) {

                    for (int i = 0; i < 100; i++) {
                        miiDataList.set(i, getBytesAtOffset(getMiiData(), 74 * i, 74));
                    }


                    // If GridExists Then
                    //     RefreshGrid()
                    // Else
                    window.CreateButtonGrid();
                    //     GridExists = True
                    // End If


                    window.buttonSave.setEnabled(true);
                    window.buttonClean.setEnabled(true);

                    for (int i = 0; i < 100; i++) {
                        if (isMii(miiDataList.get(i))) {
                            window.buttons[i].setText(getMiiName(miiDataList.get(i)));
                            window.buttons[i].setBorder(BorderFactory.createLineBorder(Color.GREEN));
                        }
                    }
                } else {
                    System.out.println("Incorrect header");
                }

            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private static void NUDIndex_ValueChanged(int value) {
        System.out.println(value);

        if (window.spinnerIndex.isEnabled()) {
            byte[] tempMiiData = miiDataList.get(selectedMiiIndex);
            miiDataList.set(selectedMiiIndex, miiDataList.get(value));
            miiDataList.set(value, tempMiiData);
            selectedMiiIndex = value;

            refreshGrid();
        }
    }

    private static void refreshGrid() {
        for (int i = 0; i < 100; i++) {
            if (isMii(miiDataList.get(i))) {
                window.buttons[i].setText(getMiiName(miiDataList.get(i)));
                window.buttons[i].setBorder(BorderFactory.createLineBorder(Color.GREEN));
            } else {
                window.buttons[i].setText("");
                window.buttons[i].setBorder(BorderFactory.createLineBorder(Color.RED));
            }
            if (i == selectedMiiIndex) {
                window.buttons[i].setBackground(Color.CYAN);
            } else {
                window.buttons[i].setBackground(Color.lightGray);
            }
        }
    }

    private static boolean verifyFile() {
        // Make sure the header is correct
        // Expected file header: 52 4E 4F 44 (RNOF in ASCII)
        return fileBytes.length >= 4 &&
                fileBytes[0] == 0x52 && // R
                fileBytes[1] == 0x4E && // N
                fileBytes[2] == 0x4F && // O
                fileBytes[3] == 0x44;  // D
    }

    private static byte[] getBytesAtOffset(byte[] data, int offset, int size) {
        if (data == null || offset < 0 || offset + size > data.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        byte[] extractedBytes = new byte[size];
        System.arraycopy(data, offset, extractedBytes, 0, size);
        return extractedBytes;
    }

    private static byte[] getMiiData() {
        int offset = 4;
        int size = 7400;

        return getBytesAtOffset(fileBytes, offset, size);
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

    private static List<byte[]> splitBytesIntoParts(byte[] bytes, int count, int size) {
        List<byte[]> parts = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            byte[] part = new byte[size];
            System.arraycopy(bytes, i * size, part, 0, size);
            parts.add(part);
        }
        return parts;
    }

    private static String getMiiName(byte[] miiData) {
        byte[] nameData = getBytesAtOffset(miiData, 2, 20);
        List<byte[]> nameDataList = splitBytesIntoParts(nameData, 10, 2);
        int nameLength = 0;
        for (byte[] b : nameDataList) {
            if (b[0] == 0x00 && b[1] == 0x00) {
                break;
            } else {
                nameLength++;
            }
        }
        return convertBinaryToUTF16(getBytesAtOffset(nameData, 0, nameLength * 2));
    }

    private static String byteArrayToHexString(byte[] bytes) {
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


}