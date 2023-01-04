package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * fileName       : CheckedTest
 * author         : baehyoyeol
 * date           : 2023/01/03
 * description    :
 */
@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception 을 상속받으면 컴파일러가 체크하는 '체크예외'가 된다.
     *  컴파일러가 체크하는 '체크예외' 의 의미를 코드로 확인해보자.
     */
    static class MyCheckedException extends Exception {
        //메시지를 가지는 생성자를 만들었음
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드를 넣어보자.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                //예외 처리 로직
                log.info("예외처리, message={}",e.getMessage(),e);
                //위의 로그에 의해 메시지와 stackTrace 를 남기고 정상 프로세스를 진행한다.
            }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언해야 한다.
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        /**
         * 체크 예외라는 것은 기본적으로 둘 중 하나는 반드시 해야한다.
         *  1. 잡아서 처리하거나
         *  2. 던지거나 (= 밖으로 던지는 것을 선언부에 명시해야 던지는 것. throws)
         *  그런데 아래의 코드는 둘 다 하지 않았다.
         */
        public void call() throws MyCheckedException {
//            throw new MyCheckedException("ex");
            throw new MyCheckedException("ex");
        }
    }
}
