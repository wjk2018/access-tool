package com.cnbi.util.calculate;


import java.util.Map;
import java.util.Objects;

/**
 * @ClassName FormulaParse
 * @Description
 * @Author Wangjunkai
 * @Date 2020/5/28 22:30
 **/

public class FormulaParse {

    public String openToken = "@{";
    public String closeToken = "}@";

    public  String parse(String text, Object obj) throws RuntimeException {
        if (!Objects.nonNull(text)) {
            return "";
        } else {
            int start = text.indexOf(this.openToken);
            if (start == -1) {
                return text;
            } else {
                char[] src = text.toCharArray();
                int offset = 0;
                StringBuilder builder = new StringBuilder();

                for(StringBuilder expression = null; start > -1; start = text.indexOf(this.openToken, offset)) {
                    if (start > 0 && src[start - 1] == '\\') {
                        builder.append(src, offset, start - offset - 1).append(this.openToken);
                        offset = start + this.openToken.length();
                    } else {
                        if (expression == null) {
                            expression = new StringBuilder();
                        } else {
                            expression.setLength(0);
                        }

                        builder.append(src, offset, start - offset);
                        offset = start + this.openToken.length();

                        int end;
                        for(end = text.indexOf(this.closeToken, offset); end > -1; end = text.indexOf(this.closeToken, offset)) {
                            if (end <= offset || src[end - 1] != '\\') {
                                expression.append(src, offset, end - offset);
                                break;
                            }

                            expression.append(src, offset, end - offset - 1).append(this.closeToken);
                            offset = end + this.closeToken.length();
                        }

                        if (end == -1) {
                            builder.append(src, start, src.length - start);
                            offset = src.length;
                        } else {
                            builder.append(handleToken(expression.toString(), obj));
                            offset = end + this.closeToken.length();
                        }
                    }
                }

                if (offset < src.length) {
                    builder.append(src, offset, src.length - offset);
                }
                return builder.toString();
            }
        }
    }
    public String handleToken(String exp, Object obj) throws RuntimeException {
        return ((Map)obj).get(exp).toString();
    }
}