package rabbitcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class RabbitCrawler {

    public static boolean verbose;
    public static Set<String> UrlList;
    public static Set<String> EmailList;
    public static Set<String> ImageList;
    public static Set<String> VideoList;
    public static Set<String> AudioList;
    public static Set<String> DocList;
    public static Set<String> ExeList;

    // Refer to http://en.wikipedia.org/wiki/Image_file_formats
    public static String[] ImageExts = {
        "jpeg", "jfif", "exif", "tiff", "raw", "gif", "bmp", "png", "ppm", "pgm", "pbm", "pnm", "pam",
        "webp", "tga", "ilbm", "deep", "img", "pcx", "ecw", "sid", "cd5", "fits", "pgf", "xcf", "psd",
        "psp", "vicar", "cgm", "svg", "jpg"
    };
    public static String[] VideoExts = {
        "3gp", "asf", "wmv", "avi", "flv", "f4v", "iff", "mkv", "mpg", "mpeg", "mp4", "ogg", "rm"
    };
    // Refer to http://en.wikipedia.org/wiki/Audio_file_format
    public static String[] AudioExts = {
        "aiff", "wav", "xmf", "wma", "act", "aac", "amr", "atrac", "au", "awb", "dct", "dss",
        "dvf", "flac", "gsm", "iklax", "m4a", "m4p", "mmf", "mp3", "mpc", "msv", "vox"
    };
    // Refer to http://pcsupport.about.com/od/tipstricks/a/execfileext.htm
    public static String[] ExeExts = {
        "action", "apk", "app", "bat", "bin", "cmd", "com", "command", "cpl", "csh", "exe", "gadget",
        "inf", "ins", "inx", "ipa", "isu", "job", "jse", "ksh", "lnk", "msc", "msi", "msp", "mst", "osx",
        "out", "paf", "pif", "prg", "ps1", "reg", "rgs", "run", "sct", "shb", "shs", "u3p", "vb", "vbe", "vbs",
        "vbscript", "workflow", "ws", "wsf", "0xe", "73k", "89k", "a6p", "ac", "acc", "acr", "actm", "ahk",
        "air", "app", "arscript", "as", "asb", "awk", "azw2", "beam", "btm", "cel", "celx", "chm", "cof",
        "crt", "dek", "dld", "dmc", "docm", "dotm", "dxl", "ear", "ebm", "ebs", "ebs2", "ecf", "eham", "elf",
        "es", "ex4", "exopc", "ezs", "fas", "fky", "fpy", "frs", "fxp", "gs", "ham", "hms", "hpf", "hta", "iim",
        "ipf", "isp", "jar", "js", "jsx", "kix", "lo", "ls", "mam", "mcr", "mel", "mpx", "mrc", "ms", "mxe", "nexe",
        "obs", "ore", "otm", "pex", "plx", "potm", "ppam", "ppsm", "pptm", "prc", "pvd", "pwd", "pyc", "pyo", "qpx",
        "rbx", "rox", "rpj", "s2a", "sbs", "sca", "scar", "scb", "scr", "script", "smm", "spr", "tcp", "thm", "tlb",
        "tms", "udf", "upx", "url", "vlx", "vpm", "wcm", "widget", "wiz", "wpk", "wpm", "xap", "xbap", "xlam", "xlm",
        "xlsm", "xltm", "xqt", "xys", "zl9"
    };

    //
    public static FileWriter UrlFstream;
    public static FileWriter EmailFstream;
    public static BufferedWriter UrlOut;
    public static BufferedWriter EmailOut;
    public static URL url;
    public static boolean exportUrls;
    public static boolean exportEmails;
    // Just for fun
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private TrustManager[] trustAllCerts;

    public static BufferedReader OpenUrlStream(URL url) throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException {
        InputStream input = null;
        URLConnection urlCon = null;
        HttpsURLConnection con = null;

        final TrustManager[] trustAllCerts = new TrustManager[]{};
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        urlCon = url.openConnection();
        if (url.getProtocol().equals("https")) {
            con = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);

        }
        try {
            if (url.getProtocol().equals("https")) {
                con.connect();
                input = (InputStream) con.getInputStream();
            } else {
                urlCon.connect();
                input = (InputStream) urlCon.getContent();
            }
        } catch (IOException e) {

        }
        if (input != null) {
            return new BufferedReader(new InputStreamReader(input, "UTF-8"));
        } else {
            return null;
        }
    }

    public static void setExportParams(boolean exportUrl, boolean exportEmail) {
        exportUrls = exportUrl;
        exportEmails = exportEmail;
    }

    public static void CrawlUrl() throws MalformedURLException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException, KeyManagementException {

        if (verbose) {
            System.out.println(ANSI_RED + "\nFetching absolute URLs and EMAILs from [" + url + "]" + ANSI_RESET + "\n\n");
        }

        BufferedReader reader = null;

        try {
            if (exportUrls) {
                UrlFstream = new FileWriter(url.getHost() + "_urls.txt", false);
                UrlOut = new BufferedWriter(UrlFstream);
            }
            if (exportEmails) {
                EmailFstream = new FileWriter(url.getHost() + "_emails.txt", false);
                EmailOut = new BufferedWriter(EmailFstream);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        try {
            reader = OpenUrlStream(url);
            if (reader != null) {
                for (String s; (s = reader.readLine()) != null;) {
                    extractEmail(s);
                    extractUrl(s);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
            if (UrlOut != null) {
                UrlOut.close();
            }
            if (EmailOut != null) {
                EmailOut.close();
            }
        }
    }

    private static void extractUrl(String s) throws MalformedURLException, IOException {

        Pattern urlPattern = Pattern.compile(
                "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matches = urlPattern.matcher(s);
        while (matches.find()) {
            String urlStr = matches.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            boolean isProvenURL = new URL(urlStr).getHost().contains(".");
            if (isProvenURL) {
                UrlList.add(urlStr);
                arrangeByTypes(new URL(urlStr));
                if (UrlOut != null) {
                    UrlOut.write(urlStr + "\n");
                }
                if (verbose) {
                    System.out.println(ANSI_BLUE + "Url -> " + urlStr + ANSI_RESET);
                }
            }
        }
    }

    private static void extractEmail(String s) throws IOException {

        Matcher matches = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(s);
        while (matches.find()) {
            String email = matches.group();
            EmailList.add(email);
            if (EmailOut != null) {
                EmailOut.write(email + "\n");
            }
            if (verbose) {
                System.out.println(ANSI_YELLOW + "Email -> " + email + ANSI_RESET);
            }
        }
    }

    public static void RabbitCrawler() {
        initArrays();
    }

    private static void initArrays() {
        UrlOut = null;
        EmailOut = null;
        EmailList = new HashSet<>();
        UrlList = new HashSet<>();
        //
        ImageList = new HashSet<>();
        VideoList = new HashSet<>();
        AudioList = new HashSet<>();
        DocList = new HashSet<>();
        ExeList = new HashSet<>();
        //

    }

    public static void destruct() {
        UrlOut = null;
        EmailOut = null;

        if (!EmailList.isEmpty()) {
            EmailList.clear();
        }
        if (!UrlList.isEmpty()) {
            UrlList.clear();
        }
        if ((ImageList != null) && !ImageList.isEmpty()) {
            ImageList.clear();
        }
        if ((VideoList != null) && !VideoList.isEmpty()) {
            VideoList.clear();
        }
        if ((AudioList != null) && !AudioList.isEmpty()) {
            AudioList.clear();
        }
        if ((DocList != null) && !DocList.isEmpty()) {
            DocList.clear();
        }
        if ((ExeList != null) && !ExeList.isEmpty()) {
            ExeList.clear();
        }
    }

    public static void arrangeByTypes(URL url) {
        if (containImage(url)) {
            if (ImageList != null) {
                ImageList.add(url.toString());
            }
        } else if (containAudio(url)) {
            if (AudioList != null) {
                AudioList.add(url.toString());
            }
        } else if (containVideo(url)) {
            if (VideoList != null) {
                VideoList.add(url.toString());
            }
        } else if (containExe(url)) {
            if (ExeList != null) {
                ExeList.add(url.toString());
            }
        } else {
            if (DocList != null) {
                DocList.add(url.toString());
            }
        }
    }

    public static boolean containImage(URL url) {
        for (String extension : ImageExts) {
            if (url.getPath().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containVideo(URL url) {
        for (String extension : VideoExts) {
            if (url.getPath().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containAudio(URL url) {
        for (String extension : AudioExts) {
            if (url.getPath().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containExe(URL url) {
        for (String extension : ExeExts) {
            if (url.getPath().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static void setUrl(URL Url) {
        url = Url;
    }

    public static void setVerbose(boolean vrb) {
        verbose = vrb;
    }
}
