package dit.hua.gr.greenride.core.port.model;

public record PhoneNumberValidationResult(
        String raw,
        boolean valid,
        String type,
        String e164
) {
    public boolean isValidMobile() {
        if (!this.valid) return false;
        if (this.type == null) return false;
        return "mobile".equalsIgnoreCase(this.type);
    }
}