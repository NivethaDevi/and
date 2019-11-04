package com.example.payright;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Prediction  {
    LinkedHashMap<String,LinkedHashMap<String,Integer>> inputData = new LinkedHashMap<>();
    LinkedHashMap<String,Integer> predictedData = new LinkedHashMap<>();
    LinkedHashMap<String,int [][]> predictedMonthlyData = new LinkedHashMap<>();
    int yearstoadd;
    int currentMonth;
    int currentYear;
    Integer salary;

    Prediction(LinkedHashMap<String,LinkedHashMap<String,Integer>> inputData,Integer salary, int yearstoadd, int currentMonth, int currentYear){
        this.inputData = inputData;
        this.salary = salary;
        this.yearstoadd = yearstoadd;
        this.currentMonth = currentMonth;
        this.currentYear = currentYear;
    }
    public LinkedHashMap predictEMI(){
        for (LinkedHashMap.Entry<String,LinkedHashMap<String,Integer>> entry : inputData.entrySet()){
            LinkedHashMap<String,Integer> fA;
            fA = entry.getValue();
            int amount;
            int emiAmount;
            if(entry.getKey().toString().toLowerCase().contains("emergency")){
                amount = (int)(0.12*salary);
            }
            else if(entry.getKey().toString().toLowerCase().contains("fd")){
                amount = (int)(0.06*salary);
            }
            else{
                amount =fA.get("amount");
            }
            predictedData.put(entry.getKey(),amount);

        }
        return predictedData;
    }
    public LinkedHashMap predictEMIMonthly(){
        for (LinkedHashMap.Entry<String,LinkedHashMap<String,Integer>> entry : inputData.entrySet()){
            LinkedHashMap<String,Integer> fA;
            fA = entry.getValue();
            int amount;
            int emiAmount;
            if(entry.getKey().toString().toLowerCase().contains("emergency")){
                amount = (int)(0.12*salary);
            }
            else if(entry.getKey().toString().toLowerCase().contains("fd")){
                amount = (int)(0.06*salary);
            }
            else{
                amount =fA.get("amount");
            }
            predictedMonthlyData.put(entry.getKey(),calculateAmount(fA.get("frequency"), amount, fA.get("month"),fA.get("year")));

        }
        return predictedMonthlyData;
    }

    public int[][] calculateAmount(int frequency, int amount, int month, int year){
        System.out.println(currentYear+yearstoadd);
        int[][] amt = new int[currentYear+yearstoadd+2][13];
        int i;
        for (int j = currentYear; j <= currentYear + yearstoadd; j++){
            for (i = 0; i < 12; i++) {
                System.out.println(j);
                amt[j][i] = 0;
            }
        }
        switch (frequency) {
            case 0:
                amt[year][month] = amount;
                break;
            case 1:
                for (i = month; i < 12; i++) {
                    amt[year][i] = amount;
                }
                for (int j = year + 1; j <= currentYear + yearstoadd; j++){
                    for (i = 0; i < 12; i++) {
                        amt[j][i] = amount;
                    }
                }
                break;
            case 96:
//                Calendar target = new GregorianCalendar(year, month, 19);
//                Calendar today = new GregorianCalendar();
                int year_diff = year - currentYear;
                int month_diff = month - currentMonth;
//                int yearsInBetween =
//                        target.get(Calendar.YEAR) - today.get(Calendar.YEAR);
//                int monthsDiff =
//                        target.get(Calendar.MONTH)-today.get(Calendar.MONTH);
                if(month_diff<0){
                    month_diff = 12-month_diff;
                }
                int totalMonths = (year_diff*12)+month_diff;
                amount = amount/totalMonths;
                for (i = currentMonth; i <12; i++) {
                    amt[currentYear][i] = amount;
                }
                for (int j = 0; j <year-currentYear; j++){
                    int condition = 12;
                    if(j==year-currentYear-1){
                        condition = month;
                    }
                    for (i = 0; i <= month; i++) {
                        System.out.println(i+":i:j:"+j);
                        amt[currentYear+j+1][i] = amount;
                    }
                }
                break;
                default:
                    for (i = month; i < 12; i++) {
                        amt[year][i] = amount/frequency;
                    }
                    for (int j = year + 1; j <= currentYear + yearstoadd; j++){
                        for (i = 0; i < 12; i++) {
                            amt[j][i] = amount/frequency;
                        }
                    }
                    break;
        }
        return amt;
    }

    public LinkedHashMap<String,Integer> freqAmount(int frequency, int amount){
        LinkedHashMap<String,Integer> fA = new LinkedHashMap<>();
        fA.put("frequency",frequency);
        fA.put("amount",amount);
        return fA;
    }

}
