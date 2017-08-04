
package com.HuszarAndras.tlog16rs.resources;

import com.HuszarAndras.tlog16rs.entities.TestEntity;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.FutureWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NegativeMinutesOfWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewDateException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotTheSameMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.WeekendNotEnabledException;
import com.HuszarAndras.tlog16rs.core.tlog16java.Counter;
import com.HuszarAndras.tlog16rs.core.tlog16java.DeleteTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.FinishingTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.ModifyTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.Service;
import com.HuszarAndras.tlog16rs.core.tlog16java.StartTaskRB;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.Task;
import com.HuszarAndras.tlog16rs.core.tlog16java.Util;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.core.tlog16java.WorkDayRB;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.core.tlog16java.WorkMonthRB;
import com.avaje.ebean.Ebean;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/timelogger")
public class TLOG16RSResource {
    private TimeLogger timeLogger;
    private boolean inUse;

    public TLOG16RSResource(){
        timeLogger = new TimeLogger();
        inUse = false;
    }
    
    @POST
    @Path("/init")
    public void initStart(){
        Ebean.save(timeLogger);
    }
    
    //1
    @GET
    @Path("/workmonths")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<WorkMonth> getMonths() throws EmptyTimeFieldException {
        TimeLogger timeLoggerTemp = Ebean.find(TimeLogger.class, 1);
        ArrayList<WorkMonth> answer = new ArrayList<WorkMonth>();
        for(int i = 0; i < timeLoggerTemp.getSize(); i++){
            answer.add(timeLoggerTemp.getWorkMonth(i));
        }
        return answer;
    }
    
    //2
    @POST
    @Path("/workmonths")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewWorkMonth(WorkMonthRB month) throws NotNewMonthException{
        
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        timeLogger.addMonth(workMonth);
        Ebean.save(timeLogger);
        
        inUse = false;
        
        return workMonth;
    }
    
    //3
    @POST
    @Path("/workmonths/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day) throws NotNewMonthException, NegativeMinutesOfWorkException, FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        int monthIndex = 0;
        int counter = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, day.getYear(), day.getMonth());
        
        WorkDay workDay = new WorkDay(day.getRequiredHours(), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
        timeLogger.getWorkMonth(monthIndex).addWorkDay(workDay, true);
        Ebean.save(timeLogger);
        inUse = false;
        
        return workDay;
    }
    
    //4
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task addNewTask(StartTaskRB task) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, NotNewMonthException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotSeparatedTimesException, FutureWorkException{
        int monthIndex = 0;
        int dayIndex = 0;
        int counter = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, task.getYear(), task.getMonth());
        
