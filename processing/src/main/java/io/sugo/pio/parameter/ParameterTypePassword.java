package io.sugo.pio.parameter;

import io.sugo.pio.tools.cipher.CipherException;
import io.sugo.pio.tools.cipher.CipherTools;

/**
 * A parameter for passwords. The parameter is written with asteriks in the GUI but can be read in
 * process configuration file. Please make sure that noone but the user can read the password from
 * such a file.
 *
 * @author Ingo Mierswa, Simon Fischer
 */
public class ParameterTypePassword extends ParameterTypeString {

    private static final long serialVersionUID = 384977559199162363L;

    public ParameterTypePassword(String key, String description) {
        super(key, description, true);
//		setExpert(false);
    }

    @Override
    public String getRange() {
        return "password";
    }


    private String encryptPassword(String value) {
        if (CipherTools.isKeyAvailable()) {
            try {
                return CipherTools.encrypt(value);
            } catch (CipherException e) {
//				LogService.getRoot().log(Level.SEVERE,
//				        "io.sugo.pio.parameter.ParameterTypePassword.encrypting_password_error");
                return value;
            }
        } else {
            return value;
        }
    }

    private String decryptPassword(String value) {
        if (CipherTools.isKeyAvailable()) {
            try {
                return CipherTools.decrypt(value);
            } catch (CipherException e) {
//				LogService.getRoot().log(Level.FINE,
//				        "io.sugo.pio.parameter.ParameterTypePassword.password_looks_like_unencrypted_plain_text");
            }
        }
        return value;
    }

//	@Override
//	public String toString(Object value) {
//		if (value == null) {
//			return "";
//		} else {
//			return encryptPassword(super.toString(value));
//		}
//	}

}
