package com.github.Gappylul.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EasyTable<T> {

    private final TableView<T> tableView = new TableView<>();

    public static <T> EasyTable<T> create() {
        return new EasyTable<>();
    }

    public <V> EasyTable<T> col(String header, Function<T, V> valueExtractor) {
        TableColumn<T, V> col = new TableColumn<>(header);
        col.setCellValueFactory(cell -> new SimpleObjectProperty<>(valueExtractor.apply(cell.getValue())));
        tableView.getColumns().add(col);
        return this;
    }

    public <V> EasyTable<T> colObs(String header, Function<T, ObservableValue<V>> propertyExtractor) {
        TableColumn<T, V> col = new TableColumn<>(header);
        col.setCellValueFactory(cell -> propertyExtractor.apply(cell.getValue()));
        tableView.getColumns().add(col);
        return this;
    }

    public <V> EasyTable<T> colStyled(String header, Function<T, V> valueExtractor, Function<V, String> cssStyleSupplier) {
        TableColumn<T, V> col = new TableColumn<>(header);
        col.setCellValueFactory(cell -> new SimpleObjectProperty<>(valueExtractor.apply(cell.getValue())));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(V item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle(cssStyleSupplier.apply(item));
                }
            }
        });
        tableView.getColumns().add(col);
        return this;
    }

    public EasyTable<T> onRowClick(BiConsumer<T, MouseEvent> listener) {
        tableView.setRowFactory(tv -> {
            var row = new javafx.scene.control.TableRow<T>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && listener != null) {
                    listener.accept(row.getItem(), event);
                }
            });
            return row;
        });
        return this;
    }

    public EasyTable<T> items(ObservableList<T> items) {
        tableView.setItems(items);
        return this;
    }

    public TableView<T> build() {
        return tableView;
    }

}
