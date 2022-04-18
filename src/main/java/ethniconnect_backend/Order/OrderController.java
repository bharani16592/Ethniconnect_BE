package ethniconnect_backend.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin()
@RestController
public class OrderController {

    @Autowired
    public OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Integer> placeOrder(@RequestBody OrderRequest orderRequest)
    {
        int orderid= orderService.placeOrder(orderRequest);
        return new ResponseEntity<Integer>(orderid, HttpStatus.OK);
    }



    @GetMapping("/order/{orderid}")
    public ResponseEntity<Orders> getOrder(@PathVariable int orderid)
    {

        return new ResponseEntity<Orders>(orderService.getOrder(orderid), HttpStatus.OK);
    }

    @GetMapping("/orderbyCustLoginId/{loginId}")
    public ResponseEntity<List<Orders>> getOrderbycustomerId(@PathVariable int loginId)
    {
        Long loginidlong = new Long(loginId);
        //return chefMenuService.getChefMenuByLoginId(loginidlong.longValue());
        return new ResponseEntity<List<Orders>>(orderService.getOrderbycustomerId(loginidlong.longValue()), HttpStatus.OK);
    }
    @PutMapping("/updateRating")
    public ResponseEntity<Orders> updateRating(@RequestBody OrderRequest orderRequest)
    {
        Orders orders= orderService.updateRatingByOrder(orderRequest);
        return new ResponseEntity<Orders>(orders, HttpStatus.OK);
    }
}
