package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE*SIDE;
    private int score;
    private boolean isGameStopped;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped){
            restart();
            return;
        }
        openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void createGame() {

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.YELLOW);
                setCellValue(x,y,"");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];
                if (!gameObject.isMine) {
                    for (GameObject neighbor : getNeighbors(gameObject)) {
                        if (neighbor.isMine) {
                            gameObject.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (gameObject.isOpen || gameObject.isFlag || isGameStopped){
            return;
        }
            gameObject.isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        if (gameObject.isMine) {
            setCellValue(gameObject.x, gameObject.y, MINE);
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            return;
        } else if (gameObject.countMineNeighbors == 0) {
            setCellValue(gameObject.x, gameObject.y, "");
            List<GameObject> neighbors = getNeighbors(gameObject);
            for (GameObject neighdor : neighbors) {
                if (!neighdor.isOpen) {
                    openTile(neighdor.x, neighdor.y);
                }

            }
        } else {
            setCellNumber(x, y, gameObject.countMineNeighbors);
        }
        score+=5;
        setScore(score);
        if (countClosedTiles == countMinesOnField) {
            win();
        }
    }

    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        
        if (gameObject.isOpen || countFlags == 0 && !gameObject.isFlag || isGameStopped) {
            return;
            
        } else if (!gameObject.isFlag) {
            countFlags--;
            gameObject.isFlag = true;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.ALICEBLUE);
        } else {
            countFlags++;
            gameObject.isFlag = false;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.YELLOW);
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU LOSE", Color.BLACK, 40);
    }
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE,"YOU WIN, BRO",Color.BLACK,40);
    }
    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        score =0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}