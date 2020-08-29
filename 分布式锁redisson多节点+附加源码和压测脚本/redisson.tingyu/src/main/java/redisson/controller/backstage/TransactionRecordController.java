package redisson.controller.backstage;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redisson.config.tingyu.operation.RedissonObject;
import java.util.Random;

/**
 * @author ransong
 * @version 1.0
 * @date 2019/11/15 0015 17:02
 */
@RestController
@RequestMapping("/backstage/transaction")
public class TransactionRecordController {

    private final static Logger logger = LoggerFactory.getLogger(TransactionRecordController.class);
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RedissonObject redissonObject;


//    @Autowired
//    RedisCache redisCache;
    /**
     *
     * @return
     */
    @RequestMapping(value = "/redisTest", method = RequestMethod.GET)
    public int transactionRecordList() {
        String lockKey = "com_lock";
        String lockKey1 = "com_locka";
        RLock rLock = redissonClient.getLock(lockKey1);
        int uuid = new Random().nextInt(10000);
        Boolean isDelBetKeyIncr = true;
        String betKeyIncr = "Test" + uuid;
        String allvalue = "0";
        rLock.lock();
        try {
            int stock =  Integer.parseInt(redissonObject.getValue(lockKey));
//            Long incr = redisCache.incrementForValue(betKeyIncr, 1);
//            Long value = redisCache.incrementForValue(allvalue, 1);
//            if (incr == null || incr != 1) {
//                isDelBetKeyIncr = false;
//                logger.info("请稍后再试");
//                return 1;
//            }
           ;//Integer.parseInt(redisCache.getForValue(lockKey));
            if (stock > 0) {
                int realStock = stock -1;
                redissonObject.setValue(lockKey,String.valueOf(realStock));
//                redisCache.putForValue(lockKey,String.valueOf(realStock));
                logger.info("扣减成功，剩余库存：" + realStock);
            } else {
                logger.info("请稍后再试");
            }
        } catch (Exception e) {

        } finally {
            rLock.unlock();
//            if (isDelBetKeyIncr) {
//                redisCache.deleteForValue(betKeyIncr);
//                logger.info(redisCache.getForValue(allvalue));
//            }
        }
        return 2;
    }

}
