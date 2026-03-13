import java.util.Arrays;

public class HyperLogLog {

    // Bucket indeksini belirlemek için kullanılan bit sayısı
    private final int p;

    // Bucket sayısı (m = 2^p)
    private final int m;

    // Her bucket için maksimum rank değerini tutan register dizisi
    private final byte[] registers;

    /**
     * HyperLogLog yapısını başlatan constructor.
     * p değeri bucket sayısını belirler (m = 2^p).
     */
    public HyperLogLog(int p) {
        if (p < 4 || p > 16) {
            throw new IllegalArgumentException("p değeri 4 ile 16 arasında olmalıdır.");
        }
        this.p = p;
        this.m = 1 << p;
        this.registers = new byte[m];
    }

    public int getP() {
        return p;
    }

    public int getM() {
        return m;
    }

    /**
     * Register dizisinin bir kopyasını döndürür.
     */
    public byte[] getRegisters() {
        return Arrays.copyOf(registers, registers.length);
    }

    /**
     * Yeni bir elemanı HyperLogLog yapısına ekler.
     *
     * İşlem adımları:
     * 1) Eleman 64-bit hash değerine dönüştürülür
     * 2) İlk p bit bucket indeksini belirler
     * 3) Kalan bitlerde ilk 1 bitinin konumu bulunur (rho değeri)
     * 4) Register değeri maksimum olacak şekilde güncellenir
     */
    public void add(String value) {

        // Elemanın 64-bit hash değeri
        long x = HashUtils.hash64(value);

        // İlk p bit bucket indeksini belirler
        int bucket = (int) (x >>> (64 - p));

        // Kalan bitler rank hesaplamak için kullanılır
        long remaining = x << p;

        int rank = rho(remaining, 64 - p);

        // Register değeri maksimum olacak şekilde güncellenir
        if (rank > registers[bucket]) {
            registers[bucket] = (byte) rank;
        }
    }

    /**
     * rho(w) fonksiyonu:
     * Kalan bitlerde soldan başlayarak ilk 1 bitinin konumunu bulur.
     *
     * Eğer tüm bitler 0 ise maxBits + 1 değeri döndürülür.
     */
    private int rho(long w, int maxBits) {

        if (w == 0) {
            return maxBits + 1;
        }

        int leadingZeros = Long.numberOfLeadingZeros(w);

        // p bit sola kaydırıldığı için bu etkiyi düzeltiyoruz
        int adjusted = leadingZeros - p + 1;

        if (adjusted < 1) {
            adjusted = 1;
        }

        if (adjusted > maxBits + 1) {
            adjusted = maxBits + 1;
        }

        return adjusted;
    }

    /**
     * HyperLogLog algoritmasında kullanılan alpha sabiti.
     */
    private double getAlphaMM() {

        double alpha;

        if (m == 16) {
            alpha = 0.673;
        }
        else if (m == 32) {
            alpha = 0.697;
        }
        else if (m == 64) {
            alpha = 0.709;
        }
        else {
            alpha = 0.7213 / (1.0 + 1.079 / m);
        }

        return alpha * m * m;
    }

    /**
     * Harmonik ortalama kullanarak ham cardinality tahmini hesaplanır.
     */
    public double estimateRaw() {

        double sum = 0.0;

        for (byte register : registers) {
            sum += 1.0 / (1L << register);
        }

        return getAlphaMM() / sum;
    }

    /**
     * Nihai cardinality tahmini.
     *
     * İçerdiği düzeltmeler:
     * 1) Small range correction
     * 2) Large range correction
     */
    public double estimate() {

        double rawEstimate = estimateRaw();

        // Sıfır register sayısını hesapla
        int zeroCount = 0;

        for (byte register : registers) {
            if (register == 0) {
                zeroCount++;
            }
        }

        // Small range correction
        if (rawEstimate <= 2.5 * m) {

            if (zeroCount > 0) {
                return m * Math.log((double) m / zeroCount);
            }

            return rawEstimate;
        }

        // Large range correction (64-bit hash uzayı için)
        double twoTo64 = Math.pow(2.0, 64.0);

        if (rawEstimate > (twoTo64 / 30.0)) {
            return -twoTo64 * Math.log(1.0 - (rawEstimate / twoTo64));
        }

        return rawEstimate;
    }

    /**
     * İki HyperLogLog yapısını birleştirir.
     *
     * Her register için maksimum değer alınır.
     * Bu özellik dağıtık sistemlerde çok önemlidir.
     */
    public HyperLogLog merge(HyperLogLog other) {

        if (other == null) {
            throw new IllegalArgumentException("Diğer HyperLogLog nesnesi null olamaz.");
        }

        if (this.p != other.p) {
            throw new IllegalArgumentException("Farklı p değerlerine sahip HLL yapıları birleştirilemez.");
        }

        HyperLogLog merged = new HyperLogLog(this.p);

        for (int i = 0; i < this.m; i++) {
            merged.registers[i] = (byte) Math.max(this.registers[i], other.registers[i]);
        }

        return merged;
    }

    /**
     * Nesnenin özet bilgisini döndürür.
     */
    @Override
    public String toString() {
        return "HyperLogLog{p=" + p + ", m=" + m + ", estimate=" + Math.round(estimate()) + "}";
    }
}