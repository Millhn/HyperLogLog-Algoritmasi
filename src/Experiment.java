import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Experiment {

    /**
     * Bu sınıf, HyperLogLog algoritmasının deneysel analizini yapmak için kullanılır.
     *
     * Amaç:
     * - Farklı p değerleri için deney yapmak
     * - Gerçek cardinality ile tahmini cardinality'yi karşılaştırmak
     * - Ortalama bağıl hatayı (relative error) hesaplamak
     * - Teorik hata ile deneysel hatayı karşılaştırmak
     */
    public static void main(String[] args) {

        // Farklı bucket sayıları için kullanılacak p değerleri
        int[] pValues = {4, 5, 6, 8, 10, 12};

        // Her deneyde üretilecek toplam eleman sayısı
        int numberOfElements = 50000;

        // Her p değeri için deney tekrar sayısı
        int trials = 10;

        System.out.println("HyperLogLog Deneysel Analizi");
        System.out.println("Her deneyde üretilen eleman sayısı: " + numberOfElements);
        System.out.println("Her p değeri için tekrar sayısı: " + trials);
        System.out.println();

        System.out.printf("%-5s %-8s %-25s %-20s%n",
                "p", "m", "Ortalama Bağıl Hata", "Teorik Hata");
        System.out.println("--------------------------------------------------------------------------");

        // Her p değeri için deneyleri çalıştır
        for (int p : pValues) {
            double totalRelativeError = 0.0;

            for (int t = 0; t < trials; t++) {
                TrialResult result = runSingleTrial(p, numberOfElements);
                totalRelativeError += result.relativeError;
            }

            // m = 2^p
            int m = 1 << p;

            // Deneylerden elde edilen ortalama bağıl hata
            double avgRelativeError = totalRelativeError / trials;

            // HyperLogLog için teorik hata formülü: 1.04 / sqrt(m)
            double theoreticalError = 1.04 / Math.sqrt(m);

            System.out.printf("%-5d %-8d %-25.6f %-20.6f%n",
                    p, m, avgRelativeError, theoreticalError);
        }
    }

    /**
     * Tek bir deney çalıştırır.
     *
     * İşlem adımları:
     * 1) Belirli bir p değeri ile HyperLogLog nesnesi oluşturulur
     * 2) Rastgele n adet sayı üretilir
     * 3) Gerçek farklı eleman sayısı HashSet ile hesaplanır
     * 4) Aynı veriler HyperLogLog yapısına eklenir
     * 5) Tahmini değer ile gerçek değer karşılaştırılır
     *
     * @param p Bucket indeks bit sayısı
     * @param n Üretilecek toplam eleman sayısı
     * @return Tek bir deneye ait sonuçlar
     */
    private static TrialResult runSingleTrial(int p, int n) {

        HyperLogLog hll = new HyperLogLog(p);

        // Gerçek farklı eleman sayısını bulmak için HashSet kullanılır
        Set<Integer> realSet = new HashSet<>();

        // Rastgele veri üretmek için Random nesnesi
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            int value = random.nextInt(1_000_000_000);

            // Gerçek cardinality için sete eklenir
            realSet.add(value);

            // HLL yapısına string olarak eklenir
            hll.add(String.valueOf(value));
        }

        // Gerçek farklı eleman sayısı
        int actual = realSet.size();

        // HyperLogLog tahmini
        double estimated = hll.estimate();

        // Bağıl hata hesabı
        double relativeError = Math.abs(estimated - actual) / actual;

        return new TrialResult(actual, estimated, relativeError);
    }

    /**
     * Tek bir deneyin sonucunu tutan yardımcı iç sınıf.
     */
    private static class TrialResult {

        // Gerçek farklı eleman sayısı
        int actual;

        // HyperLogLog tarafından tahmin edilen değer
        double estimated;

        // Gerçek değer ile tahmin arasındaki bağıl hata
        double relativeError;

        TrialResult(int actual, double estimated, double relativeError) {
            this.actual = actual;
            this.estimated = estimated;
            this.relativeError = relativeError;
        }
    }
}