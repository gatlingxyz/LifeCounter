package xyz.gatling.novat.lifecounter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import xyz.gatling.novat.lifecounter.Fragments.LifePoolFragment;

/**
 * Created by gimmiepepsi on 3/18/16.
 */
public class MultiManActivity extends BaseActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @Bind(R.id.multiman_list)
    RecyclerView multimanList;

    List<String> playerNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            setDefaultPlayerNames(extras.getInt(Constants.KEY_NUMBER_OF_PLAYERS, Constants.DEFAULT_NUMBER_OF_PLAYERS));
        }
        else{
            setDefaultPlayerNames(Constants.DEFAULT_NUMBER_OF_PLAYERS);
        }

        multimanList.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false));
        multimanList.setAdapter(new MultiManAdapter(Constants.DEFAULT_LIFE));

    }

    private void setDefaultPlayerNames(int numberOfPlayers){
        for(int i = 0; i < numberOfPlayers; i++){
            playerNames.add(getString(R.string.default_player_name_format, String.valueOf(i+1)));
        }
    }

    class MultiManViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.multiman_container)
        FrameLayout container;
        @Bind(R.id.multiman_player_name)
        TextView playerName;

        private int viewId = -1;

        public MultiManViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void init(String name, int startingLife){
            playerName.setText(name);
            viewId = View.generateViewId();
            container.setId(viewId);
            getFragmentManager().beginTransaction()
                    .add(viewId, LifePoolFragment.newInstance(startingLife, true))
                    .commit();
        }

        @OnLongClick(R.id.multiman_player_name)
        public boolean changePlayerName(final TextView textView){
            View view = LayoutInflater.from(textView.getContext()).inflate(R.layout.dialog_longpress_player_name, null);
            final EditText editText = ButterKnife.findById(view, R.id.dialog_player_name_edittext);
            editText.setText(textView.getText().toString());
            editText.setSelection(editText.getText().length());
            new AlertDialog.Builder(textView.getContext())
                    .setView(view)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textView.setText(editText.getText().toString());
                        }
                    })
                    .create().show();
            return true;
        }
    }

    class MultiManAdapter extends RecyclerView.Adapter<MultiManViewHolder>{

        int startingLife;

        public MultiManAdapter(int startingLife){
            this.startingLife = startingLife;
        }

        @Override
        public MultiManViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MultiManViewHolder(LayoutInflater.from(MultiManActivity.this).inflate(R.layout.listitem_multiman_container, null));
        }

        @Override
        public void onBindViewHolder(MultiManViewHolder holder, int position) {
            holder.init(playerNames.get(position), startingLife);
        }

        @Override
        public int getItemCount() {
            return playerNames.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multiman, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_new_game:
                multimanList.setAdapter(new MultiManAdapter(Constants.DEFAULT_LIFE));
                break;
            case R.id.menu_new_game_life:
                final View dialogView = createNewGameDialog();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.new_game)
                        .setView(dialogView)
                        .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int startingTotal = ((AppCompatSeekBar) ButterKnife.findById(dialogView, R.id.dialog_life_pool_seeker)).getProgress();
                                multimanList.setAdapter(new MultiManAdapter(startingTotal));

                            }
                        })
                        .create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
