/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16java;

import java.time.LocalDate;
import java.time.Month;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.HuszarAndras.tlog16rs.entities.Task;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.core.tlog16java.Util;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.FutureWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NegativeMinutesOfWorkException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewDateException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotTheSameMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.WeekendNotEnabledException;

/**
 *
 * @author Andris
 */
public class WorkMonthTest {
    
    public WorkMonthTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getDate method, of class WorkMonth.
     */
    @Test
    public void test1() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "08:45", "09:45", "list");
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 2));
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 9, 1));
        wd.addTask(t1);
        wd2.addTask(t2);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getSumPerMonth(), 135);
    }
    
    @Test
    public void test2() throws EmptyTimeFieldException{
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(wm.getSumPerMonth(), 0);
    }
    
    @Test
    public void test3() throws NotSeparatedTimesException, NegativeMinutesOfWorkException, FutureWorkException, NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        Task t2 = new Task("2222", "08:45", "09:45", "list");
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 2));
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 9, 1));
        wd.addTask(t1);
        wd2.addTask(t2);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getExtraMinPerMonth(), -705);
    }
    
    @Test
    public void test4() throws EmptyTimeFieldException{
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(wm.getExtraMinPerMonth(), 0);
    }
    
    @Test
    public void test5() throws NegativeMinutesOfWorkException, FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 2));
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getRequiredMinPerMonth(), 840);
    }
    
    @Test
    public void test6(){
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(wm.getRequiredMinPerMonth(), 0);
    }
    
    @Test
    public void test7() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 9, 9));
        wd2.addTask(t1);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd2);
        assertEquals(wd2.getSumPerDay(), wm.getSumPerMonth());
    }
    
    @Test
    public void test8() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 8, 28));
        wd2.addTask(t1);
        WorkMonth wm = new WorkMonth(2016, 8);
        wm.addWorkDay(wd2, true);
        assertEquals(wd2.getSumPerDay(), wm.getSumPerMonth());
    }
    
    @Test(expected = WeekendNotEnabledException.class)
    public void test9() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t1 = new Task("1111", "07:30", "08:45", "list");
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 8, 28));
        wd2.addTask(t1);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd2, false);
    }
    
    @Test(expected = NotNewDateException.class)
    public void test10() throws NegativeMinutesOfWorkException, FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
    }
    
    @Test(expected = NotTheSameMonthException.class)
    public void test11() throws NegativeMinutesOfWorkException, FutureWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(420, LocalDate.of(2016, 8, 30));
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void test12() throws InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t = new Task("1111");
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 1));
        wd.addTask(t);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.getSumPerMonth();
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void test13() throws InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, FutureWorkException, NotSeparatedTimesException, EmptyTimeFieldException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task t = new Task("1111");
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 9, 1));
        wd.addTask(t);
        WorkMonth wm = new WorkMonth(2016, 9);
        wm.addWorkDay(wd);
        wm.getExtraMinPerMonth();
    }
}
