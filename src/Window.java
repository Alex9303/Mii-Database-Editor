import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeListener;
import java.awt.Point;
import java.io.File;
import java.util.function.Consumer;

public class Window extends JFrame {
    JButton buttonNew;
    JButton buttonLoad;
    JButton buttonSave;
    JButton buttonClean;
    JTextArea textBox1;
    JPanel panel1;
    JButton buttonSetAllSystemID;
    JButton buttonSetSystemID;
    JTextField textBoxSystemID;
    JSpinner spinnerIndex;
    JButton buttonSaveMii;
    JButton buttonClearMii;
    JButton buttonMiiDone;
    JButton buttonLoadMii;
    JLabel labelMii;
    JLabel labelMiiImage;
    ImageIcon miiImage;

    JButton[] buttons = new JButton[100];
    Dimension buttonSize = new Dimension(83, 25);
    int padding = 6;

    public Window() {
        setLayout(null);
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Mii Database Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1484, 624);
        setLayout(null);

        // Initialize components
        buttonNew = new JButton("New");
        buttonNew.setBounds(12, 12, buttonSize.width, buttonSize.height);
        buttonNew.setToolTipText("Create an empty Mii database");
        add(buttonNew);

        buttonLoad = new JButton("Load");
        buttonLoad.setBounds(buttonNew.getX() + buttonSize.width + padding, buttonNew.getY(), buttonSize.width, buttonSize.height);
        buttonLoad.setToolTipText("Load Mii database");
        add(buttonLoad);

        buttonSave = new JButton("Save");
        buttonSave.setBounds(buttonLoad.getX() + buttonSize.width + padding, buttonLoad.getY(), buttonSize.width, buttonSize.height);
        buttonSave.setEnabled(false);
        buttonSave.setToolTipText("Save Mii database to file");
        add(buttonSave);

        buttonClean = new JButton("Clean");
        buttonClean.setBounds(buttonSave.getX() + buttonSize.width + padding, buttonLoad.getY(), buttonSize.width, buttonSize.height);
        buttonClean.setEnabled(false);
        buttonClean.setToolTipText("Reorganize Mii database");
        add(buttonClean);

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(100, 100, 100, 100);
        panel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel1.setVisible(false);
        add(panel1);

        labelMii = new JLabel("Select a Mii");
        labelMii.setBounds(3, 3, 150, 15);
        panel1.add(labelMii);

        buttonLoadMii = new JButton("Load Mii");
        buttonLoadMii.setEnabled(false);
        buttonLoadMii.setToolTipText("Replace selected Mii slot with file");
        panel1.add(buttonLoadMii);

        buttonSaveMii = new JButton("Save Mii");
        buttonSaveMii.setEnabled(false);
        buttonSaveMii.setToolTipText("Save selected Mii slot to file");
        panel1.add(buttonSaveMii);

        buttonClearMii = new JButton("Clear Mii");
        buttonClearMii.setEnabled(false);
        buttonClearMii.setToolTipText("Clear selected Mii slot");
        panel1.add(buttonClearMii);

        buttonMiiDone = new JButton("Done");
        buttonMiiDone.setEnabled(false);
        buttonMiiDone.setToolTipText("Deselect selected Mii slot");
        panel1.add(buttonMiiDone);

        spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        spinnerIndex.setEnabled(false);
        spinnerIndex.setToolTipText("Change index of selected Mii slot");
        panel1.add(spinnerIndex);

        textBoxSystemID = new JTextField();
        textBoxSystemID.setEnabled(false);
        textBoxSystemID.setToolTipText("System ID of the selected Mii");
        panel1.add(textBoxSystemID);

        buttonSetSystemID = new JButton("Set System ID");
        buttonSetSystemID.setEnabled(false);
        buttonSetSystemID.setToolTipText("Sets the System ID for the selected Mii");
        panel1.add(buttonSetSystemID);

        buttonSetAllSystemID = new JButton("Set All System ID");
        buttonSetAllSystemID.setEnabled(false);
        buttonSetAllSystemID.setToolTipText("Sets the System ID for all Miis in the database to the selected Mii System ID");
        panel1.add(buttonSetAllSystemID);

        textBox1 = new JTextArea();
        textBox1.setBounds(100, 100, 100, 100);
        textBox1.setFont(new Font("Consolas", Font.PLAIN, 13));
        textBox1.setLineWrap(true);
        textBox1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textBox1.setEditable(false);
        textBox1.setEnabled(false);
        textBox1.setToolTipText("Raw data of the selected Mii slot");
        panel1.add(textBox1);

