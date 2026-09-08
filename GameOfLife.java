package com.example.gameoflife;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

/**
 * RULES
 * cell lives if it has from 2 to 3 neighbors
 * other than this, the cell dies
 * cell is born if a dead cell has exactly 3 neighbors
 */

public class GameOfLife extends Application {
    int cellSize = 20;
    int speed = 1;


    public class Cell{
        public int x;
        public int y;
        public boolean alive = false;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell)) return false;
            Cell other = (Cell) o;
            return this.x == other.x && this.y == other.y && this.alive == other.alive;
        }


    }

    Set<Cell> cellSet = new HashSet<>(20);

    public void tick(GraphicsContext gc){
        //background
        gc.setFill(Color.BLUE);
        gc.fillRect(0, 0, 400, 400);

        // cell color

        for(Cell cell: cellSet){
            if(cell.alive){
                Color cc = Color.BLACK;
                gc.setFill(cc);
                gc.fillRect(cell.x*cellSize, cell.y*cellSize, cellSize, cellSize);
            }
        }

    }

    public void update(){
        // copy the set
        Set<Cell> newCells = new HashSet<>();

        //orthogonal and cartesian
        int[][] dir = {{0, 1}, {0, -1}, {1,  0}, {-1, 0},
                {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};


        // issue here
        for(Cell cell: cellSet){ // for each cell
            int neighborCount = 0; // every cell start with 0 neighbors
            if(cell.alive){ // case alive
                for(int[] nei : dir){ // for eah nei
                    Cell neighbour = new Cell();
                    neighbour.x = cell.x + nei[0];
                    neighbour.y = cell.y + nei[1];
                    neighbour.alive = true;// define nei

                    for(Cell cell2: cellSet){
                        if(cell2.equals(neighbour)){ // ISSUE
                            System.out.println(neighbour.x + " " + neighbour.y);

                            neighborCount+=1; // add to the counter
                        }
                    }
                }
                System.out.println(neighborCount);

                if(neighborCount == 2 || neighborCount == 3){
                    newCells.add(cell);
                }else{
                    newCells.add(cell);
                    cell.alive = false;
                }
            }else { // case dead
                for(int[] nei : dir){
                    Cell neighbour = new Cell();
                    neighbour.x = cell.x + nei[0];
                    neighbour.y = cell.y + nei[1];
                    neighbour.alive = true; // define nei

                    for(Cell cell2: cellSet){
                        if(cell2.equals(neighbour)){ // ISSUE
                            System.out.println(neighbour.x + " " + neighbour.y);

                            neighborCount+=1; // add to the counter
                        }
                    }
                }
                if(neighborCount == 3){ // if exactly 3
                    newCells.add(cell);
                    cell.alive = true; // cell lives
                }
            }

        }
        // update
        cellSet = newCells;
    }


    public void draw(Canvas canvas){
        canvas.setOnMouseClicked(event -> {
            System.out.println("Detected");

            int x = (int) event.getX();
            int y = (int) event.getY();

            Cell cell = new Cell();
            cell.x = x;
            cell.y = y;

            System.out.println(x + " " + y);

            for(Cell cell2: cellSet){
                if(cell2.equals(cell)){
                    cell.alive = true;
                }
            }



        });
    }

    @Override
    public void start(Stage stage){

        try {
            //draw
            Canvas canvas = new Canvas(400, 400);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Button play = new Button("Play");

            Button pause = new Button("Pause");



            VBox root = new VBox();
            HBox hb = new HBox();

            hb.setAlignment(Pos.BOTTOM_CENTER);

            hb.getChildren().addAll(play, pause);
            root.getChildren().addAll(canvas,hb);



            // initialise the set of cells
            for(int i=0; i<20; i++){
                for(int j=0; j<20; j++){
                    Cell cell = new Cell();

                    cell.x = i;
                    cell.y = j;
                    cell.alive = false;

                    cellSet.add(cell);

                }
            }

            // random alive cells
            int count = 0;
            for(Cell cell: cellSet){
                if(count > 100){
                    break;
                }
                cell.alive = true;
                count++;
            }

            AnimationTimer timer = new AnimationTimer() {
                long lastTick = 0;

                @Override
                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        update();
                        draw(canvas);
                        return;
                    }
                    if (now - lastTick > 1000000000/speed) {
                        lastTick = now;
                        tick(gc);
                        update();
                        draw(canvas);
                    }

                }
            };

            play.setOnAction(e -> {
                timer.start();
            });

            pause.setOnAction(e -> {
                timer.stop();
            });


            Scene scene = new Scene(root, 400, 450);
            stage.setTitle("Game of Life");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

