package hello.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {
    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }
    /**
     * RuntimeException 상속받은 예외는 언체크 예외.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }


    static class Service {
        Repository repository = new Repository();
        public void callCatch(){
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message ={}", e.getMessage(), e);
            }

        }

        /**
         * 언체크 예외는 throws를 선언하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }
    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
