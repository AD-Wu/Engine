package com.x.mq.rabbit.core;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.x.mq.common.IMQClient;
import com.x.mq.common.IMessageListener;
import com.x.mq.rabbit.data.RabbitMessage;
import com.x.mq.rabbit.data.RabbitParam;
import java.io.IOException;
import java.util.Date;

/**
 * @author AD
 * @date 2022/3/23 13:51
 */
public class RabbitClient implements IMQClient<RabbitMessage, RabbitParam> {

    private RabbitClientFactory fact;
    private RabbitParam param;

    RabbitClient(RabbitClientFactory fact, RabbitParam param) {
        this.fact = fact;
        this.param = param;
    }

    @Override
    public boolean existQueue(RabbitParam param) throws Exception {
        try {
            Channel chn = fact.getChannel();
            chn.queueDeclarePassive(param.getQueue());
            return true;
        } catch (Exception e) {
            // 通道正常,发生异常时表示队列不存在
            if (fact.getChannel().isOpen()) {
                return false;
            } else {
                throw new Exception("通道关闭,无法判断队列是否存在");
            }
        }
    }

    @Override
    public boolean createQueue(RabbitParam param) throws Exception {
        try {
            Channel chn = fact.getChannel();
            /*
                1.队列名称
                2.队列是否持久化
                3.队列是否具有排他性（只有同一连接共享此队列，且连接断开时队列删除）排他队列详细说明
                4.队列是否自动删除
             */
            chn.queueDeclare(param.getQueue(), true, false, false, null);
            return true;
        } catch (Exception e) {
            fact.close();
            throw e;
        }
    }

    @Override
    public boolean deleteQueue(RabbitParam param) throws Exception {
        try {
            Channel chn = fact.getChannel();
            chn.queueDelete(param.getQueue(), true, true);
            return true;
        } catch (Exception e) {
            fact.close();
            throw e;
        }
    }


    @Override
    public boolean send(RabbitMessage msg) throws Exception {
        BasicProperties props = msg.getProps();
        Builder builder = props.builder();
        // 打上消息时间戳
        if (props.getTimestamp() == null) {
            builder.timestamp(new Date());
        }
        try {
            Channel chn = fact.getChannel();
            chn.basicPublish(msg.getExchange(), param.getQueue(), builder.build(), msg.getBody());
            return true;
        } catch (Exception e) {
            fact.close();
            throw e;
        }
    }

    @Override
    public void onReceive(IMessageListener<RabbitMessage> listener) throws Exception {
        try {
            Channel chn = fact.getChannel();

            chn.basicConsume(param.getQueue(), false, new DefaultConsumer(chn) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                    throws IOException {
                    RabbitMessage msg = new RabbitMessage(properties);
                    msg.setExchange(envelope.getExchange());
                    msg.setBody(body);
                    if (listener.onReceive(msg)) {
                        chn.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            });
        } catch (Exception e) {
            fact.close();
            throw e;
        }

    }
}
