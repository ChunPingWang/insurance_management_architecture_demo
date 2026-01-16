package com.insurance.policyholder.application.port.input;

/**
 * 查詢處理器介面
 * CQRS 模式中的 Query 端
 *
 * @param <Q> 查詢類型
 * @param <R> 回傳結果類型
 */
public interface QueryHandler<Q, R> {

    /**
     * 處理查詢
     *
     * @param query 要處理的查詢
     * @return 查詢結果
     */
    R handle(Q query);
}
