package com.jlearn.exception;

import java.sql.SQLException;

/** Boc SQLException thanh loi khong kiem tra; BaseServlet tra 500 va khong lo chi tiet SQL ra man hinh. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(SQLException cause) {
        super(cause.getMessage(), cause);
    }
}
