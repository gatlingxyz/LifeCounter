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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

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

    private final static String KEY_STARTING_POOL = "key_starting_pool";

    @Bind(R.id.life_pool_modifier_sign_button)
    Button modifierSign;
    @Bind({R.id.life_pool_modifier_one, R.id.life_pool_modifier_two, R.id.life_pool_modifier_three, R.id.life_pool_modifier_four,
    R.id.life_pool_modifier_five, R.id.life_pool_modifier_six, R.id.life_pool_modifier_seven, R.id.life_pool_modifier_eight,
    R.id.life_pool_modifier_nine, R.id.life_pool_modifier_ten})
    List<TextView> modifierViews;
    @Bind(R.id.life_pool_total)
    TextView lifePoolTotal;
    @Bind(R.id.life_pool_seeker)
    AppCompatSeekBar lifePoolSeeker;

    int currentLifePoolValue = 20;
    boolean isEnemy = false;

    public static LifePoolFragment newInstance(){
        return newInstance(20);
    }

    public static LifePoolFragment newInstance(int startingTotal){
        LifePoolFragment fragment = new LifePoolFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_STARTING_POOL, startingTotal);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setLife(int number){
        currentLifePoolValue = number;
        updateLife();
    }

    public int getLife(){
        return currentLifePoolValue;
    }

    public LifePoolFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(getArguments() != null){
            currentLifePoolValue = getArguments().getInt(KEY_STARTING_POOL, 20);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STARTING_POOL, currentLifePoolValue);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isEnemy = container.getId() == R.id.life_pool_enemy;
        View view = inflater.inflate(R.layout.fragment_life_pool, null);
        ButterKnife.bind(this, view);
        modifierSign.setTag(true);
        toggleButton(true);
        lifePoolSeeker.setOnSeekBarChangeListener(this);
        lifePoolSeeker.setMax(Constants.MAXIMUM_LIFE);
        lifePoolSeeker.setProgress(currentLifePoolValue);
        return view;
    }

    @OnLongClick(R.id.life_pool_total)
    public boolean onLongClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_longpress_life_pool, null);
        final Dialog dialog = builder.setView(view).create();

//        if(isEnemy){
//            view.setRotation(180);
//        }

//        View rotateButton = ButterKnife.findById(view, R.id.dialog_life_pool_rotate);
        TextView title = ButterKnife.findById(view, R.id.dialog_life_pool_title);
        View submitButton = ButterKnife.findById(view, R.id.dialog_life_pool_submit);

        title.setText("Add to or Subtract from " + currentLifePoolValue);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int changeInLife = Integer.parseInt(((EditText)view.findViewById(R.id.dialog_life_pool_exact)).getText().toString());
                currentLifePoolValue += changeInLife;
                updateLife();
                dialog.cancel();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        return true;
    }

    private void toggleButton(boolean modifiersAreNegative){
        for(TextView modifierView : modifierViews){
            String value = modifierView.getTag().toString();
            modifierView.setText(
                    getString(
                            modifiersAreNegative ? R.string.life_pool_modifier_format_negative : R.string.life_pool_modifier_format_positive,
                            value
                    )
            );
        }
    }

    @OnClick(R.id.life_pool_modifier_sign_button)
    public void onClick(){
        boolean modifiersAreNegative = !isModifiersNegative();
        modifierSign.setTag(modifiersAreNegative);
        modifierSign.setText(modifiersAreNegative ? "Negative" : "Positive");
        toggleButton(modifiersAreNegative);
    }

    private boolean isModifiersNegative(){
        return (boolean) modifierSign.getTag();
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

        lifePoolSeeker.setProgress(currentLifePoolValue);
    }

    @OnClick({R.id.life_pool_modifier_one, R.id.life_pool_modifier_two, R.id.life_pool_modifier_three, R.id.life_pool_modifier_four,
    R.id.life_pool_modifier_five, R.id.life_pool_modifier_six, R.id.life_pool_modifier_seven, R.id.life_pool_modifier_eight,
    R.id.life_pool_modifier_nine, R.id.life_pool_modifier_ten})
    public void onModifersTapped(View v){
        boolean modifiersAreNegative = isModifiersNegative();
        int value = Integer.parseInt(v.getTag().toString());

        if(modifiersAreNegative) {
            currentLifePoolValue -= value;
            if(currentLifePoolValue < Constants.MINIMUM_LIFE){
                currentLifePoolValue = Constants.MINIMUM_LIFE;
            }
        }
        else{
            currentLifePoolValue += value;
            if(currentLifePoolValue > Constants.MAXIMUM_LIFE){
                currentLifePoolValue = Constants.MAXIMUM_LIFE;
            }
        }

        lifePoolSeeker.setProgress(currentLifePoolValue);
    }

    private void updateLife(){
        lifePoolTotal.setText(String.valueOf(currentLifePoolValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            currentLifePoolValue = progress;
        }
        updateLife();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
