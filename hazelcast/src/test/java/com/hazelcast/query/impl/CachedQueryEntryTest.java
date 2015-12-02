/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelTest.class})
public class CachedQueryEntryTest {

    private SerializationService serializationService;

    @Before
    public void before() {
        serializationService = new DefaultSerializationServiceBuilder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_whenKeyIsNull_thenThrowIllegalArgumentException() {
        Data key = null;
        new CachedQueryEntry(serializationService, key, new Object(), Extractors.empty());
    }

    @Test
    public void testGetKey() {
        String keyObject = "key";
        Data keyData = serializationService.toData(keyObject);
        CachedQueryEntry entry = new CachedQueryEntry(serializationService, keyData, new Object(), Extractors.empty());

        Object key = entry.getKey();
        assertEquals(keyObject, key);
    }

    @Test
    public void testGetTargetObject_givenKeyDataIsPortable_whenKeyFlagIsTrue_thenReturnKeyData() {
        //given
        Data keyData = mockPortableData();
        CachedQueryEntry entry = new CachedQueryEntry(serializationService, keyData, new Object(), Extractors.empty());

        //when
        Object targetObject = entry.getTargetObject(true);

        //then
        assertSame(keyData, targetObject);
    }

    @Test
    public void testGetTargetObject_givenKeyDataIsNotPortable_whenKeyFlagIsTrue_thenReturnKeyObject() {
        //given
        Object keyObject = "key";
        Data keyData = serializationService.toData(keyObject);
        CachedQueryEntry entry = new CachedQueryEntry(serializationService, keyData, new Object(), Extractors.empty());

        //when
        Object targetObject = entry.getTargetObject(true);

        //then
        assertEquals(keyObject, targetObject);
    }

    @Test
    public void testEquals_givenSameInstance_thenReturnTrue() {
        CachedQueryEntry entry1 = newEntry("key");
        CachedQueryEntry entry2 = entry1;

        assertTrue(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherIsNull_thenReturnFalse() {
        CachedQueryEntry entry1 = newEntry("key");
        CachedQueryEntry entry2 = null;

        assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherIsDifferentClass_thenReturnFalse() {
        CachedQueryEntry entry1 = newEntry("key");
        Object entry2 = new Object();

        assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherHasDifferentKey_thenReturnFalse() {
        CachedQueryEntry entry1 = newEntry("key1");
        CachedQueryEntry entry2 = newEntry("key2");

        assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherHasEqualKey_thenReturnTrue() {
        CachedQueryEntry entry1 = newEntry("key");
        CachedQueryEntry entry2 = newEntry("key");

        assertTrue(entry1.equals(entry2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenNewEntry_whenSetValue_thenThrowUnsupportedOperationException() {
        //given
        CachedQueryEntry entry = newEntry("key");

        //when
        entry.setValue(new Object());
    }

    private CachedQueryEntry newEntry(Object key) {
        Data keyData = serializationService.toData(key);
        return new CachedQueryEntry(serializationService, keyData, new Object(), Extractors.empty());
    }

    private Data mockPortableData() {
        Data keyData = mock(Data.class);
        when(keyData.isPortable()).thenReturn(true);
        return keyData;
    }


}
