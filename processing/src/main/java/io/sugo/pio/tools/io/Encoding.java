package io.sugo.pio.tools.io;


import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeStringCategory;
import io.sugo.pio.parameter.UndefinedParameterError;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedList;
import java.util.List;


/**
 * Collection of static helper methods to add and evaluate parameters to specify an encoding.
 */
public class Encoding {

    public static final String PARAMETER_ENCODING = "encoding";
    public static final String SYSTEM_ENCODING_NAME = "SYSTEM";

    public static final String[] CHARSETS;

    public static final String[] AVALIABLE_CHARSETS = new String[]{"UTF-8", "GBK", "GB2312", "US-ASCII"};

    static {
        /*CHARSETS = new String[Charset.availableCharsets().size() + 1];
        CHARSETS[0] = SYSTEM_ENCODING_NAME;
        int i = 0;
		for (String charSet : Charset.availableCharsets().keySet()) {
            CHARSETS[i + 1] = charSet;
            i++;
        }*/
        CHARSETS = AVALIABLE_CHARSETS;
    }

    public static Charset getEncoding(Operator handler) throws UndefinedParameterError, UserError {
        String selectedCharsetName = handler.getParameterAsString(PARAMETER_ENCODING);
        if (SYSTEM_ENCODING_NAME.equals(selectedCharsetName)) {
            return Charset.defaultCharset();
        }
        try {
            return Charset.forName(selectedCharsetName);
        } catch (IllegalCharsetNameException e) {
            throw new UserError(handler, "pio.error.parameter_value_impossible",
                    selectedCharsetName, PARAMETER_ENCODING, "No legal charset name.");
        } catch (UnsupportedCharsetException e) {
            throw new UserError(handler, "pio.error.parameter_value_impossible",
                    selectedCharsetName, PARAMETER_ENCODING,
                    "Charset not supported on this Java VM.");
        } catch (IllegalArgumentException e) {
            throw new UserError(handler, "pio.error.parameter_value_impossible",
                    selectedCharsetName, PARAMETER_ENCODING, "Select different charset.");
        }
    }

    public static Charset getEncoding(String charsetName) {
        if (SYSTEM_ENCODING_NAME.equals(charsetName)) {
            return Charset.defaultCharset();
        }
        return Charset.forName(charsetName);
    }

    public static List<ParameterType> getParameterTypes(ParameterHandler handler) {
        List<ParameterType> types = new LinkedList<ParameterType>();

        String encoding = SYSTEM_ENCODING_NAME;
        types.add(new ParameterTypeStringCategory(PARAMETER_ENCODING, I18N.getMessage("pio.Encoding.encoding"),
                CHARSETS, encoding, false));

        return types;
    }
}
