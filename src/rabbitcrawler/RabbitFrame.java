/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitcrawler;

import rabbitcrawler.RabbitFrame.CT;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;
import static rabbitcrawler.RabbitCrawler.url;
import static rabbitcrawler.RabbitMain.line;


public class RabbitFrame extends javax.swing.JFrame {

    final DefaultListModel model;
    private Thread t = null;
  
    public class CT implements Runnable {

        private final URL url;

        public CT(URL urlstr) {
            this.url = urlstr;
        }


        @Override
        public void run() {
            int listSize = UrlImportList.getModel().getSize();
            DefaultListModel m = null;
            msgLabel.setText("Starting...");
            crawlBtn.setText("Stop");
            for (int i = 0; i < listSize; i++) {
                Object element = UrlImportList.getModel().getElementAt(i);
                URL url = null;
                try {
                    url = new URL(element.toString());
                    if (RabbitCrawler.containAudio(url) || RabbitCrawler.containExe(url) || RabbitCrawler.containImage(url) || RabbitCrawler.containVideo(url)) {
                        continue;
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                RabbitCrawler.RabbitCrawler();
                RabbitCrawler.setVerbose(false);               
                RabbitCrawler.setExportParams(chkExportUrl.isSelected(),chkExportEmail.isSelected());
                RabbitCrawler.setUrl(url);
                String text = "Grabbing " + url.toString();
                msgLabel.setText(text);
                try {
                    RabbitCrawler.CrawlUrl();
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
                    Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                //
                if (0 < RabbitCrawler.EmailList.size()) {
                    try {
                        m = (DefaultListModel) (emailList.getModel());       
                        for (String el : RabbitCrawler.EmailList) {
                            if(!m.contains(el))m.addElement(el);
                        }
                    } catch (Exception e) {

                    }

                }
                //
                if (0 < RabbitCrawler.ImageList.size()) {
                    try {
                        m = (DefaultListModel) imageList.getModel();
                        for (String el : RabbitCrawler.ImageList) {
                            if(!m.contains(el))m.addElement(el);
                        }

                    } catch (Exception e) {

                    }

                }
                //
                if (0 < RabbitCrawler.AudioList.size()) {
                    try {
                        m = (DefaultListModel) audioList.getModel();
                        for (String el : RabbitCrawler.AudioList) {
                            if(!m.contains(el))m.addElement(el);
                        }
                    } catch (Exception e) {

                    }

                }
                //
                if (0 < RabbitCrawler.VideoList.size()) {
                    try {
                        m = (DefaultListModel) videoList.getModel();
                        for (String el : RabbitCrawler.VideoList) {
                            if(!m.contains(el))m.addElement(el);
                        }
                    } catch (Exception e) {

                    }
                }
                //
                if (0 < RabbitCrawler.ExeList.size()) {
                    try {
                        m = (DefaultListModel) execList.getModel();
                        for (String el : RabbitCrawler.ExeList) {
                            if(!m.contains(el))m.addElement(el);
                        }
                    } catch (Exception e) {

                    }
                }
                //
                if (0 < RabbitCrawler.DocList.size()) {
                    try {
                        m = (DefaultListModel) docList.getModel();;
                        for (String el : RabbitCrawler.DocList) {
                            if(!m.contains(el))m.addElement(el);
                        }
                    } catch (Exception e) {

                    }
                }
            }
            msgLabel.setText("Done");
            crawlBtn.setText("Crawl");
        }
    }

    public RabbitFrame() {
        initComponents();
        t = new Thread(new CT(url));

        model = new DefaultListModel();
        UrlImportList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(UrlImportList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        emailList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(emailList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        imageList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(imageList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        audioList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(audioList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        videoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(videoList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        execList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(execList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        docList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        openWebpage(new URL(docList.getModel().getElementAt(index).toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        addUrlTxt = new javax.swing.JTextField();
        addUrlBtn = new javax.swing.JButton();
        ImportUrlListBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        UrlImportList = new javax.swing.JList();
        crawlBtn = new javax.swing.JButton();
        exportBtn = new javax.swing.JButton();
        deleteUrlBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        msgLabel = new java.awt.Label();
        jScrollPane2 = new javax.swing.JScrollPane();
        emailList = new javax.swing.JList(new DefaultListModel());
        jScrollPane3 = new javax.swing.JScrollPane();
        execList = new javax.swing.JList(new DefaultListModel());
        jScrollPane4 = new javax.swing.JScrollPane();
        imageList = new javax.swing.JList(new DefaultListModel());
        jScrollPane5 = new javax.swing.JScrollPane();
        audioList = new javax.swing.JList(new DefaultListModel());
        jScrollPane6 = new javax.swing.JScrollPane();
        videoList = new javax.swing.JList(new DefaultListModel());
        jScrollPane7 = new javax.swing.JScrollPane();
        docList = new javax.swing.JList(new DefaultListModel());
        chkExportUrl = new javax.swing.JCheckBox();
        chkExportEmail = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RabbitCrawler");
        setBackground(new java.awt.Color(0, 153, 153));
        setName("MainFrame"); // NOI18N
        setResizable(false);

        jLabel1.setText("URL:");

        addUrlBtn.setText("Add");
        addUrlBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUrlBtnActionPerformed(evt);
            }
        });

        ImportUrlListBtn.setText("Import");
        ImportUrlListBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportUrlListBtnActionPerformed(evt);
            }
        });

        UrlImportList.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(UrlImportList);

        crawlBtn.setText("Crawl");
        crawlBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crawlBtnActionPerformed(evt);
            }
        });

        exportBtn.setText("Export");
        exportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBtnActionPerformed(evt);
            }
        });

