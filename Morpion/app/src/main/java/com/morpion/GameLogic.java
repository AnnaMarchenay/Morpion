package com.morpion;

/**
 * Created by annam on 26/03/2017.
 */

class GameLogic {

    private char[][] board;
    private char marker;
    private char compMarker;
    private int[] AI = null;

    GameLogic() {
        board = new char[3][3];
        initBoard();
    }

    void setCompMarker(char compMarker){

        this.compMarker = compMarker;
    }

    private void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '*';
            }
        }
    }

    boolean checkFull() {
        boolean isFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '*') {
                    isFull = false;
                }
            }
        }
        return isFull;
    }

    boolean addMarker(char marker, int row, int column) {

        if (board[row][column] == '*') {
            board[row][column] = marker;
            return true;
        }
        return false;
    }

    boolean hasWon() {

        return (checkRows() || checkCols() || checkDiagonals());
    }

    char getWinningMarker(){

        return marker;
    }

    int[] computerAIEasy(){
        if(winRowsAI() || winColsAI()){
            return AI;
        }else if(blockRowsAI() || blockColsAI()){
            return AI;
        }
        else{
            for(int i = 0; i < 3; i ++){
                for(int j = 0; j < 3; j++){
                    if(board[i][j] == '*'){
                        AI = new int[]{i,j};
                    }
                }
            }
            return AI;
        }
    }

    int[] computerAIMedium(){

        if(winRowsAI() || winColsAI()){
            return AI;
        }else if(blockRowsAI() || blockColsAI() || blockDiagonalsAI()){
            return AI;
        }
        else{
            for(int i = 0; i < 3; i ++){
                for(int j = 0; j < 3; j++){
                    if(board[i][j] == '*'){
                        AI = new int[]{i,j};
                    }
                }
            }
            return AI;
        }
    }

    int[] computerAIHard(){
        if(board[1][1] == '*'){
            return new int[]{1,1};
        }
        else if(winRowsAI() || winColsAI() || winDiagonalsAI()){
            return AI;
        }else if(blockRowsAI() || blockColsAI() || blockDiagonalsAI()){
            return AI;
        }
        else{
            for(int i = 0; i < 3; i ++){
                for(int j = 0; j < 3; j++){
                    if(board[i][j] == '*'){
                        AI = new int[]{i,j};
                    }
                }
            }
            return AI;
        }
    }

    private boolean blockRowsAI(){

        for(int i = 0; i < 3; i++) {
            if(checkAI(board[i][0], board[i][1]) && (board[i][2] == '*') && (board[i][0] != compMarker)){
                AI = new int[]{i,2};
                return true;
            }else if(checkAI(board[i][1], board[i][2]) && (board[i][0] == '*') && (board[i][1] != compMarker)){
                AI = new int[]{i,0};
                return true;
            }else if(checkAI(board[i][0], board[i][2]) && (board[i][1] == '*') && (board[i][0] != compMarker)){
                AI = new int[]{i,1};
                return true;
            }
        }
        return false;
    }

    private boolean blockColsAI(){

        for(int i = 0; i < 3; i++) {
            if(checkAI(board[0][i], board[1][i]) && (board[2][i] == '*') && (board[0][i] != compMarker)){
                AI = new int[]{2,i};
                return true;
            }else if(checkAI(board[1][i], board[2][i]) && (board[0][i] == '*') && (board[1][i] != compMarker)){
                AI = new int[]{0,i};
                return true;
            }else if(checkAI(board[0][i], board[2][i]) && (board[1][i] == '*') && (board[0][i] != compMarker)){
                AI = new int[]{1,i};
                return true;
            }
        }
        return false;
    }
    private boolean blockDiagonalsAI() {
        if(checkAI(board[0][0],board[1][1]) && (board[2][2] == '*') && (board[0][0] != compMarker)){
            AI = new int[]{2,2};
            return true;
        }else if(checkAI(board[0][2], board[1][1]) && (board[2][0] == '*') && (board[0][2] != compMarker)){
            AI = new int[]{2,0};
            return true;
        }else if(checkAI(board[2][0],board[1][1]) && (board[0][2] == '*') && (board[2][0] != compMarker)){
            AI = new int[]{0,2};
            return true;
        }else if(checkAI(board[2][2],board[1][1]) && (board[0][0] == '*') && (board[2][2] != compMarker)){
            AI = new int[]{0,0};
            return true;
        }
        return false;
    }

    private boolean winRowsAI(){

        for(int i = 0; i < 3; i++) {
            if(checkAI(board[i][0], board[i][1]) && (board[i][2] == '*') && (board[i][0] == compMarker)){
                AI = new int[]{i,2};
                return true;
            }else if(checkAI(board[i][1], board[i][2]) && (board[i][0] == '*') && (board[i][1] == compMarker)){
                AI = new int[]{i,0};
                return true;
            }else if(checkAI(board[i][0], board[i][2]) && (board[i][1] == '*') && (board[i][0] == compMarker)){
                AI = new int[]{i,1};
                return true;
            }
        }
        return false;
    }

    private boolean winColsAI(){

        for(int i = 0; i < 3; i++) {
            if(checkAI(board[0][i], board[1][i]) && (board[2][i] == '*') && (board[0][i] == compMarker)){
                AI = new int[]{2,i};
                return true;
            }else if(checkAI(board[1][i], board[2][i]) && (board[0][i] == '*') && (board[1][i] == compMarker)){
                AI = new int[]{0,i};
                return true;
            }else if(checkAI(board[0][i], board[2][i]) && (board[1][i] == '*') && (board[0][i] == compMarker)){
                AI = new int[]{1,i};
                return true;
            }
        }
        return false;
    }

    private boolean winDiagonalsAI() {
        if(checkAI(board[0][0],board[1][1]) && (board[2][2] == '*') && (board[0][0] == compMarker)){
            AI = new int[]{2,2};
            return true;
        }else if(checkAI(board[0][2],board[1][1]) && (board[2][0] == '*') && (board[0][2] == compMarker)){
            AI = new int[]{2,0};
            return true;
        }else if(checkAI(board[2][0],board[1][1]) && (board[0][2] == '*') && (board[2][0] == compMarker)){
            AI = new int[]{0,2};
            return true;
        }else if(checkAI(board[2][2],board[1][1]) && (board[0][0] == '*') && (board[2][2] == compMarker)){
            AI = new int[]{0,0};
            return true;
        }
        return false;
    }
    private boolean check(char c1, char c2, char c3) {
        if((c1 != '*') && (c1 == c2) && (c2 == c3)){
            marker = c1;
        }
        return ((c1 != '*') && (c1 == c2) && (c2 == c3));
    }

    private boolean checkAI(char c1, char c2){
        return ((c1 != '*') && (c1 == c2));
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (check(board[i][0], board[i][1], board[i][2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCols() {
        for (int i = 0; i < 3; i++) {
            if (check(board[0][i], board[1][i], board[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return ((check(board[0][0], board[1][1], board[2][2])) || (check(board[0][2], board[1][1], board[2][0])));
    }


}
