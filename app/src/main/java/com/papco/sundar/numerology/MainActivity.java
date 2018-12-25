package com.papco.sundar.numerology;

import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.papco.sundar.numerology.database.MasterDatabase;
import com.papco.sundar.numerology.database.entity.AlphabatValue;
import com.papco.sundar.numerology.database.entity.Favourite;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG="SUNDAR";

    public static final String SHARED_ALPHABET_TEXT_SIZE="shared_text_size";
    public static final String SHARED_VALUE_TEXT_SIZE="shared_value_text_size";

    public static final String KEY_ID="key:alphabetId";
    public static final String KEY_DEFAULT_VALUE="key:default_value";
    public static final String KEY_CURRENT_VALUE="key:editor_value";

    MainActivityVM viewModel;

    RecyclerView recycler_alphabet,recycler_favo;
    EditText inputName;
    ImageView menuButton;
    TextView bigNumber,smallNumber;
    View starClickView;

    AlphabetAdapter alphabetAdapter;
    FavouriteAdapter favouriteAdapter;
    ItemTouchHelper dragHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel=ViewModelProviders.of(this).get(MainActivityVM.class);
        recycler_alphabet=findViewById(R.id.recycler_alphabets);
        recycler_favo=findViewById(R.id.recycler_fav);
        inputName=findViewById(R.id.input_name);
        menuButton=findViewById(R.id.ic_more);
        bigNumber=findViewById(R.id.number);
        smallNumber=findViewById(R.id.second_number);
        starClickView=findViewById(R.id.star_icon_click_view);
        
        favouriteAdapter=new FavouriteAdapter(new ArrayList<Favourite>());
        dragHelper=new ItemTouchHelper(new ItemTouchHelperCallBack(favouriteAdapter));
        recycler_favo.setLayoutManager(new LinearLayoutManager(this));
        dragHelper.attachToRecyclerView(recycler_favo);
        recycler_favo.addItemDecoration(new SpacingDecoration(this,24,24));
        recycler_favo.setAdapter(favouriteAdapter);


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        inputName.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(50)});
        inputName.addTextChangedListener(new TextWatcher() {

            NumerologyValue numerologyValue=new NumerologyValue();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                numerologyValue.calculate(s.toString(),alphabetAdapter.getData());
                bigNumber.setText(numerologyValue.getPrimaryString());
                smallNumber.setText(numerologyValue.getSecondaryString());
                if(numerologyValue.isValueChanged())
                    bouceAnimate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        starClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToFavourite();
            }
        });

        registerObservers();

        if(isFirstRun())
            addDefaultValuesToDatabase();


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        alphabetAdapter.updateLastClickedValue(data.getIntExtra(KEY_CURRENT_VALUE,-1));
        super.onActivityReenter(resultCode, data);
    }

    private void registerObservers() {

        viewModel.getAlphabatList().observe(this, new Observer<List<AlphabatValue>>() {
            @Override
            public void onChanged(@Nullable List<AlphabatValue> alphabatValues) {

                if(alphabatValues==null) {
                    return;
                }

                if(alphabetAdapter==null){
                    alphabetAdapter=new AlphabetAdapter(alphabatValues);
                    recycler_alphabet.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    recycler_alphabet.setAdapter(alphabetAdapter);
                    //When the alphabat values change, the favoutites list should also update themselfs witht the
                    //new values and thus needs to rebind. so, setting the values to that adapter here
                    favouriteAdapter.setValueList(alphabatValues);
                    return;
                }

                alphabetAdapter.setData(alphabatValues);
                //Any change in the alphabat values should trigger a recalculation in the inputname box also
                //preserving the current cursor position
                int currentCursorPosition=inputName.getSelectionStart();
                inputName.setText(inputName.getText());
                inputName.setSelection(currentCursorPosition);

                //When the alphabat values change, the favoutites list should also update themselfs witht the
                //new values and thus needs to rebind. so, setting the values to that adapter here
                favouriteAdapter.setValueList(alphabatValues);
            }
        });

        viewModel.getFavourites().observe(this, new Observer<List<Favourite>>() {
            @Override
            public void onChanged(@Nullable List<Favourite> favourites) {
                if(favourites==null)
                    return;

                favouriteAdapter.setData(favourites);
            }
        });
    }

    private void showPopupMenu(View v) {

        PopupMenu popup=new PopupMenu(this,v,Gravity.END);
        popup.getMenuInflater().inflate(R.menu.main_menu,popup.getMenu());
        //if there is no items in the favourite list, then dissble the share option
        if(favouriteAdapter.getData().size()==0){
            popup.getMenu().getItem(1).setEnabled(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // TODO: 22-12-2018 handle menu item clicks here
                switch (menuItem.getItemId()){
                    case R.id.menu_reset_values:
                        showResetConfirmationDialog();
                        return true;

                    case R.id.menu_clear_favourites:
                        showClearFavouritesConfirmationDialog();
                        return true;

                    case R.id.menu_share_with_values:
                        shareFavouriteList(true);
                        return true;

                    case R.id.menu_share_without_values:
                        shareFavouriteList(false);
                        return true;
                }


                return false;
            }
        });
        popup.show();

    }

    private boolean isFirstRun(){

        SharedPreferences pref =getSharedPreferences("mysettings",MODE_PRIVATE);
        if(pref.contains("first_run")){
            return false;
        }else{
            pref.edit().putBoolean("first_run",false).commit();
            return true;
        }

    }

    private void addToFavourite(){

        //If the value is empty string or if the name already exists, then it should not
        //add anything
        if(inputName.getText().toString().isEmpty())
            return;

        if(favouriteAdapter.hasName(inputName.getText().toString())){
            Toast.makeText(this,"Name already in favourites",Toast.LENGTH_SHORT).show();
            return;
        }

        //The priority should be 0 if this is the first favourite item
        //Else, the priority should be the priority of the last item in the list+1
        //so that this item will be added in the last position of the list
        int priority;
        if(favouriteAdapter.getData().size()==0)
            priority=0;
        else
            priority=favouriteAdapter.getData().get(favouriteAdapter.getData().size()-1).getPriority()+1;

        Favourite fav=new Favourite(inputName.getText().toString().trim(),priority);
        viewModel.addFavourite(fav);
        inputName.setText("");

    }

    private void showResetConfirmationDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Reset values");
        builder.setMessage("Sure want to reset all numbers to its default value?");
        builder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.resetAlphabetValues();
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();

    }

    private void showClearFavouritesConfirmationDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Clear favourites");
        builder.setMessage("Are you sure want to clear all the favourites?");
        builder.setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.clearFavourites();
            }
        });
        builder.setNegativeButton("CANCEL",null);
        builder.show();

    }

    private void startEditorActivity(View itemView,AlphabatValue value){

        TextView text=itemView.findViewById(R.id.lbl_alphabat);
        TextView valueText=itemView.findViewById(R.id.lbl_value);
        View background=itemView.findViewById(R.id.alphabat_list_item);

        Intent intent=new Intent(this,EditorActivity.class);
        intent.putExtra(SHARED_ALPHABET_TEXT_SIZE,text.getTextSize());
        intent.putExtra(SHARED_VALUE_TEXT_SIZE,valueText.getTextSize());
        intent.putExtra(KEY_CURRENT_VALUE,value.getCurrentValue());
        intent.putExtra(KEY_DEFAULT_VALUE,value.getDefaultValue());
        intent.putExtra(KEY_ID,value.getId());
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create((View)valueText,"trans:editor_value"),
                Pair.create(background,"trans:container"),
                Pair.create((View)text,"trans:editor_alphabet"));
        startActivity(intent,options.toBundle());

    }

    private void addDefaultValuesToDatabase(){

        viewModel.addDefaultAlphabets();
    }

    private void bouceAnimate(){

        bigNumber.setScaleX(0.6f);
        bigNumber.setScaleY(0.6f);
        smallNumber.setScaleY(0.6f);
        smallNumber.setScaleX(0.6f);
        SpringAnimation animX=new SpringAnimation(bigNumber,DynamicAnimation.SCALE_X,1);
        animX.setMaxValue(2.6f);
        animX.setMinValue(0.6f);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        SpringAnimation animY=new SpringAnimation(bigNumber,DynamicAnimation.SCALE_Y,1);
        animX.setMaxValue(2.6f);
        animX.setMinValue(0.6f);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        SpringAnimation animSmallX=new SpringAnimation(smallNumber,DynamicAnimation.SCALE_X,1);
        animX.setMaxValue(2.6f);
        animX.setMinValue(0.6f);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        SpringAnimation animSmallY=new SpringAnimation(smallNumber,DynamicAnimation.SCALE_Y,1);
        animX.setMaxValue(2.6f);
        animX.setMinValue(0.6f);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        animX.start();
        animY.start();
        animSmallX.start();
        animSmallY.start();
    }

    private void shareFavouriteList(boolean withValues){

        String stringToShare=prepareListToShare(withValues);
        if(stringToShare==null)
            return;

        Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,stringToShare);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share list via.."));

    }

    private String prepareListToShare(boolean withValues){

        List<Favourite> list=favouriteAdapter.getData();
        if(list.size()==0)
            return null;

        NumerologyValue val=new NumerologyValue();
        String result="";

        for(Favourite fav:list){
            if(!withValues){
                result=result+fav.getName()+"\n";
                continue;
            }
            val.calculate(fav.getName(),alphabetAdapter.getData());
            result=result+fav.getName()+", "+ val.getPrimaryString()+"("+val.getSecondaryString()+")\n";

        }

        return result;
    }

    class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.ValueHolder>{

        @ColorInt int defaultValueColor;
        private List<AlphabatValue> data;
        private int lastClickedPosition=-1;

        public AlphabetAdapter(List<AlphabatValue> data){
            this.data=data;

            TypedValue typedValue=new TypedValue();
            getTheme().resolveAttribute(R.attr.editorValueNumber,typedValue,true);
            defaultValueColor=typedValue.data;
        }

        @NonNull
        @Override
        public ValueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view=getLayoutInflater().inflate(R.layout.alphabat_list_item,viewGroup,false);
            return new ValueHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull AlphabetAdapter.ValueHolder valueHolder, int position) {
            valueHolder.bindValues();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<AlphabatValue> newData){

            this.data=newData;
            notifyDataSetChanged();
        }

        public void updateLastClickedValue(int value){
            AlphabatValue alphabatValue=data.get(lastClickedPosition);
            alphabatValue.setCurrentValue(value);
            ValueHolder holder=(ValueHolder)recycler_alphabet.findViewHolderForAdapterPosition(lastClickedPosition);
            if(holder!=null) {
                holder.value.setText(Integer.toString(value));
                if(alphabatValue.getCurrentValue()==alphabatValue.getDefaultValue())
                    holder.value.setTextColor(defaultValueColor);
                else
                    holder.value.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

        }

        public List<AlphabatValue> getData() {
            return data;
        }



        class ValueHolder extends RecyclerView.ViewHolder{

            TextView alphabet,value;
            View backgroundView;
            char baseChar='A';

            public ValueHolder(@NonNull final View itemView) {
                super(itemView);
                alphabet=itemView.findViewById(R.id.lbl_alphabat);
                value=itemView.findViewById(R.id.lbl_value);
                backgroundView=itemView.findViewById(R.id.alphabat_list_item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lastClickedPosition=getAdapterPosition();
                        startEditorActivity(itemView,data.get(getAdapterPosition()));
                    }
                });
            }

            public void bindValues(){

                AlphabatValue val=data.get(getAdapterPosition());
                alphabet.setText(String.valueOf((char)(baseChar+getAdapterPosition())));
                value.setText(val.getCurrentValueString());
                if(val.getCurrentValue()!=val.getDefaultValue())
                    value.setTextColor(MainActivity.this.getResources().getColor(android.R.color.holo_red_dark));
                else
                    value.setTextColor(defaultValueColor);

                /*if(invisibleUpdate){
                    invisibleUpdate=false;
                    alphabet.setVisibility(View.INVISIBLE);
                    value.setVisibility(View.INVISIBLE);
                }*/


            }
        }
    }

    class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavViewHolder> implements ItemTouchHelperCallBack.DragCallBack{

        private List<Favourite> data;
        private List<AlphabatValue> valueList;
        NumerologyValue numerologyValue=new NumerologyValue();

        public FavouriteAdapter(List<Favourite> data){
            this.data=data;
           setHasStableIds(true);
        }

        @NonNull
        @Override
        public FavViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view=getLayoutInflater().inflate(R.layout.fav_list_item,viewGroup,false);
            return new FavViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull FavViewHolder favViewHolder, int i) {
            favViewHolder.bind();
        }

        @Override
        public int getItemCount() {

            //this adapter should start binding only when there is a valid data and valueList.
            //It should not bid anything if any one of the things is missing
            if(data!=null && valueList!=null)
                return data.size();
            else
                return 0;
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).getId();
        }

        public void setData(List<Favourite> data) {
            this.data=data;
            notifyDataSetChanged();
        }

        public void setValueList(List<AlphabatValue> valueList) {
            this.valueList = valueList;
            notifyDataSetChanged();
        }

        public boolean hasName(String name){

            name=name.toLowerCase().trim();
            for(Favourite fav:data){

                if(fav.getName().toLowerCase().equals(name)){
                    return true;
                }

            }
            return false;
        }

        public List<Favourite> getData() {
            return data;
        }

        @Override
        public void onDragging(int fromPosition, int toPosition) {
            Favourite fav=data.get(toPosition);
            data.set(toPosition,data.get(fromPosition));
            data.set(fromPosition,fav);
            notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            int draggedPriority;
            List<Favourite> listToUpdate=new ArrayList<>();
            if(fromPosition<toPosition){
                draggedPriority=data.get(toPosition).getPriority();
                for(int i=toPosition;i>fromPosition;--i){
                    data.get(i).setPriority(data.get(i-1).getPriority());
                    listToUpdate.add(data.get(i));
                }
                data.get(fromPosition).setPriority(draggedPriority);
                listToUpdate.add(data.get(fromPosition));
            }else{
                draggedPriority=data.get(toPosition).getPriority();
                for(int i=toPosition;i<fromPosition;++i){
                    data.get(i).setPriority(data.get(i+1).getPriority());
                    listToUpdate.add(data.get(i));
                }
                data.get(fromPosition).setPriority(draggedPriority);
                listToUpdate.add(data.get(fromPosition));
            }
            viewModel.updateFavourites(listToUpdate);

        }

        @Override
        public void onSwiped(int position) {
            viewModel.deleteFavourite(data.get(position));
        }

        class FavViewHolder extends RecyclerView.ViewHolder{

            TextView favName,favValue;
            ImageView dragHandle;

            public FavViewHolder(@NonNull View itemView) {
                super(itemView);
                favName=itemView.findViewById(R.id.fav_name);
                favValue=itemView.findViewById(R.id.fav_value);
                dragHandle=itemView.findViewById(R.id.fav_drag_handle);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputName.setText(data.get(getAdapterPosition()).getName());
                        inputName.setSelection(inputName.length());
                        inputName.requestFocus();
                    }
                });
                dragHandle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if(event.getAction()==MotionEvent.ACTION_DOWN)
                            dragHelper.startDrag(FavViewHolder.this);

                        return false;
                    }
                });
            }

            public void bind(){
                favName.setText(data.get(getAdapterPosition()).getName());
                /*numerologyValue.calculate(data.get(getAdapterPosition()).getName(),valueList);
                String val=numerologyValue.getPrimaryString()+"("+numerologyValue.getSecondaryString()+")";
                favValue.setText(val);*/
                favValue.setText(Integer.toString(data.get(getAdapterPosition()).getPriority()));
            }
        }
    }
}
