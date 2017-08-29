
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
import com.HuszarAndras.tlog16rs.core.tlog16java.DeleteTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.FinishingTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.ModifyTaskRB;
import com.HuszarAndras.tlog16rs.core.tlog16java.Service;
import com.HuszarAndras.tlog16rs.core.tlog16java.StartTaskRB;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.Task;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.core.tlog16java.WorkDayRB;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.core.tlog16java.WorkMonthRB;
import com.avaje.ebean.Ebean;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/timelogger")
public class TLOG16RSResource {
    private TimeLogger timeLogger = null;

    public TLOG16RSResource(){
        try{
            timeLogger = Ebean.find(TimeLogger.class).findUnique();
            if(timeLogger == null) 
                timeLogger = new TimeLogger();
        }catch(Exception e){
            timeLogger = new TimeLogger();
        }
    }
    
    @POST
    @Path("/init")
    public void initStart(){
        timeLogger = new TimeLogger();
        Ebean.save(timeLogger);
    }
    
    //1
    @GET
    @Path("/workmonths")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<WorkMonth> getMonths() throws EmptyTimeFieldException {
        //TimeLogger timeLoggerTemp = Ebean.find(TimeLogger.class, 1);
        //timeloggert át kell írni
        ArrayList<WorkMonth> answer = new ArrayList<WorkMonth>();
        for(int i = 0; i < timeLogger.getSize(); i++){
            answer.add(timeLogger.getWorkMonth(i));
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
        
        return workDay;
    }
    
    //3.5
    @POST
    @Path("/workmonths/workdaysweekend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWorkDayWeekEnd(WorkDayRB day) throws NotNewMonthException, NegativeMinutesOfWorkException, FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        int monthIndex = 0;
        int counter = 0;
        
        try {
        monthIndex = Service.findOrCreateMonth(timeLogger, monthIndex, day.getYear(), day.getMonth());
        
        WorkDay workDay = new WorkDay(day.getRequiredHours(), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
        timeLogger.getWorkMonth(monthIndex).addWorkDay(workDay);
        Ebean.save(timeLogger);
        return Response.status(200).build();
        
        }catch(WeekendNotEnabledException e){
            return Response.status(406).build();
        }
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
              
        for(int j = 0; j < timeLogger.getWorkMonth(monthIndex).getDayList().size(); j++){
            WorkDay wd = timeLogger.getWorkMonth(monthIndex).getDays(j);
            answer.add(wd);
        }
        
        Ebean.save(timeLogger);
        
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
              
        for(int i = 0; i < timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTaskList().size(); i++){
            Task t = timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
            answer.add(t);
        }
        
        Ebean.save(timeLogger);
        
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
        Task temporaryTask = null;
        
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
                    temporaryTask = timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).getTasks(i);
                    timeLogger.getWorkMonth(monthIndex).getDays(dayIndex).deleteTaskAt(i);                   
            }
        }
        Ebean.delete(temporaryTask);
        Ebean.save(timeLogger);
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    public void deleteAll(){
        timeLogger.deleteMonths();
        deleteDatabase();
       
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
    
    @PUT
    @Path("/workmonths/deleteDatabase")
    public void deleteDatabase(){
        List<TimeLogger> timeLoggerList = Ebean.find(TimeLogger.class).findList();
        if(!timeLoggerList.isEmpty()){
            for(int q = 0; q < timeLoggerList.size(); q++){
                TimeLogger timeLoggerTemp = timeLoggerList.get(q);
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
        }
    }
    
    @POST
    @Path("/workmonths/updateStatistics")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String update(WorkDayRB day) throws EmptyTimeFieldException{
        String stats = "valami";
        stats = timeLogger.updateMonthlyStatistics(day.getYear(), day.getMonth(), day.getDay());
        Ebean.save(timeLogger);
        return stats;
    }
    
    @POST
    @Path("/workmonths/updateDays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateDays(WorkDayRB day) throws EmptyTimeFieldException{
        String stats = "";
        for(int i = 1; i < 32; i++){
            String res = "";
            String[] temp = timeLogger.updateMonthlyStatistics(day.getYear(), day.getMonth(), i).split(" ");
            if(temp.length == 3)
                res = temp[2];
            stats = stats + " " + Integer.toString(i) + ":" + res;
        }
        return stats;
    }
}
