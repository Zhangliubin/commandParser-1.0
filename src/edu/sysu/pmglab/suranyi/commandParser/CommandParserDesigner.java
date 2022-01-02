package edu.sysu.pmglab.suranyi.commandParser;

import com.formdev.flatlaf.FlatLightLaf;
import dev.BGZIPParserFromFile;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.container.SmartList;
import edu.sysu.pmglab.suranyi.easytools.FileUtils;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * @author suranyi
 * @description parser 设计器
 */

public class CommandParserDesigner extends JFrame {
    CommandTableModel commandModel;
    CommandTableModel parserTestingModel;
    CommandTableModel ruleModel;

    private JTable commandTable;
    private JComboBox<String> globalRuleComboBox;
    private JTable ruleTable;
    private JPanel mainPanel;
    private JScrollPane commandScrollPane;
    private JButton saveButton;
    private JTabbedPane tabbedPane;
    private JTextField mainClassTextField;
    private JScrollPane ruleScrollPane;
    private JButton upButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton downButton;
    private JTextArea commandPreview;
    private JButton openButton;
    private JSpinner offsetSpinner;
    private JButton clearButton;
    private JTextArea parserTestingInputTextArea;
    private JTable parserTestingTable;
    private JScrollPane parserTestingScrollPane;
    private JScrollPane parserTestingInputScrollPane;
    private JButton parserTestingParseButton;
    private JButton parserTestingClearButton;
    private JButton parserTestingOpenButton;
    private JButton checkButton;
    private JCheckBox debugModeCheckBox;
    private JTextField searchBox;
    private SmartList<Object[]> commandBackupList;
    private SmartList<Object[]> ruleBackupList;
    private String openFileName;

    CommandParserDesigner() {
        setTitle("Command Parser Designer");

        pack();
        setResizable(false);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置窗口大小
        initSize(1000, 600);

        // 添加监听器
        addListener();

        // 设为可见
        setVisible(true);
    }

