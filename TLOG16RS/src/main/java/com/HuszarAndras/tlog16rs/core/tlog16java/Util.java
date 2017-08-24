/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import java.time.LocalDate;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.List;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import lombok.extern.slf4j.Slf4j;
import com.HuszarAndras.tlog16rs.entities.Task;

/**
 * 
 * @author Andris
 */
@Slf4j
public class Util {
    
    /**
     * rounds the endTime 
     * @param startTime startTime of the task
     * @param endTime endTime of the task
     * @return returns with the correct endTime
     */
    public static LocalTime roundToMultipleQuarterHour(LocalTime startTime, LocalTime endTime){        
        for(int i = 0; i <61; i = i + 15){
            if(Math.abs(MINUTES.between(endTime, startTime.plusMinutes(i))) < 8){
                endTime = startTime.plusMinutes(i);
            }
        }
        return endTime;
    }
    /**
     * checks if t task's time interval are separated from the time intevals of the other tasks
     * @param t
     * @param tasks
     * @return return true of false
     */
    public static boolean isSeparatedTime(Task t, List<Task> tasks){
        return !(tasks.stream().anyMatch(task -> task.commonParts(t)));
    }
    
    /**
     * checks if actualDay is weekday
     * @param actualDay
     * @return return true or false
     */
    public static boolean isWeekDay(LocalDate actualDay){        
        return (actualDay.getDayOfWeek().getValue() < 6);
    }
    
    /*
    public static boolean isMultipleQuarterHour(Task t) throws EmptyTimeFieldException{
        System.out.println(t.getMinPerTask());
        return (t.getMinPerTask() % 15 == 0);
    }*/
    
    /**
     * checks if the time interval between startTime and endTime is multipleQuarterHour
     * @param startTime
     * @param endTime
     * @return true of false
     * @throws EmptyTimeFieldException thrown if null
     * @throws NotExpectedTimeOrderException thrown if endTime before startTime
     */
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime) throws EmptyTimeFieldException, NotExpectedTimeOrderException{
        if(startTime == null || endTime == null){
            log.error("endTime or startTime has not been initialized");
            throw new EmptyTimeFieldException("null received");
        }
        if(startTime.isAfter(endTime)){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("not expected order");
        }
        return (((endTime.getMinute() - startTime.getMinute()) + 60 * (endTime.getHour() - startTime.getHour())) % 15 == 0);
    }
}
