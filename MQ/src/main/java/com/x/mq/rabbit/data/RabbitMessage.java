package com.x.mq.rabbit.data;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.x.mq.common.MQMessage;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 12:39
 */
public class RabbitMessage extends MQMessage {

    private static final long serialVersionUID = 1L;

    private String exchange;
    private final BasicProperties props;

    public RabbitMessage() {
        this(new BasicProperties());
    }

    public RabbitMessage(BasicProperties props) {
        this.exchange = "";
        this.props = props;
    }

    public BasicProperties getProps() {
        return props;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
