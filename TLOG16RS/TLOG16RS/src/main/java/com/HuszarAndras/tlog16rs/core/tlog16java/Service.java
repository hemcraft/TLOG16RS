/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.FutureWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewDateException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotTheSameMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.WeekendNotEnabledException;

/**
 *
 * @author Andris
 */
public class Service {
    
    public static int findOrCreateMonth(TimeLogger timeLogger, int monthIndex, int year, int month) throws NotNewMonthException{
        int counter = 0;
        
        for(int j = 0; j < timeLogger.getSize(); j++){
            if(timeLogger.getWorkMonth(j).getDate().getMonthValue() == month &&
                    timeLogger.getWorkMonth(j).getDate().getYear() == year){
                monthIndex = j;
            }
            else
                counter++;
        }
        if(counter == timeLogger.getSize()){
            WorkMonth workMonth = new WorkMonth(year, month);
            timeLogger.addMonth(workMonth);
        }
        for(int j = 0; j < timeLogger.getSize(); j++){
            if(timeLogger.getWorkMonth(j).getDate().getMonthValue() == month &&
                    timeLogger.getWorkMonth(j).getDate().getYear() == year){
                monthIndex = j;
            }
        }
        
        return monthIndex;
    }
    
    public static int findOrCreateDay(TimeLogger timeLogger, int monthIndex, int dayIndex, int year, int month, int day) throws FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        int counter = 0;
        
        for(int j = 0; j < timeLogger.getWorkMonth(monthIndex).getDayList().size(); j++){
            if(timeLogger.getWorkMonth(monthIndex).getDays(j).getActualDay().getDayOfMonth() == day){
                dayIndex = j;
            }
            else
                counter++;
        }
        if(counter == timeLogger.getWorkMonth(monthIndex).getDayList().size()){
            WorkDay workDay = new WorkDay();
            workDay.setActualDay(year, month, day);
            timeLogger.getWorkMonth(monthIndex).addWorkDay(workDay, true);
        }
        for(int j = 0; j < timeLogger.getWorkMonth(monthIndex).getDayList().size(); j++){
            if(timeLogger.getWorkMonth(monthIndex).getDays(j).getActualDay().getDayOfMonth() == day){
                dayIndex = j;
            }
        }
        
        return dayIndex;
    }
}
