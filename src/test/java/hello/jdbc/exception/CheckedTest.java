package hello.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {
    /**
     * Exception을 상속받으면 체크 예외에 해당한다.
     */
    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void checked_throw(){
        Service service = new Service();
        //assertThatThrownBy 예외가 발생하는 테스트를 할 때 사용.
        assertThatThrownBy(()-> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }


    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나 던지거나 둘 중 하나를 필수로 선택해야 한다.
     */
    static class Service {
        Repository repository = new Repository();
        /**
         * 예외를 잡아서  처리하는 코드
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository{
        public void call() throws MyCheckedException {
            //예외를 try ~ catch로 잡지 않았기에 메서드에 던지는 것을 선언해야 한다.
            // 던지는 것을 선언하지 않으면 컴파일 에러가 발생하기에 반드시 선언해야 한다.
            throw new MyCheckedException("ex");
        }
    }
}
