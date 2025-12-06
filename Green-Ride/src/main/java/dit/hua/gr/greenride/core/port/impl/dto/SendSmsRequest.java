package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * SendSmsRequest DTO.
 */
public record SendSmsRequest(String e164, String content) {}