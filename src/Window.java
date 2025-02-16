import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.Point;

public class Window extends JFrame {
    JButton buttonLoad;
    JButton buttonSave;
    JButton buttonClean;
    JTextArea textBox1;
    JPanel panel1;
    JButton buttonSetAllClientID;
    JButton buttonSetClientID;
    JTextField textBoxClientID;
    JSpinner spinnerIndex;
    JButton buttonSaveMii;
    JButton buttonClearMii;
    JButton buttonMiiDone;
    JButton buttonLoadMii;
    JLabel labelMii;

    JButton[] buttons = new JButton[100];
    Dimension buttonSize = new Dimension(83, 25);
    int padding = 6;

    public Window() {
        setLayout(null); // Use null layout or replace with a suitable layout manager
        initializeComponents();
    }

    private void initializeComponents()  {
        setTitle("Mii Database Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1484, 624);
        setLayout(null); // Using null layout for custom positioning

        // Initialize components
        buttonLoad = new JButton("Load");
        buttonLoad.setBounds(12, 12, buttonSize.width, buttonSize.height);
        add(buttonLoad);

        buttonSave = new JButton("Save");
        buttonSave.setBounds(buttonLoad.getX() + buttonSize.width + padding, buttonLoad.getY(), buttonSize.width, buttonSize.height);
        buttonSave.setEnabled(false);
        add(buttonSave);

        buttonClean = new JButton("Clean");
        buttonClean.setBounds(buttonSave.getX() + buttonSize.width + padding, buttonLoad.getY(), buttonSize.width, buttonSize.height);
        buttonClean.setEnabled(false);
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
        buttonLoadMii.setBounds(5, 30, buttonSize.width, buttonSize.height);
        buttonLoadMii.setEnabled(false);
        panel1.add(buttonLoadMii);

        buttonSaveMii = new JButton("Save Mii");
        buttonSaveMii.setBounds(buttonLoadMii.getX() + buttonSize.width + padding, buttonLoadMii.getY(), buttonSize.width, buttonSize.height);
        buttonSaveMii.setEnabled(false);
        panel1.add(buttonSaveMii);

        buttonClearMii = new JButton("Clear Mii");
        buttonClearMii.setBounds(buttonSaveMii.getX() + buttonSize.width + padding, buttonSaveMii.getY(), buttonSize.width, buttonSize.height);
        buttonClearMii.setEnabled(false);
        panel1.add(buttonClearMii);

        buttonMiiDone = new JButton("Done");
        buttonMiiDone.setBounds(buttonLoadMii.getX(), buttonLoadMii.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        buttonMiiDone.setEnabled(false);
        panel1.add(buttonMiiDone);

        spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        spinnerIndex.setBounds(buttonMiiDone.getX() + buttonSize.width + padding, buttonMiiDone.getY(), buttonSize.width, buttonSize.height);
        spinnerIndex.setEnabled(false);
        panel1.add(spinnerIndex);

        textBoxClientID = new JTextField();
        textBoxClientID.setBounds(buttonLoadMii.getX(), buttonMiiDone.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        textBoxClientID.setEnabled(false);
        panel1.add(textBoxClientID);

        buttonSetClientID = new JButton("Set Client ID");
        buttonSetClientID.setBounds(textBoxClientID.getX() + buttonSize.width + padding, textBoxClientID.getY(), 103, buttonSize.height);
        buttonSetClientID.setEnabled(false);
        panel1.add(buttonSetClientID);

        buttonSetAllClientID = new JButton("Set All Client ID");
        buttonSetAllClientID.setBounds(buttonSetClientID.getX() + buttonSetClientID.getWidth() + padding, buttonSetClientID.getY(), 120, buttonSize.height);
        buttonSetAllClientID.setEnabled(false);
        panel1.add(buttonSetAllClientID);

        textBox1 = new JTextArea();
        textBox1.setBounds(100, 100, 100, 100);
        textBox1.setFont(new Font("Consolas", Font.PLAIN, 13));
        textBox1.setLineWrap(true);
        textBox1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textBox1.setEditable(false);
        textBox1.setEnabled(false);
        panel1.add(textBox1);

        setVisible(true);
    }

    private void setupControls(Point gridOffset, Dimension gridButtonSize, int gridPadding) {
        panel1.setLocation((gridButtonSize.width + gridPadding) * 9 + gridOffset.x + gridButtonSize.width + 6 , gridOffset.y);
        panel1.setSize(getSize().width - panel1.getX() - 24, (gridButtonSize.height + gridPadding) * 9 + gridButtonSize.height);

        textBox1.setLocation(5, 20);
        textBox1.setSize(panel1.getWidth() - 7 - textBox1.getX(), 75);


        buttonLoadMii.setBounds(5, textBox1.getY() + textBox1.getHeight() + padding, buttonSize.width, buttonSize.height);
        buttonSaveMii.setBounds(buttonLoadMii.getX() + buttonSize.width + padding, buttonLoadMii.getY(), buttonSize.width, buttonSize.height);
        buttonClearMii.setBounds(buttonSaveMii.getX() + buttonSize.width + padding, buttonSaveMii.getY(), buttonSize.width, buttonSize.height);
        buttonMiiDone.setBounds(buttonLoadMii.getX(), buttonLoadMii.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        spinnerIndex.setBounds(buttonMiiDone.getX() + buttonSize.width + padding, buttonMiiDone.getY(), buttonSize.width, buttonSize.height);
        textBoxClientID.setBounds(buttonLoadMii.getX(), buttonMiiDone.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        buttonSetClientID.setBounds(textBoxClientID.getX() + buttonSize.width + padding, textBoxClientID.getY(), 103, buttonSize.height);
        buttonSetAllClientID.setBounds(buttonSetClientID.getX() + buttonSetClientID.getWidth() + padding, buttonSetClientID.getY(), 120, buttonSize.height);

        textBox1.setEnabled(true);

        panel1.setVisible(true);
    }

    public void CreateButtonGrid() {
        Point gridOffset = new Point(buttonLoad.getX(), buttonLoad.getY() + buttonLoad.getHeight() + padding);
        Dimension gridButtonSize = new Dimension(100, 50);
        int gridPadding = 4;

        setupControls(gridOffset, gridButtonSize, gridPadding);

        for (int row = 0; row <= 9; row++) {
            for (int col = 0; col <= 9; col++) {
                int index = row * 10 + col;

                Color borderColor = Color.RED;
                String miiName = "";

                JButton button = new JButton(miiName);
                button.setSize(gridButtonSize);
                button.setLocation(new Point(gridOffset.x + col * (gridButtonSize.width + gridPadding), gridOffset.y + row * (gridButtonSize.height + gridPadding)));
                button.setBackground(Color.lightGray);
                button.setName("ButtonGrid" + index);
                button.setMargin(new Insets(gridPadding, gridPadding, gridPadding, gridPadding));
                button.setBorder(BorderFactory.createLineBorder(borderColor));

                button.addActionListener(e -> buttonClick(index));

                add(button);

                buttons[index] = button;
            }
        }

        repaint();
    }

    private void buttonClick(int index) {
//        System.out.println(index);

        for (int i = 0; i < 100; i++) {
            if (i == index) {
                buttons[index].setBackground(Color.CYAN);
            } else {
                buttons[i].setBackground(Color.lightGray);
            }
        }

        Main.GridButton_Click(index);

    }

//    public void addButtonLoadActionListener(ActionListener listener) {
//        buttonLoad.addActionListener(listener);
//    }
//
//    public void addButtonSaveActionListener(ActionListener listener) {
//        buttonSave.addActionListener(listener);
//    }

    public void addButtonActionListener(JButton button, ActionListener listener) {
        button.addActionListener(listener);
    }

    public void addSpinnerActionListener(JSpinner spinner, ChangeListener listener) {
        spinner.addChangeListener(listener);
    }

}