    /**
     * 初始化窗口大小
     */
    void initSize(int width, int height) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((dimension.width - width) >> 1, (dimension.height - height) >> 1,
                width, height);
    }

    public void addListener() {
        commandPreview.setEditable(false);

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (tabbedPane.getSelectedIndex() == 2) {
                    try {
                        CommandParser parser = transToParser();
                        if (parser != null) {
                            commandPreview.setText(parser.toString());
                            commandPreview.setCaretPosition(0);
                        }
                    } catch (Exception exception) {
                        commandPreview.setText("");
                        JOptionPane.showOptionDialog(null, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                    }
                }
            }
        });

        commandScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (commandBackupList == null && (e.getClickCount() == 2 && commandModel.getRowCount() >= 1 && !commandModel.getValueAt(commandModel.getRowCount() - 1, 0).equals(".")) ||
                        commandTable.getRowCount() == 0) {
                    // 双击创建新行
                    commandModel.addRow(new Object[]{".", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});

                    int selectRowIndex = commandModel.getRowCount() - 1;
                    commandTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            }
        });

        for (Component component : new Component[]{commandScrollPane, ruleScrollPane, commandPreview}) {
            new DropTarget(component, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent e) {
                    try {
                        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            // 接受拖拽来的数据
                            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            if (list.size() == 1) {
                                File file = list.get(0);
                                setTitle("Command Parser Designer: " + file.getName());
                                loadFromFile(file.getAbsolutePath());
                                tabbedPane.setSelectedIndex(0);
                            } else {
                                // 拒绝拖拽来的数据
                                JOptionPane.showOptionDialog(null, "Only a single file is allowed.", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                                e.rejectDrop();
                            }
                        } else {
                            // 拒绝拖拽来的数据
                            e.rejectDrop();
                        }
                    } catch (Exception ignored) {
                        setTitle("Command Parser Designer");
                    }
                }
            });
        }

        commandModel.addCellEditor(commandTable, "request", new DefaultCellEditor(new JCheckBox()));
        commandModel.addCellEditor(commandTable, "hidden", new DefaultCellEditor(new JCheckBox()));
        commandModel.addCellEditor(commandTable, "convertTo", new DefaultCellEditor(new JComboBox<>(new String[]{"built-in", "passedIn", "boolean", "short", "integer", "long", "float", "double", "string", "short-array", "integer-array", "long-array", "float-array", "double-array", "string-array", "k1=v1;k2=v2;...", "<start>-<end> (integer)", "<start>-<end> (long)", "<start>-<end> (double)", "<index>:<start>-<end> (integer)", "<start>-<end> (string)", "<index>:<start>-<end> (string)"})));

        JComboBox<String> validateWithCombobox = new JComboBox<>(new String[]{".", "built-in", "NotDirectory", "EnsureFileExists", "RangeOf($start,$end)", "ElementOf($value,$value,...)"});
        validateWithCombobox.setEditable(true);
        commandModel.addCellEditor(commandTable, "validateWith", new DefaultCellEditor(validateWithCombobox));
        commandModel.addCellEditor(commandTable, "arity", new DefaultCellEditor(new JComboBox<>(new Object[]{0, 1, "≥1", 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})));

        ruleScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ruleBackupList == null && (e.getClickCount() == 2 && ruleModel.getRowCount() >= 1 && !ruleModel.getValueAt(ruleModel.getRowCount() - 1, 0).equals(".")) ||
                        ruleModel.getRowCount() == 0) {
                    // 双击创建新行
                    ruleModel.addRow(new Object[]{".", ".", CommandRuleType.AT_MOST_ONE});

                    int selectRowIndex = ruleModel.getRowCount() - 1;
                    ruleTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            }
        });

        ruleModel.addCellEditor(ruleTable, "ruleType", new DefaultCellEditor(new JComboBox<>(new CommandRuleType[]{CommandRuleType.AT_MOST_ONE, CommandRuleType.AT_LEAST_ONE, CommandRuleType.REQUEST_ONE, CommandRuleType.PRECONDITION, CommandRuleType.SYMBIOSIS})));

        searchBox.addActionListener(e -> {
            String filter = searchBox.getText().trim();
            if (filter.length() == 0) {
                if (commandBackupList == null && ruleBackupList == null) {
                    // 说明没有备份数据
                } else {
                    // 恢复数据
                    commandModel.data = commandBackupList;
                    ruleModel.data = ruleBackupList;
                    commandModel.flush();
                    ruleModel.flush();
                    commandTable.clearSelection();
                    ruleTable.clearSelection();
                    commandBackupList = null;
                    ruleBackupList = null;
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    upButton.setEnabled(true);
                    downButton.setEnabled(true);
                }
            } else {
                // 执行搜索
                addButton.setEnabled(false);
                deleteButton.setEnabled(false);
                upButton.setEnabled(false);
                downButton.setEnabled(false);
                commandTable.clearSelection();
                ruleTable.clearSelection();
                if (commandBackupList == null && ruleBackupList == null) {
                    commandBackupList = commandModel.data;
                    ruleBackupList = ruleModel.data;
                }

                commandModel.data = new SmartList<>();
                ruleModel.data = new SmartList<>();
                commandModel.flush();
                ruleModel.flush();
                if (filter.contains(":")) {
                    // 识别为 列名:值 形式
                    String[] values = filter.split(":");
                    if (values.length == 2) {
                        if ("command".equalsIgnoreCase(values[0]) || "commandname".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (((String) row[0]).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("request".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (row[1].equals(Boolean.parseBoolean(values[1]))) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("default".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (((String) row[2]).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("convert".equalsIgnoreCase(values[0]) || "convertto".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (((String) row[3]).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("validate".equalsIgnoreCase(values[0]) || "validatewith".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (((String) row[4]).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("arity".equalsIgnoreCase(values[0]) || "length".equalsIgnoreCase(values[0])) {
                            if ("-1".equals(values[1]) || ">=1".equals(values[1])) {
                                for (Object[] row : commandBackupList) {
                                    if ((row[5].toString()).contains("≥1")) {
                                        commandModel.addRow(row);
                                    }
                                }
                            } else {
                                for (Object[] row : commandBackupList) {
                                    if ((row[5].toString()).contains(values[1])) {
                                        commandModel.addRow(row);
                                    }
                                }
                            }
                            return;
                        } else if ("group".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if ((row[6].toString()).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("description".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if ((row[7].toString()).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("format".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if ((row[8].toString()).contains(values[1])) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("hide".equalsIgnoreCase(values[0]) || "hidden".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (row[9].equals(Boolean.parseBoolean(values[1]))) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("help".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (row[10].equals(Boolean.parseBoolean(values[1]))) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        } else if ("debug".equalsIgnoreCase(values[0])) {
                            for (Object[] row : commandBackupList) {
                                if (row[11].equals(Boolean.parseBoolean(values[1]))) {
                                    commandModel.addRow(row);
                                }
                            }
                            return;
                        }
                    }
                }

                for (Object[] row : commandBackupList) {
                    if (((String) row[0]).contains(filter) || ((String) row[2]).contains(filter) || ((String) row[3]).contains(filter)
                            || ((String) row[4]).contains(filter) || ((String) row[6]).contains(filter) || ((String) row[7]).contains(filter) ||
                            ((String) row[8]).contains(filter)) {
                        commandModel.addRow(row);
                    }
                }

                for (Object[] row : ruleBackupList) {
                    if (((String) row[0]).contains(filter) || ((String) row[1]).contains(filter)) {
                        ruleModel.addRow(row);
                    }
                }
            }
        });

        deleteButton.addActionListener(e -> {
            // 删除后光标失焦
            if (tabbedPane.getSelectedIndex() == 0) {
                int selectedRow = commandTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow <= commandTable.getRowCount() - 1) {
                    commandModel.deleteRow(selectedRow);
                }

                commandTable.clearSelection();
                ruleTable.clearSelection();
            } else if (tabbedPane.getSelectedIndex() == 1) {
                int selectedRow = ruleTable.getSelectedRow();

                if (selectedRow >= 0 && selectedRow <= ruleTable.getRowCount() - 1) {
                    ruleModel.deleteRow(selectedRow);
                }

                commandTable.clearSelection();
                ruleTable.clearSelection();
            }
        });

        addButton.addActionListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                int selectedRow = commandTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow <= commandTable.getRowCount() - 1) {
                    commandModel.insertRow(new Object[]{".", commandModel.getValueAt(selectedRow, 1), commandModel.getValueAt(selectedRow, 2), commandModel.getValueAt(selectedRow, 3), commandModel.getValueAt(selectedRow, 4), commandModel.getValueAt(selectedRow, 5), commandModel.getValueAt(selectedRow, 6), ".", ".", commandModel.getValueAt(selectedRow, 9), Boolean.FALSE, Boolean.FALSE}, selectedRow + 1);
                    commandTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                } else {
                    // 追加项目
                    if (commandModel.getRowCount() == 0) {
                        commandModel.addRow(new Object[]{".", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
                    } else {
                        commandModel.addRow(new Object[]{".", Boolean.FALSE, ".", "passedIn", ".", 0, commandModel.getValueAt(commandModel.getRowCount() - 1, 6), ".", ".", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
                    }

                    int selectRowIndex = commandModel.getRowCount() - 1;
                    commandTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            } else if (tabbedPane.getSelectedIndex() == 1) {
                int selectedRow = ruleTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow <= ruleTable.getRowCount() - 1) {
                    ruleModel.insertRow(new Object[]{".", ".", CommandRuleType.AT_MOST_ONE}, selectedRow + 1);
                    ruleTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                } else {
                    // 追加项目
                    ruleModel.addRow(new Object[]{".", ".", CommandRuleType.AT_MOST_ONE});

                    int selectRowIndex = ruleModel.getRowCount() - 1;
                    ruleTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            }
        });

        upButton.addActionListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                int selectedRow = commandTable.getSelectedRow();
                if (selectedRow >= 1 && selectedRow <= commandTable.getRowCount() - 1) {
                    commandModel.upRow(selectedRow);

                    commandTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
            } else if (tabbedPane.getSelectedIndex() == 1) {
                int selectedRow = ruleTable.getSelectedRow();
                if (selectedRow >= 1 && selectedRow <= ruleTable.getRowCount() - 1) {
                    ruleModel.upRow(selectedRow);
                    ruleTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
            }
        });

        downButton.addActionListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                int selectedRow = commandTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow <= commandTable.getRowCount() - 2) {
                    commandModel.downRow(selectedRow);
                    commandTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                }
            } else if (tabbedPane.getSelectedIndex() == 1) {
                int selectedRow = ruleTable.getSelectedRow();

                if (selectedRow >= 0 && selectedRow <= ruleTable.getRowCount() - 2) {
                    ruleModel.downRow(selectedRow);
                    ruleTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                }
            }
        });

        saveButton.addActionListener(e -> {
            if (openFileName != null) {
                int status;
                if (FileUtils.exists(openFileName)) {
                    status = JOptionPane.showOptionDialog(this, openFileName + " already exists; do you want to overwrite it?", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "Save as...", "Cancel"}, "Yes");
                } else {
                    status = JOptionPane.showOptionDialog(this, "Would you want to save as " + openFileName + "?", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "Save as...", "Cancel"}, "Yes");
                }

                if (status == 0) {
                    CommandParser parser = transToParser();
                    if (openFileName.endsWith(".java")) {
                        toJavaFile(openFileName);
                    } else {
                        parser.toFile(openFileName);
                    }
                    JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                } else if (status == 1) {
                    try {
                        JFileChooser jfc = new JFileChooser();
                        jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);
                        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Java Script Format (*.java)", "java"));
                        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Command Format (*.cp)", "cp"));
                        jfc.setCurrentDirectory(new File(openFileName).getParentFile());

                        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                        if (jfc.showSaveDialog(this) == 0) {
                            CommandParser parser = transToParser();
                            if (jfc.getFileFilter().getDescription().equals("Java Script Format (*.java)")) {
                                if (parser != null) {
                                    openFileName = jfc.getSelectedFile().getAbsolutePath();
                                    if (!openFileName.endsWith(".java")) {
                                        openFileName += ".java";
                                    }

                                    toJavaFile(openFileName);
                                    JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                                }
                            } else if (jfc.getFileFilter().getDescription().equals("Command Format (*.cp)")) {
                                if (parser != null) {
                                    openFileName = jfc.getSelectedFile().getAbsolutePath();
                                    if (!openFileName.endsWith(".cp")) {
                                        openFileName += ".cp";
                                    }

                                    parser.toFile(openFileName);
                                    setTitle("Command Parser Designer: " + openFileName);
                                    JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                                }
                            }
                        }
                    } catch (Exception exception) {
                        JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                    }
                }
            } else {
                try {
                    JFileChooser jfc = new JFileChooser();
                    jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);
                    jfc.addChoosableFileFilter(new FileNameExtensionFilter("Java Script Format (*.java)", "java"));
                    jfc.addChoosableFileFilter(new FileNameExtensionFilter("Command Format (*.cp)", "cp"));
                    jfc.setCurrentDirectory(new File(System.getProperty("user.dir")).getParentFile());

                    jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    if (jfc.showSaveDialog(this) == 0) {
                        CommandParser parser = transToParser();
                        if (jfc.getFileFilter().getDescription().equals("Java Script Format (*.java)")) {
                            if (parser != null) {
                                openFileName = jfc.getSelectedFile().getAbsolutePath();
                                if (!openFileName.endsWith(".java")) {
                                    openFileName += ".java";
                                }

                                toJavaFile(openFileName);
                                setTitle("Command Parser Designer");
                                JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                            }
                        } else if (jfc.getFileFilter().getDescription().equals("Command Format (*.cp)")) {
                            if (parser != null) {
                                openFileName = jfc.getSelectedFile().getAbsolutePath();
                                if (!openFileName.endsWith(".cp")) {
                                    openFileName += ".cp";
                                }

                                parser.toFile(openFileName);
                                setTitle("Command Parser Designer: " + openFileName);
                                JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                            }
                        }
                    }
                } catch (Exception exception) {
                    JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                }
            }
        });

        openButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (openFileName == null) {
                jfc.setCurrentDirectory(new File(System.getProperty("user.dir")).getParentFile());
            } else {
                jfc.setCurrentDirectory(new File(openFileName).getParentFile());
            }

            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    // 加载解析器
                    File file = jfc.getSelectedFile();
                    setTitle("Command Parser Designer: " + file.getName());
                    loadFromFile(file.getAbsolutePath());
                    tabbedPane.setSelectedIndex(0);
                } catch (Exception exception) {
                    setTitle("Command Parser Designer");
                }
            }
        });

        clearButton.addActionListener(e -> {
            // 将解析器映射为 GUI 组件
            commandModel.clearAll();
            ruleModel.clearAll();
            commandTable.clearSelection();
            ruleTable.clearSelection();

            // 清除搜索信息
            searchBox.setText("");
            openFileName = null;
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            upButton.setEnabled(true);
            downButton.setEnabled(true);
            ruleBackupList = null;
            commandBackupList = null;

            // 设置主程序名、偏移量、全局规则
            mainClassTextField.setText("<main class>");
            offsetSpinner.setValue(0);
            globalRuleComboBox.setSelectedItem(".");
            commandPreview.setText("");
            setTitle("Command Parser Designer");

            // 添加默认信息
            commandModel.addRow(new Object[]{"--help,-help,-h", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE});
        });

        checkButton.addActionListener(e -> {
            try {
                transToParser();
                JOptionPane.showOptionDialog(this, "Congratulations, the parser has passed the check!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
            } catch (Exception exception) {
                JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
            }
        });

        parserTestingClearButton.addActionListener(e -> {
            parserTestingInputTextArea.setText("");
            parserTestingModel.clearAll();
        });

        parserTestingOpenButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(System.getProperty("user.dir")).getParentFile());

            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                // 加载解析器
                try (FileStream input = new FileStream(jfc.getSelectedFile().getAbsolutePath())) {
                    parserTestingModel.clearAll();
                    parserTestingInputTextArea.setText(new String(input.readAll()));
                    parserTestingInputTextArea.setCaretPosition(0);
                } catch (IOException ioException) {
                    JOptionPane.showOptionDialog(this, ioException.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                }
            }

        });

        new DropTarget(parserTestingInputTextArea, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        // 接受拖拽来的数据
                        e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        if (list.size() == 1) {
                            try (FileStream input = new FileStream(list.get(0).getAbsolutePath())) {
                                parserTestingModel.clearAll();
                                parserTestingInputTextArea.setText(new String(input.readAll()));
                                parserTestingInputTextArea.setCaretPosition(0);
                            } catch (IOException ioException) {
                                JOptionPane.showOptionDialog(null, ioException.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                            }

                        } else {
                            // 拒绝拖拽来的数据
                            JOptionPane.showOptionDialog(null, "Only a single file is allowed.", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                            e.rejectDrop();
                        }
                    } else {
                        // 拒绝拖拽来的数据
                        e.rejectDrop();
                    }
                } catch (Exception ignored) {
                }
            }
        });

        parserTestingParseButton.addActionListener(e -> {
            try {
                CommandParser parser = transToParser();
                CommandMatcher options = parser.parseFromString(parserTestingInputTextArea.getText());
                parserTestingModel.clearAll();

                HashMap<String, String> commandPassedInValues = new HashMap<>();
                for (String[] commandGroup : options.caughtValues) {
                    commandPassedInValues.put(commandGroup[0], commandGroup[1]);
                }

                for (String commandName : parser.mainRegisteredCommandItems) {
                    String commandNames = Arrays.toString(parser.getCommandItem(commandName).getCommandNames()).replace(" ", "");

                    if (options.isPassedIn(commandName)) {
                        parserTestingModel.addRow(new Object[]{commandNames.substring(1, commandNames.length() - 1), options.isPassedIn(commandName), commandPassedInValues.get(commandName)});
                    } else {
                        if (parser.debug || !parser.getCommandItem(commandName).isDebug()) {
                            parserTestingModel.addRow(new Object[]{commandNames.substring(1, commandNames.length() - 1), options.isPassedIn(commandName), ""});
                        }
                    }
                }
            } catch (Exception exception) {
                JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
            }
        });
    }

    void loadFromFile(String fileName) {
        commandModel.clearAll();
        ruleModel.clearAll();
        searchBox.setText("");
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
        upButton.setEnabled(true);
        downButton.setEnabled(true);
        ruleBackupList = null;
        commandBackupList = null;

        // 将解析器映射为 GUI 组件
        CommandParser parser;
        try {
            parser = CommandParser.loadFromFile(fileName);
        } catch (Exception exception) {
            JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
            throw new CommandParserException(exception.getMessage());
        }

        // 设置主程序名、偏移量、全局规则
        mainClassTextField.setText(parser.usage.programName);
        offsetSpinner.setValue(parser.offset);
        globalRuleComboBox.setSelectedItem(parser.globalRules == null ? "." : String.valueOf(parser.globalRules));
        debugModeCheckBox.setSelected(parser.debug);

        for (String commandName : parser.mainRegisteredCommandItems) {
            CommandItem item = parser.getCommandItem(commandName);
            commandModel.addRow(item.toObject());
        }

        for (int order : parser.registeredRulesOrder) {
            CommandRule rule = parser.registeredRules.get(order);
            ruleModel.addRow(new Object[]{rule.command1, rule.command2, rule.type});
        }
        openFileName = fileName;
        JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
    }

    void toJavaFile(String outputFileName) {
        // imports
        SmartList<String> imports = new SmartList<>();
        imports.add("// TODO: package path\npackage ;\n");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.CommandParser;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.CommandMatcher;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.converter.*;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.validator.*;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.converter.array.*;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.converter.map.*;");
        imports.add("import edu.sysu.pmglab.suranyi.commandParser.converter.value.*;");
        imports.add("\n");
        imports.add("import static edu.sysu.pmglab.suranyi.commandParser.CommandOptions.*;");
        imports.add("import static edu.sysu.pmglab.suranyi.commandParser.CommandRuleType.*;");

        SmartList<String> lines = new SmartList<>();
        SmartList<String> cache = new SmartList<>();
        String className = new File(outputFileName).getName();
        className = className.endsWith(".java") ? className.substring(0, className.length() - 5) : className;

        lines.add("public enum " + className + " {\n" +
                "    /**\n" +
                "     * single instance\n" +
                "     */\n" +
                "    INSTANCE;\n\n" +
                "    final CommandParser parser;\n\n" +
                "    public static CommandParser getParser() {\n" +
                "        return INSTANCE.parser;\n" +
                "    }\n\n" +
                "    public static CommandMatcher parse(String... args) {\n" +
                "        return INSTANCE.parser.parse(args);\n" +
                "    }\n\n" +
                "    public static void toFile(String fileName) {\n" +
                "        INSTANCE.parser.toFile(fileName);\n" +
                "    }\n\n" +
                "    " + className + "() {\n" +
                "        // global options\n" +
                "        parser = new CommandParser(false);\n" +
                "        parser.setProgramName(\"" + mainClassTextField.getText().trim() + "\");\n" +
                "        parser.offset(" + offsetSpinner.getValue() + ");\n" +
                "        parser.debug(" + debugModeCheckBox.isSelected() + ");\n" +
                "        parser.registerGlobalRule(" + (Objects.equals(globalRuleComboBox.getSelectedItem(), ".") ? "null" : globalRuleComboBox.getSelectedItem()) + ");\n\n" +
                "        // add commandItems");

        SmartList<Object[]> copy = commandBackupList == null ? commandModel.data : commandBackupList;
        for (Object[] row : copy) {
            if (".".equals(row[0])) {
                continue;
            }

            SmartList<String> commandScript = new SmartList<>();
            commandScript.add("        parser.register(" + merge(true, ((String) row[0]).split(",")) + ")");

            if ((boolean) row[1]) {
                cache.add("REQUEST");
            }

            if ((boolean) row[9]) {
                cache.add("HIDDEN");
            }

            if ((boolean) row[10]) {
                cache.add("HELP");
            }

            if ((boolean) row[11]) {
                cache.add("DEBUG");
            }

            if (cache.size() != 0) {
                commandScript.add("              .addOptions(" + merge(false, cache.toStringArray()) + ")");
                cache.clear();
            }

            // 参数长度验证
            int length;
            if (!row[5].equals(CommandOptions.MISS_VALUE)) {
                try {
                    if (row[5].equals("≥1")) {
                        commandScript.add("              .arity(-1)");
                        length = -1;
                    } else {
                        commandScript.add("              .arity(" + row[5] + ")");
                        length = (int) row[5];
                    }
                } catch (NumberFormatException e) {
                    throw new CommandParserException(row[0] + ": couldn't identify arity=" + row[5]);
                }
            } else {
                // 没有设置长度，则按照转换器的长度默认值设置
                commandScript.add("              .arity(1)");
                length = 1;
            }

            switch ((String) row[3]) {
                case "boolean":
                    commandScript.add("              .convertTo(new BooleanConverter() {})");
                    break;
                case "short":
                    commandScript.add("              .convertTo(new ShortConverter() {})");
                    break;
                case "integer":
                    commandScript.add("              .convertTo(new IntConverter() {})");
                    break;
                case "long":
                    commandScript.add("              .convertTo(new LongConverter() {})");
                    break;
                case "float":
                    commandScript.add("              .convertTo(new FloatConverter() {})");
                    break;
                case "double":
                    commandScript.add("              .convertTo(new DoubleConverter() {})");
                    break;
                case "string":
                    commandScript.add("              .convertTo(new StringConverter() {})");
                    break;
                case CommandOptions.MISS_VALUE:
                    // 未指定转换器时结合其他信息推断
                    if (row[5].equals("0")) {
                        commandScript.add("              .convertTo(new PassedInConverter() {})");
                    } else if (row[5].equals("1") || row[5].equals(CommandOptions.MISS_VALUE)) {
                        commandScript.add("              .convertTo(new StringConverter() {})");
                    } else {
                        commandScript.add("              .convertTo(new StringArrayConverter() {})");
                    }
                    break;
                case "passedIn":
                    commandScript.add("              .convertTo(new PassedInConverter() {})");
                    break;
                case "boolean-array":
                    commandScript.add("              .convertTo(new BooleanArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "short-array":
                    commandScript.add("              .convertTo(new ShortArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "integer-array":
                    commandScript.add("              .convertTo(new IntArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "long-array":
                    commandScript.add("              .convertTo(new LongArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "string-array":
                    commandScript.add("              .convertTo(new StringArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "float-array":
                    commandScript.add("              .convertTo(new FloatArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "double-array":
                    commandScript.add("              .convertTo(new DoubleArrayConverter(" + (length == 1 ? "\",\"" : "") + ") {})");
                    break;
                case "k1=v1;k2=v2;...":
                    commandScript.add(
                            "              .convertTo(new KVConverter<String, String>() {\n" +
                                    "                   @Override\n" +
                                    "                   public HashMap<String, String> convert(String... params) {\n" +
                                    "                       return parseKV(params);\n" +
                                    "                   }\n" +
                                    "               })");
                    break;
                case "<start>-<end> (double)":
                    commandScript.add("              .convertTo(new NaturalDoubleRangeConverter() {})");
                    break;
                case "<start>-<end> (integer)":
                    commandScript.add("              .convertTo(new NaturalIntRangeConverter() {})");
                    break;
                case "<index>:<start>-<end> (integer)":
                    commandScript.add("              .convertTo(new NaturalIntRangeWithIndexConverter() {})");
                    break;
                case "<start>-<end> (long)":
                    commandScript.add("              .convertTo(new NaturalLongRangeConverter() {})");
                    break;
                case "<start>-<end> (string)":
                    commandScript.add("              .convertTo(new RangeConverter() {})");
                    break;
                case "<index>:<start>-<end> (string)":
                    commandScript.add("              .convertTo(new RangeWithIndexConverter() {})");
                    break;
                default:
                    // built-in 或其他的转换器
                    // 其他情况需要用户重新配置
                    commandScript.add("              .convertTo(new IConverter<Object>() {\n" +
                            "                  @Override\n" +
                            "                  public Object convert(String... params) {\n" +
                            "                      // TODO: method body and type of the return value\n" +
                            "                      return null;\n" +
                            "                  }\n" +
                            "              })");

                    break;
            }

            if (!row[2].equals(CommandOptions.MISS_VALUE)) {
                if (((String) row[2]).contains(",") && length != 0 && length != 1) {
                    commandScript.add("              .setDefaultByConverter(" + merge(true, ((String) row[2]).split(",")) + ")");
                } else {
                    commandScript.add("              .setDefaultByConverter(\"" + row[2] + "\")");
                }
            }

            // 设定验证器
            if (!row[4].equals(CommandOptions.MISS_VALUE)) {
                String[] validators = ((String) row[4]).split(";");

                for (String validator : validators) {
                    // 替换空格
                    validator = validator.replace(" ", "");

                    // 验证器转为小写
                    String validator2LowerCase = validator.toLowerCase(Locale.ROOT);
                    if (validator2LowerCase.startsWith("rangeof(") && validator2LowerCase.endsWith(")")) {
                        cache.add("new RangeValidator(" + Double.parseDouble(validator2LowerCase.substring(8, validator2LowerCase.indexOf(","))) + ", " + Double.parseDouble(validator2LowerCase.substring(validator2LowerCase.indexOf(",") + 1, validator2LowerCase.indexOf(")"))) + ")");
                    } else if (validator2LowerCase.equals("ensurefileexists")) {
                        cache.add("EnsureFileExistsValidator.INSTANCE");
                    } else if (validator2LowerCase.equals("notdirectory")) {
                        cache.add("EnsureFileIsNotDirectoryValidator.INSTANCE");
                    } else if (validator2LowerCase.startsWith("elementof(") && validator2LowerCase.endsWith(")")) {
                        cache.add("new ElementValidator(" + merge(true, (validator.substring(10, validator.length() - 1).split(","))) + ")");
                    } else {
                        cache.add("new IValidator() {\n" +
                                "                  @Override\n" +
                                "                  public void validate(String commandKey, Object params) {\n" +
                                "                      // TODO: method body\n" +
                                "                      throw new ParameterException()\n" +
                                "                  }\n" +
                                "              }");
                    }
                }

                commandScript.add("              .validateWith(" + merge(false, cache.toStringArray()) + ")");
                cache.clear();
            }

            commandScript.add("              .setOptionGroup(\"" + row[6] + "\")");

            if (!row[7].equals(CommandOptions.DEFAULT_DESCRIPTION)) {
                commandScript.add("              .setDescription(\"" + row[7] + "\")");
            }

            if (!row[8].equals(CommandOptions.DEFAULT_FORMAT)) {
                commandScript.add("              .setFormat(\"" + row[8] + "\")");
            }

            lines.add(String.join("\n", commandScript.toStringArray()) + ";");
        }

        copy = ruleBackupList == null ? ruleModel.data : ruleBackupList;
        if (copy.size() > 0) {
            lines.add("\n        // add commandRules");

            for (Object[] row : copy) {
                if (".".equals(row[0]) || ".".equals(row[1]) || ".".equals(row[2])) {
                    continue;
                }

                lines.add("        parser.registerRule(\"" + row[0] + "\", \"" + row[1] + "\", " + row[2] + ");");
            }
        }


        lines.add("    }");
        lines.add("}");

        try (FileStream file = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER)) {
            for (String imp : imports) {
                file.write(imp + "\n");
            }
            file.write("\n");

            for (String line : lines) {
                file.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String merge(boolean addQuotation, String... strings) {
        int count = 0;
        StringBuilder builder = new StringBuilder();

        for (String string : strings) {
            if (string.length() > 0) {
                count++;
            }
        }

        if (count == 0) {
            return "";
        } else {
            for (String string : strings) {
                if (string.length() > 0) {
                    if (addQuotation) {
                        builder.append("\"" + string + "\", ");
                    } else {
                        builder.append(string + ", ");
                    }
                }
            }

            return builder.substring(0, builder.length() - 2);
        }
    }

    CommandParser transToParser() {
        CommandParser parser = new CommandParser(false, mainClassTextField.getText().trim());
        parser.debug = debugModeCheckBox.isSelected();
        parser.offset((Integer) offsetSpinner.getValue());
        // 设置了 global rule
        switch ((String) globalRuleComboBox.getSelectedItem()) {
            case "AT_MOST_ONE":
                parser.registerGlobalRule(CommandRuleType.AT_MOST_ONE);
                break;
            case "AT_LEAST_ONE":
                parser.registerGlobalRule(CommandRuleType.AT_LEAST_ONE);
                break;
            case "REQUEST_ONE":
                parser.registerGlobalRule(CommandRuleType.REQUEST_ONE);
                break;
            default:
                // "." 不做任何事情
                break;
        }

        // 添加命令
        SmartList<Object[]> copy = commandBackupList == null ? commandModel.data : commandBackupList;
        for (Object[] row : copy) {
            if (".".equals(row[0])) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            builder.append(((String) row[0]));
            builder.append("\t");
            builder.append((boolean) row[1]);
            builder.append("\t");
            builder.append(row[2]);
            builder.append("\t");
            builder.append((String) row[3]);
            builder.append("\t");
            builder.append((String) row[4]);
            builder.append("\t");
            builder.append(row[5]);
            builder.append("\t");
            builder.append((String) row[6]);
            builder.append("\t");
            builder.append((String) row[7]);
            builder.append("\t");
            builder.append((String) row[8]);
            builder.append("\t");
            builder.append((boolean) row[9]);
            builder.append("\t");
            builder.append((boolean) row[10]);
            builder.append("\t");
            builder.append((boolean) row[11]);
            parser.register(CommandItem.loadFromString(builder.toString()));
        }

        // 遍历写入规则
        copy = ruleBackupList == null ? ruleModel.data : ruleBackupList;
        for (Object[] row : copy) {
            if (".".equals(row[0]) || ".".equals(row[1]) || ".".equals(row[2])) {
                continue;
            }

            parser.registerRule((String) row[0], (String) row[1], (CommandRuleType) row[2]);
        }
        return parser;
    }

    private void createUIComponents() {
        commandTable = new JTable(commandModel = new CommandTableModel("commandName", "request", "default", "convertTo", "validateWith", "arity", "group", "description", "format", "hidden", "help", "debug"));
        commandModel.addRow(new Object[]{"--help,-help,-h", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE});
        commandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ruleTable = new JTable(ruleModel = new CommandTableModel("command1", "command2", "ruleType"));
        ruleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        parserTestingTable = new JTable(parserTestingModel = new CommandTableModel("commandName", "isPassedIn", "catch"));
        parserTestingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        offsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 128, 1));

        // 可无限横向拉长
        commandTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 表格不可编辑
        parserTestingTable.setEnabled(false);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // 神秘 bug: 放在 CommandParserDesigner 里面就会报错，放在外面就不会
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ex) {
                System.out.println("Failed to initialize LaF");
            }

            new CommandParserDesigner();
        } else if (args[0].equals("bgzip")) {
            // BGZIPParser.submit(args);
            BGZIPParserFromFile.submit(args);
        }
    }
}
