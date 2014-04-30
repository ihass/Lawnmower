/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mowitnow.lawnmower.controller;

import com.mowitnow.lawnmower.model.Lawn;
import com.mowitnow.lawnmower.model.Mower;
import com.mowitnow.lawnmower.service.InputService;
import com.mowitnow.lawnmower.service.LogService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

/**
 * FXML Controller class
 *
 * @author ihassiko-a
 */
public class LawnmowerController implements Initializable {

    private static final String LAWN_STYLE = "-fx-background-color:#7CFC00;";
    private static final String CELL_STYLE = "-fx-border-color:yellow;";
    private static final String MOWN_CELL_STYLE = "-fx-background-color:#ADFF2F;";
    private static final String ERR_STYLE = "-fx-text-fill:red;";
    private static final int SPEED = 1000;

    @FXML //  fx:id="inputTA"
    private TextArea inputTA; // Value injected by FXMLLoader

    @FXML //  fx:id="mowB"
    private Button mowB; // Value injected by FXMLLoader

    @FXML //  fx:id="anchPane"
    private AnchorPane lawnAP; // Value injected by FXMLLoader

    @FXML //  fx:id="label"
    private Label msgL; // Value injected by FXMLLoader

    private GridPane lawnGP;
    private Lawn lawn;
    static LogService logger = LogService.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert inputTA != null : "fx:id=\"inputTA\" was not injected: check your FXML file 'Lawnmower.fxml'.";
        assert mowB != null : "fx:id=\"mowB\" was not injected: check your FXML file 'Lawnmower.fxml'.";
        assert lawnAP != null : "fx:id=\"lawnAP\" was not injected: check your FXML file 'Lawnmower.fxml'.";

        showLawn(this.inputTA.getText());

