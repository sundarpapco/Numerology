package com.papco.sundar.numerology;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThemeSelectionDialog extends DialogFragment {

    View greecyGreen,shadesOfSun,sandySea,radiumNight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.theme_selection,container,false);
        greecyGreen=view.findViewById(R.id.theme_button_greecy_green);
        shadesOfSun=view.findViewById(R.id.theme_button_shades_of_sun);
        sandySea=view.findViewById(R.id.theme_button_sandy_sea);
        radiumNight=view.findViewById(R.id.theme_button_radium_night);

        greecyGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(MainActivity.THEME_GREECY_GREEN);
            }
        });

        shadesOfSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(MainActivity.THEME_SHADES_OF_SUN);
            }
        });

        sandySea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(MainActivity.THEME_SANDY_SEA);
            }
        });

        radiumNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(MainActivity.THEME_RADIUM_NIGHT);
            }
        });

        loadCurrentTheme();

        return view;
    }

    private void loadCurrentTheme() {

        SharedPreferences pref=getActivity().getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        int currentTheme=pref.getInt(MainActivity.KEY_THEME,MainActivity.DEFAULT_THEME);
        switch (currentTheme){
            case MainActivity.THEME_GREECY_GREEN:
                greecyGreen.setActivated(true);
                greecyGreen.setElevation(15);
                break;

            case MainActivity.THEME_SHADES_OF_SUN:
                shadesOfSun.setActivated(true);
                shadesOfSun.setElevation(15);
                break;

            case MainActivity.THEME_SANDY_SEA:
                sandySea.setActivated(true);
                sandySea.setElevation(15);
                break;

            case MainActivity.THEME_RADIUM_NIGHT:
                radiumNight.setActivated(true);
                radiumNight.setElevation(15);
        }

    }

    private void selectTheme(int themeId){

        SharedPreferences pref=getActivity().getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        pref.edit().putInt(MainActivity.KEY_THEME,themeId).commit();
        dismiss();
        getActivity().recreate();

    }
}
