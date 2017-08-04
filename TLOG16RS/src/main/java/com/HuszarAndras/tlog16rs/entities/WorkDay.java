/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.entities;

import com.HuszarAndras.tlog16rs.core.tlog16java.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.FutureWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NegativeMinutesOfWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;
import static com.HuszarAndras.tlog16rs.core.tlog16java.Util.isSeparatedTime;
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

/**
 *
 * @author Andris
 */

@lombok.Getter
@lombok.Setter
@Slf4j
@Entity
@Table(name = "work_day")
public class WorkDay {
    @Id
    @Column(name = "id")
    private int id;
    private static transient AtomicInteger uniqueId=new AtomicInteger();
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Task> tasks;
    @Column(name = "required_min_per_day")
    private long requiredMinPerDay = 450;
    @Column(name = "actual_day")
    private LocalDate actualDay = LocalDate.now();
    @Column(name = "sum_per_day")
    private long sumPerDay;
    
    public WorkDay(long requiredMinPerDay, LocalDate actualDay) throws NegativeMinutesOfWorkException, FutureWorkException{
        id = uniqueId.getAndIncrement();
        if(requiredMinPerDay < 0){
            log.error("requiredMinPerDay cannot be less then zero");
            throw new NegativeMinutesOfWorkException("can't be negative");
        }
        if(actualDay.isAfter(LocalDate.now())){
            log.error("actualDay cannot be in the future");
            throw new FutureWorkException("can't be in the future");
        }
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = actualDay;
        tasks = new ArrayList<>();
    }
    
    public WorkDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException{
        id = uniqueId.getAndIncrement();
        if(requiredMinPerDay < 0){
            log.error("requiredMinPerDay cannot be less then zero");
            throw new NegativeMinutesOfWorkException("can't be negative");
        }
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = LocalDate.now();
        tasks = new ArrayList<>();
    }
    
    public WorkDay(){
        id = uniqueId.getAndIncrement();
        this.requiredMinPerDay = 450;
        this.actualDay = LocalDate.now();
        tasks = new ArrayList<>();
    }
    
    /**
     * gets a member of the tasks
     * @param i
     * @return 
     */
    public Task getTasks(int i){
        return tasks.get(i);
    }
    
    /**
     * gets the list of tasks
     * @return 
     */
    public List<Task> getTaskList(){
        return tasks;
    }
    
    /**
     * calculates the sumPerDay
     * @throws EmptyTimeFieldException 
     */
    private void calculateSumPerDay() throws EmptyTimeFieldException{
        sumPerDay = 0;
        for(int i = 0; i < tasks.size(); i++){
            sumPerDay = sumPerDay + tasks.get(i).getMinPerTask();
        }
    }
    
    /**
     * getter
     * @return
     * @throws EmptyTimeFieldException 
     */
    public long getSumPerDay() throws EmptyTimeFieldException{
        calculateSumPerDay();
        return sumPerDay;
    }
    
    /**
     * gets the extra minutes done this day
     * @return
     * @throws EmptyTimeFieldException 
     */
    public long getExtraMinPerDay() throws EmptyTimeFieldException{
        return (getSumPerDay() - requiredMinPerDay);
    }
    
    /**
     * sets requiredMinPerDay if it's greater than zero
     * @param min
     * @throws NegativeMinutesOfWorkException 
     */
    public void setRequiredMinPerDay(long min) throws NegativeMinutesOfWorkException{
        if(min < 0){
            log.error("requiredMinPerDay cannot be less then zero");
            throw new NegativeMinutesOfWorkException("can't be negative");
        }
        requiredMinPerDay = min;
    }
    
    /**
     * sets actualDay 
     * @param year
     * @param month
     * @param day
     * @throws FutureWorkException thrown if future date is given
     */
    public void setActualDay(int year, int month, int day) throws FutureWorkException{
        if(LocalDate.of(year, month, day).isAfter(LocalDate.now())){
            log.error("actualDay cannot be in the future");
            throw new FutureWorkException("can't be in the future");
        }
        actualDay = LocalDate.of(year, month, day);
    }
    
    /**
     * adds task to the list of tasks if it doesn't have common time interval with the other tasks
     * @param t
     * @throws NotSeparatedTimesException 
     */
    public void addTask(Task t) throws NotSeparatedTimesException{
        if(Util.isSeparatedTime(t, this.tasks)){
            tasks.add(t);
            Counter.taskCounter++;
        }
        else{
            log.error("the added task is not separated from the other tasks");
            throw new NotSeparatedTimesException("tasks have common parts");
        }
    }
    
    /**
     * gets the endTime of the task last given
     * @return 
     */
    public LocalTime latestTaskEndTime(){
        if(!tasks.isEmpty())
            return tasks.get(tasks.size() - 1).getEndTime();
        return null;
    }
    
    /**
     * writes tasks to te console
     */
    public void writeTasks(){
        for(int i = 0; i < tasks.size(); i++){
            System.out.println(i + ": " + tasks.get(i).toString());
        }
    }
    
    /**
     * lists infinished tasks
     */
    public void listUnfinishedTasks(){
        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).isUnfinished()){
                System.out.println(i + ": " + tasks.get(i).toString());
            }
        }
    }
    
    public void deleteTaskAt(int i){
        tasks.remove(i);
    }
    
    public String listTasks(){
        String temp = "";
        temp = actualDay.toString();
        for(int i = 0; i < tasks.size(); i++){
            temp = temp  + "\n " + tasks.get(i).toString();
        }
        return temp;
    }
}