        this.inputTA.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                showLawn((String) newVal);
            }
        });

        logger.msg().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, final Object oldVal,
                    final Object newVal) {
                msgL.setText((String) newVal);
                if (logger.getCode() == LogService.ERR) {
                    msgL.setStyle(ERR_STYLE);
                    mowB.setDisable(true);
                } else {
                    mowB.setDisable(false);
                }
            }
        });

    }

    private void showLawn(final String input) {
        final InputService inServ = InputService.getInstance(input);
        this.lawn = inServ.createLawn(this.inputTA.getText());
        final List<Mower> mowerList = inServ.createMowerList(this.inputTA.getText());
        this.lawn.setMowerList(mowerList);
        this.lawnGP = createNewLawnGP();
    }

    private GridPane createNewLawnGP() {
        final int lawnWidth = this.lawn.getWidth();
        final int lawnHeight = this.lawn.getHeight();

        // Start : Clear lawnAP for a new lawnGP.
        final GridPane newLawnGP = new GridPane();
        newLawnGP.setStyle(LAWN_STYLE);
        newLawnGP.setGridLinesVisible(true);
        AnchorPane.setTopAnchor(newLawnGP, 0.0);
        AnchorPane.setRightAnchor(newLawnGP, 0.0);
        AnchorPane.setLeftAnchor(newLawnGP, 0.0);
        AnchorPane.setBottomAnchor(newLawnGP, 0.0);
        this.lawnAP.getChildren().clear();
        this.lawnAP.getChildren().add(newLawnGP);
        // End : Clear lawnAP for a new lawnGP.

        // Start : Draw cells.
        for (int x = 0; x < lawnWidth; x++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.00 / (double) lawnWidth);
            col.setHalignment(HPos.CENTER);
            newLawnGP.getColumnConstraints().add(col);
        }

        for (int y = 0; y < lawnHeight; y++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.00 / (double) lawnHeight);
            row.setValignment(VPos.CENTER);
            newLawnGP.getRowConstraints().add(row);
        }
        // End : Draw cells.

        // Start : Fill cells.
        for (int x = 0; x < lawnWidth; x++) {
            for (int y = 0; y < lawnHeight; y++) {
                final StackPane cellSP = new StackPane();
                cellSP.setStyle(CELL_STYLE);

                Label lbl = new Label("(" + (x + 1) + "," + (lawnHeight - y) + ")");
                StackPane.setAlignment(lbl, Pos.TOP_RIGHT);
                cellSP.getChildren().add(lbl);

                newLawnGP.add(cellSP, x, y);

            }
        }

        List<Mower> mowerList = lawn.getMowerList();
        for (Mower mower : mowerList) {
            final int mowerX = mower.getX();
            final int mowerY = mower.getY();

            final double cellWidth = newLawnGP.getColumnConstraints().get(0).getPercentWidth();
            final double cellHeight = newLawnGP.getRowConstraints().get(0).getPercentHeight();
            Polygon mowerP = new Polygon();

            mowerP.getPoints().add(cellWidth * 1.5);
            mowerP.getPoints().add(0.0);
            mowerP.getPoints().add(cellWidth * 3);
            mowerP.getPoints().add(cellHeight * 3);
            mowerP.getPoints().add(0.0);
            mowerP.getPoints().add(cellHeight * 3);
            mowerP.setRotate((double) mower.getDirection().getAngle());

            ObservableList<Node> nLaGPChildren = newLawnGP.getChildren();
            for (Node node : nLaGPChildren) {
                if (node instanceof StackPane) {
                    if (GridPane.getColumnIndex(node) == mowerX - 1 && GridPane.getRowIndex(node) == lawnHeight - mowerY) {
                        ((StackPane) node).getChildren().add(mowerP);
                        StackPane.setAlignment(mowerP, Pos.CENTER);
                        break;
                    }
                }
            }

        }
        // End : Fill cells.

        return newLawnGP;
    }

    private synchronized void updateDirection(final int mowerX, final int mowerY, int newDir) {
        final int lawnHeight = this.lawn.getHeight();

        ObservableList<Node> laGPchildren = this.lawnGP.getChildren();
        for (Node node : laGPchildren) {
            if (node instanceof StackPane) {
                if (GridPane.getColumnIndex(node) == mowerX - 1 && GridPane.getRowIndex(node) == lawnHeight - mowerY) {
                    StackPane cell = ((StackPane) node);
                    ObservableList<Node> cellChildren = cell.getChildren();
                    for (Node cellChild : cellChildren) {
                        if (cellChild instanceof Polygon) {
                            ((Polygon) cellChild).setRotate((double) newDir);
                            cell.setStyle(MOWN_CELL_STYLE);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private synchronized void updateY(final int mowerX, final int oldY, final int newY) {
        final int mowerY = oldY;
        final int lawnHeight = this.lawn.getHeight();
        Polygon mowerP = null;

        ObservableList<Node> laGPchildren = this.lawnGP.getChildren();
        for (Node node : laGPchildren) {
            if (node instanceof StackPane) {
                if (GridPane.getColumnIndex(node) == mowerX - 1 && GridPane.getRowIndex(node) == lawnHeight - mowerY) {
                    StackPane cell = ((StackPane) node);
                    ObservableList<Node> cellChildren = cell.getChildren();
                    for (Node cellChild : cellChildren) {
                        if (cellChild instanceof Polygon) {
                            mowerP = (Polygon) cellChild;
                            cellChildren.remove(cellChild);
                            cell.setStyle(MOWN_CELL_STYLE);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        for (Node node : laGPchildren) {
            if (node instanceof StackPane) {
                if (GridPane.getColumnIndex(node) == mowerX - 1 && GridPane.getRowIndex(node) == lawnHeight - newY) {
                    StackPane cell = ((StackPane) node);
                    ObservableList<Node> cellChildren = cell.getChildren();
                    cellChildren.add(mowerP);
                    cell.setStyle(MOWN_CELL_STYLE);
                    break;
                }
            }
        }
    }

    private synchronized void updateX(final int mowerY, int oldX, int newX) {
        final int mowerX = oldX;
        final int lawnHeight = this.lawn.getHeight();
        Polygon mowerP = null;

        ObservableList<Node> laGPchildren = this.lawnGP.getChildren();
        for (Node node : laGPchildren) {
            if (node instanceof StackPane) {
                if (GridPane.getColumnIndex(node) == mowerX - 1 && GridPane.getRowIndex(node) == lawnHeight - mowerY) {
                    StackPane cell = ((StackPane) node);
                    ObservableList<Node> cellChildren = cell.getChildren();
                    for (Node cellChild : cellChildren) {
                        if (cellChild instanceof Polygon) {
                            mowerP = (Polygon) cellChild;
                            cellChildren.remove(cellChild);
                            cell.setStyle(MOWN_CELL_STYLE);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        for (Node node : laGPchildren) {
            if (node instanceof StackPane) {
                if (GridPane.getColumnIndex(node) == newX - 1 && GridPane.getRowIndex(node) == lawnHeight - mowerY) {
                    StackPane cell = ((StackPane) node);
                    ObservableList<Node> cellChildren = cell.getChildren();
                    cellChildren.add(mowerP);
                    cell.setStyle(MOWN_CELL_STYLE);
                    break;
                }
            }
        }
    }

    private void enableMowB() {
        Task enableMowBtask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mowB.setDisable(false);
                    }
                });
                return null;
            }
        };

        Thread enableMowBthread = new Thread(enableMowBtask);
        enableMowBthread.setDaemon(true);
        enableMowBthread.start();
        try {
            enableMowBthread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Called when the "Mow !" button is fired.
     *
     * @param event the action event.
     */
    @FXML
    public void mowFired(ActionEvent event) {
        this.mowB.setDisable(true);

        showLawn(this.inputTA.getText());

        List<Mower> mowerList = this.lawn.getMowerList();
        for (final Mower mower : mowerList) {
            mower.observableDirection().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue o, Object oldVal,
                        final Object newVal) {
                    final int mowerX = mower.getX();
                    final int mowerY = mower.getY();
                    Task updateDirectionTask = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateDirection(mowerX, mowerY, (int) newVal);
                                    try {
                                        Thread.sleep(SPEED);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                            return null;
                        }
                    };
                    Thread updateDirectionThread = new Thread(updateDirectionTask);
                    updateDirectionThread.setDaemon(true);
                    updateDirectionThread.start();
                    try {
                        updateDirectionThread.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            mower.y().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue o, final Object oldVal,
                        final Object newVal) {
                    final int mowerX = mower.getX();
                    Task updateYtask = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateY(mowerX, (int) oldVal, (int) newVal);
                                    try {
                                        Thread.sleep(SPEED);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                            return null;
                        }
                    };
                    Thread updateYthread = new Thread(updateYtask);
                    updateYthread.setDaemon(true);
                    updateYthread.start();
                    try {
                        updateYthread.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            mower.x().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue o, final Object oldVal,
                        final Object newVal) {
                    final int mowerY = mower.getY();
                    Task updateXtask = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateX(mowerY, (int) oldVal, (int) newVal);
                                    try {
                                        Thread.sleep(SPEED);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                            return null;
                        }
                    };
                    Thread updateXthread = new Thread(updateXtask);
                    updateXthread.setDaemon(true);
                    updateXthread.start();
                    try {
                        updateXthread.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LawnmowerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            mower.mow(this.lawn);
        }
        enableMowB();
    }
}
