package ethniconnect_backend.Order;

import ethniconnect_backend.ChefCreateMenu.*;
import ethniconnect_backend.ChefDetails.Chef;
import ethniconnect_backend.ChefDetails.ChefRepository;
import ethniconnect_backend.Cuisines.CuisineCategoriesRepository;
import ethniconnect_backend.CustomerDetails.Customer;
import ethniconnect_backend.CustomerDetails.CustomerRepository;
import ethniconnect_backend.UserCredentials.UserCredentialsRepository;
import ethniconnect_backend.email.EmailSender;
import ethniconnect_backend.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ChefMenuRepository chefMenuRepository;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    CuisineCategoriesRepository cuisineCategoriesRepository;
    @Autowired
    private ChefRepository chefRepository;
    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private OrderItemsRepository orderItemsRepository;


    @Autowired
    OrderItemsService orderItemsService;

    public Integer placeOrder(OrderRequest orderRequest) {



        Orders order = new Orders();

        //orderRequest.setOrderid(order.getOrderid());
        order.setOrder_amount(orderRequest.getTotalprice());
        //order.setChef_loginid(orderRequest.getChefLoginid());
        order.setCustLoginid(orderRequest.getCustomerLoginid());
        order.setOrderItems(orderRequest.getOrderItems());

        order.setOrder_date(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));

        Orders savedOrder = orderRepository.save(order);
        Optional <Customer> customer = customerRepository.findByLoginid(orderRequest.getCustomerLoginid());


        for(OrderItem orderItem:orderRequest.getOrderItems())
        {
            orderItem.setOrderid(savedOrder.getOrderid());
            OrderItem saved=orderItemsRepository.save(orderItem);

            //Optional<Chef> chef= chefRepository.findByLoginid(saved.getChefMenu().getChef().getLoginid());
            //String chefemail = chef.get().getChef_emailid();
            /*emailSender.orderRequest(
                    chefemail,
                    //"ethniconnect@gmail.com",
                    emailService.buildOrderRequestEmail(savedOrder.getOrderid(),
                            customer.get().getCust_emailid(),
                            savedOrder.getOrderItems().toString()));*/
        }
        List<OrderSummaryEmail> orderSummary = new ArrayList<>();
        OrderSummaryEmail orderSummaryEmail=null;
        List<OrderItem> orderItems = orderItemsRepository.findOrderItemByOrderid(savedOrder.getOrderid());
        for(int i=0;i<orderItems.size();i++ )
        {
            orderSummaryEmail = new OrderSummaryEmail();
            orderSummaryEmail.setItemname(chefMenuRepository.findById(orderItems.get(i).getMenu_id()).get().getItem_name());
            orderSummaryEmail.setQuantity(orderItems.get(i).getQuantity());
            orderSummaryEmail.setSpecialInstructions(orderItems.get(i).getSpecial_instructions());
            orderSummary.add(orderSummaryEmail);
        }
        ChefMenu chefMenu = chefMenuRepository.findById(orderItems.get(0).getMenu_id()).get();
        Optional<Chef> chef  = chefRepository.findByLoginid(chefMenu.getLoginid());
        String chefEmailId= chef.get().getChef_emailid();
        String itemname = chefMenu.getItem_name();
        int quantity = orderItems.get(0).getQuantity();
        String customerEmail= userCredentialsRepository.findByLoginid(orderRequest.getCustomerLoginid()).get().getEmail();

        emailSender.emailChefOrderDetails(
               chefEmailId,
                //"ethniconnect@gmail.com",
                emailService.buildOrderRequestEmail(savedOrder.getOrderid(),
                        //customer.get().getCust_emailid() ,
                        customerEmail,
                        orderSummary));
        emailSender.emailCustomerOrderDetails(
                customerEmail,
                //customer.get().getCust_emailid(),
                //"ethniconnect@gmail.com",
                emailService.buildOrderDetailsEmail(savedOrder.getOrderid(),
                        chefEmailId,orderSummary));

        /*savedOrder.getOrderItems().toString())*/
                return savedOrder.getOrderid();

    }


    public Orders getOrder(int orderid) {

        Orders order=  orderRepository.findByOrderid(orderid).get();
        return order;
    }
    public List<Orders> getOrderbycustomerId(long loginid) {

        List<Orders> orders=  orderRepository.findAllByCustLoginid(loginid);
        return orders;

    }

    public Orders updateRatingByOrder(OrderRequest orderRequest) {
        Optional<Orders> orders =  orderRepository.findByOrderid(orderRequest.getOrderid());
        Orders ordersObj = orders.get();
        long chefLoginId = ordersObj.getOrderItems().get(0).getChefMenu().getChef().getLoginid();

        Optional<Chef> chef = chefRepository.findByLoginid(chefLoginId);
        Chef chefNew = chef.get();
        double avgRating = chefNew.getAvgRating();
        avgRating = (avgRating+orderRequest.getRatings())/2;
        chefNew.setAvgRating(avgRating);
        chefRepository.save(chefNew);
        ordersObj.setRating(orderRequest.getRatings());
        // ordersObj.setChef_loginid(orderRequest.getChefLoginId());
        ordersObj.setOrder_date(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        Orders savedOrder = orderRepository.save(ordersObj);
        return savedOrder;
    }


}