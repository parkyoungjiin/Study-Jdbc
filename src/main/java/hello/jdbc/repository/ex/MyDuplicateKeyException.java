package hello.jdbc.repository.ex;

//런타임 에러를 상속받아도 되지만 DB에서 발생한 에러임을 직관적으로 알기 위해서 MyDbException을 상속받는다.
public class MyDuplicateKeyException extends MyDbException {
    public MyDuplicateKeyException() {
        super();
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
