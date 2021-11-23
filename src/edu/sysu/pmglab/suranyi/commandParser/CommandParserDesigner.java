package edu.sysu.pmglab.suranyi.commandParser;

import com.formdev.flatlaf.FlatLightLaf;
import dev.BGZIPParserFromFile;

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

/**
 * @author suranyi
 * @description parser 设计器
 */

public class CommandParserDesigner extends JFrame {
    CommandTableModel commandModel;
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

    CommandParserDesigner() {
        setTitle("Command Parser Designer");

        pack();
        setResizable(true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置窗口大小
        initSize(1200, 600);

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
                if ((e.getClickCount() == 2 && commandModel.getRowCount() >= 1 && !commandModel.getValueAt(commandModel.getRowCount() - 1, 0).equals(".")) ||
                        commandTable.getRowCount() == 0) {
                    // 双击创建新行
                    commandModel.addRow(new Object[]{".", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.FALSE, Boolean.FALSE});

                    int selectRowIndex = commandModel.getRowCount() - 1;
                    commandTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            }
        });

        for (Component component: new Component[]{commandScrollPane, ruleScrollPane, commandPreview}) {
            new DropTarget(component, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent e) {
                    try {
                        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            // 接受拖拽来的数据
                            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            List<File> list = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            if (list.size() == 1) {
                                loadFromFile(list.get(0).getAbsolutePath());
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
        }

        commandModel.addCellEditor(commandTable, "request", new DefaultCellEditor(new JCheckBox()));
        commandModel.addCellEditor(commandTable, "hidden", new DefaultCellEditor(new JCheckBox()));
        commandModel.addCellEditor(commandTable, "convertTo", new DefaultCellEditor(new JComboBox<>(new String[]{"built-in", "passedIn", "boolean", "short", "integer", "long", "double", "string", "short-array", "integer-array", "long-array", "double-array", "string-array", "k1=v1;k2=v2;...", "<start>-<end> (integer)", "<start>-<end> (long)", "<start>-<end> (double)", "<index>:<start>-<end> (integer)", "<start>-<end> (string)", "<index>:<start>-<end> (string)"})));

        JComboBox<String> validateWithCombobox = new JComboBox<>(new String[]{".", "built-in", "NotDirectory", "EnsureFileExists", "NotDirectory EnsureFileExists", "RangeOf($start,$end)"});
        validateWithCombobox.setEditable(true);
        commandModel.addCellEditor(commandTable, "validateWith", new DefaultCellEditor(validateWithCombobox));
        commandModel.addCellEditor(commandTable, "arity", new DefaultCellEditor(new JComboBox<>(new Object[]{0, 1, "≥1", 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})));

        ruleScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 2 && ruleModel.getRowCount() >= 1 && !ruleModel.getValueAt(ruleModel.getRowCount() - 1, 0).equals(".")) ||
                        ruleModel.getRowCount() == 0) {
                    // 双击创建新行
                    ruleModel.addRow(new Object[]{".", ".", CommandRuleType.AT_MOST_ONE});

                    int selectRowIndex = ruleModel.getRowCount() - 1;
                    ruleTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
                }
            }
        });

        ruleModel.addCellEditor(ruleTable, "ruleType", new DefaultCellEditor(new JComboBox<>(new CommandRuleType[]{CommandRuleType.AT_MOST_ONE, CommandRuleType.AT_LEAST_ONE, CommandRuleType.REQUEST_ONE, CommandRuleType.INTERDEPEND})));

        deleteButton.addActionListener(e -> {
            // 删除后光标失焦
            if (tabbedPane.getSelectedIndex() == 0) {
                int selectedRow = commandTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow <= commandTable.getRowCount() - 1) {
                    commandModel.deleteRow(selectedRow);
                }
            } else if (tabbedPane.getSelectedIndex() == 1) {
                int selectedRow = ruleTable.getSelectedRow();

                if (selectedRow >= 0 && selectedRow <= ruleTable.getRowCount() - 1) {
                    ruleModel.deleteRow(selectedRow);
                }
            }
        });

        addButton.addActionListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                commandModel.addRow(new Object[]{".", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.FALSE, Boolean.FALSE});
                int selectRowIndex = commandModel.getRowCount() - 1;
                commandTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
            } else if (tabbedPane.getSelectedIndex() == 1) {
                ruleModel.addRow(new Object[]{".", ".", CommandRuleType.AT_MOST_ONE});
                int selectRowIndex = ruleModel.getRowCount() - 1;
                ruleTable.setRowSelectionInterval(selectRowIndex, selectRowIndex);
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
            JFileChooser jfc = new JFileChooser();
            jfc.addChoosableFileFilter(new FileNameExtensionFilter("Command Parser Format", "cp"));
            jfc.setCurrentDirectory(new File(System.getProperty("user.dir")).getParentFile());

            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    CommandParser parser = transToParser();
                    if (parser != null) {
                        parser.toFile(jfc.getSelectedFile().getAbsolutePath());
                        JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
                    }
                } catch (Exception exception) {
                    JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
                }
            }
        });

        openButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.addChoosableFileFilter(new FileNameExtensionFilter("Command Parser Format", "cp"));
            jfc.setCurrentDirectory(new File(System.getProperty("user.dir")).getParentFile());

            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                // 加载解析器
                loadFromFile(jfc.getSelectedFile().getAbsolutePath());
            }
        });

        clearButton.addActionListener(e -> {
            // 将解析器映射为 GUI 组件
            commandModel.clearAll();
            ruleModel.clearAll();

            // 设置主程序名、偏移量、全局规则
            mainClassTextField.setText("<main class>");
            offsetSpinner.setValue(0);
            globalRuleComboBox.setSelectedItem(".");
            commandPreview.setText("");
        });
    }

    void loadFromFile(String fileName) {
        // 加载解析器
        CommandParser parser;
        try {
            parser = CommandParser.loadFromFile(fileName);
        } catch (Exception exception) {
            JOptionPane.showOptionDialog(this, exception.getMessage(), "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"}, "OK");
            return;
        }

        // 将解析器映射为 GUI 组件
        commandModel.clearAll();
        ruleModel.clearAll();

        // 设置主程序名、偏移量、全局规则
        mainClassTextField.setText(parser.usage.programName);
        offsetSpinner.setValue(parser.offset);
        globalRuleComboBox.setSelectedItem(parser.globalRules == null ? "." : String.valueOf(parser.globalRules));

        for (String commandName : parser.mainRegisteredCommandItems) {
            CommandItem item = parser.getCommandItem(commandName);
            commandModel.addRow(item.toObject());
        }

        for (CommandRule rule : parser.registeredRules.values()) {
            ruleModel.addRow(new Object[]{rule.command1, rule.command2, rule.type});
        }

        JOptionPane.showOptionDialog(this, "Finish!", "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"OK"}, "OK");
    }

    CommandParser transToParser() {
        CommandParser parser = new CommandParser(false, mainClassTextField.getText().trim());
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
            case "INTERDEPEND":
                parser.registerGlobalRule(CommandRuleType.INTERDEPEND);
                break;
            default:
                // "." 不做任何事情
                break;
        }
        // 添加命令
        for (Object[] row : commandModel.data) {
            if (".".equals(row[0])) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            builder.append(((String) row[0]).replace(" ", ""));
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
            parser.register(CommandItem.loadFromString(builder.toString()));
        }

        // 遍历写入规则
        for (Object[] row : ruleModel.data) {
            if (".".equals(row[0]) || ".".equals(row[1]) || ".".equals(row[2])) {
                continue;
            }

            parser.registerRule((String) row[0], (String) row[1], (CommandRuleType) row[2]);
        }
        return parser;
    }

    private void createUIComponents() {
        commandTable = new JTable(commandModel = new CommandTableModel("commandName", "request", "default", "convertTo", "validateWith", "arity", "group", "description", "format", "hidden", "help"));
        commandModel.addRow(new Object[]{"--help,-help,-h", Boolean.FALSE, ".", "passedIn", ".", 0, "Options", ".", ".", Boolean.TRUE, Boolean.TRUE});
        commandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ruleTable = new JTable(ruleModel = new CommandTableModel("command1", "command2", "ruleType"));
        ruleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        offsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 128, 1));

        // 可无限横向拉长
        commandTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
        } else {
            // BGZIPParser.submit(args);
            BGZIPParserFromFile.submit(args);
        }
    }
}
