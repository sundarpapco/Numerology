package com.papco.sundar.numerology;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.papco.sundar.numerology.database.MasterDatabase;
import com.papco.sundar.numerology.database.entity.AlphabatValue;

import java.util.List;

public class EditorActivity extends AppCompatActivity {

    public static final String TAG="SUNDAR";

    ViewGroup container,topParent;
    Rect alphabetTextBounds,valueTextBounds;
    TextView alphabet,mValue,mSave;
    ImageView mAdd,mSubtract;
    float startTextSize=-1;
    float startValueTextSize=-1;
    AlphabatValue value;
    @ColorInt int defaultTextColor;
    MasterDatabase db;
    boolean isReturning=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeTransitions();
        setContentView(R.layout.activity_value_editor);
        linkViews();

        value=getAlphabetFromIntent();
        loadDefaultColorFromTheme();
        initAlphabetValueViews();

        startTextSize=getIntent().getFloatExtra(MainActivity.SHARED_ALPHABET_TEXT_SIZE,-1);
        startValueTextSize=getIntent().getFloatExtra(MainActivity.SHARED_VALUE_TEXT_SIZE,-1);
        captureAlphabatInitialValues();
        setSharedElementCallback();

        topParent=findViewById(R.id.editor_parent);
        topParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideViews();
            }
        });
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addValue();
            }
        });
        mSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractValue();
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewValue();
            }
        });

    }

    private void linkViews(){
        alphabet=findViewById(R.id.editor_alphabet);
        mValue=findViewById(R.id.editor_value);
        mSave=findViewById(R.id.editor_save_button);
        mAdd=findViewById(R.id.editor_add);
        mSubtract=findViewById(R.id.editor_remove);
        container=findViewById(R.id.container_for_text);
    }

    private void loadDefaultColorFromTheme(){
        TypedValue typedValue=new TypedValue();
        getTheme().resolveAttribute(R.attr.editorValueNumber,typedValue,true);
        defaultTextColor=typedValue.data;

    }

    private void initAlphabetValueViews(){
        alphabet.setText(String.valueOf((char)('A'+value.getId()-1)));
        mValue.setText(Integer.toString(value.getCurrentValue()));
        if(value.getDefaultValue()==value.getCurrentValue())
            mValue.setTextColor(defaultTextColor);
        else
            mValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

    }

    private void saveNewValue() {

        int currentValue=Integer.parseInt(mValue.getText().toString());

        //if the value has not changed from the already existing value, no need to update.
        //just return to previous activity

        if(currentValue==value.getCurrentValue()){
            hideViews();
            return;
        }

        if(db==null)
            db=MasterDatabase.getInstance(this);
        value.setCurrentValue(currentValue);
        isReturning=true; // setting this flag will save change to db at transition end hook
        hideViews();
    }

    private void addValue() {

        int currentValue=Integer.parseInt(mValue.getText().toString());
        int newValue=currentValue+1;
        if(newValue>9)
            newValue=9;
        mValue.setText(Integer.toString(newValue));
        if(newValue==value.getDefaultValue())
            mValue.setTextColor(defaultTextColor);
        else
            mValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private void subtractValue() {

        int currentValue=Integer.parseInt(mValue.getText().toString());
        int newValue=currentValue-1;
        if(newValue<1)
            newValue=1;
        mValue.setText(Integer.toString(newValue));
        if(newValue==value.getDefaultValue())
            mValue.setTextColor(defaultTextColor);
        else
            mValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private AlphabatValue getAlphabetFromIntent() {

        AlphabatValue val=new AlphabatValue();
        val.setId(getIntent().getIntExtra(MainActivity.KEY_ID,-1));
        val.setDefaultValue(getIntent().getIntExtra(MainActivity.KEY_DEFAULT_VALUE,-1));
        val.setCurrentValue(getIntent().getIntExtra(MainActivity.KEY_CURRENT_VALUE,-1));
        return val;
    }


    private void setSharedElementCallback() {

        setEnterSharedElementCallback(new SharedElementCallback() {

            float endTextSize=-1;
            float endValueTextSize=-1;
            boolean isFirstRun=true;

            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {

                for(View v:sharedElements){
                    if(v.getId()==R.id.editor_alphabet){
                        endTextSize=((TextView)v).getTextSize();
                        if(startTextSize>=0)
                            ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX,startTextSize);

                    }
                    if(v.getId()==R.id.editor_value){
                        endValueTextSize=((TextView)v).getTextSize();
                        if(startValueTextSize>=0)
                            ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX,startValueTextSize);

                    }
                }
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {

                for(View v:sharedElements){
                    if(v.getId()==R.id.editor_alphabet){

                        if (isFirstRun && alphabetTextBounds!=null)
                           setBounds(v,alphabetTextBounds);

                       if(endTextSize>=0)
                           ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX,endTextSize);

                    }

                    if(v.getId()==R.id.editor_value){

                        if (isFirstRun && valueTextBounds!=null)
                            setBounds(v,valueTextBounds);

                        if(endValueTextSize>=0)
                            ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX,endValueTextSize);

                    }
                }
                isFirstRun=false;
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
            }



            private void setBounds(View view,Rect bounds){

                view.setTop(bounds.top);
                view.setBottom(bounds.bottom);
                view.setLeft(bounds.left);
                view.setRight(bounds.right);

            }
        });
        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if(isReturning){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.getAlphabatValueDao().updateAlphabatValue(value);
                        }
                    }).start();
                }else
                    showViews();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

    }

    private void captureAlphabatInitialValues() {

        alphabet.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                alphabet.getViewTreeObserver().removeOnPreDrawListener(this);
                alphabetTextBounds=new Rect();
                alphabetTextBounds.left=alphabet.getLeft();
                alphabetTextBounds.right=alphabet.getRight();
                alphabetTextBounds.bottom=alphabet.getBottom();
                alphabetTextBounds.top=alphabet.getTop();

                return true;
            }
        });

        mValue.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mValue.getViewTreeObserver().removeOnPreDrawListener(this);
                valueTextBounds=new Rect();
                valueTextBounds.left=mValue.getLeft();
                valueTextBounds.right=mValue.getRight();
                valueTextBounds.bottom=mValue.getBottom();
                valueTextBounds.top=mValue.getTop();
                return true;
            }
        });


    }

    private void showViews(){


        ObjectAnimator saveAnimator=ObjectAnimator.ofFloat(mSave,"alpha",0f,1f);
        ObjectAnimator removeAnimator=ObjectAnimator.ofFloat(mSubtract,"alpha",0f,1f);
        ObjectAnimator addAnimator=ObjectAnimator.ofFloat(mAdd,"alpha",0f,1f);
        mSave.setVisibility(View.VISIBLE);
        mAdd.setVisibility(View.VISIBLE);
        mSubtract.setVisibility(View.VISIBLE);
        saveAnimator.start();
        removeAnimator.start();
        addAnimator.start();


    }

    @Override
    public void onBackPressed() {
        hideViews();
    }
    

    private void hideViews(){

        //if the user changed the number using + or -, but didnt saved it and pressed back, we should
        //check for that and restore the number taking the color of the text into account
        // to original state before transition

        if(!isReturning) {
            mValue.setText(Integer.toString(value.getCurrentValue()));
            if(value.getCurrentValue()!=value.getDefaultValue())
                mValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            else
                mValue.setTextColor(defaultTextColor);
        }else {
            //doing this to notify the mainActivity on ActivityReenter that the value has changed by user
            Intent data=new Intent();
            data.putExtra(MainActivity.KEY_CURRENT_VALUE,value.getCurrentValue());
            setResult(Activity.RESULT_OK,data);
        }
        ObjectAnimator saveAnimator=ObjectAnimator.ofFloat(mSave,"alpha",1f,0f);
        ObjectAnimator removeAnimator=ObjectAnimator.ofFloat(mSubtract,"alpha",1f,0f);
        ObjectAnimator addAnimator=ObjectAnimator.ofFloat(mAdd,"alpha",1f,0f);

        AnimatorSet set=new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAdd.setVisibility(View.INVISIBLE);
                mSubtract.setVisibility(View.INVISIBLE);
                mSave.setVisibility(View.INVISIBLE);
                finishAfterTransition();
            }
        });
        set.playTogether(saveAnimator,removeAnimator,addAnimator);
        set.start();

    }

    private void initializeTransitions(){

        getWindow().setEnterTransition(new Fade());
        Fade fadeOut=new Fade();
        fadeOut.setMode(Fade.MODE_OUT);
        getWindow().setReturnTransition(fadeOut);
        Transition trans=TransitionInflater.from(this).inflateTransition(R.transition.textviewshared);
        getWindow().setSharedElementEnterTransition(trans);

    }


}
