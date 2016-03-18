package xyz.gatling.novat.lifecounter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gimmiepepsi on 3/18/16.
 */
public class MultiManActivity extends BaseActivity {

    @Bind(R.id.multiman_list)
    RecyclerView multimanList;

    List<String> playerNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);
        ButterKnife.bind(this);

        if(getIntent().getExtras() != null){

        }

        multimanList.setAdapter(new MultiManAdapter());


    }

    class MultiManViewHolder extends RecyclerView.ViewHolder{

        public MultiManViewHolder() {
            super(LayoutInflater.from(MultiManActivity.this).inflate(R.layout.listitem_multiman_container, null));
        }
    }

    class MultiManAdapter extends RecyclerView.Adapter<MultiManViewHolder>{

        @Override
        public MultiManViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MultiManViewHolder();
        }

        @Override
        public void onBindViewHolder(MultiManViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
