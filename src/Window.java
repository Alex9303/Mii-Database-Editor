import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeListener;
import java.awt.Point;
import java.util.function.Consumer;

public class Window extends JFrame {
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

    JButton[] buttons = new JButton[100];
    Dimension buttonSize = new Dimension(83, 25);
    int padding = 6;

    public Window() {
        setLayout(null);
        initializeComponents();
    }

    private void initializeComponents()  {
        setTitle("Mii Database Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1484, 624);
        setLayout(null);

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
        buttonLoadMii.setEnabled(false);
        panel1.add(buttonLoadMii);

        buttonSaveMii = new JButton("Save Mii");
        buttonSaveMii.setEnabled(false);
        panel1.add(buttonSaveMii);

        buttonClearMii = new JButton("Clear Mii");
        buttonClearMii.setEnabled(false);
        panel1.add(buttonClearMii);

        buttonMiiDone = new JButton("Done");
        buttonMiiDone.setEnabled(false);
        panel1.add(buttonMiiDone);

        spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        spinnerIndex.setEnabled(false);
        panel1.add(spinnerIndex);

        textBoxSystemID = new JTextField();
        textBoxSystemID.setEnabled(false);
        panel1.add(textBoxSystemID);

        buttonSetSystemID = new JButton("Set System ID");
        buttonSetSystemID.setEnabled(false);
        panel1.add(buttonSetSystemID);

        buttonSetAllSystemID = new JButton("Set All System ID");
        buttonSetAllSystemID.setEnabled(false);
        panel1.add(buttonSetAllSystemID);

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
        textBoxSystemID.setBounds(buttonLoadMii.getX(), buttonMiiDone.getY() + buttonSize.height + padding, buttonSize.width, buttonSize.height);
        buttonSetSystemID.setBounds(textBoxSystemID.getX() + buttonSize.width + padding, textBoxSystemID.getY(), 114, buttonSize.height);
        buttonSetAllSystemID.setBounds(buttonSetSystemID.getX() + buttonSetSystemID.getWidth() + padding, buttonSetSystemID.getY(), 130, buttonSize.height);

        textBox1.setEnabled(true);
        panel1.setVisible(true);
    }

    private JButton createGridButton(int index, Point location, Dimension size, Color borderColor) {
        JButton button = new JButton();
        button.setSize(size);
        button.setLocation(location);
        button.setBackground(Color.LIGHT_GRAY);
        button.setName("ButtonGrid" + index);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createLineBorder(borderColor));
        button.addActionListener(e -> buttonClick(index));
        return button;
    }

    public void CreateButtonGrid() {
        Point gridOffset = new Point(buttonLoad.getX(), buttonLoad.getY() + buttonLoad.getHeight() + padding);
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
                buttons[index] = createGridButton(index, location, gridButtonSize, Color.RED);
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
}