        deleteUrlBtn.setText("Delete selected");
        deleteUrlBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteUrlBtnActionPerformed(evt);
            }
        });

        clearBtn.setText("Clear all");
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        emailList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane2.setViewportView(emailList);
        emailList.getAccessibleContext().setAccessibleDescription("");

        execList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane3.setViewportView(execList);

        imageList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane4.setViewportView(imageList);

        audioList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane5.setViewportView(audioList);

        videoList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane6.setViewportView(videoList);

        docList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane7.setViewportView(docList);

        chkExportUrl.setText("Background export of URLs");
        chkExportUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkExportUrlActionPerformed(evt);
            }
        });

        chkExportEmail.setText("Background export of e-mails");
        chkExportEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkExportEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1418, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(27, 27, 27)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(addUrlTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(msgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ImportUrlListBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                .addComponent(addUrlBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(crawlBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                .addComponent(exportBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(deleteUrlBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                .addComponent(clearBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(chkExportUrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chkExportEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addComponent(addUrlTxt))
                        .addGap(12, 12, 12)
                        .addComponent(msgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addUrlBtn)
                            .addComponent(crawlBtn)
                            .addComponent(deleteUrlBtn)
                            .addComponent(chkExportUrl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkExportEmail)
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ImportUrlListBtn)
                            .addComponent(exportBtn)
                            .addComponent(clearBtn))
                        .addGap(6, 6, 6)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addUrlBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUrlBtnActionPerformed
        try {
            if (!addUrlTxt.getText().isEmpty()) {
                URL url = new URL(addUrlTxt.getText());

                if (!model.contains(url.toString())) {
                    model.addElement(addUrlTxt.getText().toString());
                    UrlImportList.setModel(model);
                    msgLabel.setText(null);
                    addUrlTxt.setText(null);
                } else {
                    msgLabel.setText("Duplicate element skipped");
                }

            } else {
                msgLabel.setText("Type URL");
            }
        } catch (MalformedURLException ex) {
            msgLabel.setText("Invalid URL (Syntax is: protocol://host[:port/path?query#ref])");
        }

    }//GEN-LAST:event_addUrlBtnActionPerformed

    private void ImportUrlListBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportUrlListBtnActionPerformed
        final JFileChooser importDialog = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        File file = null;
        importDialog.setFileFilter(filter);
        int returnVal = importDialog.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename = importDialog.getSelectedFile().getName().toString();
            if (filename.isEmpty()) {
                return;
            }
            file = new File(importDialog.getSelectedFile().getName());

        }
        BufferedReader reader = null;

        try {
            if (file != null && file.canRead()) {
                reader = new BufferedReader(new FileReader(file));

                while ((line = reader.readLine()) != null) {
                    try {
                        URL url = new URL(line);
                        if (RabbitCrawler.containAudio(url) || RabbitCrawler.containExe(url) || RabbitCrawler.containImage(url) || RabbitCrawler.containVideo(url)) {
                            continue;
                        }
                        if (!model.contains(line)) {
                            model.addElement(line);
                        }
                    } catch (MalformedURLException e) {
                    }

                }
                UrlImportList.setModel(model);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }

        }
    }//GEN-LAST:event_ImportUrlListBtnActionPerformed

    private void crawlBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crawlBtnActionPerformed

        if (t != null) {
            boolean alive = t.isAlive();
            if (alive) {
                msgLabel.setText("Interrupting crawling...");
                t.interrupt();
                if (t.isInterrupted()) {
                    msgLabel.setText("Interrupted");
                }
                crawlBtn.setText("Crawl");
                return;
            }
        }
        try {  
            t = new Thread(new CT(url));
            t.start();
        } catch (Exception e) {
        }
        //
    }//GEN-LAST:event_crawlBtnActionPerformed
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (IOException e) {
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
        }
    }
    private void exportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBtnActionPerformed
        final JFileChooser exportDialog = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        File saveFile = null;
        exportDialog.setFileFilter(filter);
        int returnVal = exportDialog.showSaveDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            String FilePath = exportDialog.getSelectedFile().getPath();
            saveFile = new File(FilePath);

            if (!FilePath.endsWith(".txt")) {
                FilePath = FilePath.concat(".txt");
            }

            FileWriter fw = null;
            try {
                fw = new FileWriter(FilePath);
            } catch (IOException ex) {
                Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                int size = UrlImportList.getModel().getSize();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        fw.write(UrlImportList.getModel().getElementAt(i).toString() + "\n");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(RabbitFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }//GEN-LAST:event_exportBtnActionPerformed

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        DefaultListModel listmodel;
        try {
            listmodel = (DefaultListModel) UrlImportList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) docList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) execList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) videoList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) audioList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) imageList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
        try {
            listmodel = (DefaultListModel) emailList.getModel();
            listmodel.removeAllElements();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_clearBtnActionPerformed

    private void deleteUrlBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUrlBtnActionPerformed
        DefaultListModel listmodel;
        try {
            listmodel = (DefaultListModel) UrlImportList.getModel();
            listmodel.removeElementAt(UrlImportList.getSelectedIndex());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_deleteUrlBtnActionPerformed

    private void chkExportUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkExportUrlActionPerformed
       
    }//GEN-LAST:event_chkExportUrlActionPerformed

    private void chkExportEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkExportEmailActionPerformed
        
    }//GEN-LAST:event_chkExportEmailActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RabbitFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RabbitFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ImportUrlListBtn;
    private javax.swing.JList UrlImportList;
    private javax.swing.JButton addUrlBtn;
    private javax.swing.JTextField addUrlTxt;
    private javax.swing.JList audioList;
    private javax.swing.JCheckBox chkExportEmail;
    private javax.swing.JCheckBox chkExportUrl;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton crawlBtn;
    private javax.swing.JButton deleteUrlBtn;
    private javax.swing.JList docList;
    private javax.swing.JList emailList;
    private javax.swing.JList execList;
    private javax.swing.JButton exportBtn;
    private javax.swing.JList imageList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private java.awt.Label msgLabel;
    private javax.swing.JList videoList;
    // End of variables declaration//GEN-END:variables

}
