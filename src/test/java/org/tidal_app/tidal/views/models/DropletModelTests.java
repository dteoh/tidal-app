/*
 * Tidal, a communications aggregation and notification tool. 
 * Copyright (C) 2010 Douglas Teoh 
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details. You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.tidal_app.tidal.views.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Unit tests for {@link DropletModel}.
 * 
 * @author Douglas Teoh
 * 
 */
public class DropletModelTests {

    private RippleModel ripple1;
    private RippleModel ripple2;
    private RippleModel ripple3;

    @Before
    public void setUp() {
        ripple1 = new RippleModel("ONE", "UNIT_TEST", "Ripple One", "", 10000);
        ripple2 = new RippleModel("TWO", "UNIT_TEST", "Ripple Two", "", 20000);
        ripple3 = new RippleModel("THREE", "UNIT_TEST", "Ripple Three", "",
                30000);
    }

    @After
    public void tearDown() {
        ripple1 = null;
        ripple2 = null;
        ripple3 = null;
    }

    @Test
    public void testGetDropletName() {
        final String dropletName = "TestDroplet";
        final DropletModel model = new DropletModel(dropletName, ripple1);
        assertEquals(dropletName, model.getDropletName());
    }

    /**
     * Array: Test no ripples.
     */
    @Test
    public void testDropletModelStringRippleModelArray1() {
        final DropletModel model = new DropletModel("TestDroplet");
        assertTrue(Iterables.isEmpty(model.getDropletContents()));
    }

    /**
     * Array: One ripple.
     */
    @Test
    public void testDropletModelStringRippleModelArray2() {
        final DropletModel model = new DropletModel("TestDroplet", ripple1);
        assertEquals(1, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
    }

    /**
     * Array: Two ripples.
     */
    @Test
    public void testDropletModelStringRippleModelArray3() {
        final DropletModel model = new DropletModel("TestDroplet", ripple1,
                ripple2);
        assertEquals(2, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple2));
    }

    /**
     * Array: Three ripples.
     */
    @Test
    public void testDropletModelStringRippleModelArray4() {
        final DropletModel model = new DropletModel("TestDroplet", ripple1,
                ripple2, ripple3);
        assertEquals(3, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple3));
    }

    /**
     * Iterable: No ripples.
     */
    @Test
    public void testDropletModelStringIterableOfRippleModel1() {
        final List<RippleModel> ripples = Lists.newLinkedList();
        final DropletModel model = new DropletModel("TestDroplet", ripples);
        assertTrue(Iterables.isEmpty(model.getDropletContents()));
    }

    /**
     * Iterable: One ripple.
     */
    @Test
    public void testDropletModelStringIterableOfRippleModel2() {
        final List<RippleModel> ripples = Lists.newArrayList(ripple1);
        final DropletModel model = new DropletModel("TestDroplet", ripples);
        assertEquals(1, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
    }

    /**
     * Iterable: Two ripples.
     */
    @Test
    public void testDropletModelStringIterableOfRippleModel3() {
        final List<RippleModel> ripples = Lists.newArrayList(ripple1, ripple2);
        final DropletModel model = new DropletModel("TestDroplet", ripples);
        assertEquals(2, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple2));
    }

    /**
     * Iterable: Three ripples.
     */
    @Test
    public void testDropletModelStringIterableOfRippleModel4() {
        final List<RippleModel> ripples = Lists.newArrayList(ripple1, ripple2,
                ripple3);
        final DropletModel model = new DropletModel("TestDroplet", ripples);
        assertEquals(3, Iterables.size(model.getDropletContents()));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(model.getDropletContents(), ripple3));
    }

    /**
     * Merge two empty models together.
     */
    @Test
    public void testMergeWith1() {
        final DropletModel model1 = new DropletModel("TestDroplet1");
        final DropletModel model2 = new DropletModel("TestDroplet2");

        final DropletModel merged1 = model1.mergeWith(model2);
        assertTrue(Iterables.isEmpty(merged1.getDropletContents()));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertTrue(Iterables.isEmpty(merged2.getDropletContents()));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge one empty model with a model with one ripple.
     */
    @Test
    public void testMergeWith2() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1);
        final DropletModel model2 = new DropletModel("TestDroplet2");

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(1, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(1, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge two models, each with one ripple.
     */
    @Test
    public void testMergeWith3() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple2);

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(2, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple2));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(2, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple2));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge two models, each with the same/duplicate ripple.
     */
    @Test
    public void testMergeWith4() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple1);

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(1, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(1, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge two models, each with the same/duplicate ripple and one
     * non-duplicate ripple.
     */
    @Test
    public void testMergeWith5() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1,
                ripple3);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple1,
                ripple2);

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(3, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple3));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(3, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple3));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge two models, one with only one ripple, the other with two ripples.
     */
    @Test
    public void testMergeWith6() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple2,
                ripple3);

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(3, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple3));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(3, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple3));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Merge two models, one with only one ripple, the other with two ripples.
     */
    @Test
    public void testMergeWith7() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple2);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple1,
                ripple3);

        final DropletModel merged1 = model1.mergeWith(model2);
        assertEquals(3, Iterables.size(merged1.getDropletContents()));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged1.getDropletContents(), ripple3));
        assertEquals("TestDroplet1", merged1.getDropletName());

        final DropletModel merged2 = model2.mergeWith(model1);
        assertEquals(3, Iterables.size(merged2.getDropletContents()));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple1));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple2));
        assertTrue(Iterables.contains(merged2.getDropletContents(), ripple3));
        assertEquals("TestDroplet2", merged2.getDropletName());
    }

    /**
     * Test if droplet contents are sorted once the model is constructed.
     */
    @Test
    public void testGetDropletContents1() {
        final DropletModel model = new DropletModel("TestDroplet1", ripple1,
                ripple2);

        // Newest first.
        final List<RippleModel> ripples = Lists.newArrayList(ripple2, ripple1);
        assertTrue(Iterables.elementsEqual(model.getDropletContents(), ripples));
    }

    /**
     * Test if droplet contents are sorted if models are merged.
     */
    @Test
    public void testGetDropletContents2() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1,
                ripple3);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple2);
        final DropletModel merged = model1.mergeWith(model2);

        // Newest first.
        final List<RippleModel> ripples = Lists.newArrayList(ripple3, ripple2,
                ripple1);
        assertTrue(Iterables
                .elementsEqual(merged.getDropletContents(), ripples));
    }

    /**
     * Test if droplet contents are sorted if models are merged and duplicates
     * are removed.
     */
    @Test
    public void testGetDropletContents3() {
        final DropletModel model1 = new DropletModel("TestDroplet1", ripple1,
                ripple3);
        final DropletModel model2 = new DropletModel("TestDroplet2", ripple1,
                ripple2);
        final DropletModel merged = model1.mergeWith(model2);

        // Newest first.
        final List<RippleModel> ripples = Lists.newArrayList(ripple3, ripple2,
                ripple1);
        assertTrue(Iterables
                .elementsEqual(merged.getDropletContents(), ripples));
    }

}
