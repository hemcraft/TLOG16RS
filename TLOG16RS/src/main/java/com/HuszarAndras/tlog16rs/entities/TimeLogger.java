/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.entities;

import com.HuszarAndras.tlog16rs.core.tlog16java.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;
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
@Slf4j
@Entity
@Table(name = "time_logger")
public class TimeLogger {
    @Id
    @Column(name = "id") @GeneratedValue
    private int id;
    //private static transient AtomicInteger uniqueId=new AtomicInteger();
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<WorkMonth> months;
    
    public TimeLogger() {
        months = new ArrayList<>();
        //id = 1;
    }
    
    public WorkMonth getWorkMonth(int i){
        return months.get(i);
    }
    
    public int getSize(){
        return months.size();
    }
    
    public List<WorkMonth> getMonthList(){
        return months;
    }
    
    /**
     * checks if given month is alread in the list
     * @param wm
     * @return 
     */
    public boolean isNewMonth(WorkMonth wm){
        for(int i = 0; i < months.size(); i++){
            if(wm.getYear() == months.get(i).getYear()){
                if(wm.getMonthValue() == months.get(i).getMonthValue())
                    return false;
            }
        }
        return true;
    }
    
    /**
     * adss a month to the list
     * @param wm
     * @throws NotNewMonthException already stored
     */
    public void addMonth(WorkMonth wm) throws NotNewMonthException{
        if(isNewMonth(wm)){
            months.add(wm);
            Counter.monthCounter++;
        }
        else{
            log.error("this month is alreardy in the calendar");
            throw new NotNewMonthException("not new month");
        }
    }
    
    public void listMonths(){
            for(int j = 0; j < getSize(); j++){
                System.out.println(j + ": " + getWorkMonth(j).getDate().toString() + ",");
            }
        }
    
    public void listDays(int i){
        months.get(i).writeDays();
    }
    
    public void listTasks(int i, int j){
        months.get(i).getDayList().get(j).writeTasks();
    }
    
    /**
     * adds task to the calendar
     * @param whichMonth
     * @param whichDay
     * @param taskId
     * @param comment
     * @param startTime
     * @throws EmptyTimeFieldException if null
     * @throws InvalidTaskIdException if wrong format
     * @throws NoTaskIdException if taskId null
     * @throws NotSeparatedTimesException tasks have common time intervals
     * @throws NotExpectedTimeOrderException if endTime before startTime
     */
    public void addTask(int whichMonth, int whichDay, String taskId, String comment, String startTime) throws EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, NotExpectedTimeOrderException{
        Task t = new Task(taskId);
        t.setComment(comment);
        t.setStartTime(startTime);
        if(!months.get(whichMonth).getDayList().get(whichDay).getTaskList().isEmpty())
            System.out.println(months.get(whichMonth).getDayList().get(whichDay).latestTaskEndTime());
        months.get(whichMonth).getDayList().get(whichDay).addTask(t);
    }
    
    /**
     * finished task by giving it an endTime
     * @param whichMonth
     * @param whichDay
     * @param whichTask
     * @param endTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException 
     */
    public void finishTask(int whichMonth, int whichDay, int whichTask, String endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        Task temp = months.get(whichMonth).getDayList().get(whichDay).getTaskList().get(whichTask);
        temp.setEndTime(endTime);
    }
    
    /**
     * lists unfinished tasks
     */
    public void showUnfinishedTasks(int whichMonth, int whichDay){
        months.get(whichMonth).getDayList().get(whichDay).listUnfinishedTasks();
    }
    /**
     * deletes given task
     * @param whichMonth
     * @param whichDay
     * @param whichTask 
     */
    public void deleteTask(int whichMonth, int whichDay, int whichTask){
        months.get(whichMonth).getDayList().get(whichDay).getTaskList().remove(whichTask);
    }
    
    /**
     * sets all the field of the task, if left empty then nothing changes
     * @param whichMonth
     * @param whichDay
     * @param whichTask
     * @throws NotExpectedTimeOrderException endTime before startTime
     * @throws InvalidTaskIdException wring format
     * @throws EmptyTimeFieldException thrown if null
     * @throws NoTaskIdException taskId null
     */
    public void setTaskFields(int whichMonth, int whichDay, int whichTask) throws NotExpectedTimeOrderException, InvalidTaskIdException, EmptyTimeFieldException, NoTaskIdException{
        Scanner sc = new Scanner(System.in);
        Task t = months.get(whichMonth).getDayList().get(whichDay).getTaskList().get(whichTask);
        System.out.println("What is the taskId?");
        System.out.println("[" + t.getTaskId() + "]");
        String taskId = sc.nextLine();
        if(!taskId.isEmpty())
            t.setTaskId(taskId);
        System.out.println("What does the task do?");
        System.out.println("[" + t.getComment() + "]");
        String comment = sc.nextLine();
        if(!comment.isEmpty())
            t.setComment(comment);
        System.out.println("When does the task start?");
        System.out.println("[" + t.getStartTime() + "]");
        String startTime = sc.nextLine();
        if(!startTime.isEmpty())
            t.setStartTime(startTime);
        System.out.println("When does the task end?");
        System.out.println("[" + t.getEndTime() + "]");
        String endTime = sc.nextLine();
        if(!endTime.isEmpty())
            t.setEndTime(endTime);
            
    }
    
    public void writeStatistics(int whichMonth) throws EmptyTimeFieldException{
        long sumPerMonth = months.get(whichMonth).getSumPerMonth();
        long requiredMinPerMonth = months.get(whichMonth).getRequiredMinPerMonth();
        System.out.println("Statistics: sumPerMonth: " 
                + sumPerMonth 
                + ", requiredMinPerMonth: " 
                + requiredMinPerMonth);
        
        System.out.println();
        for(int i = 0; i < months.get(whichMonth).getDayList().size(); i++){
            System.out.println(i + " : " + " requiredMinsPerDay " 
                    +  months.get(whichMonth).getDayList().get(i).getRequiredMinPerDay()
                    + " sumPerDay: " 
                    + months.get(whichMonth).getDayList().get(i).getSumPerDay());
        }
    }

    public void deleteMonths() {
        months.clear();
    }
}
