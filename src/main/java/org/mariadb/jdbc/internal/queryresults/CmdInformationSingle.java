package org.mariadb.jdbc.internal.queryresults;

/*
MariaDB Client for Java

Copyright (c) 2012-2014 Monty Program Ab.

This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option)
any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
for more details.

You should have received a copy of the GNU Lesser General Public License along
with this library; if not, write to Monty Program Ab info@montyprogram.com.

This particular MariaDB Client for Java file is work
derived from a Drizzle-JDBC. Drizzle-JDBC file which is covered by subject to
the following copyright and notice provisions:

Copyright (c) 2009-2011, Marcus Eriksson

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of the driver nor the names of its contributors may not be
used to endorse or promote products derived from this software without specific
prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS  AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/

import org.mariadb.jdbc.internal.protocol.Protocol;
import org.mariadb.jdbc.internal.queryresults.resultset.SelectResultSet;

import java.sql.ResultSet;
import java.sql.Statement;

public class CmdInformationSingle implements CmdInformation {
    private long insertId;
    private long updateCount;
    private int autoIncrement;

    /**
     * Single Query result.
     *
     * @param insertId      insert id.
     * @param updateCount   update count
     * @param autoIncrement connection autoincrement
     */
    public CmdInformationSingle(long insertId, long updateCount, int autoIncrement) {
        this.insertId = insertId;
        this.updateCount = updateCount;
        this.autoIncrement = autoIncrement;
    }

    @Override
    public int[] getUpdateCounts() {
        return new int[] {(int) updateCount};
    }

    @Override
    public long getLargeUpdateCount() {
        return updateCount;
    }

    @Override
    public long[] getLargeUpdateCounts() {
        return new long[] {updateCount};
    }

    @Override
    public int getUpdateCount() {
        return (int) updateCount;
    }

    @Override
    public void addStats(long updateCount, long insertId) {
        //not expected
    }

    @Override
    public void addStats(long updateCount) {
        //not expected
    }

    /**
     * Get generated Keys.
     *
     * @param protocol current protocol
     * @return a resultSet containing the single insert ids.
     */
    public ResultSet getGeneratedKeys(Protocol protocol) {
        if (insertId == 0) {
            return SelectResultSet.createEmptyResultSet();
        } else {
            if (updateCount == 1) {
                return SelectResultSet.createGeneratedData(new long[]{insertId}, protocol, true);
            } else if (updateCount == Statement.EXECUTE_FAILED) {
                return SelectResultSet.createEmptyResultSet();
            } else {
                long[] ret = new long[(int) updateCount];
                for (int i = 0; i < updateCount; i++) {
                    ret[i] = insertId + i * autoIncrement;
                }
                return SelectResultSet.createGeneratedData(ret, protocol, true);
            }
        }
    }

    public int getCurrentStatNumber() {
        return 1;
    }

    @Override
    public boolean moreResults() {
        updateCount = NO_UPDATE_COUNT;
        return false;
    }

    public boolean isCurrentUpdateCount() {
        return updateCount != NO_UPDATE_COUNT;
    }
}

