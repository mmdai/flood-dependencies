package cn.flood.pulsar.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarConsumerThreadCountOverflowException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(PulsarConsumerThreadCountOverflowException.class);

    public PulsarConsumerThreadCountOverflowException() {
    }

    public PulsarConsumerThreadCountOverflowException(String message) {
        super(message);
    }

}
