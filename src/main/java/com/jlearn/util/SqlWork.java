package com.jlearn.util;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlWork<T> {
    T apply(Connection c) throws SQLException;
}
