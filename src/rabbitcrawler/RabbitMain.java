package rabbitcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RabbitMain {

    public static boolean verbose;
    public static String line;
    public static Map<String, Set<String>> tmp;
    public static Map<String, Map<String, Set<String>>> Statistics;

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, KeyManagementException {
        if (args.length < 1) {
            System.out.println("Usage: program <filename> [silent]");
            System.exit(1);
        }
        //
        Statistics = new HashMap<>();
        String ListFileName = args[0].toString();
        File file = new File(ListFileName);
        if (!file.canRead()) {
            System.out.println("File '" + ListFileName + "' not found");
            System.exit(1);
        }
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                try {
                    URL url = new URL(line);
                    RabbitCrawler.RabbitCrawler();
                    RabbitCrawler.setVerbose(verbose);
                    RabbitCrawler.setUrl(url);
                    RabbitCrawler.CrawlUrl();
                    tmp = new HashMap<>();
                    tmp.put("Email", RabbitCrawler.EmailList);
                    tmp.put("Summary", RabbitCrawler.UrlList);
                    tmp.put("Image", RabbitCrawler.ImageList);
                    tmp.put("Audio", RabbitCrawler.AudioList);
                    tmp.put("Video", RabbitCrawler.VideoList);
                    tmp.put("Executable", RabbitCrawler.ExeList);
                    tmp.put("Document", RabbitCrawler.DocList);

                    Statistics.put(url.getHost(), tmp);

                } catch (MalformedURLException e) {
                }

            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                System.out.println();
                for (Map.Entry mapEntry : Statistics.entrySet()) {
                    System.out.printf(RabbitCrawler.ANSI_CYAN + "%15s" + RabbitCrawler.ANSI_RESET + "\n\t\t", mapEntry.getKey());
                    Map<String, List<String>> t = (Map<String, List<String>>) mapEntry.getValue();
                    for (Map.Entry stat : t.entrySet()) {
                        Set<String> d = (Set<String>) stat.getValue();
                        System.out.printf(RabbitCrawler.ANSI_PURPLE + "%s:" + RabbitCrawler.ANSI_RESET + "%4d " + RabbitCrawler.ANSI_YELLOW + RabbitCrawler.ANSI_RESET, stat.getKey(), d.size());
                    }
                    System.out.println();
                }
                RabbitCrawler.destruct();
            } catch (IOException e) {
            }

        }
    }

}
