package com.github.Gappylul.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EasyGridTable<T> {

    private final TableView<ObservableList<T>> tableView;
    private Function<T, String> textMapper = item -> "";
    private Function<T, String> styleMapper = item -> "";
    private BiConsumer<T, MouseEvent> clickListener;
    private boolean showRowHeader = true;

    public EasyGridTable() {
        this.tableView = new TableView<>();
        disableSelection();
    }

    public static <T> EasyGridTable<T> create() {
        return new EasyGridTable<>();
    }

    public EasyGridTable<T> disableSelection() {
        this.tableView.setSelectionModel(null);
        return this;
    }

    public EasyGridTable<T> text(Function<T, String> textMapper) {
        this.textMapper = textMapper;
        return this;
    }

    public EasyGridTable<T> style(Function<T, String> styleMapper) {
        this.styleMapper = styleMapper;
        return this;
    }

    public EasyGridTable<T> onCellClick(BiConsumer<T, MouseEvent> clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public EasyGridTable<T> showRowHeader(boolean show) {
        this.showRowHeader = show;
        return this;
    }

    /**
     * Builds and populates the TableView for an N x M grid.
     */
    public TableView<ObservableList<T>> build(int rows, int cols, BiFunction<Integer, Integer, T> cellSupplier) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        if (showRowHeader) {
            TableColumn<ObservableList<T>, String> rowHeaderCol = new TableColumn<>("Y\\X");
            rowHeaderCol.setSortable(false);
            rowHeaderCol.setPrefWidth(45);
            rowHeaderCol.setCellValueFactory(param ->
                new SimpleObjectProperty<>(String.valueOf(tableView.getItems().indexOf(param.getValue())))
            );
            rowHeaderCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            tableView.getColumns().add(rowHeaderCol);
        }

        for (int colIndex = 0; colIndex < cols; colIndex++) {
            final int c = colIndex;
            TableColumn<ObservableList<T>, T> column = new TableColumn<>(String.valueOf(c));
            column.setSortable(false);
            column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().get(c)));

            column.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        setOnMouseClicked(null);
                    } else {
                        setText(textMapper.apply(item));
                        setStyle(styleMapper.apply(item));
                        if (clickListener != null) {
                            setOnMouseClicked(e -> clickListener.accept(item, e));
                        }
                    }
                }
            });

            tableView.getColumns().add(column);
        }

        ObservableList<ObservableList<T>> gridData = FXCollections.observableArrayList();
        for (int r = 0; r < rows; r++) {
            ObservableList<T> row = FXCollections.observableArrayList();
            for (int c = 0; c < cols; c++) {
                row.add(cellSupplier.apply(c, r));
            }
            gridData.add(row);
        }

        tableView.setItems(gridData);
        return tableView;
    }

    public TableView<ObservableList<T>> getTableView() {
        return tableView;
    }

    public void refresh() {
        tableView.refresh();
    }

}
