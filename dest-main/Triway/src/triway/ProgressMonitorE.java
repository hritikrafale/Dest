/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package triway;

/**
 * @author SSN
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static triway.Home.jTable1;
import static triway.Home.jTable3;
import static triway.Home.Row;
//import java.util.logging.Logger;

public class ProgressMonitorE extends JPanel {
    
    //final static Logger logger = Logger.getLogger(ProgressMonitor.class);
    
    private JProgressBar progressBar;
    public static CopyFiles operation;
    String sourceDirectory;
    String destinationDirectory;
    private JLabel size;
    private JLabel time;
    private JLabel from;
    private JLabel to;
    JFrame frame;
    public static int fileCopiedDb;
    public static long fileCopiedSizeDb;
    public static String timeToCopy;


    public static void startCopyProcess(int row, int col) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProgressMonitorE().createAndShowGUI(row, col);
            }
        });
    }

    private void createAndShowGUI(int row, int col) {
        Action pause = new AbstractAction("Pause") {
            @Override
            public void actionPerformed(ActionEvent e) {
                operation.pause();
            }
        };

        Action resume = new AbstractAction("Resume") {
            @Override
            public void actionPerformed(ActionEvent e) {
                operation.resume();
            }
        };

        Action stop = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                operation.cancel(true);
            }
        };


        sourceDirectory = jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString();


        destinationDirectory = jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString();

        File theDir = new File(destinationDirectory + "/" + sourceDirectory.substring(0, 1) + "_" + jTable3.getModel().getValueAt(row, 3).toString() + "_" + jTable3.getModel().getValueAt(row, 2).toString() + "_" + jTable3.getModel().getValueAt(row, 4).toString());
        if (!theDir.exists()) {
            theDir.mkdirs();
        }


        File srcDir = new File(sourceDirectory);
        File destDir = null;

        if (srcDir.exists() && (srcDir.listFiles() != null && srcDir.listFiles().length > 0)) {
            destDir = new File(theDir.toString());
        }
        operation = new CopyFiles(srcDir, destDir, row, col);
        //operation.addPropertyChangeListener(this);
        operation.execute();

        operation.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("paused".equals(evt.getPropertyName())) {
                    if ((Boolean) evt.getNewValue() == true) {
                        System.out.println("Paused");
                    } else {
                        if (evt.getPropertyName().equals("progress")) {
                            // get the % complete from the progress event
                            // and set it on the progress monitor
                            int progress = ((Integer) evt.getNewValue()).intValue();
                            //progressMonitor.setProgress(progress);
                            progressBar.setValue(progress);
                        }
                        System.out.println("Resumed");
                    }
                    String text = (Boolean) evt.getNewValue() ? "Paused..." : "Resumed...";
                }
            }
        });
    }


    static class CopyData {

        private long i;
        private String s;
        private long i0;
        private long i1;
        private String t;
        private boolean isPaused;

        public CopyData(long i, String s, long i0, long i1, String t, boolean isPaused) {
            this.i = i;
            this.s = s;
            this.i0 = i0;
            this.i1 = i1;
            this.t = t;
            this.isPaused = isPaused;
        }

        long getTotalKiloBytes() {
            return i0;
        }

        long getProgress() {
            return i;
        }

        long getKiloBytesCopied() {
            return i1;
        }

        String getFileName() {
            return s;
        }

        String getTime() {
            return t;
        }

        boolean getisPaused() {
            return isPaused;
        }
    }

    static class CopyFiles extends SwingWorker<List<CopyData>, CopyData> {
        private File srcDir;
        private File destDir;

        private int row;
        private int col;

        public CopyFiles(File srcDir, File destDir, int row, int col) {
            this.srcDir = srcDir;
            this.destDir = destDir;
            this.row = row;
            this.col = col;
        }

        private boolean isPaused;

        public final void pause() {
            if (!isPaused() && !isDone()) {
                isPaused = true;
                firePropertyChange("paused", false, true);
            }
        }

        public final void resume() {
            if (isPaused() && !isDone()) {
                isPaused = false;
                firePropertyChange("paused", true, false);
            }
        }

        public final boolean isPaused() {
            return isPaused;
        }

        public String timeToString(long nanos) {

            Optional<TimeUnit> first = Stream.of(DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS).filter(u -> u.convert(nanos, NANOSECONDS) > 0).findFirst();
            TimeUnit unit = first.isPresent() ? first.get() : NANOSECONDS;

            double value = (double) nanos / NANOSECONDS.convert(1, unit);
            //return String.format("%.4g %s", value, unit.name().toLowerCase());
            return String.format("%.4g %s", value, "(mins)");
        }

        long getTotalKiloBytes(long totalBytes) {
            return totalBytes / 1000;
        }

        long getKiloBytesCopied(long bytesCopied) {
            return bytesCopied / 1000;
        }

        long calcTotalBytes(File directory) {
            long length = 0;
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += calcTotalBytes(file);
            }
            return length;
        }

        public void process(List<CopyData> data) {
            if (isCancelled()) {
                return;
            }
            CopyData update = new CopyData(0, "", 0, 0, "", false);
            for (CopyData d : data) {
                // progress updates may be batched, so get the most recent
                if (d.getKiloBytesCopied() > update.getKiloBytesCopied()) {
                    update = d;
                }
            }
            String progressNote = Math.round((double) update.getKiloBytesCopied() / 1000000.00 * 100.00) / 100.00 + " GB " + " of " + Math.round((double) update.getTotalKiloBytes() / 1000000.00 * 100.00) / 100.00 + " GB";

            DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
            if (update.getProgress() < 100 && !update.getisPaused()) {
                tableModel.setValueAt((int) update.getProgress(), row, col);

            } else if (update.getProgress() < 100 && update.getisPaused()) {

            } else {
                tableModel.setValueAt((int) update.getProgress(), row, col);
            }
        }

        long startTime = System.nanoTime();

        @Override
        public List<CopyData> doInBackground() throws Exception {
            //if (!isCancelled()) {
            if (!isPaused()) {
                int progress = 0;
                System.out.println("inside doing background");
                // initialize bound property progress (inherited from SwingWorker)
                setProgress(0);
                // get the files to be copied from the source directory
                File[] files = srcDir.listFiles();
                fileCopiedDb = files.length;
                // determine the scope of the task
                long totalBytes = calcTotalBytes(srcDir);
                fileCopiedSizeDb = calcTotalBytes(srcDir);
                long bytesCopied = 0;

                while (progress < 100) {

                    if (!isPaused()) {

                        System.out.println(Thread.currentThread().getName());
                        System.out.println("Copy process - not paused");
                        // copy the files to the destination directory
                        for (File f : files) {


                            File destFile = new File(destDir, f.getName());
                            long previousLen = 0;
                            //taskOutput.append("Now Copying "+ f.getName() + "\n");
                            try {
                                InputStream in = new FileInputStream(f);
                                OutputStream out = new FileOutputStream(destFile);
                                byte[] buf = new byte[12000000];
                                int counter = 0;
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    if (!isPaused()) {
                                        out.write(buf, 0, len);
                                        counter += len;
                                        bytesCopied += (destFile.length() - previousLen);
                                        previousLen = destFile.length();
                                        int PROGRESS_CHECKPOINT = 0;
                                        if (counter > PROGRESS_CHECKPOINT || bytesCopied == totalBytes) {
                                            // get % complete for the task
                                            progress = (int) ((100 * bytesCopied) / totalBytes);
                                            counter = 0;
                                            CopyData current = new CopyData(progress, f.getName(), getTotalKiloBytes(totalBytes), getKiloBytesCopied(bytesCopied), timeToString((((totalBytes - bytesCopied) * 100) * (totalBytes / bytesCopied))), isPaused());

                                            setProgress(progress);
                                            publish(current);
                                        }
                                    } else {
                                        try {
                                            //Thread.sleep(2000);
                                            waitFn();

                                            System.out.println(Thread.currentThread().getName());

                                            System.out.println("Thread is sleeping!!");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                in.close();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            waitFn();
                            System.out.println("Thread is sleeping!!");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //taskOutput.append("Copied "+ f.getName() + "\n");

                }
            } else {
                try {

                    waitFn();


//5 s                                                            System.out.println(Thread.currentThread().getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}

            }


            return null;
        }

        public void waitFn() {
            int delay = 2000; // number of milliseconds to sleep

            long start = System.currentTimeMillis();
            while (start >= System.currentTimeMillis() - delay) ; // do nothing

        }

        @Override
        public void done() {
            try {
                // call get() to tell us whether the operation completed or
                // was canceled; we don't do anything with this result
                CopyData result = (CopyData) get();
                //taskOutput.append("Copy operation completed.\n");
                //jTable1.setValueAt("Copied Not Verified", jTable1.getSelectedRow(), jTable1.getColumn("Status").getModelIndex());
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            } catch (CancellationException e) {
                // get() throws CancellationException if background task was canceled
                //taskOutput.append("Copy operation canceled.\n");
                //jTable1.setValueAt("Canceled", jTable1.getSelectedRow(), jTable1.getColumn("Status").getModelIndex());
            } catch (ExecutionException e) {
                //taskOutput.append("Exception: " + "Please select Directory not Drive");

                JOptionPane.showMessageDialog(null, "Please select Directory not Drive");
            }
            Toolkit.getDefaultToolkit().beep();
            //progressMonitor.setProgress(0);
            long endTime = System.nanoTime();
            long a = endTime - startTime;
            timeToCopy = timeToString(a);
        }
    }
}