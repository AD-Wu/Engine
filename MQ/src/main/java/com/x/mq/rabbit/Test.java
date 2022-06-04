package com.x.mq.rabbit;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.x.doraemon.DateTimes;
import com.x.mq.common.IMQClient;
import com.x.mq.common.IMQClientFactory;
import com.x.mq.common.IMessageListener;
import com.x.mq.rabbit.core.RabbitClientFactory;
import com.x.mq.rabbit.data.RabbitConfig;
import com.x.mq.rabbit.data.RabbitMessage;
import com.x.mq.rabbit.data.RabbitParam;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author AD
 * @date 2022/3/23 16:42
 */
public class Test {

    private static String queue = "sunday";

    public static void main(String[] args) throws Exception {

        RabbitConfig conf = new RabbitConfig();
        RabbitParam param = new RabbitParam();
        param.setQueue(queue);
        IMQClientFactory<RabbitMessage, RabbitParam> factory = new RabbitClientFactory(conf);
        IMQClient<RabbitMessage,RabbitParam> client = factory.createClient(param);

        client.deleteQueue(param);

        client.createQueue(param);

        boolean error = false;

        try {
            client.onReceive(new IMessageListener<RabbitMessage>() {
                @Override
                public boolean onReceive(RabbitMessage msg) {
                    String body = new String(msg.getBody());
                    Date time = msg.getProps().getTimestamp();
                    // if(body.startsWith("1")){
                    //     return false;
                    // }
                    System.out.println(Thread.currentThread().getName() + "   " + body + "   " + DateTimes.format(time));
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }

        if(!error){
            for (int i = 1; i < 5; i++) {
                Builder builder = new BasicProperties.Builder();
                builder.expiration("10000");
                RabbitMessage msg = new RabbitMessage(builder.build());
                msg.setBody((i + "-hello world").getBytes());
                client.send(msg);
                TimeUnit.SECONDS.sleep(1);
            }
        }

    }
}
