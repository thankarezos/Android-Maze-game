package com.example.mymaze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.compose.animation.core.Motion;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class MazeActivity2 extends View{
    private enum Direction{UP, DOWN, RIGHT, LEFT};
    private Walls[][] walls;
    private Walls player, exit;
    private static final int COLS = 7, ROWS = 10;
    private float cellSize, hMargin, vMargin;
    private static final float WALL_THICC = 4;
    private Paint wallPaint, playerPaint, exitPaint;
    private Random random;

    public MazeActivity2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICC);

        playerPaint = new Paint();
        playerPaint.setColor(Color.GRAY);

        exitPaint = new Paint();
        exitPaint.setColor(Color.BLUE);

        random = new Random();

        createMaze();
    }

    private void createMaze(){
        Stack<Walls> stack = new Stack<>();
        
        Walls current, next;
        walls = new Walls[COLS][ROWS];

        for (int x=0; x<COLS; x++){
            for (int y=0; x<ROWS; x++){
                walls[x][y] = new Walls(x,y);
            }
        }

        player = walls[0][0];
        exit = walls[COLS-1][ROWS-1];


        current = walls[0][0];
        current.visited = true;
        do {
            next = getNeighbour(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else {
                current = stack.pop();
            }
        }while(!stack.empty());
    }

    private void removeWall(Walls current, Walls next) {
        if (current.col == next.col && current.row == next.row+1){
            current.topWall = false;
            next.bottomWall = false;
        }

        if (current.col == next.col && current.row == next.row-1){
            current.topWall = false;
            next.bottomWall = false;
        }

        if (current.col == next.col+1 && current.row == next.row){
            current.leftWall = false;
            next.rightWall = false;
        }

        if (current.col == next.col-1 && current.row == next.row){
            current.rightWall = false;
            next.leftWall = false;
        }

    }

    private Walls getNeighbour(Walls wall) {
        ArrayList<Walls> neighbours = new ArrayList<>();

        if (wall.col > 0){ //left rei
            if (!walls[wall.col -1][wall.row].visited) {
                neighbours.add(walls[wall.col -1][wall.row]);
            }
        }

        if (wall.col < COLS-1){ //right nei
            if (!walls[wall.col +1][wall.row].visited) {
                neighbours.add(walls[wall.col +1][wall.row]);
            }
        }

        if (wall.row > 0){ //top nei
            if (!walls[wall.col][wall.row -1].visited) {
                neighbours.add(walls[wall.col][wall.row -1]);
            }
        }

        if (wall.row < ROWS-1){ //bottom nei
            if (!walls[wall.col][wall.row +1].visited) {
                neighbours.add(walls[wall.col][wall.row +1]);
            }
        }

        if (neighbours.size() > 0) {
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }
        return null;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        int width = getWidth();
        int height = getHeight();
        if (width/height < COLS/ROWS) {
            cellSize = width / (COLS + 1);
        }else{
            cellSize = height/(ROWS+1);
        }

        hMargin = (width-COLS*cellSize)/2;
        vMargin = (height-ROWS*cellSize)/2;

        canvas.translate(hMargin, vMargin);
        for (int x=0; x<COLS; x++){
            for (int y=0; x<ROWS; x++){

                if (walls[x][y].topWall){
                    canvas.drawLine(x*cellSize, y*cellSize, (x+1)*cellSize, y*cellSize, wallPaint);
                }

                if (walls[x][y].leftWall){
                    canvas.drawLine(x*cellSize, y*cellSize, x*cellSize, (y+1)*cellSize, wallPaint);
                }

                if (walls[x][y].bottomWall){
                    canvas.drawLine(x*cellSize, (y+1)*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                }

                if (walls[x][y].rightWall){
                    canvas.drawLine((x+1)*cellSize, y*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                }
            }
        }

        float margin = cellSize/10;
        canvas.drawRect(player.col*cellSize+margin, player.row*cellSize+margin, (player.col+1)*cellSize-margin, (player.row+1)*cellSize-margin, playerPaint);
        canvas.drawRect(exit.col*cellSize+margin, exit.row*cellSize+margin, (exit.col+1)*cellSize-margin, (exit.row+1)*cellSize-margin, exitPaint);

    }

    private void checkExit(){
        if (player == exit){createMaze();}
    }

    private void moveplayer(Direction direction){
        switch (direction){
            case UP:
                if (!player.topWall){player = walls[player.col][player.row-1];}
                break;
            case DOWN:
                if (!player.bottomWall){player = walls[player.col][player.row+1];}
                break;
            case LEFT:
                if (!player.leftWall){player = walls[player.col-1][player.row];}
                break;
            case RIGHT:
                if (!player.rightWall){player = walls[player.col+1][player.row];}
                break;
        }
        checkExit();
        invalidate();
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){return true;}

        if (event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();
            float playerCenterX = hMargin + (player.col+0.5f)*cellSize;
            float playerCenterY = vMargin + (player.row+0.5f)*cellSize;

            float dx = x-playerCenterX;
            float dy = y-playerCenterY;
            float absDx = Math.abs(dx);
            float absDy = Math.abs(dy);

            if (absDx > cellSize || absDy > cellSize){
                if (absDx > absDy){
                    if (dx > 0) {moveplayer(Direction.RIGHT);}else{moveplayer(Direction.LEFT);}
                }else {
                    if (dy > 0) {moveplayer(Direction.DOWN);}else{moveplayer(Direction.UP);}
                }
            }
        }
        return true;
        //return super.onFilterTouchEventForSecurity(event);
    }

    private class Walls{
        boolean leftWall = true;
        boolean rightWall = true;
        boolean topWall = true;
        boolean bottomWall = true;
        boolean visited = false;
        int col, row;

        public Walls(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
}