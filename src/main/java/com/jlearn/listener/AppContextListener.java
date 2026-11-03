package com.jlearn.listener;

import com.jlearn.config.AppConfig;
import com.jlearn.util.DbUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;
import java.util.logging.Logger;

/** Khoi dong/tat: mui gio co dinh Asia/Ho_Chi_Minh (quy tac chung 9), tao thu muc upload, dong pool. */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        try {
            Path dir = AppConfig.uploadDir();
            Files.createDirectories(dir);
            LOG.info("Thu muc upload: " + dir);
        } catch (Exception e) {
            LOG.warning("Khong tao duoc thu muc upload: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DbUtil.shutdown();
    }
}
