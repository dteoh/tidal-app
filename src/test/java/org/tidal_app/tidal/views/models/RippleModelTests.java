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
import nl.jqno.equalsverifier.EqualsVerifier;

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
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#getId()}.
     */
    @Test
    public void testGetId() {
        assertEquals(id, model.getId());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#getOrigin()}.
     */
    @Test
    public void testGetOrigin() {
        assertEquals(origin, model.getOrigin());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#getSubject()}.
     */
    @Test
    public void testGetSubject() {
        assertEquals(subject, model.getSubject());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#getContent()}.
     */
    @Test
    public void testGetContent() {
        assertEquals(content, model.getContent());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#getReceived()}.
     */
    @Test
    public void testGetReceived() {
        assertEquals(received, model.getReceived());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject1() {
        assertTrue(model.equals(model));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.views.resources.models.RippleModel#equals(java.lang.Object)}
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
     * Test the contract for object equals.
     */
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(RippleModel.class).verify();
    }

}
