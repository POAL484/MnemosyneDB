package org.mnemosynehermesteam.server.mhrl;

public record MHRLSyntaxParseResult(boolean status, int errCode, String errDescription) {
    public static MHRLSyntaxParseResult generateOK() {
        return new MHRLSyntaxParseResult(true, 0, null);
    }
    public static MHRLSyntaxParseResult generateErr(int code, String errDescription) {
        return new MHRLSyntaxParseResult(false, code, errDescription);
    }
    public static MHRLSyntaxParseResult generateNullFields(String fieldString) {
        return MHRLSyntaxParseResult.generateErr(-3, "%s must not be null".formatted(fieldString));
    }
    public static MHRLSyntaxParseResult generateJsonSyntaxError() {
        return MHRLSyntaxParseResult.generateErr(-1, "Json syntax error or incorrect fields types");
    }
}
