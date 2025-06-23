package notizprojekt.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());

    @Autowired
    private DataSource dataSource;

    @Bean
    public CommandLineRunner testDatabaseConnection() {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                logger.info("Database connection test: " + connection.getCatalog());
                logger.info("Database connection successful!");
            } catch (SQLException e) {
                logger.severe("Database connection failed: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public CommandLineRunner checkTables(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Check if the nutzer table exists
                Integer userCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM nutzer", Integer.class);
                logger.info("Found " + userCount + " users in the database");
                
                // Check if the notiz table exists
                Integer noteCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM notiz", Integer.class);
                logger.info("Found " + noteCount + " notes in the database");
                
                // Check if position_x and position_y columns exist in notiz table
                try {
                    Integer columnCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notiz' AND COLUMN_NAME IN ('position_x', 'position_y')", 
                            Integer.class);
                    if (columnCount != null && columnCount == 2) {
                        logger.info("Position columns exist in notiz table");
                    } else {
                        logger.info("Adding missing position columns to notiz table");
                        if (columnCount == null || columnCount == 0) {
                            jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_x INT DEFAULT 0");
                            jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_y INT DEFAULT 0");
                        } else if (columnCount == 1) {
                            // Check which one exists
                            Integer posXExists = jdbcTemplate.queryForObject(
                                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notiz' AND COLUMN_NAME = 'position_x'", 
                                    Integer.class);
                            if (posXExists == 0) {
                                jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_x INT DEFAULT 0");
                            } else {
                                jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_y INT DEFAULT 0");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error checking position columns: " + e.getMessage());
                }
                
                // Check if color column exists in notiz table
                try {
                    Integer colorColumnExists = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notiz' AND COLUMN_NAME = 'color'", 
                            Integer.class);
                    if (colorColumnExists != null && colorColumnExists > 0) {
                        logger.info("Color column exists in notiz table");
                    } else {
                        logger.info("Adding color column to notiz table");
                        jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN color VARCHAR(20) DEFAULT '#FFFF88'");
                    }
                } catch (Exception e) {
                    logger.warning("Error checking color column: " + e.getMessage());
                }
                
            } catch (Exception e) {
                logger.severe("Error checking database tables: " + e.getMessage());
                throw e;
            }
        };
    }
}