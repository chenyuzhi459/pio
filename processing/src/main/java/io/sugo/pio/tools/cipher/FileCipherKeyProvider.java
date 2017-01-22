package io.sugo.pio.tools.cipher;


import io.sugo.pio.tools.FileSystemService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The default {@link CipherKeyProvider} for RapidMiner Studio. It reads the Cipher key from a file
 * called "cipher.key" which is stored within the RapidMiner user folder.
 *
 * @author Nils Woehler
 * @since 6.2.0
 */
public class FileCipherKeyProvider implements CipherKeyProvider {

	/** Default file name used to store the cipher. */
	private static final String DEFAULTKEY_FILE_NAME = "cipher.key";

	@Override
	public SecretKey loadKey() throws KeyLoadingException {
		// try to load key from file
		File keyFile = new File(FileSystemService.getUserRapidMinerDir(), DEFAULTKEY_FILE_NAME);
		try (FileInputStream fis = new FileInputStream(keyFile); ObjectInputStream in = new ObjectInputStream(fis)) {
			int length = in.readInt();
			byte[] rawKey = new byte[length];
			int actualLength = in.read(rawKey);
			if (length != actualLength) {
				throw new IOException("Cannot read key file (unexpected length)");
			}
			return KeyGeneratorTool.makeKey(rawKey);
		} catch (IOException e) {
			// catch to log the problem, then throw again to indicate error
//			LogService.getRoot().log(Level.WARNING, "io.sugo.pio.tools.cipher.KeyGeneratorTool.read_key_error",
//					e.getMessage());
			throw new KeyLoadingException("Cannot retrieve key: " + e.getMessage());
		}
	}

	@Override
	public SecretKey createKey(int keyLength, String algorithm) throws KeyGenerationException {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyGenerationException("Cannot generate key, generation algorithm not known.");
		}

		keyGenerator.init(keyLength, new SecureRandom());

		// actual key generation
		return keyGenerator.generateKey();
	}

	@Override
	public void storeKey(Key key) throws KeyStoringException {
		Path keyPath = FileSystemService.getUserRapidMinerDir().toPath().resolve(DEFAULTKEY_FILE_NAME);
		try {
			KeyGeneratorTool.storeKey(key.getEncoded(), keyPath);
		} catch (IOException e) {
			throw new KeyStoringException("Could not store new cipher key", e);
		}
	}

}
