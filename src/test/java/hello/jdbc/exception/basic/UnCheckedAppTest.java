package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);

    }

    static class Controller {
        Service service = new Service();

        public void request(){
            service.logic();
        }
    }


    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            // 체크 예외가 발생했을 때 매번 예외를 던져야 한다.
            repository.call();
            networkClient.call();
        }

    }
    static class NetworkClient{
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }
    static class Repository{
        //call() -> runSQL() -> SQLException -> RuntimeSQLException
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                //체크 예외를 언체크 예외로 변환하여 던진다.
                throw new RuntimeSQLException(e);
            }

        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
