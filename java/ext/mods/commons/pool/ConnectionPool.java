/*
 * Copyleft © 2024-2026 L2Brproject
 * * This file is part of L2Brproject derived from aCis409/RusaCis3.8
 * * L2Brproject is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License.
 * * L2Brproject is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
 * Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
 * as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;
import ext.mods.commons.logging.CLogger;
import ext.mods.Config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class ConnectionPool {
    private ConnectionPool() { throw new IllegalStateException("Utility class"); }
    
    private static final CLogger LOGGER = new CLogger(ConnectionPool.class.getName());
    private static final AtomicLong TOTAL_QUERIES = new AtomicLong(0);
    
    private static HikariDataSource _source;
    
    // Configuração para ativar/desativar o debug das queries
    private static final boolean DEBUG_QUERIES = false; 
    // Tempo em ms para considerar uma query "lenta" (se quiser logar só as lentas)
    private static final long SLOW_QUERY_THRESHOLD_MS = 50; 
    
    public static void init() {
        try {
            HikariConfig config = new HikariConfig();
            
                      
            config.setJdbcUrl(Config.DATABASE_URL);
            config.setUsername(Config.DATABASE_LOGIN);
            config.setPassword(Config.DATABASE_PASSWORD);
            
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            config.setMaximumPoolSize(50);
            config.setMinimumIdle(10);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(1800000);
            
            config.setConnectionTestQuery("SELECT 1"); 
            config.setLeakDetectionThreshold(60000);
            config.setPoolName("GameServerPool");
            config.setRegisterMbeans(true);
            
            _source = new HikariDataSource(config);
            
            LOGGER.info("HikariCP Pool: 50/10 | Ready!");
            if (DEBUG_QUERIES) {
                LOGGER.warn("HikariCP Query Debugging is ENABLED. This may impact performance.");
            }
            
        } catch (Exception e) {
            LOGGER.error("HikariCP failed!", e);
        }
    }
    
    public static void shutdown() {
        if (_source != null) {
            _source.close();
            _source = null;
        }
    }
    
    public static Connection getConnection() throws SQLException {
        Connection conn = _source.getConnection();
        TOTAL_QUERIES.incrementAndGet();
        
        if (DEBUG_QUERIES) {
            return wrapConnection(conn);
        }
        return conn;
    }
    
    public static long getTotalQueries() {
        return TOTAL_QUERIES.get();
    }

    public static String getStats() {
        if (_source == null) return "HikariCP: not initialized";
        try {
            var mx = _source.getHikariPoolMXBean();
            if (mx != null) {
                return String.format("HikariCP: %d active | %d idle | %d total | %d queries",
                    mx.getActiveConnections(),
                    mx.getIdleConnections(),
                    mx.getTotalConnections(),
                    TOTAL_QUERIES.get());
            }
        } catch (Exception ignored) { }
        return String.format("HikariCP: %d queries total", TOTAL_QUERIES.get());
    }

    // =========================================================================================
    // WRAPPER DE DEBUG (Proxy Pattern)
    // Intercepta as chamadas prepareStatement e createStatement para logar o SQL
    // =========================================================================================

    private static Connection wrapConnection(final Connection realConnection) {
        return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();

                    if ("prepareStatement".equals(methodName) && args != null && args.length > 0) {
                        String sql = (String) args[0];
                        // Você pode imprimir a stacktrace para saber de ONDE veio a query
                        // StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                        // LOGGER.info("QUERY PREPARED: " + sql + " | FROM: " + stack[3].getClassName() + "." + stack[3].getMethodName());
                        
                        LOGGER.info("QUERY PREPARED: " + sql);
                        
                        PreparedStatement ps = (PreparedStatement) method.invoke(realConnection, args);
                        return wrapPreparedStatement(ps, sql);
                    }
                    
                    if ("createStatement".equals(methodName)) {
                        Statement st = (Statement) method.invoke(realConnection, args);
                        return wrapStatement(st);
                    }

                    return method.invoke(realConnection, args);
                }
            }
        );
    }

    private static PreparedStatement wrapPreparedStatement(final PreparedStatement realPs, final String sql) {
        return (PreparedStatement) Proxy.newProxyInstance(
            PreparedStatement.class.getClassLoader(),
            new Class<?>[]{PreparedStatement.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    
                    if ("execute".equals(methodName) || "executeQuery".equals(methodName) || "executeUpdate".equals(methodName) || "executeBatch".equals(methodName)) {
                        long start = System.currentTimeMillis();
                        try {
                            return method.invoke(realPs, args);
                        } finally {
                            long duration = System.currentTimeMillis() - start;
                            if (duration > SLOW_QUERY_THRESHOLD_MS) {
                                LOGGER.warn("SLOW QUERY (" + duration + "ms): " + sql);
                            } else {
                                // Se quiser logar o tempo de TODAS as queries, descomente abaixo:
                                // LOGGER.info("EXECUTED (" + duration + "ms): " + sql);
                            }
                        }
                    }
                    return method.invoke(realPs, args);
                }
            }
        );
    }

    private static Statement wrapStatement(final Statement realSt) {
        return (Statement) Proxy.newProxyInstance(
            Statement.class.getClassLoader(),
            new Class<?>[]{Statement.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    
                    if (("execute".equals(methodName) || "executeQuery".equals(methodName) || "executeUpdate".equals(methodName)) && args != null && args.length > 0) {
                        String sql = (String) args[0];
                        long start = System.currentTimeMillis();
                        try {
                            LOGGER.info("DIRECT QUERY: " + sql);
                            return method.invoke(realSt, args);
                        } finally {
                            long duration = System.currentTimeMillis() - start;
                            if (duration > SLOW_QUERY_THRESHOLD_MS) {
                                LOGGER.warn("SLOW DIRECT QUERY (" + duration + "ms): " + sql);
                            }
                        }
                    }
                    return method.invoke(realSt, args);
                }
            }
        );
    }
}