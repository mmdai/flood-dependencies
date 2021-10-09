package cn.flood.pulsar.exception;

public class PulsarProducerTypeCheckException extends RuntimeException {

    public PulsarProducerTypeCheckException() {
    }

    public PulsarProducerTypeCheckException(String message) {
        super(message);
    }

}
