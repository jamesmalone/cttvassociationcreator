package obanminter;


        import java.io.UnsupportedEncodingException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;


/**
 * @author Simon Jupp
 * @date 28/07/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class HashingIdGenerator {

    public static final EncodingAlgorithm DEFAULT_ENCODING = EncodingAlgorithm.MD5;
    private static final String HEX_CHARACTERS = "0123456789ABCDEF";


    /**
     * method to generate hash id given a list of strings
     * @param contents
     * @return
     */
    public static String generateHashEncodedID(String... contents) {
        return generateHashEncodedID(DEFAULT_ENCODING, contents);
    }

    private static String getHexRepresentation(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEX_CHARACTERS.charAt((b & 0xF0) >> 4)).append(HEX_CHARACTERS.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static String generateHashEncodedID(EncodingAlgorithm algorithm, String... contents) {
        StringBuilder idContent = new StringBuilder();
        for (String s : contents) {
            idContent.append(s);
        }
        try {
            // encode the content using SHA-1
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getAlgorithmName());
            byte[] digest = messageDigest.digest(idContent.toString().getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String idKey = getHexRepresentation(digest);

            return idKey;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    algorithm.getAlgorithmName() + " algorithm not available, this is required to generate ID");
        }
    }




    public enum EncodingAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256");

        private final String algorithm;

        private EncodingAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithmName() {
            return algorithm;
        }
    }

}
