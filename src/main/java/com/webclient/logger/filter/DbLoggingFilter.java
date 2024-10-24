package com.webclient.logger.filter;

import com.webclient.logger.entity.ApiLog;
import com.webclient.logger.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DbLoggingFilter implements ExchangeFilterFunction {

    private final ApiLogRepository apiLogRepository;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return logRequest(request)
                .flatMap(apiLog -> getClientResponseMono(request, next, apiLog));
    }

    private Mono<ClientResponse> getClientResponseMono(ClientRequest request, ExchangeFunction next, ApiLog apiLog) {
        return next.exchange(request)
                .flatMap(response -> logResponse(apiLog, response));
    }

    private Mono<ApiLog> logRequest(ClientRequest request) {
        return Mono.fromCallable(() -> {
            ApiLog apiLog = new ApiLog();
            apiLog.setRequestUri(request.url().toString());
            apiLog.setRequestBody("test"); // todo: get body
            apiLog.setRequestTime(Instant.now());
            return apiLogRepository.save(apiLog);
        });
    }

    private Mono<ClientResponse> logResponse(ApiLog apiLog, ClientResponse response) {
        return response.bodyToMono(String.class)
                .doOnNext(responseBody -> saveLog(apiLog, response, responseBody))
                .map(responseBody -> buildClientResponse(response, responseBody));
    }

    private void saveLog(ApiLog apiLog, ClientResponse response, String responseBody) {
        apiLog.setResponseBody(responseBody);
        apiLog.setStatusCode(response.statusCode().value());
        apiLog.setRequestTime(Instant.now());
        apiLogRepository.save(apiLog);
    }

    private static ClientResponse buildClientResponse(ClientResponse response, String responseBody) {
        return ClientResponse.create(response.statusCode())
                .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                .body(responseBody)
                .build();
    }

}
