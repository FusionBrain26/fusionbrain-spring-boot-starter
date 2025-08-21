package ai.fusionbrain.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FusionBrainErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String message = String.format("Failed request to FusionBrain API. Status: %d, Method: %s",
                response.status(), methodKey);

        log.error(message);
        return new FusionBrainServerException(message);
    }
}