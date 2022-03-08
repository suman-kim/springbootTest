package com.jpabook.jpashop.service;

import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.domain.item.Book;
import com.jpabook.jpashop.domain.item.Item;
import com.jpabook.jpashop.exception.NotEnoughStockException;
import com.jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.util.AssertionErrors.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() {

        //given
        Member member = createMember();

        Book book = createBook("시골 jpa", 10000, 10);

        int orderCount = 2;
        System.out.println("member.getId() ==" + member.getId());
        System.out.println("book.getId() ==" + book.getId());
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        System.out.println("getOrder.getOrderItems() == " + getOrder.getOrderItems());

        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다.",1,getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다.",10000 * orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야 한다.",8,book.getStockQuantity());

    }

    @Test
    public void 주문취소() {

        //given
        Member member = createMember();
        Book item = createBook("시골 jpa",10000,10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(),item.getId(),orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("주문 취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL,getOrder.getStatus());
        Assert.assertEquals("주문이 취소 된 상품은 그만큼 재고가 증가해야한다",10,item.getStockQuantity());

    }


    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount = 11;
        //when
        try {
            orderService.order(member.getId(), item.getId(), orderCount); //예외 터짐
        } catch(NotEnoughStockException e){
            return;
        }
        //then
        fail("재고 수량 부족 예외가 발생해야 한다");
    }

    private Book createBook(String name, int orderPrice, int count) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(orderPrice);
        book.setStockQuantity(count);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }
}