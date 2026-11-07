package com.jlearn.util;

import com.jlearn.config.AppConfig;
import com.jlearn.exception.DataAccessException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ket noi + transaction.
 * NEU PROJECT DA CO lop lay Connection (HikariCP) thi XOA getConnection() o day va goi lop do;
 * chi can giu tx() / read() vi cac Service goi hai ham nay.
 * Cau hinh: app.properties (db.url, db.user, db.password, db.pool) hoac bien moi truong JLEARN_DB_URL ...
 */
public final class DbUtil {
    private static volatile HikariDataSource ds;

    private DbUtil() { }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            synchronized (DbUtil.class) {
                if (ds == null) {
                    HikariConfig hc = new HikariConfig();
                    hc.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    hc.setJdbcUrl(AppConfig.get("db.url", "jdbc:mysql://localhost:3306/nihongo_app"));
                    hc.setUsername(AppConfig.get("db.user", "root"));
                    hc.setPassword(AppConfig.get("db.password", ""));
                    hc.setMaximumPoolSize(AppConfig.getInt("db.pool", 10));
                    ds = new HikariDataSource(hc);
                }
            }
        }
        return ds.getConnection();
    }

    /** Chay cac cau SELECT (khong transaction rieng). */
    public static <T> T read(SqlWork<T> work) {
        try (Connection c = getConnection()) {
            return work.apply(c);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Chay mot transaction: thanh cong thi commit, bat ky loi nao thi rollback roi nem lai.
     * Gap deadlock (SQLState 40001) thi tu chay lai toi da 3 lan (work phai thuan DB, khong co tac dung phu ben ngoai).
     */
    public static <T> T tx(SqlWork<T> work) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return txOnce(work);
            } catch (DataAccessException e) {
                Throwable cause = e.getCause();
                boolean deadlock = cause instanceof SQLException && "40001".equals(((SQLException) cause).getSQLState());
                if (!deadlock || attempt >= 3) throw e;
                try { Thread.sleep(20L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); throw e; }
            }
        }
    }

    private static <T> T txOnce(SqlWork<T> work) {
        try (Connection c = getConnection()) {
            boolean oldAuto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                T result = work.apply(c);
                c.commit();
                return result;
            } catch (Throwable t) {
                try { c.rollback(); } catch (SQLException ignore) { /* giu loi goc */ }
                if (t instanceof SQLException) throw new DataAccessException((SQLException) t);
                if (t instanceof RuntimeException) throw (RuntimeException) t;
                if (t instanceof Error) throw (Error) t;
                throw new IllegalStateException(t);
            } finally {
                try { c.setAutoCommit(oldAuto); } catch (SQLException ignore) { /* bo qua */ }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public static void shutdown() {
        if (ds != null) ds.close();
    }
}
