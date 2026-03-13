# HyperLogLog Implementation – Büyük Veri Analitiğinde Olasılıksal Veri Yapıları

## Proje Tanımı

Bu projede **HyperLogLog (HLL)** algoritması sıfırdan tasarlanmış ve Java programlama dili kullanılarak gerçekleştirilmiştir.

HyperLogLog, büyük veri kümelerinde **farklı eleman sayısını (cardinality)** düşük bellek kullanarak yaklaşık olarak tahmin eden olasılıksal bir veri yapısıdır.

Bu projede algoritmanın temel bileşenleri implement edilmiş ve teorik hata analizi gerçekleştirilmiştir.

---

## Cardinality Estimation Problemi

Cardinality estimation, bir veri kümesindeki **benzersiz eleman sayısını** belirleme problemidir.

Örneğin: apple, banana, apple, orange, banana
Farklı elemanlar: apple, banana, orange


Cardinality = **3**

Büyük veri sistemlerinde tüm elemanları saklamak maliyetli olduğu için **yaklaşık yöntemler** kullanılmaktadır.

---

## HyperLogLog Algoritması

HyperLogLog algoritması şu prensiplere dayanır:

1. Veriler hash fonksiyonu ile sayıya dönüştürülür.
2. Hash değerleri bucket'lara ayrılır.
3. Her bucket için **leading zero (ardışık sıfır)** sayısı hesaplanır.
4. Bu değerler register dizisinde tutulur.
5. Harmonik ortalama kullanılarak cardinality tahmini yapılır.

---

## Algoritmanın Temel Bileşenleri

Bu projede HyperLogLog algoritmasının aşağıdaki bileşenleri implement edilmiştir:

- Yüksek kaliteli hash fonksiyonu (SHA-1)
- Bucketing mekanizması
- Register yapısı
- Leading zero hesaplama
- Harmonik ortalama ile cardinality tahmini
- Small range correction
- Large range correction
- Merge özelliği (iki HLL yapısını birleştirme)

---

## Matematiksel Formüller

### Bucket Sayısı
m = 2^p

Burada:

- `p` = bucket indeks bit sayısı
- `m` = toplam bucket sayısı

---

### Cardinality Tahmini


HyperLogLog tahmini şu formül ile hesaplanır:
E = αm * m² / ( Σ 2^(-M[j]) )

Burada:

- `E` = cardinality tahmini
- `m` = bucket sayısı
- `M[j]` = register değeri
- `αm` = bias correction sabiti

---

### Small Range Correction

Eğer tahmin edilen değer küçük ise:
E = m * ln(m / V)


Burada:

- `V` = boş bucket sayısı

---

### Large Range Correction

Büyük cardinality değerleri için:

E = -2^64 * ln(1 - E / 2^64)
---

### Hata Oranı

HyperLogLog algoritmasının teorik hata oranı:


Error ≈ 1.04 / √m


Bu formüle göre:

- bucket sayısı arttıkça hata oranı azalır.

---

## Proje Yapısı

Proje aşağıdaki Java sınıflarından oluşmaktadır:

### HashUtils.java
- SHA-1 hash fonksiyonu kullanarak giriş verisini 64-bit hash değerine dönüştürür.

### HyperLogLog.java
- Algoritmanın ana implementasyonunu içerir.
- Bucket yapısı
- Register dizisi
- Cardinality tahmini
- Correction mekanizmaları
- Merge işlemi

### Experiment.java
- Farklı `p` değerleri için deneyler gerçekleştirir.
- Gerçek cardinality ile HLL tahminini karşılaştırır.
- Ortalama bağıl hata hesaplar.

### Main.java
- HyperLogLog algoritmasının kullanım örneklerini gösterir.
- Basit örnek
- Merge örneği

---

## Çalıştırma

Projeyi çalıştırmak için:
Ardından:

java Main

Deneysel analiz için:

java Experiment
Örnek Çıktı

TEMEL HYPERLOGLOG ÖRNEĞİ
Gerçek farklı eleman sayısı : 7
HLL tahmini : 7
Ham tahmin değeri : 7.2

```bash
javac *.java

Bucket sayısı aşağıdaki formül ile belirlenir:

