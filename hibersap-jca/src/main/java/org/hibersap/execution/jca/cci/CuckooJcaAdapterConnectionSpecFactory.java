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

package org.hibersap.execution.jca.cci;

import org.hibersap.InternalHiberSapException;
import org.hibersap.session.Credentials;

import javax.resource.cci.ConnectionSpec;
import java.util.Arrays;

public class CuckooJcaAdapterConnectionSpecFactory extends AbstractConnectionSpecFactory {

    private static final String CONNECTION_SPEC_IMPL_CLASS_NAME = "org.cuckoo.ra.cci.ApplicationPropertiesImpl";

    public ConnectionSpec createConnectionSpec( Credentials credentials ) throws InternalHiberSapException {
        try {
            Object[] arguments = {
                    credentials.getUser(),
                    credentials.getPassword()
//                    ,
//                    credentials.getLanguage(),
//                    credentials.getClient(),
//                    credentials.getAliasUser(),
//                    credentials.getSsoTicket(),
//                    credentials.getX509Certificate()
            };

            Class<?>[] parameterTypes = new Class<?>[arguments.length];
            Arrays.fill( parameterTypes, String.class );

            Class<?> connSpecClass = getConnectionSpecClass( CONNECTION_SPEC_IMPL_CLASS_NAME );
            return newConnectionSpecInstance( connSpecClass, parameterTypes, arguments );
        } catch ( IllegalArgumentException e ) {
            throw new InternalHiberSapException( e.getMessage(), e );
        } catch ( ClassNotFoundException e ) {
            throw new InternalHiberSapException( e.getMessage(), e );
        }
    }
}
