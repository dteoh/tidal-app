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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RippleModel.
 * 
 * @author Douglas Teoh
 * 
 */
public class RippleModelTests {

    private RippleModel model;
    private Object id;
    private String origin;
    private String subject;
    private String content;
    private long received;

    @Before
    public void setUp() {
        id = "TestID";
        origin = "TestingProcess";
        subject = "RippleModelTests";
        content = "TestingProcedure";
        received = System.currentTimeMillis();
        model = new RippleModel(id, origin, subject, content, received);
    }

    @After
    public void tearDown() {
        model = null;
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#getId()}.
     */
    @Test
    public void testGetId() {
        assertEquals(id, model.getId());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#getOrigin()}.
     */
    @Test
    public void testGetOrigin() {
        assertEquals(origin, model.getOrigin());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#getSubject()}.
     */
    @Test
    public void testGetSubject() {
        assertEquals(subject, model.getSubject());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#getContent()}.
     */
    @Test
    public void testGetContent() {
        assertEquals(content, model.getContent());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#getReceived()}.
     */
    @Test
    public void testGetReceived() {
        assertEquals(received, model.getReceived());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject1() {
        assertTrue(model.equals(model));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.models.RippleModel#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject2() {
        assertFalse(model.equals(null));
    }

    /**
     * Test comparison between two models, when the first model has an older
     * receive time than the second model. Both models have the same subject
     * line.
     */
    @Test
    public void testCompareTo1() {
        final RippleModel older = new RippleModel(id, origin, subject, content,
                10000);
        final RippleModel newer = new RippleModel(id, origin, subject, content,
                20000);

        assertTrue(older.compareTo(newer) > 0);
    }

    /**
     * Test comparison between two models, when the first model has the same
     * receive time as the second model. Both models have the same subject line.
     */
    @Test
    public void testCompareTo2() {
        final RippleModel same1 = new RippleModel(id, origin, subject, content,
                10000);
        final RippleModel same2 = new RippleModel(id, origin, subject, content,
                10000);

        assertTrue(same1.compareTo(same2) == 0);
    }

    /**
     * Test comparison between two models, when the first model has the same
     * receive time as the second model. The first model has a subject that is
     * lexically ordered before the second model's subject.
     */
    @Test
    public void testCompareTo3() {
        final RippleModel same1 = new RippleModel(id, origin, "ABC", content,
                10000);
        final RippleModel same2 = new RippleModel(id, origin, "BCD", content,
                10000);

        assertTrue(same1.compareTo(same2) < 0);
    }

    /**
     * Test comparison between two models, when the first model has the same
     * receive time as the second model. The first model has a subject that is
     * lexically ordered after the second model's subject.
     */
    @Test
    public void testCompareTo4() {
        final RippleModel same1 = new RippleModel(id, origin, "BCD", content,
                10000);
        final RippleModel same2 = new RippleModel(id, origin, "ABC", content,
                10000);

        assertTrue(same1.compareTo(same2) > 0);
    }

    /**
     * Test comparison against null object.
     */
    @Test
    public void testCompareTo5() {
        assertTrue(model.compareTo(null) < 0);
    }

    /**
     * Reflexive property.
     */
    @Test
    public void testEquals1() {
        assertTrue(model.equals(model));
    }

    /**
     * Symmetric property.
     */
    @Test
    public void testEquals2() {
        final RippleModel model2 = new RippleModel(id, origin, subject,
                content, received);
        assertTrue(model.equals(model2));
        assertTrue(model2.equals(model));
    }

    /**
     * Transitive property.
     */
    @Test
    public void testEquals3() {
        final RippleModel model2 = new RippleModel(id, origin, subject,
                content, received);
        final RippleModel model3 = new RippleModel(id, origin, subject,
                content, received);

        assertTrue(model.equals(model2));
        assertTrue(model2.equals(model3));
        assertTrue(model.equals(model3));
    }

    /**
     * Inequality against null.
     */
    @Test
    public void testEquals4() {
        assertFalse(model.equals(null));
    }

    /**
     * Incompatible class.
     */
    @Test
    public void testEquals5() {
        assertFalse(model.equals("string"));
    }

    /**
     * Null content.
     */
    @Test
    public void testEquals6() {
        final RippleModel model2 = new RippleModel(id, origin, subject, null,
                received);
        assertFalse(model.equals(model2));
    }

    /**
     * Different content.
     */
    @Test
    public void testEquals7() {
        final RippleModel model2 = new RippleModel(id, origin, subject, "Blah",
                received);
        assertFalse(model.equals(model2));
    }

    /**
     * Null ID.
     */
    @Test
    public void testEquals8() {
        final RippleModel model2 = new RippleModel(null, origin, subject,
                content, received);
        assertFalse(model.equals(model2));
    }

    /**
     * Different ID.
     */
    @Test
    public void testEquals9() {
        final RippleModel model2 = new RippleModel("Blah", origin, subject,
                content, received);
        assertFalse(model.equals(model2));
    }

    /**
     * Null origin.
     */
    @Test
    public void testEquals10() {
        final RippleModel model2 = new RippleModel(id, null, subject, content,
                received);
        assertFalse(model.equals(model2));
    }

    /**
     * Different origin.
     */
    @Test
    public void testEquals11() {
        final RippleModel model2 = new RippleModel(id, "Blah", subject,
                content, received);
        assertFalse(model.equals(model2));
    }

    /**
     * Different received.
     */
    @Test
    public void testEquals12() {
        final RippleModel model2 = new RippleModel(id, origin, subject,
                content, received + 1);
        assertFalse(model.equals(model2));
    }

    /**
     * Null subject.
     */
    @Test
    public void testEquals13() {
        final RippleModel model2 = new RippleModel(id, origin, null, content,
                received);
        assertFalse(model.equals(model2));
    }

    /**
     * Different subject.
     */
    @Test
    public void testEquals14() {
        final RippleModel model2 = new RippleModel(id, origin, "Blah", content,
                received);
        assertFalse(model.equals(model2));
    }

}
