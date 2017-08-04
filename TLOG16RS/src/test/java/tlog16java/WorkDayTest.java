/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16java;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.HuszarAndras.tlog16rs.entities.Task;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.core.tlog16java.Util;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import static org.junit.Assert.*;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.FutureWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NegativeMinutesOfWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;

/**
 *
 * @author Andris
 */
public class WorkDayTest {
    
    public WorkDayTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void test1() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, NotSeparatedTimesException, EmptyTimeFieldException{
        Task t = new Task("2222", "07:30", "08:45", "list");
        WorkDay wd = new WorkDay();
        wd.addTask(t);
        assertEquals(-375, wd.getExtraMinPerDay());
    }
    @Test
    public void test2() throws EmptyTimeFieldException{
        WorkDay wd = new WorkDay();
        assertEquals(wd.getExtraMinPerDay(), -wd.getRequiredMinPerDay());   
    }
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void test3() throws NegativeMinutesOfWorkException{
        WorkDay wd = new WorkDay();
        wd.setRequiredMinPerDay(-10);
    }
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void test4() throws NegativeMinutesOfWorkException{
        WorkDay wd = new WorkDay(-10);
    }
    
    @Test(expected = FutureWorkException.class)
    public void test5() throws FutureWorkException{
        WorkDay wd = new WorkDay();
        wd.setActualDay(2050, 7, 4);
    }
    @Test(expected = FutureWorkException.class)
    public void test6() throws FutureWorkException, NegativeMinutesOfWorkException{
        WorkDay wd = new WorkDay(450, LocalDate.of(2050, 7, 4));
    }
    @Test
    public void test7() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "08:45", "09:45", "list");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
        assertEquals(wd.getSumPerDay(), 135);
    }
    @Test
    public void test8() throws EmptyTimeFieldException{
        WorkDay wd = new WorkDay();
        assertEquals(wd.getSumPerDay(), 0);
    }
    
    @Test
    public void test9() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "09:30", "11:45", "list");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
        assertEquals(wd.latestTaskEndTime(), LocalTime.of(11, 45));
    }
    @Test
    public void test10(){
        WorkDay wd = new WorkDay();
        assertEquals(wd.latestTaskEndTime(), null);
    }
    
    @Test(expected = NotSeparatedTimesException.class)
    public void test11() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "08:30", "09:45", "list");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
    }
    @Test
    public void test12() throws NegativeMinutesOfWorkException, FutureWorkException{
        WorkDay wd = new WorkDay(400, LocalDate.of(2000, 7, 4));
        assertEquals(wd.getActualDay(), LocalDate.of(2000, 7, 4));
        assertEquals(wd.getRequiredMinPerDay(), 400);
    }
    
    @Test
    public void test13(){
        WorkDay wd = new WorkDay();
        assertEquals(wd.getActualDay(), LocalDate.now());
        assertEquals(wd.getRequiredMinPerDay(), 450);
    }
    
    @Test
    public void test14() throws NegativeMinutesOfWorkException{
        WorkDay wd = new WorkDay(300);   
        assertEquals(wd.getRequiredMinPerDay(), 300);
    }
    @Test
    public void test15(){
        
    }
    @Test
    public void test16() throws FutureWorkException{
        WorkDay wd = new WorkDay();
        wd.setActualDay(2016, 9, 1);
        assertEquals(wd.getActualDay(), LocalDate.of(2016, 9, 1));
    }
    
    @Test
    public void test17() throws NegativeMinutesOfWorkException{
        WorkDay wd = new WorkDay();
        wd.setRequiredMinPerDay(300);
        assertEquals(wd.getRequiredMinPerDay(), 300);
    }
    @Test(expected = EmptyTimeFieldException.class)
    public void test18() throws NotSeparatedTimesException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("1111");
        WorkDay wd = new WorkDay();
        wd.addTask(t);
        wd.getSumPerDay();
    }
    @Test(expected = NotSeparatedTimesException.class)
    public void test19() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "08:30", "09:45", "list");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
    }
}
