/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.entities;

import com.HuszarAndras.tlog16rs.core.tlog16java.*;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewDateException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotTheSameMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.WeekendNotEnabledException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andris
 */

@lombok.Getter
@lombok.Setter
@Slf4j
@Entity
@Table(name = "work_month")
public class WorkMonth {
    @Id
    @Column(name = "id") @GeneratedValue
    private int id;
    //private static transient AtomicInteger uniqueId=new AtomicInteger();
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<WorkDay> days;
    private transient YearMonth date;
    @Column(name = "new_date")
    private String newDate;
    @Column(name = "sum_per_month")
    private long sumPerMonth;
    @Column(name = "required_min_per_month")
    private long requiredMinPerMonth;
    
    public WorkMonth(int year, int month){
        this.date = YearMonth.of(year, month);
        days = new ArrayList<>();
        newDate = date.toString();
        //id = uniqueId.getAndIncrement();
    }
    
    public WorkMonth(int year, Month month){
        this.date = YearMonth.of(year, month);
        days = new ArrayList<>();
        newDate = date.toString();
        //id = uniqueId.getAndIncrement();
    }
    
    /**
     * calculates the required minutes this month
     */
    private void calculateRequiredMinPerMonth(){
        requiredMinPerMonth = 0;
        for(int i = 0; i < days.size(); i++){
            requiredMinPerMonth += days.get(i).getRequiredMinPerDay();
        }
    } 
    
    /**
     * calculates time spent working this month
     * @throws EmptyTimeFieldException 
     */
    private void calculateSumPerMonth() throws EmptyTimeFieldException{
        sumPerMonth = 0;
        for(int i = 0; i < days.size(); i++){
            sumPerMonth = sumPerMonth + days.get(i).getSumPerDay();
        }
    }
    
    public YearMonth getDate(){
        return date;
    }
    
    /**
     * gets a member of the days
     * @param i
     * @return 
     */
    public WorkDay getDays(int i){
        return days.get(i);
    }
    /**
     * gets the list of days
     * @return 
     */
    public List<WorkDay> getDayList(){
        return days;
    }
    
    /**
     * getter
     * @return
     * @throws EmptyTimeFieldException 
     */
    public long getSumPerMonth() throws EmptyTimeFieldException{
        calculateSumPerMonth();
        return sumPerMonth;
    }
    
    /**
     * getter
     * @return 
     */
    public long getRequiredMinPerMonth(){
        calculateRequiredMinPerMonth();
        return requiredMinPerMonth;
    }
    /**
     * gets the extre minutes this month
     * @return
     * @throws EmptyTimeFieldException 
     */
    public long getExtraMinPerMonth() throws EmptyTimeFieldException{
        long extraMinThisMonth = 0;
        for(int i = 0; i < days.size(); i++){
            extraMinThisMonth += days.get(i).getExtraMinPerDay();
        }
        return extraMinThisMonth;
    }
    
    /**
     * checks if given date is already stored
     * @param workDay
     * @return 
     */
    public boolean isNewDate(WorkDay workDay){
            for(int i = 0; i < days.size(); i++){
                if(workDay.getActualDay().equals(days.get(i).getActualDay()))
                    return false;
            }
            return true;
    }
    
    /**
     * checks if date belongs to month
     * @param workDay
     * @return 
     */
    public boolean isSameMonth(WorkDay workDay){
        return (date.getMonthValue() == workDay.getActualDay().getMonthValue());          
    }
    
    /**
     * adds day to the list of days
     * @param wd
     * @param isWeekendEnabled
     * @throws WeekendNotEnabledException thrown if weekend is not enabled
     * @throws NotNewDateException thrown if date already stored
     * @throws NotTheSameMonthException thrown if date doesn't belong to month
     */
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        boolean weekendDayToo = (isWeekendEnabled == true) && isSameMonth(wd) && isNewDate(wd);
        boolean weekDayOnly = (isWeekendEnabled == false) && (Util.isWeekDay(wd.getActualDay())) && isSameMonth(wd) && isNewDate(wd);
        if(isWeekendEnabled == false)
        {
            log.error("weekend is not enabled");
            throw new WeekendNotEnabledException("weekend not enabled");
        }
        if(!isNewDate(wd)){
            log.error("this date is already in the calendar");
            throw new NotNewDateException("not new date");
        }
        if(!isSameMonth(wd)){
            log.error("the given day does not belong to the selected month/year");
            throw new NotTheSameMonthException("not the same month");
        }
        if(weekendDayToo || weekDayOnly){
            days.add(wd);
            Counter.dayCounter++;
        }
    }
    
    /**
     * adds day to the list of days
     * @param wd
     * @param isWeekendEnabled
     * @throws NotNewDateException thrown if date already stored
     * @throws NotTheSameMonthException thrown if date doesn't belong to month
     */
    public void addWorkDay(WorkDay wd) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        boolean isWeekendEnabled = false;
        boolean weekDayOnly = (Util.isWeekDay(wd.getActualDay())) && isSameMonth(wd) && isNewDate(wd);
        if(!isNewDate(wd)){
            log.error("this date is already in the calendar");
            throw new NotNewDateException("not new date");
        }
        if(!isSameMonth(wd)){
            log.error("the given day does not belong to the selected month/year");
            throw new NotTheSameMonthException("not the same month");
        }
        if(weekDayOnly){
            days.add(wd);
            Counter.dayCounter++;
        }
    }
    
    /**
     * writes to the console
     */
    public void writeDays(){
        for(int i = 0; i < days.size(); i++){
            System.out.println(i + ": " + days.get(i).getActualDay().toString());
        }
    }
    
    public String writeStatistics() throws EmptyTimeFieldException{
        String answer = date.toString();
        for(int i = 0; i < days.size(); i++){
            answer = answer + " " + days.get(i).getActualDay();
            for(int j = 0; j < days.get(i).getTaskList().size(); j++){
                Task t = days.get(i).getTaskList().get(j);
                answer = answer + t.getTaskId() + " " + t.getComment() + "" + t.getStartTime() + " " + t.getEndTime()
                         + " " + t.getMinPerTask() + "\n";
            }
            answer = answer + "\n";
        }
        answer = answer + "\n";
        return answer;
    }
    
    public int getYear(){
        String temp = StringUtils.substring(newDate, 0, 4);
        return Integer.parseInt(temp);
    }
    
    public int getMonthValue(){
        String temp = StringUtils.substring(newDate, 5, 7);
        return Integer.parseInt(temp);
    }
}
