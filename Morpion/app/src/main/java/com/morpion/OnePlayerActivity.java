package com.morpion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class OnePlayerActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton one_one;
    private ImageButton one_two;
    private ImageButton one_three;

    private ImageButton two_one;
    private ImageButton two_two;
    private ImageButton two_three;

    private ImageButton three_one;
    private ImageButton three_two;
    private ImageButton three_three;

    private GameLogic gameLogic;

    private char playerMarker;
    private char computerMarker;

    private Player player;
    private Player computer;

    private int playerScore;
    private int computerScore;

    private String difficulty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);

        Bundle extras = getIntent().getExtras();

        gameLogic = new GameLogic();


        one_one = (ImageButton) findViewById(R.id.one_one);
        one_two = (ImageButton) findViewById(R.id.one_two);
        one_three = (ImageButton) findViewById(R.id.one_three);
        two_one = (ImageButton) findViewById(R.id.two_one);
        two_two = (ImageButton) findViewById(R.id.two_two);
        two_three = (ImageButton) findViewById(R.id.two_three);
        three_one = (ImageButton) findViewById(R.id.three_one);
        three_two = (ImageButton) findViewById(R.id.three_two);
        three_three = (ImageButton) findViewById(R.id.three_three);

        TextView playerScoreLabel = (TextView) findViewById(R.id.player_score);
        TextView computerScoreLabel = (TextView) findViewById(R.id.computer_score);


        if (extras != null) {

            player = (Player) extras.getSerializable("Joueur");
            computer = (Player) extras.getSerializable("Ordinateur");
            difficulty = extras.getString("Difficulté");
        }

            playerMarker = player.getPlayerMarker();
            playerScore = player.getScore();
        String playerName = player.getName();

        String computerName = computer.getName();
            computerScore = computer.getScore();
            computerMarker = computer.getPlayerMarker();
            gameLogic.setCompMarker(computerMarker);
        playerScoreLabel.setText(playerName + " : " + playerScore);
        computerScoreLabel.setText(computerName + " : " + computerScore);

        Random rowAI = new Random();
        Random colAI = new Random();

            one_one.setOnClickListener((v) -> onClick(v));
            one_two.setOnClickListener((v) -> onClick(v));
            one_three.setOnClickListener((v) -> onClick(v));
            two_one.setOnClickListener((v) -> onClick(v));
            two_two.setOnClickListener((v) -> onClick(v));
            two_three.setOnClickListener((v) -> onClick(v));
            three_one.setOnClickListener((v) -> onClick(v));
            three_two.setOnClickListener((v) -> onClick(v));
            three_three.setOnClickListener((v) -> onClick(v));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_one:
                gameLogic.addMarker(playerMarker, 0, 0);
                checkGameStatus();
                updateUI(playerMarker, 0, 0);
                computerLogic();
                one_one.setEnabled(false);
                break;
            case R.id.one_two:
                gameLogic.addMarker(playerMarker, 0, 1);
                checkGameStatus();
                updateUI(playerMarker, 0, 1);
                computerLogic();
                one_two.setEnabled(false);
                break;
            case R.id.one_three:
                gameLogic.addMarker(playerMarker, 0, 2);
                checkGameStatus();
                updateUI(playerMarker, 0, 2);
                computerLogic();
                one_three.setEnabled(false);
                break;
            case R.id.two_one:
                gameLogic.addMarker(playerMarker, 1, 0);
                checkGameStatus();
                updateUI(playerMarker, 1, 0);
                computerLogic();
                two_one.setEnabled(false);
                break;
            case R.id.two_two:
                gameLogic.addMarker(playerMarker, 1, 1);
                checkGameStatus();
                updateUI(playerMarker, 1, 1);
                computerLogic();
                two_two.setEnabled(false);
                break;
            case R.id.two_three:
                gameLogic.addMarker(playerMarker, 1, 2);
                checkGameStatus();
                updateUI(playerMarker, 1, 2);
                computerLogic();
                two_three.setEnabled(false);
                break;
            case R.id.three_one:
                gameLogic.addMarker(playerMarker, 2, 0);
                checkGameStatus();
                updateUI(playerMarker, 2, 0);
                computerLogic();
                three_one.setEnabled(false);
                break;
            case R.id.three_two:
                gameLogic.addMarker(playerMarker, 2, 1);
                checkGameStatus();
                updateUI(playerMarker, 2, 1);
                computerLogic();
                three_two.setEnabled(false);
                break;
            case R.id.three_three:
                gameLogic.addMarker(playerMarker, 2, 2);
                checkGameStatus();
                updateUI(playerMarker, 2, 2);
                computerLogic();
                three_three.setEnabled(false);
                break;
        }
    }


    public void computerLogic() {

        if(!gameLogic.checkFull()&& !gameLogic.hasWon()) {

            int[] location = null;
            if (difficulty != null) {
                switch (difficulty) {
                    case "Facile":
                        location = gameLogic.computerAIEasy();
                        break;
                    case "Moyenne":

                        location = gameLogic.computerAIMedium();
                        break;
                    case "Difficile":

                        location = gameLogic.computerAIHard();
                        break;
                }
            }

            if (location != null) {
                final int row = location[0];
                final int col = location[1];


                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    gameLogic.addMarker(computerMarker, row, col);
                    updateUI(computerMarker, row, col);

                }, 2000);
            }
        }
    }


    public void updateUI(char marker, int row, int col){

        if (row == 0 && col == 0) {
            if (marker == 'x') {
                one_one.setBackgroundResource(R.drawable.cross);
            } else {
                one_one.setBackgroundResource(R.drawable.circle);
            }
            one_one.setEnabled(false);
        } else if (row == 0 && col == 1) {
            if (marker == 'x') {
                one_two.setBackgroundResource(R.drawable.cross);
            } else {
                one_two.setBackgroundResource(R.drawable.circle);
            }
            one_two.setEnabled(false);
        } else if (row == 0 && col == 2) {
            if (marker == 'x') {
                one_three.setBackgroundResource(R.drawable.cross);
            } else {
                one_three.setBackgroundResource(R.drawable.circle);
            }
            one_three.setEnabled(false);
        } else if (row == 1 && col == 0) {
            if (marker == 'x') {
                two_one.setBackgroundResource(R.drawable.cross);
            } else {
                two_one.setBackgroundResource(R.drawable.circle);
            }
            two_one.setEnabled(false);
        } else if (row == 1 && col == 1) {
            if (marker == 'x') {
                two_two.setBackgroundResource(R.drawable.cross);
            } else {
                two_two.setBackgroundResource(R.drawable.circle);
            }
            two_two.setEnabled(false);
        } else if (row == 1 && col == 2) {
            if (marker == 'x') {
                two_three.setBackgroundResource(R.drawable.cross);
            } else {
                two_three.setBackgroundResource(R.drawable.circle);
            }
            two_three.setEnabled(false);
        } else if (row == 2 && col == 0) {
            if (marker == 'x') {
                three_one.setBackgroundResource(R.drawable.cross);
            } else {
                three_one.setBackgroundResource(R.drawable.circle);
            }
            three_one.setEnabled(false);
        } else if (row == 2 && col == 1) {
            if (marker == 'x') {
                three_two.setBackgroundResource(R.drawable.cross);
            } else {
                three_two.setBackgroundResource(R.drawable.circle);
            }
            three_two.setEnabled(false);
        } else if (row == 2 && col == 2) {
            if (marker == 'x') {
                three_three.setBackgroundResource(R.drawable.cross);
            } else {
                three_three.setBackgroundResource(R.drawable.circle);
            }
            three_three.setEnabled(false);
        }
        checkGameStatus();
    }

    public void checkGameStatus() {

        if (gameLogic.hasWon()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (gameLogic.getWinningMarker() == playerMarker) {
                builder.setTitle("Bravo, tu as gagné!");
                if(player!=null) {
                    player.setScore(playerScore + 1);
                }
            } else {
                builder.setTitle("Tu as perdu, tu feras mieux la prochaine fois!");
                computer.setScore(computerScore + 1);

            }

            builder.setPositiveButton("Nouveau jeu", (dialog, id) -> {
                Intent newGame = new Intent(OnePlayerActivity.this, OnePlayerActivity.class);
                newGame.putExtra("Joueur", player);
                newGame.putExtra("Ordinateur", computer);
                newGame.putExtra("Difficulté", difficulty);
                startActivity(newGame);
            }).setNegativeButton("Menu principal", (dialog, id) -> {
                Intent loginActivity = new Intent(OnePlayerActivity.this, MainMenuActivity.class);
                startActivity(loginActivity);
            });
            builder.create().show();
        } else if (gameLogic.checkFull()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tu feras mieux la prochaine fois")
                    .setPositiveButton("Nouveau jeu", (dialog, id) -> {
                        Intent newGame = new Intent(OnePlayerActivity.this, OnePlayerActivity.class);
                        newGame.putExtra("Joueur", player);
                        newGame.putExtra("Ordinateur", computer);
                        newGame.putExtra("Difficulté", difficulty);
                        startActivity(newGame);
                    })
                    .setNegativeButton("Menu principal", (dialog, id) -> {
                        Intent loginActivity = new Intent(OnePlayerActivity.this, MainMenuActivity.class);
                        startActivity(loginActivity);
                    });
            builder.create().show();

        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes vous sur de quitter?")
                .setPositiveButton("Oui", (dialog, id) -> {
                    Intent loginActivity = new Intent(OnePlayerActivity.this, MainMenuActivity.class);
                    startActivity(loginActivity);
                })
                .setNegativeButton("Non", (dialog, id) -> {
                });
        builder.create().show();
    }
}
