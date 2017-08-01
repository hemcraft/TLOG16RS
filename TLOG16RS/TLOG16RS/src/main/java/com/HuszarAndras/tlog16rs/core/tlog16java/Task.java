/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import java.time.LocalTime;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import static com.HuszarAndras.tlog16rs.core.tlog16java.Util.roundToMultipleQuarterHour;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 *Task that the user has to finish
 * @author Andris
 */
@lombok.Getter
@lombok.Setter
@Slf4j
@Entity
public class Task {
    private int id;
    private String taskId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String comment;
    
    public Task(String taskId, int startHour, int startMin, int endHour, int endMin, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        id++;
        this.taskId = taskId;
        if(taskId == ""){
            log.error("taskId cannot be empty");
            throw new NoTaskIdException("taskId cant be empty");
        }
        if(!isValidTaskId()){
            log.error("taskId is invalid");
            throw new InvalidTaskIdException("wrong format");
        }
        this.comment = comment;
        this.startTime.of(startHour, startMin);
        if(this.getStartTime().isAfter(LocalTime.of(endHour, endMin))){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("endTime before startTime");
        }
        this.endTime.of(endHour, endMin);
        if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
            this.endTime = roundToMultipleQuarterHour(this.getStartTime(), LocalTime.of(endHour, endMin));
        }
    }
    
    public Task(String taskId, String startTime, String endTime, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        id++;
        this.taskId = taskId;
        if(taskId == ""){
            log.error("taskId cannot be empty");
            throw new NoTaskIdException("taskId cant be empty");
        }
        if(!isValidTaskId()){
            log.error("taskId is invalid");
            throw new InvalidTaskIdException("wrong format");
        }
        this.comment = comment;
        this.startTime = LocalTime.parse(startTime);
        if(this.getStartTime().isAfter(LocalTime.parse(endTime))){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("endTime before startTime");
        }
        this.endTime = LocalTime.parse(endTime);
        if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
            this.endTime = roundToMultipleQuarterHour(this.getStartTime(), LocalTime.parse(endTime));
        }
    }
    
    public Task(String taskId) throws InvalidTaskIdException, NoTaskIdException{
        id++;
        this.taskId = taskId;
        if(taskId == ""){
            log.error("taskId cannot be empty");
            throw new NoTaskIdException("taskId cant be empty");
        }
        if(!isValidTaskId()){
            log.error("taskId is invalid");
            throw new InvalidTaskIdException("wrong format");
        }
    }

    public Task(String taskId, String startTime, String endTime) {
        id++;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    public String getTaskId(){
        return taskId;
    }
    
    public LocalTime getStartTime(){
        return startTime;
    }
    
    public LocalTime getEndTime(){
        return endTime;
    }
    
    public String getComment(){
        return comment;
    }
    
    /**
     * gets the required time it takes to finish the task
     * @return
     * @throws EmptyTimeFieldException throw if null
     */
    public long getMinPerTask() throws EmptyTimeFieldException{
        if(endTime == null || startTime == null){
            log.error("endTime or startTime has not been initialized");
            throw new EmptyTimeFieldException("empty");
        }
        return ((endTime.getMinute() - startTime.getMinute()) + 60 * (endTime.getHour() - startTime.getHour()));
    }
    
    /**
     * sets the taskId
     * @param taskid
     * @throws InvalidTaskIdException thrown if wrong format
     * @throws NoTaskIdException thrown if null
     */
    public void setTaskId(String taskid) throws InvalidTaskIdException, NoTaskIdException{
        this.taskId = taskid;
        if(taskId == null){
            log.error("the taskId cannot be empty");
            throw new NoTaskIdException("taskId cant be empty");
        }
        if(!isValidTaskId()){
            log.error("the taskId is invalid");
            throw new InvalidTaskIdException("wrong format");
        }
    }
    
    public void setComment(String comment){
        this.comment = comment;
    }
    
    /**
     * set the startTime, rounds endTime if necessary
     * @param hour
     * @param min
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setStartTime(int hour, int min) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.startTime.of(hour, min);
        if(this.endTime != null){
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), this.endTime);
            }
            if(startTime.isAfter(endTime)){
                log.error("startTime cannot be after endTime");
                throw new NotExpectedTimeOrderException("not expected order");
            }
        }
    }
    
    /**
     * set the startTime, rounds endTime if necessary
     * @param time
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setStartTime(String time) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.startTime = LocalTime.parse(time);
        if(endTime != null){
            if(LocalTime.parse(time).isAfter(endTime)){
                log.error("startTime cannot be after endTime");
                throw new NotExpectedTimeOrderException("not expected order");
            }
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), this.endTime);
            }
        }
    }
    
    /**
     * set the startTime, rounds endTime if necessary
     * @param time
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setStartTime(LocalTime time) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.startTime = time;
        if(endTime != null){
            if(time.isAfter(endTime)){
                log.error("startTime cannot be after endTime");
                throw new NotExpectedTimeOrderException("not expected order");
            }
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), this.endTime);
            }
        }
    }
    
    /**
     * set the endTime, rounds endTime if necessary
     * @param hour
     * @param min
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setEndTime(int hour, int min) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        if(this.getStartTime().isAfter(LocalTime.of(hour, min))){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("endTime before startTime");
        }
        this.endTime.of(hour, min);
        if(this.startTime != null){
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), LocalTime.of(hour, min));
            }
        }
    }
    
    /**
     * set the endTime, rounds endTime if necessary
     * @param hour
     * @param min
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setEndTime(String time) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        if(time.isEmpty()){
            log.error("endTime cannot be empty");
            throw new EmptyTimeFieldException("empty time field");
        }
        if(this.getStartTime().isAfter(LocalTime.parse(time))){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("endTime before startTime");  
        }
        this.endTime = LocalTime.parse(time);
        if(this.startTime != null){
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), LocalTime.parse(time));
            }
        }
    }
    
    /**
     * set the endTime, rounds endTime if necessary
     * @param hour
     * @param min
     * @throws NotExpectedTimeOrderException thrown if wrong format
     * @throws EmptyTimeFieldException  thrown if null
     */
    public void setEndTime(LocalTime time) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        if(this.getStartTime().isAfter(time)){
            log.error("startTime cannot be after endTime");
            throw new NotExpectedTimeOrderException("endTime before startTime");
        }
        this.endTime = time;
        if(this.startTime != null){
            if(!Util.isMultipleQuarterHour(this.getStartTime(), this.getEndTime())){
                this.endTime = roundToMultipleQuarterHour(this.getStartTime(), time);
            }
        }
    }
    
    /**
     * decides if taskId if valid
     * @return 
     */
    public boolean isValidTaskId(){
        return isValidRedMineTaskId() || isValidLTTaskId();
    }
    
    /**
     * checks if taskId is 4 digits
     * @return 
     */
    private boolean isValidRedMineTaskId(){
        return taskId.matches("\\d\\d\\d\\d");
    }
    
    /**
     * checks if taskId is LT-4digits
     * @return 
     */
    private boolean isValidLTTaskId(){
        return taskId.matches("LT-\\d\\d\\d\\d");
    }
    
    /**
     * checks if two tasks have common intervals
     * @param t
     * @return 
     */
    public boolean commonParts(Task t){       
        return !((this.getStartTime().isAfter(t.getEndTime()) 
                ||
                (this.getEndTime().isBefore(t.getStartTime()))
                ||
                this.getEndTime().compareTo(t.getStartTime()) == 0
                ||
                this.getStartTime().compareTo(t.getEndTime()) == 0)
                &&
                (this.getStartTime().compareTo(t.getStartTime()) != 0)
                );
    }
    
    /**
     * writes statistics to console
     * @return 
     */
    public String toString(){
        String temp = taskId + " : " + comment + " starts at: " + startTime + " ends at: " + endTime;
        return temp;
    }
    
    /**
     * decides if the task has an endTime
     * @return 
     */
    public boolean isUnfinished(){
        return (endTime == null);
    }
}