        dayIndex = Service.findOrCreateDay(timeLogger, monthIndex, dayIndex, task.getYear(), task.getMonth(), task.getDay());
        
        
        Task t = new Task(task.getTaskId(), task.getStartTime(), task.getStartTime(), task.getComment());     
        timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).addTask(t);
        Ebean.save(timeLogger);
        inUse = false;
        
        return t;
    }
    
    //5
    @GET
    @Path("/workmonths/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<WorkDay> listDays(@PathParam(value = "year") int year, @PathParam(value = "month") int month) throws EmptyTimeFieldException, NotNewMonthException {
        ArrayList<WorkDay> answer = new ArrayList<WorkDay>();
        int monthIndex = 0;
        int counter = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, year, month);
        Ebean.save(timeLogger);
        TimeLogger timeLoggerTemp = Ebean.find(TimeLogger.class, 1);
        
        for(int j = 0; j < timeLoggerTemp.getWorkMonth(monthIndex).getDayList().size(); j++){
            WorkDay wd = timeLoggerTemp.getWorkMonth(monthIndex).getDays(j);
            answer.add(wd);
        }
        inUse = false;
        
        return answer;
    }
        
    //6
    @GET
    @Path("/workmonths/{year}/{month}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Task> listTasks(@PathParam(value = "year") int year, @PathParam(value = "month") int month, @PathParam(value = "day") int day) throws EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, FutureWorkException, NotNewMonthException {    
        ArrayList<Task> answer = new ArrayList<Task>();
        
        int monthIndex = 0;
        int dayIndex = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, year, month);
        
        dayIndex = Service.findOrCreateDay(timeLogger, monthIndex, dayIndex, year, month, day);
        
        Ebean.save(timeLogger);
        
        TimeLogger timeLoggerTemp = Ebean.find(TimeLogger.class, 1);
              
        for(int i = 0; i < timeLoggerTemp.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size(); i++){
            Task t = timeLoggerTemp.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
            answer.add(t);
        }
        inUse = false;
        
        return answer;
    }
    
    //7
    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    public void finishTask(FinishingTaskRB task) throws NotNewMonthException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotExpectedTimeOrderException, EmptyTimeFieldException, NotSeparatedTimesException, FutureWorkException, InvalidTaskIdException, NoTaskIdException{
        int monthIndex = 0;
        int dayIndex = 0;
        int counter = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, task.getYear(), task.getMonth());
        
        dayIndex = Service.findOrCreateDay(timeLogger, monthIndex, dayIndex, task.getYear(), task.getMonth(), task.getDay());
        
        for(int i = 0; i < timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size(); i++){
            Task t = timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
            if((t.getTaskId().equals(task.getTaskId())) && (t.getStartTime() == LocalTime.parse(task.getStartTime()))){
                timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i).setEndTime(task.getEndTime());
            }else{
                counter++;
            }          
        }
        
        if(counter == timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size()){
            Task t = new Task(task.getTaskId(), task.getStartTime(), task.getEndTime(), "");
            timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).addTask(t);
        }
        inUse = false;
        
        Ebean.save(timeLogger);
    }
    
    
    //8
    @PUT
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    public void modifyTask(ModifyTaskRB task) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotNewMonthException, NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, FutureWorkException{
        int monthIndex = 0;
        int dayIndex = 0;
        int counter = 0;
        
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, task.getYear(), task.getMonth());
        
        dayIndex = Service.findOrCreateDay(timeLogger, monthIndex, dayIndex, task.getYear(), task.getMonth(), task.getDay());
        
        for(int i = 0; i < timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size(); i++){
            Task t = timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
            if((t.getTaskId().equals(task.getTaskId())) && (t.getStartTime() == LocalTime.parse(task.getStartTime()))){
                timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i).setTaskId(task.getNewTaskId());
                timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i).setEndTime(task.getNewEndTime());
                timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i).setStartTime(task.getNewStartTime());
                timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i).setComment(task.getNewComment());
            }else{
                counter++;
            }          
        }
        
        if(counter == timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size()){
            Task t = new Task(task.getNewTaskId(), task.getNewStartTime(), task.getNewEndTime(), task.getNewComment());
            timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).addTask(t);
        }
        inUse = false;
        
        Ebean.save(timeLogger);
    }
    
    //9
    @PUT
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteTask(DeleteTaskRB task) throws NotNewMonthException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        int monthIndex = 0;
        int dayIndex = 0;
        int counter = 0;
        
        for(int j = 0; j < timeLogger.getSize(); j++){
            if(timeLogger.getWorkMonth(j).getDate().getMonthValue() == task.getMonth() &&
                    timeLogger.getWorkMonth(j).getDate().getYear() == task.getYear()){
                monthIndex = j;
            }
        }
        
        for(int j = 0; j < timeLogger.getWorkMonth(monthIndex).getDayList().size(); j++){
            if(timeLogger.getWorkMonth(monthIndex).getDays(j).getActualDay().getDayOfMonth() == task.getDay()){
                dayIndex = j;
            }
        }
        
        for(int i = 0; i < timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size(); i++){
            Task t = timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
            if((t.getTaskId().equals(task.getTaskId())) && (t.getStartTime() == LocalTime.parse(task.getStartTime()))){
                    timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).deleteTaskAt(i);
            }
        }
        inUse = false;
        
        Ebean.save(timeLogger);
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    public void deleteAll(){
        timeLogger.deleteMonths();
        TimeLogger timeLoggerTemp = Ebean.find(TimeLogger.class, 1);
        for(int i = 0; i < timeLoggerTemp.getSize(); i++){
            for(int j = 0; j < timeLoggerTemp.getWorkMonth(i).getDayList().size(); j++){
                for(int z = 0; z < timeLoggerTemp.getWorkMonth(i).getDays(j).getTaskList().size(); z++){
                    Ebean.delete(timeLoggerTemp.getWorkMonth(i).getDays(j).getTasks(z));
                }
                Ebean.delete(timeLoggerTemp.getWorkMonth(i).getDays(j));
            }
            Ebean.delete(timeLoggerTemp.getWorkMonth(i));
        }
        Ebean.delete(timeLoggerTemp);
       
    }
    
    @POST
    @Path("/timelogger/save/test")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String testSave(String text){
        TestEntity testEntity = new TestEntity();
        testEntity.setText(text);
        //Ebean.save(testEntity);
        return text;
    }
}
