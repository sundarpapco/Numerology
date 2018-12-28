package com.papco.sundar.numerology;

import com.papco.sundar.numerology.database.entity.AlphabatValue;

import java.util.List;

public class NumerologyValue {

    private int primary,secondary;
    private boolean valueChanged=false;

    public NumerologyValue(){

        primary=0;
        secondary=0;

    }

    public int getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

    public String getPrimaryString(){
        return Integer.toString(primary);
    }

    public String getSecondaryString(){
        return Integer.toString(secondary);
    }

    public boolean isValueChanged() {
        return valueChanged;
    }

    private int valueOfName(String name, List<AlphabatValue> list){

        int value=0;
        name=name.toUpperCase();
        char baseChar='A';
        for(int i=0;i<name.length();++i){
            for(int j=0;j<26;++j){
                if(name.charAt(i)==(char)(baseChar+j)){
                    value=value+list.get(j).getCurrentValue();
                    continue;
                }
            }
        }
        return value;

    }

    public void calculate(String name,List<AlphabatValue> list){

        if(name==null || list==null){
            return;
        }

        int oldPrimary=primary;

        primary=valueOfName(name,list);
        secondary=primary;
        int quotient;
        int remainder;

        while(primary>9) {
            quotient=primary;
            remainder=0;
            while (quotient != 0) {
                remainder = remainder + (quotient % 10);
                quotient = quotient / 10;
            }
            primary = remainder;
            if(primary>9)
                secondary=primary;
        }


        if(primary!=oldPrimary)
            valueChanged=true;
        else
            valueChanged=false;
    }

}
