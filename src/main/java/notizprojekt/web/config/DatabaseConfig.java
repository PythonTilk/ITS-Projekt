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
                    jdbcTemplate.queryForObject(
                            "SELECT position_x, position_y FROM notiz LIMIT 1", 
                            (rs, rowNum) -> rs.getInt("position_x") + "," + rs.getInt("position_y"));
                    logger.info("Position columns exist in notiz table");
                } catch (Exception e) {
                    logger.info("Adding position columns to notiz table");
                    jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_x INT DEFAULT 0");
                    jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN position_y INT DEFAULT 0");
                }
                
                // Check if color column exists in notiz table
                try {
                    jdbcTemplate.queryForObject(
                            "SELECT color FROM notiz LIMIT 1", 
                            (rs, rowNum) -> rs.getString("color"));
                    logger.info("Color column exists in notiz table");
                } catch (Exception e) {
                    logger.info("Adding color column to notiz table");
                    jdbcTemplate.execute("ALTER TABLE notiz ADD COLUMN color VARCHAR(20) DEFAULT '#FFFF88'");
                }
                
            } catch (Exception e) {
                logger.severe("Error checking database tables: " + e.getMessage());
                throw e;
            }
        };
    }
}