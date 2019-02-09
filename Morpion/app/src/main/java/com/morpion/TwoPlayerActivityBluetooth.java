package com.morpion;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class TwoPlayerActivityBluetooth extends AppCompatActivity implements View.OnClickListener{


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int PLAYER_LOCATION_READ = 2;
    public static final int PLAYER_LOCATION_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_DISCONNECTED = 6;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String MARKER = "marker";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private String mMarker = null;
    private StringBuffer mOutStringBuffer;
    private String oMarker = null;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGameService gameService = null;

    private EditText remoteNameLabel;
    private ImageView myMarker;
    private ImageView opponentMarker;

    private String hostName;

    private Player hostPlayer;
    private Player remotePlayer;

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
    private boolean isTurn = false;

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_bluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        EditText hostNameLabel = (EditText) findViewById(R.id.player_name);
        remoteNameLabel = (EditText) findViewById(R.id.remote_name);

        myMarker = (ImageView) findViewById(R.id.m_marker);
        opponentMarker = (ImageView) findViewById(R.id.o_marker);

        hostNameLabel.setEnabled(false);
        remoteNameLabel.setEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.options_bar);
        setTitle("Salle Bluetooth");
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            hostName = extras.getString("Joueur 1");
        }

        hostNameLabel.setText(hostName);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth indisponible", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (gameService == null) setupGame();
        }

    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (gameService != null) {
            if (gameService.getState() == BluetoothGameService.STATE_NONE) {
                gameService.start();
            }
        }
    }

    private void setupGame() {
        gameService = new BluetoothGameService(mHandler);
        mOutStringBuffer = new StringBuffer("");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameService != null) gameService.stop();
    }

    public void setupBoardView() {
        setContentView(R.layout.activity_two_player);
        gameLogic = new GameLogic();

        //initialize buttons
        one_one = (ImageButton) findViewById(R.id.one_one);
        one_two = (ImageButton) findViewById(R.id.one_two);
        one_three = (ImageButton) findViewById(R.id.one_three);
        two_one = (ImageButton) findViewById(R.id.two_one);
        two_two = (ImageButton) findViewById(R.id.two_two);
        two_three = (ImageButton) findViewById(R.id.two_three);
        three_one = (ImageButton) findViewById(R.id.three_one);
        three_two = (ImageButton) findViewById(R.id.three_two);
        three_three = (ImageButton) findViewById(R.id.three_three);

        TextView playerOneName = (TextView) findViewById(R.id.player_one_score);
        TextView playerTwoName = (TextView) findViewById(R.id.player_two_score);

        playerOneName.setText(hostPlayer.getName() + ": " + hostPlayer.getScore());
        playerTwoName.setText(remotePlayer.getName() + ": " + remotePlayer.getScore());


        /*one_one.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        one_two.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        one_three.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        two_one.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        two_two.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        two_three.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        three_one.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        three_two.setBackgroundResource(R.drawable.tic_tac_toe_blank);
        three_three.setBackgroundResource(R.drawable.tic_tac_toe_blank);*/

        one_one.setOnClickListener((v)-> onClick(v));
        one_two.setOnClickListener((v)-> onClick(v));
        one_three.setOnClickListener((v)-> onClick(v));
        two_one.setOnClickListener((v)-> onClick(v));
        two_two.setOnClickListener((v)-> onClick(v));
        two_three.setOnClickListener((v)-> onClick(v));
        three_one.setOnClickListener((v)-> onClick(v));
        three_two.setOnClickListener((v)-> onClick(v));
        three_three.setOnClickListener((v)-> onClick(v));

        if(isTurn){
            Toast.makeText(TwoPlayerActivityBluetooth.this, "C'est ton tour!", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendLocation(String location) {

        if (gameService.getState() != BluetoothGameService.STATE_CONNECTED) {
            Toast.makeText(this, "Introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] sendLocation = location.getBytes();

        gameService.writeLocation(sendLocation);
        mOutStringBuffer.setLength(0);
    }

    public void updateUI(char marker, int row, int col) {
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

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothGameService.STATE_CONNECTED:
                            break;
                        case BluetoothGameService.STATE_CONNECTING:
                            break;
                        case BluetoothGameService.STATE_LISTEN:

                        case BluetoothGameService.STATE_NONE:
                            break;
                        case BluetoothGameService.STATE_DISCONNECTED:
                            Intent bluetoothIntent = new Intent(TwoPlayerActivityBluetooth.this, TwoPlayerActivityBluetooth.class);
                            bluetoothIntent.putExtra("playerOne", hostName);
                            startActivity(bluetoothIntent);
                            break;
                        case BluetoothGameService.STATE_NEW_GAME:
                            if(dialog != null && dialog.isShowing()){
                                dialog.dismiss();
                            }
                            setupBoardView();
                            break;
                    }
                    break;
                case PLAYER_LOCATION_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);

                    break;
                case PLAYER_LOCATION_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    int row = Character.getNumericValue(readMessage.charAt(0));
                    int col = Character.getNumericValue(readMessage.charAt(2));

                    isTurn = !isTurn;

                    gameLogic.addMarker(oMarker.charAt(0), row, col);
                    updateUI(oMarker.charAt(0), row, col);
                    break;
                case MESSAGE_DEVICE_NAME:
                    String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    mMarker = msg.getData().getString(MARKER);

                    assert mMarker != null;
                    if (mMarker.equals("")) {
                        mMarker = "o";
                        oMarker = "x";
                        myMarker.setBackgroundResource(R.drawable.circle);
                        opponentMarker.setBackgroundResource(R.drawable.cross);
                    } else if (mMarker.equals("x")) {
                        oMarker = "o";
                        isTurn = true;
                        myMarker.setBackgroundResource(R.drawable.cross);
                        opponentMarker.setBackgroundResource(R.drawable.circle);
                    }
                    Toast.makeText(TwoPlayerActivityBluetooth.this, "Connecté à " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    remoteNameLabel.setText(mConnectedDeviceName);
                    hostPlayer = new Player(hostName, mMarker.charAt(0), 0);
                    remotePlayer = new Player(mConnectedDeviceName, oMarker.charAt(0), 0);

                    setupBoardView();


                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(TwoPlayerActivityBluetooth.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    gameService.connect(device, "x");
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupGame();
                } else {
                    Toast.makeText(this, "Impossible de quitter", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.scan) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
            }

        return false;
    }

    public void checkGameStatus() {
        char playerMarker = hostPlayer.getPlayerMarker();
        if (gameLogic.hasWon()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (gameLogic.getWinningMarker() == playerMarker) {
                builder.setTitle("Bravo, tu as gagné!");
                hostPlayer.setScore(hostPlayer.getScore() + 1);
            } else {
                builder.setTitle("Tu as perdu, tu feras mieux la prochaine fois!");
                remotePlayer.setScore(remotePlayer.getScore() + 1);

            }

            builder.setPositiveButton("Nouveau jeu", (dialog1, id) -> gameService.setNewGame()).setNegativeButton("Main Menu", (dialog12, id) -> {
                Intent loginActivity = new Intent(TwoPlayerActivityBluetooth.this, MainMenuActivity.class);
                gameService.stop();
                startActivity(loginActivity);
            });
            dialog = builder.create();
            dialog.show();
        } else if (gameLogic.checkFull()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tu feras mieux la prochaine fois")
                    .setPositiveButton("Nouveau jeu", (dialog2, id) -> gameService.setNewGame())
                    .setNegativeButton("Menu principal", (dialog3, id) -> {
                        Intent loginActivity = new Intent(TwoPlayerActivityBluetooth.this, MainMenuActivity.class);
                        gameService.stop();
                        startActivity(loginActivity);
                    });
            dialog = builder.create();
            dialog.show();

        }
    }


    @Override
    public void onClick(View v) {
        char playerMarker = mMarker.charAt(0);
        if (isTurn) {
            switch (v.getId()) {

                case R.id.one_one:
                    gameLogic.addMarker(playerMarker, 0, 0);
                    updateUI(playerMarker, 0, 0);
                    sendLocation("0,0");

                    break;
                case R.id.one_two:
                    gameLogic.addMarker(playerMarker, 0, 1);
                    updateUI(playerMarker, 0, 1);
                    sendLocation("0,1");

                    break;
                case R.id.one_three:
                    gameLogic.addMarker(playerMarker, 0, 2);
                    updateUI(playerMarker, 0, 2);
                    sendLocation("0,2");

                    break;
                case R.id.two_one:
                    gameLogic.addMarker(playerMarker, 1, 0);
                    updateUI(playerMarker, 1, 0);
                    sendLocation("1,0");

                    break;
                case R.id.two_two:
                    gameLogic.addMarker(playerMarker, 1, 1);
                    updateUI(playerMarker, 1, 1);
                    sendLocation("1,1");

                    break;
                case R.id.two_three:
                    gameLogic.addMarker(playerMarker, 1, 2);
                    updateUI(playerMarker, 1, 2);
                    sendLocation("1,2");

                    break;
                case R.id.three_one:
                    gameLogic.addMarker(playerMarker, 2, 0);
                    updateUI(playerMarker, 2, 0);
                    sendLocation("2,0");
                    break;
                case R.id.three_two:
                    gameLogic.addMarker(playerMarker, 2, 1);
                    updateUI(playerMarker, 2, 1);
                    sendLocation("2,1");

                    break;
                case R.id.three_three:
                    gameLogic.addMarker(playerMarker, 2, 2);
                    updateUI(playerMarker, 2, 2);
                    sendLocation("2,2");

                    break;

            }
            isTurn = !isTurn;
        }else{
            Toast.makeText(this, "Attends ton tour!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes vous sur de quitter?")
                .setPositiveButton("Oui", (dialog1, id) -> {
                    Intent loginActivity = new Intent(TwoPlayerActivityBluetooth.this, MainMenuActivity.class);
                    gameService.stop();
                    startActivity(loginActivity);
                })
                .setNegativeButton("Non", (dialog2, id) -> {
                });
        builder.create().show();
    }
}
