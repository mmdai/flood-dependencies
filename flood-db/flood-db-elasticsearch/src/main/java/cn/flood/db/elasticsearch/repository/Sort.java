package cn.flood.db.elasticsearch.repository;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.search.sort.SortOrder;

/**
 * program: esdemo description: 排序对象封装 author: X-Pacific zhang create: 2019-01-21 17:16
 **/
public class Sort {

  private List<Order> orders = null;


  public Sort(Order... ods) {
    orders = new ArrayList<>();
    for (Order od : ods) {
      orders.add(od);
    }
  }

  public List<Order> listOrders() {
    return orders;
  }

  public Sort and(Sort sort) {
    if (orders == null) {
      orders = new ArrayList<>();
    }
    sort.orders.forEach(order -> orders.add(order));
    return this;
  }

  public static class Order implements Serializable {

    private final SortOrder direction;
    private final String property;

    public Order(SortOrder direction, String property) {
      this.direction = direction;
      this.property = property;
    }

    public SortOrder getDirection() {
      return direction;
    }

    public String getProperty() {
      return property;
    }
  }
}
