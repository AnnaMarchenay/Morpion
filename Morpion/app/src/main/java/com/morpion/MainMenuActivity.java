package com.morpion;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        TextView onePlayerLabel = (TextView) findViewById(R.id.one_player_label);
        TextView twoPlayersLabel = (TextView) findViewById(R.id.two_players_label);

        onePlayerLabel.setOnClickListener((v)-> onePlayer(v));
        twoPlayersLabel.setOnClickListener((v)-> twoPlayers(v));


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes vous sur de vouloir quitter le jeu ?")
                .setPositiveButton("Oui", (dialog, id) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Non", (dialog, id) -> {
                });
        builder.create().show();
    }

    public void onePlayer(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View layout = inflater.inflate(R.layout.one_player_options, null);
        final Intent onePlayerGame = new Intent(MainMenuActivity.this, OnePlayerActivity.class);

        final RadioButton oMarker = (RadioButton) layout.findViewById(R.id.o_marker);
        final RadioButton xMarker = (RadioButton) layout.findViewById(R.id.x_marker);

        final RadioButton easy = (RadioButton) layout.findViewById(R.id.easy);
        final RadioButton medium = (RadioButton) layout.findViewById(R.id.medium);
        final RadioButton hard = (RadioButton) layout.findViewById(R.id.hard);

        final TextView playerName = (TextView) layout.findViewById(R.id.username);


        oMarker.setOnClickListener(v1 -> {
            oMarker.setBackgroundResource(R.drawable.circle);
            xMarker.setBackgroundResource(R.drawable.black_cross);
        });

        xMarker.setOnClickListener(v2 -> {
            xMarker.setBackgroundResource(R.drawable.cross);
            oMarker.setBackgroundResource(R.drawable.black_circle);
        });

        builder.setView(layout)
                .setTitle("Options")
                .setPositiveButton("Commencer", (dialog, id) -> {
                    if ((playerName.getText().length() > 0) && (oMarker.isChecked() || xMarker.isChecked()) && (easy.isChecked() || medium.isChecked() || hard.isChecked())) {
                        if (oMarker.isChecked()) {
                            if (easy.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Facile");
                            } else if (medium.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Moyenne");
                            } else if (hard.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Difficile");
                            } else {
                                Toast.makeText(MainMenuActivity.this, "Veuillez indiquer une difficulté", Toast.LENGTH_SHORT).show();
                            }

                            Player player = new Player(playerName.getText().toString(), 'o', 0);
                            Player computer = new Player("Ordinateur", 'x', 0);
                            onePlayerGame.putExtra("Joueur", player);
                            onePlayerGame.putExtra("Ordinateur", computer);
                        } else if (xMarker.isChecked()) {
                            if (easy.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Facile");
                            } else if (medium.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Moyenne");
                            } else if (hard.isChecked()) {
                                onePlayerGame.putExtra("Difficulté", "Difficile");
                            } else {
                                Toast.makeText(MainMenuActivity.this, "Veuillez indiquer une difficulté", Toast.LENGTH_SHORT).show();
                            }

                            Player player = new Player(playerName.getText().toString(), 'x', 0);
                            Player computer = new Player("Ordinateur", 'o', 0);
                            onePlayerGame.putExtra("Joueur", player);
                            onePlayerGame.putExtra("Ordinateur", computer);
                        }
                        startActivity(onePlayerGame);
                    } else {
                        Toast.makeText(MainMenuActivity.this, "Veuillez entrer un pseudo et choir un symbole", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", (dialog, id) -> {

                });
        builder.create().show();
    }


    public void twoPlayers(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View layout = inflater.inflate(R.layout.two_player_options, null);

        final TextView playerOneName = (TextView) layout.findViewById(R.id.player_one);
        final TextView playerTwoName = (TextView) layout.findViewById(R.id.player_two);


        final RadioButton oMarker = (RadioButton) layout.findViewById(R.id.o_marker);
        final RadioButton xMarker = (RadioButton) layout.findViewById(R.id.x_marker);


            playerTwoName.setVisibility(View.GONE);
            oMarker.setVisibility(View.GONE);
            xMarker.setVisibility(View.GONE);


        oMarker.setOnClickListener(v2 -> {
            oMarker.setBackgroundResource(R.drawable.circle);
            xMarker.setBackgroundResource(R.drawable.black_cross);
        });

        xMarker.setOnClickListener(v3 -> {
            xMarker.setBackgroundResource(R.drawable.cross);
            oMarker.setBackgroundResource(R.drawable.black_circle);
        });
        builder.setView(layout)
                .setTitle("Options")
                .setPositiveButton("Commencer", (dialog, id) -> {

                        Intent bluetoothIntent = new Intent(MainMenuActivity.this, TwoPlayerActivityBluetooth.class);
                        String playerOne = null;
                        if (playerOneName.getText().length() > 0) {
                            playerOne = playerOneName.getText().toString();
                            bluetoothIntent.putExtra("Joueur 1", playerOne);
                            startActivity(bluetoothIntent);
                        } else {
                            Toast.makeText(MainMenuActivity.this, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show();
                        }


                })
                .setNegativeButton("Annuler", (dialog, id) -> {

                });

        builder.create().show();
    }












}
