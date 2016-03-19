package xyz.gatling.novat.lifecounter;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import xyz.gatling.novat.lifecounter.Fragments.LifePoolFragment;

/**
 * Created by gimmiepepsi on 3/5/16.
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.button_left)
    Button buttonLeft;
    @Bind(R.id.button_right)
    Button buttonRight;

    LifePoolFragment enemy, player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        newGame(Constants.DEFAULT_LIFE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onWindowFocusChanged(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void newGame(int startingLife){
        newGame(startingLife, false);
    }

    private void newGame(int startingLife, boolean reset){
        newGame(startingLife, startingLife, reset);
    }

    private void newGame(int enemyLife, int playerLife, boolean reset){
        enemy = (LifePoolFragment) getFragmentManager().findFragmentByTag("enemy");
        player = (LifePoolFragment) getFragmentManager().findFragmentByTag("player");

        if(reset){
            enemy.newGame(enemyLife);
            player.newGame(playerLife);
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(enemy == null){
            enemy = LifePoolFragment.newInstance(enemyLife);
            fragmentTransaction.add(R.id.life_pool_enemy, enemy, "enemy");
        }
        if(player == null){
            player = LifePoolFragment.newInstance(playerLife);
            fragmentTransaction.add(R.id.life_pool_player, player, "player");
        }
        fragmentTransaction.disallowAddToBackStack().commit();
    }

    private boolean isCurrentGameSolo(){
        return buttonRight.getTag() != null && (boolean) buttonRight.getTag();
    }

    private void switchPlayerMode(){
        boolean isCurrentGameSolo = isCurrentGameSolo();
        ButterKnife.findById(this, R.id.life_pool_enemy).setVisibility(isCurrentGameSolo ? View.VISIBLE : View.GONE);
        buttonRight.setText(isCurrentGameSolo ? R.string.two_players : R.string.one_player);
        buttonRight.setTag(!isCurrentGameSolo);

        if(Utils.isScreenInPortrait(this)){
            ButterKnife.findById(this, R.id.life_pool_player).setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            isCurrentGameSolo ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT,
                            isCurrentGameSolo ? 5f : 1f));

        }
        else{
            ButterKnife.findById(this, R.id.life_pool_player).setLayoutParams(
                    new LinearLayout.LayoutParams(
                            isCurrentGameSolo ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            isCurrentGameSolo ? 1f : 5f));
        }
    }



    private void startMultiMan(){
        Intent intent = new Intent(this, MultiManActivity.class);
        intent.putExtra(Constants.KEY_IS_MULTIMAN, true);
        startActivity(intent);
    }

    private void showStartNewGameWithSpecificLifeDialog(){
        final View dialogView = createNewGameDialog();
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_game)
                .setView(dialogView)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int startingTotal = ((AppCompatSeekBar) ButterKnife.findById(dialogView, R.id.dialog_life_pool_seeker)).getProgress();
                        newGame(startingTotal, true);
                    }
                })
                .create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_one_player).setVisible(!isCurrentGameSolo());
        menu.findItem(R.id.menu_two_player).setVisible(isCurrentGameSolo());
        return super.onPrepareOptionsMenu(menu);
    }

    @OnClick({R.id.button_left, R.id.button_right})
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button_left: //New game
                newGame(Constants.DEFAULT_LIFE, true);
                break;
            case R.id.button_right: //Single pool
                switchPlayerMode();
                break;
        }
    }

    @OnLongClick({R.id.button_left, R.id.button_right})
    public boolean onLongClick(View v){
        switch (v.getId()){
            case R.id.button_left: //New game
                showStartNewGameWithSpecificLifeDialog();
                break;
            case R.id.button_right: //One/Two players
                startMultiMan();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_new_game:
                newGame(Constants.DEFAULT_LIFE, true);
                break;
            case R.id.menu_new_game_life:
                showStartNewGameWithSpecificLifeDialog();
                break;
            case R.id.menu_one_player:
            case R.id.menu_two_player:
                switchPlayerMode();
                break;
            case R.id.menu_multiman:
                startMultiMan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
