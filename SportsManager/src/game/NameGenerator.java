package game;

import java.util.Random;

public class NameGenerator {

    private static final Random random = new Random();

    private static final String[] FIRST_NAMES = {
        "Ahmet", "Mehmet", "Mustafa", "Ali", "Hüseyin", "Hasan", "İbrahim", "İsmail",
        "Yusuf", "Osman", "Murat", "Ömer", "Emre", "Burak", "Cem", "Deniz",
        "Enes", "Furkan", "Gökhan", "Halil", "Kadir", "Kerem", "Oğuz", "Onur",
        "Serkan", "Sinan", "Tolga", "Uğur", "Volkan", "Yılmaz", "Barış", "Can",
        "Doruk", "Erdem", "Fatih", "Gökçen", "Hamza", "İlker", "Kaan", "Levent",
        "Mert", "Necati", "Orhan", "Polat", "Rıza", "Selim", "Taner", "Umut",
        "Vedat", "Yasin", "Zafer", "Adem", "Bilal", "Cenk", "Doğan", "Erkan",
        "Ferhat", "Güven", "Haydar", "İlhan", "Kemal", "Latif", "Mesut", "Nihat",
        "Okan", "Recep", "Sefa", "Tarık", "Ufuk", "Veli", "Yavuz", "Zeki",
        "Arda", "Berkay", "Caner", "Dursun", "Eren", "Ferit", "Görkem", "Harun"
    };

    private static final String[] LAST_NAMES = {
        "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Yıldız", "Yıldırım", "Öztürk",
        "Aydın", "Özdemir", "Arslan", "Doğan", "Kılıç", "Aslan", "Çetin", "Kara",
        "Koç", "Kurt", "Özkan", "Şimşek", "Polat", "Korkmaz", "Çakır", "Erdoğan",
        "Acar", "Bal", "Bulut", "Güneş", "Güler", "Aktaş", "Aksoy", "Avcı",
        "Başaran", "Bayrak", "Atak", "Bozkurt", "Coşkun", "Duran", "Elmas", "Ergün",
        "Ertürk", "Genç", "Gül", "Gündüz", "Işık", "Kahraman", "Karaca", "Keskin",
        "Koçak", "Mutlu", "Özen", "Peker", "Sarı", "Sönmez", "Taş", "Tekin",
        "Toprak", "Tunç", "Türk", "Uçar", "Ünal", "Uzun", "Yalçın", "Zengin",
        "Altın", "Bakır", "Candan", "Dağ", "Eker", "Fidan", "Göker", "Han",
        "İnan", "Kalkan", "Lale", "Mercan", "Narin", "Oğuz", "Parlak", "Reis"
    };

    private static final String[] TEAM_NAMES = {
        "Saryaspor", "Serkanspor", "Aslanspor", "Boğaspor", "Kaplanspor",
        "Şimşekspor", "Fırtınaspor", "Ateşspor", "Çelikspor", "Doğanspor",
        "Gökkuşağı FK", "Anadolu FK", "Başkent FK", "Denizli FK", "Ege FK",
        "Akdeniz FK", "Amed Spor FK", "Trakya FK", "Marmara FK", "İç Anadolu FK",
        "Güneydoğu FK", "Doğu FK", "Batı FK", "Kuzey FK"
    };

    private static final String[] CITY_NAMES = {
        "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya",
        "Adana", "Konya", "Gaziantep", "Kayseri", "Trabzon",
        "Samsun", "Eskişehir", "Denizli", "Mersin", "Diyarbakır",
        "Sivas", "Malatya", "Elazığ", "Erzurum", "Rize",
        "Hatay", "Manisa", "Balıkesir", "Bolu"
    };

    private static final String[] COACH_SPECIALTIES = {
        "Defensive", "Offensive", "Balanced", "Taktikçi", "Genç Yetiştirici"
    };

    public static String generatePlayerName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
               LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    public static String generateCoachName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
               LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    public static String getTeamName(int index) {
        if (index < TEAM_NAMES.length) {
            return TEAM_NAMES[index];
        }
        return "Takım " + (index + 1);
    }

    public static String getCityName(int index) {
        if (index < CITY_NAMES.length) {
            return CITY_NAMES[index];
        }
        return CITY_NAMES[random.nextInt(CITY_NAMES.length)];
    }

    public static String getRandomSpecialty() {
        return COACH_SPECIALTIES[random.nextInt(COACH_SPECIALTIES.length)];
    }

    public static int generateOVR() {
        return 40 + random.nextInt(60); 
    }

    public static int generatePlayerAge() {
        return 17 + random.nextInt(22); 
    }

    public static int generateCoachExperience() {
        return 1 + random.nextInt(30);
    }

    public static int generateCoachRating() {
        return 40 + random.nextInt(60);
    }

    public static Position getPositionForIndex(int index) {
        if (index < 2) return Position.GOALKEEPER;
        if (index < 8) return Position.DEFENDER;
        if (index < 14) return Position.MIDFIELDER;
        return Position.FORWARD;
    }
}