        labelMiiImage = new JLabel();
        labelMiiImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelMiiImage.setVisible(false);
        panel1.add(labelMiiImage);

        setVisible(true);
    }

    private void setupControls(Point gridOffset, Dimension gridButtonSize, int gridPadding) {
        panel1.setLocation((gridButtonSize.width + gridPadding) * 9 + gridOffset.x + gridButtonSize.width + 6, gridOffset.y);
        panel1.setSize(getSize().width - panel1.getX() - 24, (gridButtonSize.height + gridPadding) * 9 + gridButtonSize.height);

        textBox1.setLocation(5, 20);
        textBox1.setSize(panel1.getWidth() - 7 - textBox1.getX(), 75);

        buttonLoadMii.setBounds(5, textBox1.getY() + textBox1.getHeight() + padding, buttonSize.width, buttonSize.height);
        buttonSaveMii.setBounds(buttonLoadMii.getX() + buttonSize.width + padding, buttonLoadMii.getY(), buttonSize.width, buttonSize.height);
        buttonClearMii.setBounds(buttonSaveMii.getX() + buttonSize.width + padding, buttonSaveMii.getY(), buttonSize.width, buttonSize.height);
        buttonMiiDone.setBounds(buttonLoadMii.getX(), buttonLoadMii.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        spinnerIndex.setBounds(buttonMiiDone.getX() + buttonSize.width + padding, buttonMiiDone.getY(), buttonSize.width, buttonSize.height);
        textBoxSystemID.setBounds(buttonLoadMii.getX(), buttonMiiDone.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        buttonSetSystemID.setBounds(textBoxSystemID.getX() + buttonSize.width + padding, textBoxSystemID.getY(), 114, buttonSize.height);
        buttonSetAllSystemID.setBounds(buttonSetSystemID.getX() + buttonSetSystemID.getWidth() + padding, buttonSetSystemID.getY(), 130, buttonSize.height);
        labelMiiImage.setBounds(panel1.getWidth() - 208, panel1.getHeight() - 208, 208, 208);

        textBox1.setEnabled(true);
        panel1.setVisible(true);
    }

    private JButton createGridButton(int index, Point location, Dimension size) {
        JButton button = new JButton();
        button.setSize(size);
        button.setLocation(location);
        button.setBackground(Color.LIGHT_GRAY);
        button.setName("ButtonGrid" + index);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createLineBorder(Color.RED));
        button.addActionListener(e -> buttonClick(index));
        return button;
    }

    public void CreateButtonGrid() {
        Point gridOffset = new Point(buttonNew.getX(), buttonNew.getY() + buttonNew.getHeight() + padding);
        Dimension gridButtonSize = new Dimension(100, 50);
        int gridPadding = 4;

        setupControls(gridOffset, gridButtonSize, gridPadding);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int index = row * 10 + col;
                Point location = new Point(
                        gridOffset.x + col * (gridButtonSize.width + gridPadding),
                        gridOffset.y + row * (gridButtonSize.height + gridPadding)
                );
                buttons[index] = createGridButton(index, location, gridButtonSize);
                add(buttons[index]);
            }
        }
        repaint();
    }

    private void buttonClick(int index) {
        for (int i = 0; i < 100; i++) {
            if (i == index) {
                buttons[index].setBackground(Color.CYAN);
            } else {
                buttons[i].setBackground(Color.lightGray);
            }
        }
        Main.GridButton_Click(index);
    }

    public void addButtonActionListener(JButton button, ActionListener listener) {
        button.addActionListener(listener);
    }

    public void addSpinnerChangeListener(JSpinner spinner, ChangeListener listener) {
        spinner.addChangeListener(listener);
    }

    public void addTextFieldKeyListener(JTextField textField, Consumer<KeyEvent> listener) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                listener.accept(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                listener.accept(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                listener.accept(e);
            }
        });
    }

    public void setMiiIcon(File file) {
        miiImage = new ImageIcon(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        Image scaledImage = miiImage.getImage().getScaledInstance(labelMiiImage.getWidth(), labelMiiImage.getHeight(), Image.SCALE_SMOOTH);
        labelMiiImage.setIcon(new ImageIcon(scaledImage));
    }
}