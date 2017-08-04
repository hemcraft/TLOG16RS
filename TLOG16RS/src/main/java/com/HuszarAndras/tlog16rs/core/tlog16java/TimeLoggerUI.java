/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

import java.time.LocalDate;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;
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
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.entities.Task;

/**
 *
 * @author Andris
 */
public class TimeLoggerUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NegativeMinutesOfWorkException, FutureWorkException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException, NotExpectedTimeOrderException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotNewMonthException {
        // TODO code application logic here
        String menu = "0. Exit\n" +
                      "1. List months: shows a counter, the year & the month line by line (example: 1. 2016-09, 2. 2016-10, ...)\n" +
                "2. List days:\n" +
                "   lists the months, select one by ask for row number\n" +
                "   list all workdays of this month\n" +
                "3. List tasks for a specific day (ask for month & day)\n" +
                "4. Add new month: specify year & month with integers\n" +
                "5. Add day to a specific month:\n" +
                "   list the workmonths (2. menu item)\n" +
                "   ask the index of workmonth\n" +
                "   ask the day\n" +
                "   ask the required working hours, default value=7.5\n" +
                "6. Start a task for a day\n" +
                "   ask for month, day, task id, what you do (comment)\n" +
                "   ask for start time in format 10:30 \n" +
                "       if there is a task in the day, get the end time of the last task and show it in braces! If the user enters an empty value, save that time in the task!\n" +
                "   don't ask for the end time!\n" +
                "7. Finish a specific task: \n" +
                "   ask for month & day, \n" +
                "   display only unfinished tasks\n" +
                "   ask for end time (format: 12:45, with validation)\n" +
                "8. Delete a task: ask for month, day, select task - ask for confirmation!\n" +
                "9. Modify task: ask for month, day, task, let change every fields (shows previous value in braces, if the input is empty, don't change the value!)\n" +
                "10. Statistics: ask for month, then print the statistics of the month, and the statistics of the days of this month\n" +
                "";
        
        System.out.println(menu);
        
        TimeLogger timeLogger = new TimeLogger();
        
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while(true){
            i = sc.nextInt();
            switch(i) {
                case 0 : {
                    System.exit(0);
                    break;
                }
                case 1 : {
                    timeLogger.listMonths();
                    break;
                }
                case 2 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    break;
                }
                case 3 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    System.out.println("Select one");
                    int whichDay = sc.nextInt();
                    timeLogger.listTasks(whichMonth, whichDay);
                    break;
                }
                case 4 : {
                    System.out.println("Which year & month? \n Type year number\nthen number of month");
                    int whichYear = sc.nextInt();
                    int whichMonth = sc.nextInt();
                    timeLogger.addMonth(new WorkMonth(whichYear, whichMonth));
                    break;
                }
                case 5 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    //timeLogger.listDays(whichMonth);
                    System.out.println("Which Day?");
                    int whichDay = sc.nextInt();
                    System.out.println("What are the required working hours?");
                    int requiredWorkingHours = sc.nextInt();
                    timeLogger.getMonthList().get(whichMonth).addWorkDay(new WorkDay(requiredWorkingHours,
                                                                 LocalDate.of(LocalDate.now().getYear(), 
                                                                         timeLogger.getMonthList().get(whichMonth).getDate().getMonthValue(), 
                                                                         whichDay)), 
                                                                 true);
                    break;
                }
                case 6 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    System.out.println("Which Day?");
                    int whichDay = sc.nextInt();
                    sc.nextLine();
                    System.out.println("What is the ID of the task?");
                    String taskId = sc.nextLine();
                    System.out.println("What does the task do");
                    String comment = sc.nextLine();
                    System.out.println("When does the task start? format: 'hh:mm'");
                    String startTime = sc.nextLine();
                    try{
                    timeLogger.addTask(whichMonth, whichDay, taskId, comment, startTime);
                    }catch(EmptyTimeFieldException e){
                        e.printStackTrace();
                    }
                    break;
                }
                case 7 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    System.out.println("Which Day?");
                    int whichDay = sc.nextInt();
                    sc.nextLine();
                    timeLogger.showUnfinishedTasks(whichMonth, whichDay);
                    System.out.println("Select one");
                    int whichTask = sc.nextInt();
                    sc.nextLine();
                    System.out.println("What is the endtime of the task? format: 'hh:mm'");
                    String endTime = sc.nextLine();
                    try{
                        timeLogger.finishTask(whichMonth, whichDay, whichTask, endTime);
                    }catch(NotExpectedTimeOrderException e){
                        e.printStackTrace();
                    }
                    break;
                }
                case 8 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    System.out.println("Which Day?");
                    int whichDay = sc.nextInt();
                    timeLogger.listTasks(whichMonth, whichDay);
                    int whichTask = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Are you sure? y/n");
                    String answer = sc.nextLine();
                   // if(answer == "y")
                    //{
                       // System.out.println("igen");
                        timeLogger.deleteTask(whichMonth, whichDay, whichTask);
                    //}
                    break;
                }
                case 9 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.listDays(whichMonth);
                    System.out.println("Which Day?");
                    int whichDay = sc.nextInt();
                    sc.nextLine();
                    timeLogger.listTasks(whichMonth, whichDay);
                    int whichTask = sc.nextInt();
                    timeLogger.setTaskFields(whichMonth, whichDay, whichTask);
                    break;
                }
                case 10 : {
                    timeLogger.listMonths();
                    System.out.println("Select one");
                    int whichMonth = sc.nextInt();
                    timeLogger.writeStatistics(whichMonth);
                    break;
                }
            }
        } 
    }
}
