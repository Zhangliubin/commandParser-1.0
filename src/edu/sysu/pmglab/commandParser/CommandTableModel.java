package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.container.Array;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.Arrays;

/**
 * @author suranyi
 * @description 命令表模型
 */

class CommandTableModel extends AbstractTableModel {
    final String[] columnNames;
    Array<Object[]> data = new Array<>();

    /**
     * 不支持添加列元素
     *
     * @param columnNames 列名
     */
    CommandTableModel(String... columnNames) {
        this.columnNames = columnNames;
    }

    public void insertRow(Object[] row, int rowIndex) {
        synchronized (this) {
            int currentRowCount = getRowCount();
            if (row == null) {
                // 插入新行
                Object[] empty = new Object[columnNames.length];
                Arrays.fill(empty, ".");
                data.insert(rowIndex, empty);
            } else {
                data.insert(rowIndex, row);
            }

            fireTableChanged(new TableModelEvent(this, rowIndex, currentRowCount + 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }
    }

    void flush() {
        fireTableChanged(new TableModelEvent(this, 0, 256,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public void addRow(Object[] row) {
        synchronized (this) {
            int currentRowCount = getRowCount();
            if (row == null) {
                // 插入新行
                Object[] empty = new Object[columnNames.length];
                Arrays.fill(empty, ".");
                data.add(empty);
            } else {
                data.add(row);
            }

            fireTableChanged(new TableModelEvent(this, currentRowCount, currentRowCount + 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }
    }

    public void deleteRow(int rowIndex) {
        synchronized (this) {
            if (getRowCount() == 0) {
                return;
            }

            if (rowIndex == getRowCount() - 1) {
                data.popLast();
                fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex,
                        TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
            } else {
                int currentRowCount = getRowCount();
                // 将剩余的部分弹出并重新添加
                Array<Object[]> pops = new Array<>(getRowCount() - rowIndex - 1);
                for (int i = 0; i < pops.getCapacity(); i++) {
                    pops.add(data.popLast());
                }
                data.popLast();

                fireTableChanged(new TableModelEvent(this, 0, currentRowCount,
                        TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));

                for (int i = pops.getCapacity() - 1; i >= 0; i--) {
                    addRow(pops.get(i));
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (data.size() == rowIndex) {
            data.add(new Object[columnNames.length]);
        }

        data.get(rowIndex)[columnIndex] = value;

        fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public void addCellEditor(JTable parent, int columnIndex, TableCellEditor editor) {
        parent.getColumnModel().getColumn(columnIndex).setCellEditor(editor);
    }


    public void addCellEditor(JTable parent, String columnName, TableCellEditor editor) {
        parent.getColumnModel().getColumn(getColumnIndex(columnName)).setCellEditor(editor);
    }

    public void upRow(int selectedRow) {
        synchronized (this) {
            if (selectedRow == 0) {
                return;
            }

            Object[] row1 = data.get(selectedRow - 1);
            Object[] row2 = data.get(selectedRow);
            data.set(selectedRow, row1);
            data.set(selectedRow - 1, row2);

            fireTableChanged(new TableModelEvent(this, selectedRow - 1, selectedRow,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }
    }

    public void downRow(int selectedRow) {
        synchronized (this) {
            if (selectedRow == getRowCount() - 1) {
                return;
            }

            Object[] row1 = data.get(selectedRow + 1);
            Object[] row2 = data.get(selectedRow);
            data.set(selectedRow, row1);
            data.set(selectedRow + 1, row2);

            fireTableChanged(new TableModelEvent(this, selectedRow, selectedRow + 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }
    }

    public void clearAll() {
        int currentRowNum = getRowCount();
        data.clear();
        fireTableChanged(new TableModelEvent(this, 0, currentRowNum, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }
}
