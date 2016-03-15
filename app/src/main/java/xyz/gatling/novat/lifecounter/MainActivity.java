package xyz.gatling.novat.lifecounter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import xyz.gatling.novat.lifecounter.Fragments.LifePoolFragment;

/**
 * Created by gimmiepepsi on 3/5/16.
 */
public class MainActivity extends Activity {

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
        resetLifePools();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onWindowFocusChanged(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void resetLifePools(){
        resetLifePools(20);
    }

    private void resetLifePools(int playerLife){
        resetLifePools(playerLife, playerLife);
    }

    private void resetLifePools(int enemyLife, int playerLife){
        enemy = (LifePoolFragment) getFragmentManager().findFragmentByTag("enemy");
        player = (LifePoolFragment) getFragmentManager().findFragmentByTag("player");

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

    @OnClick({R.id.button_left, R.id.button_right})
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button_left: //New game
                resetLifePools();
                break;
            case R.id.button_right: //Single pool
                boolean isCurrentGameSolo = buttonRight.getTag() != null && (boolean) buttonRight.getTag();
                ButterKnife.findById(this, R.id.life_pool_enemy).setVisibility(isCurrentGameSolo ? View.VISIBLE : View.GONE);
                buttonRight.setText(isCurrentGameSolo ? "Solo Pool" : "Two Pools");
                buttonRight.setTag(!isCurrentGameSolo);
                ButterKnife.findById(this, R.id.life_pool_player).setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                isCurrentGameSolo ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT,
                                isCurrentGameSolo ? 5f : 1f)
                );
                break;
        }
    }

    @OnLongClick({R.id.button_left})
    public boolean onLongClick(View v){
        switch (v.getId()){
            case R.id.button_left: //New game
                final View dialogView = createNewGameDialog();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.new_game)
                        .setView(dialogView)
                        .setPositiveButton("Start!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int startingTotal = ((AppCompatSeekBar)ButterKnife.findById(dialogView, R.id.dialog_life_pool_seeker)).getProgress();
                                resetLifePools(startingTotal);
                            }
                        })
                        .create().show();
        }
        return true;
    }

    private View createNewGameDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_longpress_newgame, null);
        final TextView lifePoolTotal = ButterKnife.findById(view, R.id.dialog_life_pool_total);
        final AppCompatSeekBar lifePoolSeeker = ButterKnife.findById(view, R.id.dialog_life_pool_seeker);
        lifePoolSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lifePoolTotal.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
