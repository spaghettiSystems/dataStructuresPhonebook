package com.company;

import com.bulenkov.darcula.DarculaLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class guistuff {
    PhoneRecords phoneBook;
    boolean filterOn = false;

    private JPanel mainPanel;
    private JList phonesList;
    private JButton addButton;
    private JTextField phoneTextField;
    private JTextField nameTextField;
    private JButton saveButton;
    private JTabbedPane tabbedPane1;
    private JPanel phoneTab;
    private JButton deleteButton;
    private JTextField filterTextBox;
    private JButton chooseFileButton1;
    private JTextField saveFileTextField;
    private JButton openButton;
    private JTextField openFileTextField;
    private JButton chooseFileButton;
    private JButton saveFileButton;
    private JButton filterButton;

    public guistuff() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (phoneBook == null) {
                    phoneBook = new PhoneRecords();
                    phonesList.setModel(new PhoneListModel());
                }

                if (verifyInput()) {
                    return;
                }

                if (phoneBook.addRecord(new PhoneRecords.Phone(phoneTextField.getText(), nameTextField.getText()))) {
                    JOptionPane.showMessageDialog(mainPanel, "You cannot save the same phone number twice!", "Duplicate phones are not allowed", JOptionPane.ERROR_MESSAGE);
                }

                phonesList.updateUI();

            }
        });
        phonesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (phonesList.getSelectedIndex() < 0) {
                    return;
                }
                int lastSelectedIndex = phonesList.getSelectedIndex();
                PhoneRecords.Phone toBeDisplayed;
                if (filterOn) {
                    toBeDisplayed = phoneBook.filteredPhoneArrayList.get(lastSelectedIndex);
                } else {
                    toBeDisplayed = phoneBook.phoneArrayList.get(lastSelectedIndex);
                }
                phoneTextField.setText(toBeDisplayed.phone);
                nameTextField.setText(toBeDisplayed.name);

            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (phonesList.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(mainPanel, "You need to select an element before you delete it!", "Seriously?", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                phoneBook.deleteRecord(phonesList.getSelectedIndex());
                phonesList.clearSelection();
                phonesList.updateUI();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (phonesList.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(mainPanel, "You need to select an element before you save stuff into it!", "Seriously?", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int lastSelectedIndex = phonesList.getSelectedIndex();

                if (verifyInput()) {
                    return;
                }

                PhoneRecords.Phone toBeModified = phoneBook.phoneArrayList.get(lastSelectedIndex);
                toBeModified.phone = phoneTextField.getText();
                toBeModified.name = nameTextField.getText();

                phonesList.updateUI();
            }
        });
        chooseFileButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileDialog = new JFileChooser();
                int fileValid = fileDialog.showSaveDialog(mainPanel);
                if (fileValid == JFileChooser.APPROVE_OPTION) {
                    File file = fileDialog.getSelectedFile();
                    saveFileTextField.setText(file.getAbsolutePath());
                }
            }
        });
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileDialog = new JFileChooser();
                int fileValid = fileDialog.showOpenDialog(mainPanel);
                if (fileValid == JFileChooser.APPROVE_OPTION) {
                    File file = fileDialog.getSelectedFile();
                    openFileTextField.setText(file.getAbsolutePath());
                }
            }
        });
        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    phoneBook.savePhoneBook(saveFileTextField.getText());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid file", "Nice try bro", JOptionPane.ERROR_MESSAGE);
                } catch (NullPointerException e) {
                    JOptionPane.showMessageDialog(mainPanel, "How can you save a phonebook if you didn't create one?", "Nice try bro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    phoneBook = new PhoneRecords(openFileTextField.getText());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid file", "Nice try bro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                phonesList.setModel(new PhoneListModel());
                phonesList.updateUI();
            }
        });
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String filter = filterTextBox.getText();
                if (filter.length() < 1) {
                    filterOn = false;
                    //.setModel(new PhoneListModel());
                    phonesList.updateUI();
                    return;
                }
                filterOn = true;

                phoneBook.initializeFilteredList(filter);

                //phonesList.setModel(new PhoneListModel());
                phonesList.updateUI();
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        JFrame frame = new JFrame("main");
        frame.setContentPane(new guistuff().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private boolean verifyInput() {
        phoneTextField.setText(removeNonNumbers(phoneTextField.getText()));

        if (!(phoneTextField.getText().length() > 6 || phoneTextField.getText().length() > 16)) {
            JOptionPane.showMessageDialog(mainPanel, "The phone number needs to be at least 7 numbers long but not longer than 16!", "Invalid input format", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        if (nameTextField.getText().length() < 3) {
            JOptionPane.showMessageDialog(mainPanel, "Names need to be at least 3 characters long", "Invalid input format", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    String removeNonNumbers(String epic) {
        for (int i = 0; i < epic.length(); i++) {
            if (!(epic.charAt(i) >= '0' && epic.charAt(i) <= '9')) {
                epic = epic.replace(epic.charAt(i) + "", "");
                i--;
            }
        }
        return epic;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setPreferredSize(new Dimension(500, 400));
        tabbedPane1 = new JTabbedPane();
        mainPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        phoneTab = new JPanel();
        phoneTab.setLayout(new GridLayoutManager(5, 4, new Insets(5, 5, 5, 5), -1, -1));
        tabbedPane1.addTab("Phonebook", phoneTab);
        final JScrollPane scrollPane1 = new JScrollPane();
        phoneTab.add(scrollPane1, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        phonesList = new JList();
        scrollPane1.setViewportView(phonesList);
        final JLabel label1 = new JLabel();
        label1.setText("Phone Number");
        phoneTab.add(label1, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setText("Add");
        phoneTab.add(addButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        phoneTab.add(deleteButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        phoneTab.add(saveButton, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        phoneTextField = new JTextField();
        phoneTab.add(phoneTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameTextField = new JTextField();
        phoneTab.add(nameTextField, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Name");
        phoneTab.add(label2, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        phoneTab.add(spacer1, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        filterTextBox = new JTextField();
        filterTextBox.setText("");
        phoneTab.add(filterTextBox, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        filterButton = new JButton();
        filterButton.setText("Filter");
        phoneTab.add(filterButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(-1, -1));
        tabbedPane1.addTab("Load/Save", panel1);
        chooseFileButton1 = new JButton();
        chooseFileButton1.setIcon(new ImageIcon(getClass().getResource("/com/company/file.png")));
        chooseFileButton1.setText("");
        panel1.add(chooseFileButton1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveFileTextField = new JTextField();
        saveFileTextField.setText("");
        panel1.add(saveFileTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        saveFileButton = new JButton();
        saveFileButton.setText("Save");
        panel1.add(saveFileButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        panel1.add(openButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openFileTextField = new JTextField();
        panel1.add(openFileTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        chooseFileButton = new JButton();
        chooseFileButton.setIcon(new ImageIcon(getClass().getResource("/com/company/file.png")));
        chooseFileButton.setText("");
        panel1.add(chooseFileButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private class PhoneListModel extends AbstractListModel {

        @Override
        public int getSize() {
            if (filterOn) {
                return phoneBook.filteredPhoneArrayList.size();
            }
            return phoneBook.phoneArrayList.size();
        }

        @Override
        public Object getElementAt(int i) {
            if (filterOn) {
                return phoneBook.filteredPhoneArrayList.get(i).phone;
            }
            return phoneBook.phoneArrayList.get(i).phone;
        }
    }

}
