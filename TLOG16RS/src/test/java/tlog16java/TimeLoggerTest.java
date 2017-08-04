/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16java;

import java.time.LocalDate;
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
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotNewMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotSeparatedTimesException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotTheSameMonthException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.WeekendNotEnabledException;

/**
 *
 * @author Andris
 */
public class TimeLoggerTest {
    
    public TimeLoggerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void test1() throws NegativeMinutesOfWorkException, FutureWorkException, NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException, NotNewMonthException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay wd = new WorkDay(420, LocalDate.of(2016, 4, 14));
        WorkMonth wm = new WorkMonth(2016, 4);
        Task t = new Task("1111", "07:30", "10:30", "list");
        wd.addTask(t);
        wm.addWorkDay(wd);
        TimeLogger tl = new TimeLogger();
        tl.addMonth(wm);
        assertEquals(t.getMinPerTask(), tl.getWorkMonth(0).getSumPerMonth());
    }
    
    @Test(expected = NotNewMonthException.class)
    public void test2() throws NotNewMonthException{
        TimeLogger tl = new TimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 4);
        tl.addMonth(workMonth1);
        tl.addMonth(workMonth2);
    }
    
}
