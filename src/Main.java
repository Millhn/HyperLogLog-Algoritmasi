import java.util.HashSet;
import java.util.Set;

public class Main {

    /**
     * Programın başlangıç noktası.
     * İki farklı örnek çalıştırılır:
     *
     * 1) Temel HyperLogLog kullanım örneği
     * 2) İki farklı HLL yapısının birleştirilmesi (merge)
     */
    public static void main(String[] args) {

        basicExample();

        System.out.println("--------------------------------------------------");

        mergeExample();
    }

    /**
     * Temel kullanım örneği.
     *
     * Küçük bir veri kümesi üzerinde HyperLogLog algoritmasının
     * farklı eleman sayısını nasıl tahmin ettiğini gösterir.
     */
    private static void basicExample() {

        // p = 10 -> m = 2^10 = 1024 bucket
        HyperLogLog hll = new HyperLogLog(10);

        // Örnek veri kümesi
        String[] data = {
                "apple", "banana", "orange", "apple", "grape",
                "banana", "melon", "kiwi", "orange", "pear"
        };

        // Gerçek farklı eleman sayısını hesaplamak için HashSet kullanılır
        Set<String> realSet = new HashSet<>();

        for (String item : data) {
            hll.add(item);
            realSet.add(item);
        }

        System.out.println("TEMEL HYPERLOGLOG ÖRNEĞİ");

        // Gerçek cardinality
        System.out.println("Gerçek farklı eleman sayısı : " + realSet.size());

        // HyperLogLog tahmini
        System.out.println("HLL tahmini                 : " + Math.round(hll.estimate()));

        // Raw estimate (düzeltme uygulanmadan önceki değer)
        System.out.println("Ham tahmin değeri           : " + hll.estimateRaw());
    }

    /**
     * Merge (birleştirme) örneği.
     *
     * HyperLogLog'un en önemli özelliklerinden biri
     * iki farklı veri yapısının veri kaybı olmadan birleştirilebilmesidir.
     *
     * Bu örnekte iki farklı veri kümesi oluşturulmakta ve
     * iki ayrı HLL yapısı daha sonra merge edilmektedir.
     */
    private static void mergeExample() {

        HyperLogLog hll1 = new HyperLogLog(10);
        HyperLogLog hll2 = new HyperLogLog(10);

        // Gerçek birleşik cardinality hesaplamak için
        Set<String> unionSet = new HashSet<>();

        // Birinci veri kümesi: user_0 - user_9999
        for (int i = 0; i < 10000; i++) {
            String value = "user_" + i;

            hll1.add(value);
            unionSet.add(value);
        }

        // İkinci veri kümesi: user_5000 - user_14999
        for (int i = 5000; i < 15000; i++) {
            String value = "user_" + i;

            hll2.add(value);
            unionSet.add(value);
        }

        // İki HLL yapısını birleştir
        HyperLogLog merged = hll1.merge(hll2);

        System.out.println("HYPERLOGLOG MERGE ÖRNEĞİ");

        // Gerçek birleşik cardinality
        System.out.println("Gerçek birleşik farklı eleman sayısı : " + unionSet.size());

        // Birleştirilmiş HLL tahmini
        System.out.println("Birleşmiş HLL tahmini                : " + Math.round(merged.estimate()));

        // Ham tahmin değeri
        System.out.println("Ham birleşmiş tahmin                 : " + merged.estimateRaw());
    }
}