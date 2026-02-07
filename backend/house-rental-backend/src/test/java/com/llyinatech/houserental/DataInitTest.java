package com.llyinatech.houserental;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
public class DataInitTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void initPermissionData() {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/permission_data.sql"));
            System.out.println("Permission data initialized successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize permission data", e);
        }
    }
}
