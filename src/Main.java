import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static Window window;
    private static final List<byte[]> miiDataList = IntStream.range(0, 100)
            .mapToObj(i -> new byte[74])
            .collect(Collectors.toList());
    public static int selectedMiiIndex = -1;
    private static boolean gridExists = false;


    public static void main(String[] args) {
        window = new Window();

        // Top level controls
        window.addButtonActionListener(window.buttonLoad, e -> ButtonLoad_Click());
        window.addButtonActionListener(window.buttonSave, e -> ButtonSave_Click());
        window.addButtonActionListener(window.buttonClean, e -> ButtonClean_Click());

        // Mii controls
        window.addButtonActionListener(window.buttonLoadMii, e -> ButtonLoadMii_Click());
        window.addButtonActionListener(window.buttonSaveMii, e -> ButtonSaveMii_Click());
        window.addButtonActionListener(window.buttonClearMii, e -> ButtonClearMii_Click());

        // Mii rearrangement controls
        window.addButtonActionListener(window.buttonMiiDone, e -> ButtonMiiDone_Click());
        window.addSpinnerChangeListener(window.spinnerIndex, e -> NUDIndex_ValueChanged((Integer) window.spinnerIndex.getValue()));

        // Mii Client ID management controls
        window.addTextFieldKeyListener(window.textBoxClientID, e -> TextBoxClientID_TextChanged());
        window.addButtonActionListener(window.buttonSetClientID, e -> ButtonSetClientID_Click());
        window.addButtonActionListener(window.buttonSetAllClientID, e -> ButtonSetAllClientID_Click());
    }

    public static void GridButton_Click(int index) {
        selectedMiiIndex = index;
        byte[] currentMiiData = miiDataList.get(selectedMiiIndex);

        if (Util.isMii(currentMiiData)) {
            window.textBox1.setEnabled(true);
            window.buttonLoadMii.setEnabled(true);
            window.buttonMiiDone.setEnabled(true);
            window.buttonClearMii.setEnabled(true);
            window.buttonSaveMii.setEnabled(true);
            window.textBoxClientID.setEnabled(true);
            window.buttonSetClientID.setEnabled(false);
            window.buttonSetAllClientID.setEnabled(true);

            setMiiDataText(currentMiiData);

        } else {
            window.labelMii.setText("Empty Mii Slot at index " + selectedMiiIndex + ".");

            window.textBox1.setEnabled(false);
            window.buttonLoadMii.setEnabled(true);
            window.buttonMiiDone.setEnabled(true);
            window.buttonClearMii.setEnabled(false);
            window.buttonSaveMii.setEnabled(false);
            window.textBoxClientID.setEnabled(false);
            window.buttonSetClientID.setEnabled(false);
            window.buttonSetAllClientID.setEnabled(false);

            window.textBox1.setText("");
            window.textBoxClientID.setText("");
        }

        window.spinnerIndex.setValue(selectedMiiIndex);
        window.spinnerIndex.setEnabled(true);
    }

    private static void ButtonLoad_Click() {
        File selectedFile = FileDialog.openFileDialog("Select a file", new String[]{"dat"}, "DAT files");

        if (selectedFile == null) {
            System.out.println("No file selected.");
        } else if (selectedFile.length() != 779968) {
            System.out.println("Incorrect file size. Expected: 779968 bytes, got: " + selectedFile.length() + " bytes");
        } else {
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                byte[] fileHeader = Util.getBytesAtOffset(fileBytes, 0, 4);
                System.out.println("Successfully loaded file");

                if (Util.verifyHeader(fileHeader)) {
                    selectedMiiIndex = -1;

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
                    window.spinnerIndex.setValue(0);

                    byte[] miiData = Util.getBytesAtOffset(fileBytes, 4, 7400);
                    for (int i = 0; i < 100; i++) {
                        miiDataList.set(i, Util.getBytesAtOffset(miiData, 74 * i, 74));
                    }

                    if (!gridExists) {
                        window.CreateButtonGrid();
                        gridExists = true;
                    }

                    window.buttonSave.setEnabled(true);
                    window.buttonClean.setEnabled(true);

                    refreshGrid();
                } else {
                    System.out.println("Incorrect header");
                }

            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private static void ButtonSave_Click() {
        File saveFile = FileDialog.saveFileDialog("Save Database File", new String[]{"dat"}, "Database files (*.dat)");

        if (saveFile != null) {
            String fileName = saveFile.getAbsolutePath();
            byte[] saveData;

            if (!fileName.toLowerCase().endsWith(".dat")) {
                fileName += ".dat";
            }

            try {
                Files.write(new File(fileName).toPath(), miiDataList.get(selectedMiiIndex));
                System.out.println("File saved successfully.");
            } catch (IOException ex) {
                System.out.println("Error saving file: " + ex.getMessage());
            }
        }
    }

    private static void ButtonClean_Click() {
        System.out.println("Implement " + "ButtonClean_Click");
    }

    private static void ButtonLoadMii_Click() {
        File selectedFile = FileDialog.openFileDialog("Select a file", new String[]{"mii"}, "MII files");

        if (selectedFile == null) {
            System.out.println("No file selected.");
        } else if (selectedFile.length() != 74) {
            System.out.println("Incorrect file size. Expected: 74 bytes, got: " + selectedFile.length() + " bytes");
        } else {
            try {
                byte[] newMiiData = Files.readAllBytes(selectedFile.toPath());
                System.out.println("Successfully loaded file");

                if (Util.isMii(newMiiData)) {
                    miiDataList.set(selectedMiiIndex, newMiiData);

                    window.textBox1.setEnabled(true);
                    window.buttonLoadMii.setEnabled(true);
                    window.buttonMiiDone.setEnabled(true);
                    window.buttonClearMii.setEnabled(true);
                    window.buttonSaveMii.setEnabled(true);
                    window.textBoxClientID.setEnabled(true);
                    window.buttonSetClientID.setEnabled(false);
                    window.buttonSetAllClientID.setEnabled(true);

                    setMiiDataText(newMiiData);

                    refreshGrid();
                } else {
                    System.out.println("Invalid Mii Data");
                }

            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private static void ButtonSaveMii_Click() {
        File saveFile = FileDialog.saveFileDialog("Save Mii File", new String[]{"mii"}, "MII files (*.mii)");

        if (saveFile != null) {
            String fileName = saveFile.getAbsolutePath();

            if (!fileName.toLowerCase().endsWith(".mii")) {
                fileName += ".mii";
            }

            try {
                Files.write(new File(fileName).toPath(), miiDataList.get(selectedMiiIndex));
                System.out.println("File saved successfully.");
            } catch (IOException ex) {
                System.out.println("Error saving file: " + ex.getMessage());
            }
        }
    }

    private static void ButtonClearMii_Click() {
        if (selectedMiiIndex > -1) {
            miiDataList.set(selectedMiiIndex, new byte[74]);

            window.labelMii.setText("Empty Mii Slot at index " + selectedMiiIndex + ".");
            window.textBox1.setText("");
            window.textBoxClientID.setText("");

            window.buttonSaveMii.setEnabled(false);
            window.buttonClearMii.setEnabled(false);
            window.textBoxClientID.setEnabled(false);
            window.buttonSetClientID.setEnabled(false);
            window.buttonSetAllClientID.setEnabled(false);

            refreshGrid();
        }
    }

    private static void ButtonMiiDone_Click() {
        selectedMiiIndex = -1;

        window.labelMii.setText("Select a Mii");
        window.textBox1.setText("");
        window.textBoxClientID.setText("");
        window.spinnerIndex.setValue(0);

        window.textBox1.setEnabled(false);
        window.buttonLoadMii.setEnabled(false);
        window.buttonSaveMii.setEnabled(false);
        window.buttonClearMii.setEnabled(false);
        window.buttonMiiDone.setEnabled(false);
        window.spinnerIndex.setEnabled(false);
        window.textBoxClientID.setEnabled(false);
        window.buttonSetClientID.setEnabled(false);
        window.buttonSetAllClientID.setEnabled(false);

        refreshGrid();
    }

    private static void NUDIndex_ValueChanged(int value) {
        if (window.spinnerIndex.isEnabled() && selectedMiiIndex > 0) {
            byte[] tempMiiData = miiDataList.get(selectedMiiIndex);
            miiDataList.set(selectedMiiIndex, miiDataList.get(value));
            miiDataList.set(value, tempMiiData);
            selectedMiiIndex = value;

            refreshGrid();
        }
    }

    private static void TextBoxClientID_TextChanged() {
        String originalText = window.textBoxClientID.getText();
        String newText = originalText.replaceAll("[^a-fA-F0-9]", "").toUpperCase();

        if (!newText.equals(originalText)) {
            window.textBoxClientID.setText(newText);
        }

        if (!window.textBox1.getText().isEmpty()) {
            window.buttonSetClientID.setEnabled(window.textBoxClientID.getText().length() == 8 && !window.textBoxClientID.getText().equals(window.textBox1.getText().substring(28 * 2, 28 * 2 + 8)));
            window.buttonSetAllClientID.setEnabled(window.textBoxClientID.getText().length() == 8);
        }
    }

    private static void ButtonSetClientID_Click() {
        if (window.textBoxClientID.getText().length() == 8) {
            byte[] newClientIDBytes = Util.hexStringToByteArray(window.textBoxClientID.getText());
            miiDataList.set(selectedMiiIndex, Util.overwriteBytes(miiDataList.get(selectedMiiIndex), 28, newClientIDBytes));

            setMiiDataText(miiDataList.get(selectedMiiIndex));
            window.buttonSetClientID.setEnabled(false);
        }
    }

    private static void ButtonSetAllClientID_Click() {
        if (window.textBoxClientID.getText().length() == 8) {
            System.out.println("All Mii Client IDs have been set to " + window.textBoxClientID.getText());

            byte[] newClientIDBytes = Util.hexStringToByteArray(window.textBoxClientID.getText());

            for (int i = 0; i < 100; i++) {
                if (Util.isMii(miiDataList.get(i))) {
                    miiDataList.set(i, Util.overwriteBytes(miiDataList.get(i), 28, newClientIDBytes));
                }
            }

            setMiiDataText(miiDataList.get(selectedMiiIndex));
            window.buttonSetClientID.setEnabled(false);
        }
    }

    private static void refreshGrid() {
        for (int i = 0; i < 100; i++) {
            if (Util.isMii(miiDataList.get(i))) {
                window.buttons[i].setText(Util.getMiiName(miiDataList.get(i)));
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

    private static void setMiiDataText(byte[] currentMiiData) {
        window.labelMii.setText(Util.getMiiName(currentMiiData));
        String miiDataHexString = Util.byteArrayToHexString(currentMiiData);
        window.textBox1.setText(miiDataHexString);
        if (!miiDataHexString.isEmpty()) {
            window.textBoxClientID.setText(miiDataHexString.substring(28 * 2, 28 * 2 + 8));
        }
    }
}