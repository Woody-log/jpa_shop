package jpabook.jpashop.excpetion;

public class NoEnoughtStockException extends RuntimeException{
    public NoEnoughtStockException() {
        super();
    }

    public NoEnoughtStockException(String message) {
        super(message);
    }

    public NoEnoughtStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEnoughtStockException(Throwable cause) {
        super(cause);
    }
}
