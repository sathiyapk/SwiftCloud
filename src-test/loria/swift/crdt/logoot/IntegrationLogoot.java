/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loria.swift.crdt.logoot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import swift.clocks.ClockFactory;
import swift.crdt.SwiftTester;
import swift.crdt.TesterUtils;
import swift.crdt.TxnTester;
import swift.exceptions.NoSuchObjectException;
import swift.exceptions.VersionNotFoundException;
import swift.exceptions.WrongTypeException;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationLogoot {
    SwiftTester swift1, swift2;
    LogootVersionned l1, l2;

    private LogootTxnLocal getTxnLocal(LogootVersionned i, TxnTester txn) {
        return (LogootTxnLocal) TesterUtils.getTxnLocal(i, txn);
    }
    
    @Before
    public void setUp() throws WrongTypeException, NoSuchObjectException, VersionNotFoundException {
        swift1 = new SwiftTester("client1");
        swift2 = new SwiftTester("client2");
        l1 = new LogootVersionned();
        l1.init(null, ClockFactory.newClock(), ClockFactory.newClock(), true);
        l2 = new LogootVersionned();
        l2.init(null, ClockFactory.newClock(), ClockFactory.newClock(), true);
    } 
    
    
    public IntegrationLogoot() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testInsert() {
        TxnTester txn = swift1.beginTxn(l1);        
        getTxnLocal(l1, txn).update("aaa\nbbb\n");        
        txn.commit();
        assertEquals("aaa\nbbb\n", getTxnLocal(l1, swift1.beginTxn()).getText()); 
    }

    @Test
    public void testDelete() {
        TxnTester txn = swift1.beginTxn(l1);        
        getTxnLocal(l1, txn).update("aaa\nbbb\n");
        txn.commit();
        
        assertEquals("aaa\nbbb\n", getTxnLocal(l1, swift1.beginTxn()).getText()); 
        LogootIdentifier b = l1.getDoc().idTable.get(2);        
        
        txn = swift1.beginTxn(l1);        
        getTxnLocal(l1, txn).update("bbb\n");
        txn.commit();        
        assertEquals("bbb\n", getTxnLocal(l1, swift1.beginTxn()).getText()); 
        assertEquals(b, l1.getDoc().idTable.get(2));        
    }
}