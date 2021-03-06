/*
 * Copyright (c) 2008-2012 akquinet tech@spree GmbH
 *
 * This file is part of Hibersap.
 *
 * Hibersap is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Hibersap is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Hibersap. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.hibersap.conversion;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharConverterTest {

    private CharConverter converter = new CharConverter();

    @Test
    public void testConvertToJava() {
        assertEquals( (Object) ' ', converter.convertToJava( null ) );
        assertEquals( (Object) ' ', converter.convertToJava( " " ) );
        assertEquals( (Object) 'A', converter.convertToJava( "A" ) );
        assertEquals( (Object) 'A', converter.convertToJava( "AB" ) );
    }

    @Test
    public void testConvertToSap() {
        assertEquals( "", converter.convertToSap( null ) );
        assertEquals( " ", converter.convertToSap( ' ' ) );
        assertEquals( "A", converter.convertToSap( 'A' ) );
    }
}
