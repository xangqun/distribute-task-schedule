/**
 * Copyright 2017-2025 schedule Group.
 */
package com.xxl.job.admin.core.druid;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.stat.JdbcConnectionStat;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.profile.Profiler;

import java.sql.SQLException;

/**
 * @author laixiangqun
 * @since 2018-7-10
 */
public class MonitorFilter extends StatFilter {
    private final static Log LOG                        = LogFactory.getLog(MonitorFilter.class);

    @Override
    public void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        internalAfterStatementExecute(statement, false, updateCount);
    }

    @Override
    public void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        internalAfterStatementExecute(statement, true);
    }

    @Override
    public void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        internalAfterStatementExecute(statement, firstResult);
    }

    @Override
    public void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        internalAfterStatementExecute(statement, false, result);

    }

    /**
     * 超时告警
     * @param statement
     * @param firstResult
     * @param updateCountArray
     */
    private final void internalAfterStatementExecute(StatementProxy statement, boolean firstResult,
                                                     int... updateCountArray) {
        final long nowNano = System.nanoTime();
        final long nanos = nowNano - statement.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().afterExecute(nanos);

        final JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.incrementExecuteSuccessCount();

            sqlStat.decrementRunningCount();
            sqlStat.addExecuteTime(statement.getLastExecuteType(), firstResult, nanos);
            statement.setLastExecuteTimeNano(nanos);
            if ((!firstResult) && statement.getLastExecuteType() == StatementExecuteType.Execute) {
                try {
                    int updateCount = statement.getUpdateCount();
                    sqlStat.addUpdateCount(updateCount);
                } catch (SQLException e) {
                    LOG.error("getUpdateCount error", e);
                }
            } else {
                for (int updateCount : updateCountArray) {
                    sqlStat.addUpdateCount(updateCount);
                    sqlStat.addFetchRowCount(0);
                    MonitorFilterContext.getMonitorInstance().addUpdateCount(updateCount);
                }
            }

            long millis = nanos / (1000 * 1000);
            if (millis >= slowSqlMillis) {
                String slowParameters = buildSlowParameters(statement);
                sqlStat.setLastSlowParameters(slowParameters);

                String lastExecSql = statement.getLastExecuteSql();
                if (logSlowSql) {
                    LOG.error("slow sql " + millis + " millis. " + lastExecSql + "" + slowParameters);
                }
            }
        }

        String sql = statement.getLastExecuteSql();
        MonitorFilterContext.getMonitorInstance().executeAfter(sql, nanos, null);

        Profiler.release(nanos);
    }

    /**
     * sql错误告警
     * @param statement
     * @param sql
     * @param error
     */
    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        ConnectionProxy connection = statement.getConnectionProxy();
        JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        long nanos = System.nanoTime() - statement.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().error(error);
        dataSourceStat.getStatementStat().afterExecute(nanos);

        connectionCounter.error(error);

        // SQL
        JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.decrementExecutingCount();
            sqlStat.error(error);
            sqlStat.addExecuteTime(statement.getLastExecuteType(), statement.isFirstResultSet(), nanos);
            statement.setLastExecuteTimeNano(nanos);
        }
        MonitorFilterContext.getMonitorInstance().executeAfter(sql, nanos, error);
        Profiler.release(nanos);

    }
}
