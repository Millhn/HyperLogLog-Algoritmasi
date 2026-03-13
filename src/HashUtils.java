import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {

    /**
     * Bu sınıf sadece hash üretimi için yardımcı fonksiyon içerir.
     * Bu nedenle constructor private yapılmıştır.
     */
    private HashUtils() {
    }

    /**
     * Verilen string değerden 64-bit bir hash üretir.
     *
     * Kullanılan yöntem:
     * 1) Önce SHA-1 algoritması ile hash hesaplanır
     * 2) SHA-1 çıktısı 160 bit (20 byte) olduğu için
     *    ilk 8 byte alınarak 64-bit bir sayı oluşturulur
     * 3) Bu değer HyperLogLog algoritmasında kullanılır
     *
     * @param value Hash üretilecek giriş değeri
     * @return 64-bit hash değeri
     */
    public static long hash64(String value) {

        try {

            // SHA-1 hash algoritmasını oluştur
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // String değeri byte dizisine çevirip hash hesapla
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));

            // İlk 8 byte kullanılarak 64-bit sayı oluşturulur
            long result = 0L;

            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (digest[i] & 0xFFL);
            }

            return result;

        } catch (NoSuchAlgorithmException e) {

            // SHA-1 bulunamazsa runtime hatası fırlatılır
            throw new RuntimeException("SHA-1 algoritması bulunamadı.", e);

        }
    }
}