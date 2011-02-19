package org.hibersap.session;

/*
 * Copyright (C) 2008 akquinet tech@spree GmbH
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibersap.HibersapException;
import org.hibersap.execution.Connection;
import org.hibersap.execution.PojoMapper;
import org.hibersap.mapping.model.BapiMapping;

/*
 * @author Carsten Erker
 */
public class SessionImpl
    implements Session, SessionImplementor
{
    private static final Log LOG = LogFactory.getLog( SessionImpl.class );

    private boolean closed = false;

    private final SessionManagerImplementor sessionManager;

    private PojoMapper pojoMapper;

    private Connection connection;

    private final Set<ExecutionInterceptor> interceptors = new HashSet<ExecutionInterceptor>();

    private final Credentials credentials;

    public SessionImpl( SessionManagerImplementor sessionManager )
    {
        this( sessionManager, null );
    }

    public SessionImpl( SessionManagerImplementor sessionManager, Credentials credentials )
    {
        this.sessionManager = sessionManager;
        this.credentials = credentials;

        pojoMapper = new PojoMapper( sessionManager.getConverterCache() );
        connection = sessionManager.getSettings().getContext().getConnection();
        if ( credentialsProvided() )
        {
            LOG.debug( "Providing credentials" );
            connection.setCredentials( credentials );
        }
        interceptors.addAll( sessionManager.getInterceptors() );
    }

    public Transaction beginTransaction()
    {
        errorIfClosed();
        return connection.beginTransaction( this );
    }

    public void close()
    {
        errorIfClosed();
        connection.close();
        setClosed();
    }

    private boolean credentialsProvided()
    {
        return credentials != null;
    }

    private void errorIfClosed()
    {
        if ( isClosed() )
        {
            throw new HibersapException( "Session is already closed" );
        }
    }

    public void execute( Object bapiObject )
    {
        errorIfClosed();
        Class<?> bapiClass = bapiObject.getClass();
        Map<Class<?>, BapiMapping> bapiMappings = sessionManager.getBapiMappings();
        if ( bapiMappings.containsKey( bapiClass ) )
        {
            execute( bapiObject, bapiMappings.get( bapiClass ) );
        }
        else
        {
            throw new HibersapException( bapiClass.getName() + " is not mapped as a Bapi class" );
        }
    }

    public void execute( Object bapiObject, BapiMapping bapiMapping )
    {
        errorIfClosed();

        String bapiName = bapiMapping.getBapiName();
        LOG.debug( "Executing " + bapiName );

        Map<String, Object> functionMap = pojoMapper.mapPojoToFunctionMap( bapiObject, bapiMapping );

        notifyInterceptorsBeforeExecution( bapiMapping, functionMap );

        connection.execute( bapiName, functionMap );

        notifyInterceptorsAfterExecution( bapiMapping, functionMap );

        pojoMapper.mapFunctionMapToPojo( bapiObject, functionMap, bapiMapping );
    }

    private void notifyInterceptorsAfterExecution( BapiMapping bapiMapping, Map<String, Object> functionMap )
    {
        for ( ExecutionInterceptor interceptor : interceptors )
        {
            interceptor.afterExecute( bapiMapping, functionMap );
        }
    }

    private void notifyInterceptorsBeforeExecution( BapiMapping bapiMapping, Map<String, Object> functionMap )
    {
        for ( ExecutionInterceptor interceptor : interceptors )
        {
            interceptor.beforeExecute( bapiMapping, functionMap );
        }
    }

    public SessionManagerImplementor getSessionManager()
    {
        return sessionManager;
    }

    public Transaction getTransaction()
    {
        errorIfClosed();
        return connection.getTransaction();
    }

    public boolean isClosed()
    {
        return closed;
    }

    private void setClosed()
    {
        closed = true;
    }

    public void addInterceptor( ExecutionInterceptor interceptor )
    {
        interceptors.add( interceptor );
    }
}
