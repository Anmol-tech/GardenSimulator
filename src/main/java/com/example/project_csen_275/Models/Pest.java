package com.example.project_csen_275.Models;

public class Pest {
    private int x, y;

    public Pest(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveRandomly(int maxRows, int maxCols) {
        x = (x + (int) (Math.random() * 3) - 1 + maxRows) % maxRows;
        y = (y + (int) (Math.random() * 3) - 1 + maxCols) % maxCols;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

