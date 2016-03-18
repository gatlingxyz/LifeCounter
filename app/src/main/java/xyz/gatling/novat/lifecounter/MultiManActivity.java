package xyz.gatling.novat.lifecounter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
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

        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            setDefaultPlayerNames(extras.getInt(Constants.KEY_NUMBER_OF_PLAYERS, Constants.DEFAULT_NUMBER_OF_PLAYERS));
        }
        else{
            setDefaultPlayerNames(Constants.DEFAULT_NUMBER_OF_PLAYERS);
        }

        multimanList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        multimanList.setAdapter(new MultiManAdapter());


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

        public void init(String name){
            playerName.setText(name);
            viewId = View.generateViewId();
            container.setId(viewId);
            getFragmentManager().beginTransaction()
                    .add(viewId, LifePoolFragment.newMultiManInstance())
                    .commit();
        }
    }

    class MultiManAdapter extends RecyclerView.Adapter<MultiManViewHolder>{

        @Override
        public MultiManViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MultiManViewHolder(LayoutInflater.from(MultiManActivity.this).inflate(R.layout.listitem_multiman_container, null));
        }

        @Override
        public void onBindViewHolder(MultiManViewHolder holder, int position) {
            holder.init(playerNames.get(position));
        }

        @Override
        public int getItemCount() {
            return playerNames.size();
        }
    }
}
