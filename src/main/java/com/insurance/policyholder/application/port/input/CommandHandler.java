package com.insurance.policyholder.application.port.input;

/**
 * 命令處理器介面
 * CQRS 模式中的 Command 端
 *
 * @param <C> 命令類型
 * @param <R> 回傳結果類型
 */
public interface CommandHandler<C, R> {

    /**
     * 處理命令
     *
     * @param command 要處理的命令
     * @return 處理結果
     */
    R handle(C command);
}
