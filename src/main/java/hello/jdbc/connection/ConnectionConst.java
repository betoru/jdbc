package hello.jdbc.connection;

/**
 * 커넥션 상수로 쓸거라서 객체 생성을 하면 안되겠지?
 * 생성을 못하게 abstract 로 만듦
 */
public abstract class ConnectionConst {
  //외부에서 쓸거니까 public 내부에서'만' 쓸거라면 private
  //h2 DB 접근 방식은 규약이라서 아래와 같이 작성해야함.
  public static final String URL = "jdbc:h2:tcp://localhost/~/test";
  public static final String USERNAME = "sa";
  public static final String PASSWORD = "";


}
