package de.yanwittmann.cmdtool.api;

import de.yanwittmann.cmdtool.data.DataProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Use an object of this class to translate a text using the Google Translate API.<br>
 * Use bulk requests to speed up the translation process and to prevent error 429 (you can only perform a certain amount of requests in a time window).<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class GoogleTranslate {

    private String fromLanguage = "en";
    private String toLanguage = "de";

    public String translate(String text) {
        try {
            String[] response = DataProvider.getResponseFromURL(prepareTranslateURL(text));
            if (response.length != 0) {
                String asOneLine = String.join("", response);
                if (asOneLine.matches(TRANSLATE_RESULT_REGEX))
                    return asOneLine.replaceAll(TRANSLATE_RESULT_REGEX, "$1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public String translate(String from, String to, String text) {
        this.fromLanguage = from;
        this.toLanguage = to;
        return translate(text);
    }

    private HashMap<Object, String> bulkRequests;
    private HashMap<Integer, Object> bulkRequestsObjects;
    private boolean initializedBulkMap = false;

    public void addRequest(Object key, String text) {
        if (!initializedBulkMap) {
            bulkRequests = new HashMap<>();
            bulkRequestsObjects = new HashMap<>();
            initializedBulkMap = true;
        }
        bulkRequestsObjects.put(bulkRequests.size(), key);
        bulkRequests.put(key, text);
    }

    public HashMap<Object, String> performRequests() {
        if (!initializedBulkMap || bulkRequests.size() == 0) return bulkRequests;
        StringJoiner request = new StringJoiner("||");
        for (Map.Entry<Object, String> individualRequest : bulkRequests.entrySet()) {
            Integer key = bulkRequestsObjects.entrySet().stream().filter(connection -> connection.getValue().equals(individualRequest.getKey())).findFirst().map(Map.Entry::getKey).orElse(null);
            request.add(key + "==" + individualRequest.getValue());
        }
        String[] results = translate(request.toString()).split(" ?\\|\\| ?");
        for (String result : results) {
            String[] splitted = result.split(" ?(?:==|(?:\\\\u003d){2}) ?", 2);
            if (splitted.length >= 2)
                bulkRequests.put(bulkRequestsObjects.get(Integer.parseInt(splitted[0])), splitted[1]);
        }
        clearRequests();
        return bulkRequests;
    }

    public void clearRequests() {
        initializedBulkMap = false;
    }

    public void setLanguages(String fromLanguage, String toLanguage) {
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }

    public void setFromLanguage(String fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public void setToLanguage(String toLanguage) {
        this.toLanguage = toLanguage;
    }

    private String prepareTranslateURL(String text) {
        return TRANSLATE_URL.replace("SOURCE", fromLanguage).replace("DEST", toLanguage).replace("TEXT", text);
    }

    public final static String TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=SOURCE&tl=DEST&dt=t&q=TEXT";
    public final static String TRANSLATE_RESULT_REGEX = "\\[+\"(.+?)\",\".*";

    public enum LANGUAGE {
        Afrikaans("af"),
        Albanian("sq"),
        Amharic("am"),
        Arabic("ar"),
        Armenian("hy"),
        Azerbaijani("az"),
        Basque("eu"),
        Belarusian("be"),
        Bengali("bn"),
        Bosnian("bs"),
        Bulgarian("bg"),
        Catalan("ca"),
        Cebuano("ceb (ISO-639-2)"),
        ChineseSimplified("zh-CN"),
        ChineseTraditional("zh-TW"),
        Corsican("co"),
        Croatian("hr"),
        Czech("cs"),
        Danish("da"),
        Dutch("nl"),
        English("en"),
        Esperanto("eo"),
        Estonian("et"),
        Finnish("fi"),
        French("fr"),
        Frisian("fy"),
        Galician("gl"),
        Georgian("ka"),
        German("de"),
        Greek("el"),
        Gujarati("gu"),
        HaitianCreole("ht"),
        Hausa("ha"),
        Hawaiian("haw"),
        Hebrew("he or iw"),
        Hindi("hi"),
        Hmong("hmn"),
        Hungarian("hu"),
        Icelandic("is"),
        Igbo("ig"),
        Indonesian("id"),
        Irish("ga"),
        Italian("it"),
        Japanese("ja"),
        Javanese("jv"),
        Kannada("kn"),
        Kazakh("kk"),
        Khmer("km"),
        Kinyarwanda("rw"),
        Korean("ko"),
        Kurdish("ku"),
        Kyrgyz("ky"),
        Lao("lo"),
        Latin("la"),
        Latvian("lv"),
        Lithuanian("lt"),
        Luxembourgish("lb"),
        Macedonian("mk"),
        Malagasy("mg"),
        Malay("ms"),
        Malayalam("ml"),
        Maltese("mt"),
        Maori("mi"),
        Marathi("mr"),
        Mongolian("mn"),
        MyanmarBurmese("my"),
        Nepali("ne"),
        Norwegian("no"),
        NyanjaChichewa("ny"),
        OdiaOriya("or"),
        Pashto("ps"),
        Persian("fa"),
        Polish("pl"),
        PortuguesePortugal("pt"),
        PortugueseBrazil("pt"),
        Punjabi("pa"),
        Romanian("ro"),
        Russian("ru"),
        Samoan("sm"),
        ScotsGaelic("gd"),
        Serbian("sr"),
        Sesotho("st"),
        Shona("sn"),
        Sindhi("sd"),
        SinhalaSinhalese("si"),
        Slovak("sk"),
        Slovenian("sl"),
        Somali("so"),
        Spanish("es"),
        Sundanese("su"),
        Swahili("sw"),
        Swedish("sv"),
        TagalogFilipino("tl"),
        Tajik("tg"),
        Tamil("ta"),
        Tatar("tt"),
        Telugu("te"),
        Thai("th"),
        Turkish("tr"),
        Turkmen("tk"),
        Ukrainian("uk"),
        Urdu("ur"),
        Uyghur("ug"),
        Uzbek("uz"),
        Vietnamese("vi"),
        Welsh("cy"),
        Xhosa("xh"),
        Yiddish("yi"),
        Yoruba("yo"),
        Zulu("zu");

        public final String identifier;

        LANGUAGE(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

}
