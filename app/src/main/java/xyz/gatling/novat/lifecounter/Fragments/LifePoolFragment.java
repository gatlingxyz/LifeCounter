package xyz.gatling.novat.lifecounter.Fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import xyz.gatling.novat.lifecounter.Constants;
import xyz.gatling.novat.lifecounter.R;

/**
 * Created by gimmiepepsi on 3/5/16.
 */
public class LifePoolFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.life_pool_total)
    TextView lifePoolTotal;
    @Bind(R.id.life_pool_seeker)
    AppCompatSeekBar lifePoolSeeker;
    @Bind(R.id.life_pool_minus)
    View minusSign;
    @Bind(R.id.life_pool_plus)
    View plusSign;

    int currentLifePoolValue = Constants.DEFAULT_LIFE;
    boolean isMultiMan = false;

    public static LifePoolFragment newInstance(){
        return newInstance(20, false);
    }

    public static LifePoolFragment newInstance(int startingTotal) {
        return newInstance(startingTotal, false);
    }

    public static LifePoolFragment newInstance(int startingTotal, boolean isMultiMan){
        LifePoolFragment fragment = new LifePoolFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_STARTING_POOL, startingTotal);
        bundle.putBoolean(Constants.KEY_IS_MULTIMAN, isMultiMan);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void newGame(int startingLife){
        currentLifePoolValue = startingLife;
        updateLife();
    }

    public LifePoolFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(getArguments() != null){
            currentLifePoolValue = getArguments().getInt(Constants.KEY_STARTING_POOL, 20);
            isMultiMan = getArguments().getBoolean(Constants.KEY_IS_MULTIMAN, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.KEY_STARTING_POOL, currentLifePoolValue);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_life_pool, null);
        ButterKnife.bind(this, view);
        lifePoolSeeker.setOnSeekBarChangeListener(this);
        lifePoolSeeker.setMax(Constants.MAXIMUM_LIFE);
        updateLife();
        return view;
    }

    @OnClick({R.id.life_pool_minus, R.id.life_pool_plus})
    public void onPlusOrMinusClicked(View view){
        switch(view.getId()){
            case R.id.life_pool_minus:
                currentLifePoolValue -= 1;
                break;
            case R.id.life_pool_plus:
                currentLifePoolValue += 1;
                break;
        }
        updateLife();
    }

    @OnLongClick({R.id.life_pool_minus, R.id.life_pool_plus})
    public boolean onLongClick(View selectedView){
        final boolean adding = selectedView.getId() == R.id.life_pool_plus;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_longpress_life_pool, null);
        final Dialog dialog = builder.setView(view).create();

        TextView title = ButterKnife.findById(view, R.id.dialog_life_pool_title);
        View submitButton = ButterKnife.findById(view, R.id.dialog_life_pool_submit);

        title.setText(getString(
                adding ? R.string.dialog_add_life_format : R.string.dialog_subtract_life_format,
                currentLifePoolValue));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int changeInLife = Integer.parseInt(((EditText)view.findViewById(R.id.dialog_life_pool_exact)).getText().toString());

                if(adding){
                    currentLifePoolValue += changeInLife;
                }
                else{
                    currentLifePoolValue -= changeInLife;
                }

                if(currentLifePoolValue > Constants.MAXIMUM_LIFE){
                    currentLifePoolValue = Constants.MAXIMUM_LIFE;
                }
                else if (currentLifePoolValue < Constants.MINIMUM_LIFE){
                    currentLifePoolValue = Constants.MINIMUM_LIFE;
                }
                updateLife();
                dialog.cancel();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        return true;
    }



    private void updateLife(){
        lifePoolSeeker.setProgress(currentLifePoolValue);
        lifePoolTotal.setText(String.valueOf(currentLifePoolValue));
        minusSign.setVisibility(currentLifePoolValue == Constants.MINIMUM_LIFE ? View.INVISIBLE : View.VISIBLE);
        plusSign.setVisibility(currentLifePoolValue == Constants.MAXIMUM_LIFE ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            currentLifePoolValue = progress;
        }
        updateLife();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
