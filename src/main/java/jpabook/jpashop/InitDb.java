package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final  InitServie initServie;

    @PostConstruct
    public void init() {
        initServie.dbInit1();
        initServie.dbInit2();

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitServie {
        private final EntityManager em;

        public void dbInit1() {
            Member member = getMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = getBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = getBook("JPA2 BOOK", 20000, 500);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = getMember("userB", "진수", "2", "222222");
            em.persist(member);

            Book book1 = getBook("SPRING1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = getBook("SPRING2 BOOK", 20000, 500);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Book getBook(String s, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(s);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        private Member getMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }
}
