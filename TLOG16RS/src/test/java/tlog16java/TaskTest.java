/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16java;

import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.HuszarAndras.tlog16rs.core.tlog16java.Util;
import com.HuszarAndras.tlog16rs.entities.TimeLogger;
import com.HuszarAndras.tlog16rs.entities.WorkDay;
import com.HuszarAndras.tlog16rs.entities.WorkMonth;
import com.HuszarAndras.tlog16rs.entities.Task;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.EmptyTimeFieldException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.InvalidTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NoTaskIdException;
import com.HuszarAndras.tlog16rs.core.timelogger.exceptions.NotExpectedTimeOrderException;

/**
 *
 * @author Andris
 */
public class TaskTest {
    
    public TaskTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    /**
     * Test of getTaskId method, of class Task.
     */
    @Test(expected = NotExpectedTimeOrderException.class)
    public void test1() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException {
        Task t = new Task("4444", "10:00", "09:30", "list");
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void test2() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task t = new Task("4444");
        t.setEndTime("");
    }
    
    @Test
    public void test3() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4444", "07:30", "08:45", "list");
        assertEquals(75, t.getMinPerTask());
    }
    
    @Test(expected = InvalidTaskIdException.class)
    public void test4() throws InvalidTaskIdException, NoTaskIdException{
        Task t = new Task("154858");
    }
    
    @Test(expected = InvalidTaskIdException.class)
    public void test5() throws InvalidTaskIdException, NoTaskIdException{
        Task t = new Task("LT-154858");
    }
    
    @Test(expected = NoTaskIdException.class)
    public void test6() throws InvalidTaskIdException, NoTaskIdException{
        Task t = new Task("");
    }
    
    @Test
    public void test7() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "10:00", "10:30", "");
        assertEquals("", t.getComment());
    }
    
    @Test
    public void test8() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:50", "list");
        assertEquals(LocalTime.parse("07:45"), t.getEndTime());
    }
    
    @Test
    public void test9() throws InvalidTaskIdException, NoTaskIdException, NotExpectedTimeOrderException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setStartTime("07:20");
        assertEquals(LocalTime.parse("07:50"), t.getEndTime());
        t.setStartTime(7, 20);
        assertEquals(LocalTime.parse("07:50"), t.getEndTime());
        t.setStartTime(LocalTime.parse("07:20"));
        assertEquals(LocalTime.parse("07:50"), t.getEndTime());
    }
    
    @Test
    public void test10() throws InvalidTaskIdException, NoTaskIdException, NotExpectedTimeOrderException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setEndTime("07:50");
        assertEquals(LocalTime.parse("07:45"), t.getEndTime());
        t.setEndTime(7, 50);
        assertEquals(LocalTime.parse("07:45"), t.getEndTime());
        t.setEndTime(LocalTime.parse("07:50"));
        assertEquals(LocalTime.parse("07:45"), t.getEndTime());
    }
    
    @Test(expected = NoTaskIdException.class)
    public void test11() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setTaskId(null);
    }
    
    @Test(expected = InvalidTaskIdException.class)
    public void test12() throws InvalidTaskIdException, NoTaskIdException{
        Task t = new Task("333");
    }
    
    @Test(expected = NotExpectedTimeOrderException.class)
    public void test13() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setStartTime("08:30");
    }
    
    @Test(expected = NotExpectedTimeOrderException.class)
    public void test14() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setEndTime("07:00");
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void test15() throws InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858");
        t.getMinPerTask();
    }
    
    @Test
    public void test16() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setStartTime("07:00");
        assertEquals(LocalTime.parse("07:00"), t.getStartTime());
    }
    
    @Test
    public void test17() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        t.setEndTime("08:00");
        assertEquals(LocalTime.parse("08:00"), t.getEndTime());
    }
    
    @Test
    public void test18() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{
        Task t = new Task("4858", "07:30", "07:45", "list");
        assertEquals("4858", t.getTaskId());
        assertEquals("list", t.getComment());
        assertEquals(LocalTime.parse("07:30"), t.getStartTime());
        assertEquals(LocalTime.parse("07:45"), t.getEndTime());
    }
    
}
