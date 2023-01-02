package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConnectionUtil {

  public static Connection getConnection() {
    try {
      /**
       * 이게 동작을 하는지 테스트코드로 확인
       */
      Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); //hello.jdbc.connection.ConnectionConst -> URL, USERNAME, PASSWORD
      log.info("get connection={}, class={}", connection, connection.getClass());
      return connection;
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }
  }
}
