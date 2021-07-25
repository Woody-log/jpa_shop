package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepositiory;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.service.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepositiory orderRepositiory;
    private final OrderQueryRepository orderQaueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepositiory.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepositiory.findAll(new OrderSearch());
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }

    // 일 대 다 페치조인을 하지만 결국에 데이터가 뻥튀기된다.
    // db에서 뻥튀기된 결과를 application에서 distinct로 최적화 해준다.
    // db => application으로의 데이터 전송량이 너무 많고 application에서 distinct 비용 소모.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> allWithItem = orderRepositiory.findAllWithItem();
        List<OrderDto> result = allWithItem.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return result;
    }

    // batch size를 통하여 정확하게 필요한 데이터만 조회한다.
    // 패치조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB데이터 전송량이 감소한다.
    // 컬렉션 패치 조인은 페이징이 불가능하다. 하지만 이방법은 페이징이 가능하다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit) {
        List<Order> allWithItem = orderRepositiory.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = allWithItem.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return result;
    }

    // Dto로 받아오기 n+1 문제 발생.
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQaueryRepository.findOrderQueryDto();
    }

    // Dto에 쿼리 두번으로 해결
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQaueryRepository.findAllByDto_optimization();
    }

    // 쿼리 한방으로 해결
    // order 기준으로 페이징이 안된다.
    /*@GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() {
        return orderQaueryRepository.findAllByDto_flat();
    }*/

    @Data
    static class OrderDto {
        Long orderId;
        String name;
        LocalDateTime orderDate;
        OrderStatus orderStaus;
        Address address;
        List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStaus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;    // 상품명
        private int orderPrice;     // 주문 가격
        private int count;          // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
