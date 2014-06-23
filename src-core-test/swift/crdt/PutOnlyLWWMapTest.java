/*****************************************************************************
 * Copyright 2011-2012 INRIA
 * Copyright 2011-2012 Universidade Nova de Lisboa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package swift.crdt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import swift.crdt.core.CRDTIdentifier;
import swift.crdt.core.TxnHandle;
import swift.exceptions.SwiftException;

public class PutOnlyLWWMapTest {
    TxnHandle txn;
    PutOnlyLWWMapCRDT<Integer, Integer> i;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws SwiftException {
        txn = TxnTester.createIsolatedTxnTester();
        i = txn.get(new CRDTIdentifier("A", "Int"), true, PutOnlyLWWMapCRDT.class);
    }

    @Test
    public void initTest() {
        assertTrue(i.getValue().isEmpty());
    }

    @Test
    public void emptyTest() {
        // lookup on empty set
        assertTrue(!i.contains(0));
        assertTrue(i.getValue().isEmpty());
    }

    @Test
    public void putOnceTest() {
        // insert one element
        i.put(1, 11);
        assertTrue(i.contains(1));
        assertEquals(i.get(1), Integer.valueOf(11));
    }

    @Test
    public void putDifferentElementsTest() {
        // insert one element
        i.put(1, 11);
        i.put(2, 22);
        assertTrue(i.contains(1));
        assertTrue(i.contains(2));
        assertEquals(i.get(1), Integer.valueOf(11));
        assertEquals(i.get(2), Integer.valueOf(22));
        assertEquals(i.getValue().size(), 2);
    }

    @Test
    public void putOverwriteTest() {
        // insert one element
        i.put(1, 11);
        i.put(1, 12);
        assertTrue(i.contains(1));
        assertEquals(i.get(1), Integer.valueOf(12));
    }
}
