
import org.junit.Test;
import org.testcontainers.databend.DatabendContainer;

import java.sql.*;

import com.databend.jdbc.DatabendStatement;

import static java.lang.String.format;

public class TestContainerDatabend {
    private final DatabendContainer dockerContainer;

    public TestContainerDatabend() {
        dockerContainer = new DatabendContainer("datafuselabs/databend:v1.2.615");
        dockerContainer.withUsername("databend").withPassword("databend").withUrlParam("ssl", "false");
        dockerContainer.start();
    }

    @Test
    public void testSimple() {
        try (Connection connection = DriverManager.getConnection(getJdbcUrl())) {
            DatabendStatement statement = (DatabendStatement) connection.createStatement();
            statement.execute("SELECT 1");
            ResultSet r = statement.getResultSet();
            while (r.next()) {
                int resultSetInt = r.getInt(1);
                System.out.println(resultSetInt);
                assert resultSetInt == 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute statement: ", e);
        }
    }

    public String getJdbcUrl() {
        return format("jdbc:databend://%s:%s@%s:%s/",
                dockerContainer.getUsername(),
                dockerContainer.getPassword(),
                dockerContainer.getHost(),
                dockerContainer.getMappedPort(8000));
    }
}
