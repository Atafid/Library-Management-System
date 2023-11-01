package com.example.tp_bibliotheque.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class Page {
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private GridPane grid;
    @FXML private Label pageLabel;
    private int pageCount;
    private Vector<PageObject> objects;

    public Page(Button _prevButton, Button _nextButton, Vector<PageObject> _objects, GridPane _grid, Label _pageLabel) {
        prevButton = _prevButton;
        nextButton = _nextButton;
        grid = _grid;
        pageLabel = _pageLabel;

        pageCount = 0;
        objects = _objects;

        prevButton.setOnAction(event -> {
            onPrevClick();
        });

        nextButton.setOnAction(event -> {
            onNextClick();
        });

        updateFXML();
    }

    @FXML
    public void updateFXML() {
        prevButton.setVisible(pageCount>0);
        nextButton.setVisible((pageCount+1)*grid.getRowCount()<objects.size());
        pageLabel.setVisible(prevButton.isVisible()||nextButton.isVisible());
        pageLabel.setText(String.valueOf(pageCount));

        grid.getChildren().clear();
        for(int i=0; i<grid.getRowCount();i++) {
            if(pageCount*grid.getRowCount()+i>=objects.size()) {
                break;
            }
            objects.get(pageCount * grid.getRowCount() + i).fillGrid(grid, i);
        }
    }

    public void onPrevClick() {
        pageCount--;
        updateFXML();
    }
    public void onNextClick() {
        pageCount++;
        updateFXML();
    }

    public void setObjects(Vector<PageObject> newObjects) {
        objects = newObjects;
    }
}
