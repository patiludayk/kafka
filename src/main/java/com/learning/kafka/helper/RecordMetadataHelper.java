package com.learning.kafka.helper;

import com.learning.kafka.dto.ProducerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class RecordMetadataHelper<K, V> {

    /**
     * get record metadata from future and retry in case of timeout
     * @param future
     * @param retry
     * @return ProducerResponse
     * @throws InterruptedException
     * @throws ExecutionException
     */
    ProducerResponse retryGetProducerRecordMetadata(ListenableFuture<SendResult<K, V>> future, int retry) throws InterruptedException, ExecutionException {
        while (retry > 0) {
            try {
                /**
                 * Do not implement this in production until and unless special use case
                 * This will have huge performance impact.
                 * Let's consider, each message might take 500ms, then 100*500 becomes 50000 in terms of seconds its 50seconds.
                 * Considering 50seconds is very high which will cause hell lot of problems in case of the performance factors.
                 */
                RecordMetadata result = future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata();
                return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
            } catch (TimeoutException e) {
                log.error("timeout waiting from broker response for producer. Retrying again");
                return retryGetProducerRecordMetadata(future, retry--);
            }
        }
        return ProducerResponse.builder().msg("msg failed to deliver.").error(new TimeoutException("timeout waiting from broker response for producer.")).build();
    }

    /**
     * function for stream map. One of the way to handle exception in stream
     * @param future
     * @return ProducerResponse
     */
    ProducerResponse apply(ListenableFuture<SendResult<K, V>> future) {
        try {
            //retry 3 times in case of timeout
            return retryGetProducerRecordMetadata(future, 3);
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

    public ProducerResponse retryGetProducerRecordMetadataNew(ListenableFuture<SendResult<K, V>> future, int retry) throws InterruptedException, ExecutionException {
        while (retry > 0) {
            try {
                /**
                 * Do not implement this in production until and unless special use case
                 * This will have huge performance impact.
                 * Let's consider, each message might take 500ms, then 100*500 becomes 50000 in terms of seconds its 50seconds.
                 * Considering 50seconds is very high which will cause hell lot of problems in case of the performance factors.
                 */
                RecordMetadata result = future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata();
                return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
            } catch (TimeoutException e) {
                log.error("timeout waiting from broker response for producer. Retrying again");
                return retryGetProducerRecordMetadata(future, retry--);
            }
        }
        return ProducerResponse.builder().msg("msg failed to deliver.").error(new TimeoutException("timeout waiting from broker response for producer.")).build();
    }

}
